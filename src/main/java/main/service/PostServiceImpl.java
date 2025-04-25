package main.service;

import main.api.request.ModerationRequest;
import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.*;
import main.dto.*;
import main.mapper.PostMapper;
import main.model.Post;
import main.model.PostVote;
import main.model.enums.ModerationStatus;
import main.repository.*;
import main.service.strategy.enums.FilterMode;
import main.service.strategy.filter.FilterStrategy;
import main.utils.RandomUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final int MAX_FILE_SIZE = 5_242_880;
    private static final int POSTS_ON_PAGE = 10;
    private static final String POST_NOT_FOUND_ERROR_MESSAGE = "Пост не найден";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final byte MODERATOR_VALUE = 1;
    private static final String INACTIVE_POST_STATUS = "inactive";
    private static final String PENDING_POST_STATUS = "pending";
    private static final String DECLINED_POST_STATUS = "declined";
    private static final String PUBLISHED_POST_STATUS = "published";
    private static final String ACCEPT_DECISION = "accept";
    private static final String PNG_FILE_EXTENSION = "png";
    private static final String JPG_FILE_EXTENSION = "jpg";
    private static final String FILE_SIZE_ERROR_MESSAGE = "Размер файла превышает допустимый размер";
    private static final String FILE_EXTENSION_ERROR_MESSAGE = "Неверный формат изображения! Изображение должно быть в формате png или jpg";
    private static final String UPLOAD_FOLDER_PATH = "/upload/ab/cd/ef/";
    private static final int LENGTH_OF_HASH = 5;
    private static final String USER_NOT_FOUND_MESSAGE_PATTERN = "user {0} not found";
    private static final String IMAGE_ERROR_KEY = "image";
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private final PostVotesRepository postVotesRepository;
    private final PostMapper postMapper;
    private final PostCommentsRepository postCommentsRepository;
    private static final EnumMap<FilterMode, FilterStrategy> filterStrategyMap = new EnumMap<>(FilterMode.class);

    private final TagsRepository tagsRepository;

    @Autowired
    public PostServiceImpl(PostsRepository postsRepository, PostMapper postMapper, UsersRepository usersRepository,
                           PostVotesRepository postVotesRepository, PostCommentsRepository postCommentsRepository,
                           TagsRepository tagsRepository) {
        this.postsRepository = postsRepository;
        this.postMapper = postMapper;
        this.usersRepository = usersRepository;
        this.postVotesRepository = postVotesRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.tagsRepository = tagsRepository;
    }

    public static void addFilterStrategy(FilterMode mode, FilterStrategy filterStrategy) {
        filterStrategyMap.put(mode, filterStrategy);
    }

    @Transactional
    @Override
    public PostsResponse getPosts(int offset, int limit, FilterMode mode) {
        Page<PostFlatDto> posts = filterStrategyMap.get(mode).execute(getPageNumber(offset), limit);
        return new PostsResponse(posts.getTotalElements(), getPostDtosFromPosts(posts.getContent()));
    }

    @Override
    public PostsResponse getPostsByQuery(int offset, int limit, String query) {
        Pageable page = PageRequest.of(getPageNumber(offset), limit);
        Page<PostFlatDto> posts = postsRepository.findPostsByTextLike(query, page);
        return new PostsResponse(posts.getTotalElements(), getPostDtosFromPosts(posts.getContent()));
    }

    @Override
    public PostsResponse getPostsByDate(int offset, int limit, String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate parsedDate = LocalDate.parse(date, formatter);
        ZoneId zoneId = ZoneId.systemDefault();

        Instant dayStart = parsedDate.atStartOfDay(zoneId).toInstant();
        Instant dayEnd = parsedDate.plusDays(1).atStartOfDay(zoneId).toInstant();

        Pageable page = PageRequest.of(getPageNumber(offset), limit);
        Page<PostFlatDto> posts = postsRepository.findPostsByTime(dayStart, dayEnd, page);

        return new PostsResponse(posts.getTotalElements(), getPostDtosFromPosts(posts.getContent()));
    }

    @Override
    public PostsResponse getPostsByTag(int offset, int limit, String tag) {
        Pageable page = PageRequest.of(getPageNumber(offset), limit);
        Page<PostFlatDto> posts = postsRepository.findPostsByTag(tag, page);
        return new PostsResponse(posts.getTotalElements(), getPostDtosFromPosts(posts.getContent()));
    }

    @Transactional
    @Override
    public PostDetailsDto getPostById(int id) {
        PostDetailsFlatDto postDetailsDto = postsRepository.findPostDetailsById(id).orElseThrow(() ->
                new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
        int viewCount = postDetailsDto.viewCount();
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            postsRepository.updateViewCount(++viewCount, id);
        } else {
            main.model.User authUser = getAuthUser();
            if (postDetailsDto.userId() != authUser.getId() && authUser.getIsModerator() != MODERATOR_VALUE) {
                postsRepository.updateViewCount(++viewCount, id);
            }
        }
        return postMapper.toCurrentPostDto(postDetailsDto, postCommentsRepository.findCommentsByPostId(id), tagsRepository.findTagNamesByPostId(id));
    }

    @Override
    public PostsResponse getModerationPosts(int offset, int limit, ModerationStatus status) {
        Pageable page = PageRequest.of(getPageNumber(offset), limit);
        Page<PostFlatDto> posts = postsRepository.findPostsByModerationStatus(status, page);
        return new PostsResponse(posts.getTotalElements(), getPostDtosFromPosts(posts.getContent()));
    }

    @Override
    public CalendarResponse getCalendar(int year) {
        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setYears(postsRepository.findYearsWithCreatedPosts());
        calendarResponse.setPosts(postsRepository.countPostsByYear(year).stream().collect(Collectors.toMap(CalendarDTO::getDate, CalendarDTO::getCount)));
        return calendarResponse;
    }

    @Override
    public PostsResponse getMyPosts(int offset, int limit, String status) {
        Page<PostFlatDto> posts = Page.empty();
        main.model.User currentUser = getAuthUser();
        Pageable page = PageRequest.of(getPageNumber(offset), limit);
        switch (status) {
            case INACTIVE_POST_STATUS -> posts = postsRepository.findPostsByUser(currentUser, page);
            case PENDING_POST_STATUS -> posts = postsRepository
                    .findPostsByUserAndModerationStatus(currentUser, ModerationStatus.NEW, page);
            case DECLINED_POST_STATUS -> posts = postsRepository
                    .findPostsByUserAndModerationStatus(currentUser, ModerationStatus.DECLINED, page);
            case PUBLISHED_POST_STATUS -> posts = postsRepository
                    .findPostsByUserAndModerationStatus(currentUser, ModerationStatus.ACCEPTED, page);
        }
        return new PostsResponse(posts.getTotalElements(), getPostDtosFromPosts(posts.getContent()));
    }

    @Override
    public ResultResponse addPost(PostRequest postRequest) {
        Post post = postMapper.fromPostRequestToPost(postRequest, getAuthUser());
        postsRepository.save(post);
        return new ResultResponse(true);
    }

    @Override
    public ResultResponse updatePost(int id, PostRequest postRequest) {
        Post post = postMapper.fromPostRequestToPost(id, postRequest, getAuthUser());
        postsRepository.save(post);
        return new ResultResponse(true);
    }

    @Override
    public ResultResponse makePostVote(PostVoteRequest postVoteRequest, byte postVoteValue) {
        main.model.User currentUser = getAuthUser();
        Post post = postsRepository.findById(postVoteRequest.getPostId()).
                orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
        Optional<PostVote> postVote = postVotesRepository.findPostVoteByUserAndPost(currentUser, post);
        postVote.ifPresentOrElse(pv -> {
                    if (pv.getValue() != postVoteValue) {
                        pv.setValue(postVoteValue);
                        pv.setTime(LocalDateTime.now());
                        postVotesRepository.save(pv);
                    }
                },
                () -> postVotesRepository.save(new PostVote(currentUser, post, LocalDateTime.now(), postVoteValue)));
        return new ResultResponse(true);
    }

    @Override
    public StatisticsResponse getMyStatistic() {
        main.model.User currentUser = getAuthUser();
        return postsRepository.getMyStatistic(currentUser.getId());
    }

    @Override
    public StatisticsResponse getAllStatistic() {
        return postsRepository.getAllStatistic();
    }

    @Override
    public ResultResponse moderation(ModerationRequest moderationRequest) {
        Post post = postsRepository.findById(moderationRequest.getPostId())
                .orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
        main.model.User moderator = getAuthUser();
        Integer moderatorId = moderator.getId();
        if (moderationRequest.getDecision().equals(ACCEPT_DECISION)) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else {
            post.setModerationStatus(ModerationStatus.DECLINED);
        }
        post.setModeratorId(moderatorId);
        postsRepository.save(post);
        return new ResultResponse(true);
    }

    @Override
    public Object image(MultipartFile multipartFile) throws IOException {
        String contentType = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (contentType != null && multipartFile.getSize() < MAX_FILE_SIZE && (contentType.equals(PNG_FILE_EXTENSION) || contentType.equals(JPG_FILE_EXTENSION))) {
            String hash = RandomUtil.generateRandomHash(LENGTH_OF_HASH);
            String path = "." + UPLOAD_FOLDER_PATH + hash + "." + contentType;
          try (FileOutputStream outputStream = new FileOutputStream(path)) {
              outputStream.write(multipartFile.getBytes());
              return UPLOAD_FOLDER_PATH + hash + "." + contentType;
          }
        } else {
            return getErrorsResponse(multipartFile);
        }
    }

    private static ErrorsResponse getErrorsResponse(MultipartFile multipartFile) {
        ErrorsResponse errorsResponse = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            errors.put(IMAGE_ERROR_KEY, FILE_SIZE_ERROR_MESSAGE);
        }
        errors.put(IMAGE_ERROR_KEY, FILE_EXTENSION_ERROR_MESSAGE);
        errorsResponse.setResult(false);
        errorsResponse.setErrors(errors);
        return errorsResponse;
    }

    private main.model.User getAuthUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format(USER_NOT_FOUND_MESSAGE_PATTERN, email)));
    }

    private int getPageNumber(int offset) {
        return offset / POSTS_ON_PAGE;
    }

    private List<PostDto> getPostDtosFromPosts(List<PostFlatDto> posts) {
        return posts.stream().map(postMapper::toPostDto).toList();
    }
}
