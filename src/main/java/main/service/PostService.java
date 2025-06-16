package main.service;

import main.api.request.ModerationRequest;
import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.CalendarResponse;
import main.api.response.ResultResponse;
import main.dto.CalendarDTO;
import main.dto.PostDetailsDto;
import main.dto.PostDetailsFlatDto;
import main.mapper.PostMapper;
import main.model.Post;
import main.model.PostVote;
import main.model.enums.ModerationStatus;
import main.repository.PostCommentsRepository;
import main.repository.PostsRepository;
import main.repository.TagsRepository;
import main.repository.UsersRepository;
import main.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PostService {


    private static final String POST_NOT_FOUND_ERROR_MESSAGE = "Пост не найден";
    private static final String ACCEPT_DECISION = "accept";
    private static final String USER_NOT_FOUND_MESSAGE_PATTERN = "user {0} not found";
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private final PostMapper postMapper;
    private final PostCommentsRepository postCommentsRepository;
    private final ImageService imageService;
    private final TagsRepository tagsRepository;

    @Autowired
    public PostService(PostsRepository postsRepository, PostMapper postMapper, UsersRepository usersRepository,
                       PostCommentsRepository postCommentsRepository, TagsRepository tagsRepository, ImageService imageService) {
        this.postsRepository = postsRepository;
        this.postMapper = postMapper;
        this.usersRepository = usersRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.tagsRepository = tagsRepository;
        this.imageService = imageService;
    }

    @Transactional
    public PostDetailsDto getPostById(int id) {
        PostDetailsFlatDto postDetailsDto = postsRepository.findPostDetailsById(id).orElseThrow(() ->
                new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
        int viewCount = postDetailsDto.viewCount();
        if (!SecurityUtils.isAuthenticated()) {
            postsRepository.updateViewCount(++viewCount, id);
        } else {
            User user = SecurityUtils.getCurrentUser();
            if (!postDetailsDto.email().equals(user.getUsername()) && !user.getAuthorities().contains(new SimpleGrantedAuthority("user:moderate"))) {
                postsRepository.updateViewCount(++viewCount, id);
            }
        }
        return postMapper.toCurrentPostDto(postDetailsDto, postCommentsRepository.findCommentsByPostId(id), tagsRepository.findTagNamesByPostId(id));
    }

    public CalendarResponse getCalendar(int year) {
        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setYears(postsRepository.findYearsWithCreatedPosts());
        calendarResponse.setPosts(postsRepository.countPostsByYear(year).stream().collect(Collectors.toMap(CalendarDTO::getDate, CalendarDTO::getCount)));
        return calendarResponse;
    }

    @Transactional
    public ResultResponse addPost(PostRequest postRequest) {
        Post post = postMapper.fromPostRequestToPost(postRequest, getEntityOfAuthenticatedUser());
        postsRepository.save(post);
        return new ResultResponse(true);
    }

    @Transactional
    public ResultResponse updatePost(int id, PostRequest postRequest) {
        Post post = postMapper.fromPostRequestToPost(id, postRequest, getEntityOfAuthenticatedUser());
        postsRepository.save(post);
        return new ResultResponse(true);
    }

    @Transactional
    public ResultResponse makePostVote(PostVoteRequest postVoteRequest, byte postVoteValue) {
        main.model.User currentUser = getEntityOfAuthenticatedUser();
        Post post = postsRepository.findById(postVoteRequest.getPostId()).
                orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
        PostVote postVote = new PostVote(currentUser, post, LocalDateTime.now(), postVoteValue);
        post.addVote(postVote);
        postsRepository.save(post);
        return new ResultResponse(true);
    }

    @Transactional
    public ResultResponse moderation(ModerationRequest moderationRequest) {
        Post post = postsRepository.findById(moderationRequest.getPostId())
                .orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
        User user = SecurityUtils.getCurrentUser();
        String email = user.getUsername();
        Integer moderatorId = usersRepository.findUserIdByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(MessageFormat.format(USER_NOT_FOUND_MESSAGE_PATTERN, email)));

        if (moderationRequest.getDecision().equals(ACCEPT_DECISION)) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else {
            post.setModerationStatus(ModerationStatus.DECLINED);
        }

        post.setModeratorId(moderatorId);
        postsRepository.save(post);
        return new ResultResponse(true);
    }

    public Object image(MultipartFile multipartFile) throws IOException {
        return imageService.uploadImage(multipartFile);
    }

    private main.model.User getEntityOfAuthenticatedUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return usersRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format(USER_NOT_FOUND_MESSAGE_PATTERN, email)));
    }
}
