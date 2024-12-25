package main.service;

import main.api.request.ModerationRequest;
import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.*;
import main.dto.CalendarDTO;
import main.dto.CurrentPostDto;
import main.dto.PostDto;
import main.mapper.PostMapper;
import main.model.Post;
import main.model.PostVote;
import main.model.enums.ModerationStatus;
import main.repository.PostVotesRepository;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import main.utils.RandomUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final int MAX_FILE_SIZE = 5_242_880;
    private static final int POSTS_ON_PAGE = 10;
    private static final String POST_NOT_FOUND_ERROR_MESSAGE = "Пост не найден";
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private final PostVotesRepository postVotesRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostServiceImpl(PostsRepository postsRepository, PostMapper postMapper, UsersRepository usersRepository,
                           PostVotesRepository postVotesRepository) {
        this.postsRepository = postsRepository;
        this.postMapper = postMapper;
        this.usersRepository = usersRepository;
        this.postVotesRepository = postVotesRepository;
    }

    @Override
    public PostsResponse getPosts(int offset, int limit, String mode) {
        Page<Post> posts = Page.empty();
        int pageNumber = offset / POSTS_ON_PAGE;
        switch (mode) {
            case "recent" -> {
                Sort dateSort = Sort.by(Sort.Direction.DESC, "time");
                Pageable page = PageRequest.of(pageNumber, limit, dateSort);
                posts = postsRepository.findAllByIsActiveAndModerationStatus(page);
            }
            case "popular" -> {
                Pageable page = PageRequest.of(pageNumber, limit);
                posts = postsRepository.findAllByPostCommentCount(page);
            }
            case "best" -> {
                Pageable page = PageRequest.of(pageNumber, limit);
                posts = postsRepository.findAllLikedPosts(page);
            }
            case "early" -> {
                Sort dateSort = Sort.by(Sort.Direction.ASC, "time");
                Pageable page = PageRequest.of(pageNumber, limit, dateSort);
                posts = postsRepository.findAllByIsActiveAndModerationStatus(page);
            }
        }
        List<PostDto> postDtos = posts.stream().map(postMapper::toPostDto).toList();
        return new PostsResponse(posts.getTotalElements(), postDtos);
    }

    @Override
    public PostsResponse getPostsByQuery(int offset, int limit, String query) {
        int pageNumber = offset / POSTS_ON_PAGE;
        Pageable page = PageRequest.of(pageNumber, limit);
        Page<Post> posts = postsRepository.findPostsByIsActiveAndModerationStatusAndTextLike(query, page);
        List<PostDto> postDtos = posts.stream().map(postMapper::toPostDto).toList();
        return new PostsResponse(posts.getTotalElements(), postDtos);
    }

    @Override
    public PostsResponse getPostsByDate(int offset, int limit, String date) {
        int pageNumber = offset / POSTS_ON_PAGE;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dayStart = LocalDate.parse(date, formatter).atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        Pageable page = PageRequest.of(pageNumber, limit);
        Page<Post> posts = postsRepository.findPostsByIsActiveAndModerationStatusAndTime(dayStart, dayEnd, page);
        List<PostDto> postDtos = posts.stream().map(postMapper::toPostDto).toList();
        return new PostsResponse(posts.getTotalElements(), postDtos);
    }

    @Override
    public PostsResponse getPostsByTag(int offset, int limit, String tag) {
        int pageNumber = offset / POSTS_ON_PAGE;
        Pageable page = PageRequest.of(pageNumber, limit);
        Page<Post> posts = postsRepository.findPostsByTag(tag, page);
        List<PostDto> postDtos = posts.stream().map(postMapper::toPostDto).toList();
        return new PostsResponse(posts.getTotalElements(), postDtos);
    }

    @Override
    public CurrentPostDto getPostById(int id) {
        Post post = postsRepository.findById(id).orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            postsRepository.incrementViewCount(id);
        } else {
            main.model.User authUser = getAuthUser();
            if (!post.getUser().equals(authUser) && authUser.getIsModerator() != 1) {
                postsRepository.incrementViewCount(id);
            }
        }
        return postMapper.toCurrentPostDto(post);
    }

    @Override
    public PostsResponse getModerationPosts(int offset, int limit, String status) {
        Page<Post> posts = Page.empty();
        int pageNumber = offset / POSTS_ON_PAGE;
        Pageable page = PageRequest.of(pageNumber, limit);
        switch (status) {
            case "new" -> posts = postsRepository.findPostByModerationStatus(ModerationStatus.NEW, page);
            case "declined" -> posts = postsRepository.findPostByModerationStatus(ModerationStatus.DECLINED, page);
            case "accepted" -> posts = postsRepository.findPostByModerationStatus(ModerationStatus.ACCEPTED, page);
        }
        List<PostDto> postDtos = posts.stream().map(postMapper::toPostDto).toList();
        return new PostsResponse(posts.getTotalElements(), postDtos);
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
        Page<Post> posts = Page.empty();
        int pageNumber = offset / POSTS_ON_PAGE;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        main.model.User currentUser = usersRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user" + email + "not fount"));
        Pageable page = PageRequest.of(pageNumber, limit);
        switch (status) {
            case "inactive" -> posts = postsRepository.findPostsByUserAndIsActive(currentUser, (byte) 0, page);
            case "pending" -> posts = postsRepository
                    .findPostsByUserAndIsActiveAndModerationStatus(currentUser, (byte) 1, ModerationStatus.NEW, page);
            case "declined" -> posts = postsRepository
                    .findPostsByUserAndIsActiveAndModerationStatus(currentUser, (byte) 1, ModerationStatus.DECLINED, page);
            case "published" -> posts = postsRepository
                    .findPostsByUserAndIsActiveAndModerationStatus(currentUser, (byte) 1, ModerationStatus.ACCEPTED, page);
        }
        return new PostsResponse(posts.getTotalElements(), posts.stream().map(postMapper::toPostDto).toList());
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
    public ResultResponse like(PostVoteRequest postVoteRequest) {
        return createPostVote(postVoteRequest, (byte) 1, (byte) -1);
    }

    @Override
    public ResultResponse dislike(PostVoteRequest postVoteRequest) {
        return createPostVote(postVoteRequest, (byte) -1, (byte) 1);
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
        String decision = moderationRequest.getDecision();
        Integer postId = post.getId();
        Integer moderatorId = moderator.getId();
        if (decision.equals("accept")) {
            postsRepository.updateModerationStatus(ModerationStatus.ACCEPTED, moderatorId, postId);
        } else {
            postsRepository.updateModerationStatus(ModerationStatus.DECLINED, moderatorId, postId);
        }
        return new ResultResponse(true);
    }

    @Override
    public Object image(MultipartFile multipartFile) throws IOException {
        String contentType = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (multipartFile.getSize() < MAX_FILE_SIZE && (contentType.equals("png") || contentType.equals("jpg"))) {
            String hash = RandomUtil.generateRandomHash(5);
            String path = "./upload/ab/cd/ef/" + hash + "." + contentType;
            FileOutputStream outputStream = new FileOutputStream(path);
            outputStream.write(multipartFile.getBytes());
            return "/upload/ab/cd/ef/" + hash + "." + contentType;
        } else {
            return getErrorsResponse(multipartFile);
        }
    }

    private static ErrorsResponse getErrorsResponse(MultipartFile multipartFile) {
        ErrorsResponse errorsResponse = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            errors.put("image", "Размер файла превышает допустимый размер");
        }
        errors.put("image", "Неверный формат изображения! Изображение должно быть в формате png или jpg");
        errorsResponse.setResult(false);
        errorsResponse.setErrors(errors);
        return errorsResponse;
    }

    private ResultResponse createPostVote(PostVoteRequest postVoteRequest, byte postVoteValue, byte currentPostVoteValue) {
        main.model.User currentUser = getAuthUser();
        Post post = postsRepository.findById(postVoteRequest.getPostId()).
                orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
        Optional<PostVote> postVote = postVotesRepository.findPostVoteByUserAndPost(currentUser, post);
        if (postVote.isEmpty()) {
            PostVote newPostVote = new PostVote(currentUser, post, LocalDateTime.now(), postVoteValue);
            postVotesRepository.save(newPostVote);
        } else if (postVote.get().getValue() == currentPostVoteValue) {
            postVotesRepository.updatePostVote(postVoteValue, currentUser, post);
        }
        return new ResultResponse(true);
    }

    private Post createPost(PostRequest postRequest) {
        main.model.User currentUser = getAuthUser();
        Post post = new Post();
        List<Tag> tags = new ArrayList<>();
        postRequest.getTags().forEach(t -> tags.add(new Tag(t)));
        post.setIsActive(postRequest.getActive());
        post.setTitle(postRequest.getTitle());
        post.setText(postRequest.getText());
        post.setUser(currentUser);
        post.setTags(tags);
        if (globalSettingsService.getGlobalSettings().isPostPremoderation()) {
            if (currentUser.getIsModerator() == 1) {
                post.setModerationStatus(ModerationStatus.ACCEPTED);
            } else {
                post.setModerationStatus(ModerationStatus.NEW);
            }
        } else {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        }
        LocalDateTime now = LocalDateTime.now();
        if (postRequest.getTimestamp() <= Timestamp.valueOf(now).getTime()) {
            post.setTime(now);
        } else {
            post.setTime(new Timestamp(postRequest.getTimestamp()).toLocalDateTime());
        }
        return post;
    }

    private Post createPost(int id, PostRequest postRequest) {
        Post post = createPost(postRequest);
        post.setId(id);
        return post;
    }

    private main.model.User getAuthUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        return usersRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user" + email + "not found"));
    }
}
