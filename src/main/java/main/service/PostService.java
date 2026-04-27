package main.service;

import lombok.extern.slf4j.Slf4j;
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
import main.model.Tag;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.repository.PostCommentsRepository;
import main.repository.PostsRepository;
import main.repository.TagsRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {


    private static final String POST_NOT_FOUND_ERROR_MESSAGE = "Пост не найден";
    private static final String ACCEPT_DECISION = "accept";
    private static final String POST_PREMODERATION_SETTING = "POST_PREMODERATION";
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private final PostMapper postMapper;
    private final PostCommentsRepository postCommentsRepository;
    private final ImageService imageService;
    private final TagsRepository tagsRepository;

    private final GlobalSettingsService globalSettingsService;

    @Autowired
    public PostService(PostsRepository postsRepository, PostMapper postMapper, UsersRepository usersRepository,
                       PostCommentsRepository postCommentsRepository, TagsRepository tagsRepository, ImageService imageService,
                       GlobalSettingsService globalSettingsService) {
        this.postsRepository = postsRepository;
        this.postMapper = postMapper;
        this.usersRepository = usersRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.tagsRepository = tagsRepository;
        this.imageService = imageService;
        this.globalSettingsService = globalSettingsService;
    }

    @Transactional(readOnly = true)
    public PostDetailsFlatDto getPostDetails(int id) {
        return postsRepository.findPostDetailsById(id).orElseThrow(() ->
                new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
    }

    @Transactional
    public void incrementViewCount(PostDetailsFlatDto post, UserDetails userDetails) {
        if (shouldIncrementViewCount(post, userDetails)) {
            postsRepository.updateViewCount(post.viewCount() + 1, post.id());
        }
    }

    public PostDetailsDto buildFullPostDetailsDto(PostDetailsFlatDto postDetails) {
        return postMapper.toCurrentPostDto(postDetails,
                postCommentsRepository.findCommentsByPostId(postDetails.id()),
                tagsRepository.findTagNamesByPostId(postDetails.id()));
    }

    private boolean shouldIncrementViewCount(PostDetailsFlatDto post, UserDetails user) {
        if (user == null) {
            return true;
        }
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("user:moderate"))) {
            return false;
        }
        return !post.email().equals(user.getUsername());
    }

    public CalendarResponse getCalendar(int year) {
        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setYears(postsRepository.findYearsWithCreatedPosts());
        calendarResponse.setPosts(postsRepository.countPostsByYear(year).stream().collect(Collectors.toMap(CalendarDTO::getDate, CalendarDTO::getCount)));
        return calendarResponse;
    }

    @Transactional
    public ResultResponse addPost(PostRequest postRequest, int userId) {
        log.info("User {} is adding a new post", userId);
        Post post = createPost(postRequest, userId);
        postsRepository.save(post);
        return new ResultResponse(true);
    }

    @Transactional
    public ResultResponse updatePost(int id, PostRequest postRequest, int userId) {
        log.info("User {} is updating post {}", userId, id);
        Post post = createPost(postRequest, userId, id);
        postsRepository.save(post);
        return new ResultResponse(true);
    }

    private Post createPost(PostRequest postRequest, int userId) {
        User user = usersRepository.getReferenceById(userId);
        Post post = postMapper.fromPostRequestToPost(postRequest);

        List<Tag> tags = new ArrayList<>();
        postRequest.getTags().forEach(t -> tags.add(new Tag(t)));

        if (Boolean.TRUE.equals(globalSettingsService.getGlobalSettings().get(POST_PREMODERATION_SETTING)) && user.getIsModerator() != 1) {
            post.setModerationStatus(ModerationStatus.NEW);
        }

        post.setTags(tags);
        post.setUser(user);

        return post;
    }

    private Post createPost(PostRequest postRequest, int userId, Integer postId) {
        Post post = createPost(postRequest, userId);
        post.setId(postId);
        return post;
    }

    @Transactional
    public ResultResponse makePostVote(PostVoteRequest postVoteRequest, byte postVoteValue, int userId) {
        User currentUser = usersRepository.getReferenceById(userId);
        Post post = findPostById(postVoteRequest.getPostId());
        PostVote postVote = new PostVote(currentUser, post, LocalDateTime.now(), postVoteValue);
        post.addVote(postVote);
        postsRepository.save(post);
        log.info("User {} voted for post {}", userId, post.getId());
        return new ResultResponse(true);
    }

    @Transactional
    public ResultResponse moderation(ModerationRequest moderationRequest, int moderatorId) {
        Post post = findPostById(moderationRequest.getPostId());

        if (moderationRequest.getDecision().equals(ACCEPT_DECISION)) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else {
            post.setModerationStatus(ModerationStatus.DECLINED);
        }

        post.setModeratorId(moderatorId);
        postsRepository.save(post);
        log.info("Moderator {} moderated post {} with decision: {}", moderatorId, post.getId(), moderationRequest.getDecision());
        return new ResultResponse(true);
    }

    public Object image(MultipartFile multipartFile) throws IOException {
        return imageService.uploadImage(multipartFile);
    }

    private Post findPostById(int id) {
        return postsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
    }
}
