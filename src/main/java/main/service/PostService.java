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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PostService {


    private static final String POST_NOT_FOUND_ERROR_MESSAGE = "Пост не найден";
    private static final String ACCEPT_DECISION = "accept";
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
        savePost(postRequest, userId, null);
        return new ResultResponse(true);
    }

    @Transactional
    public ResultResponse updatePost(int id, PostRequest postRequest, int userId) {
        savePost(postRequest, userId, id);
        return new ResultResponse(true);
    }

    private void savePost(PostRequest postRequest, int userId, Integer postId) {
        User user = usersRepository.getReferenceById(userId);
        Post post;
        if (postId == null) {
            post = postMapper.fromPostRequestToPost(postRequest, user);
        } else {
            post = postMapper.fromPostRequestToPost(postId, postRequest, user);
        }
        postsRepository.save(post);
    }

    @Transactional
    public ResultResponse makePostVote(PostVoteRequest postVoteRequest, byte postVoteValue, int userId) {
        User currentUser = usersRepository.getReferenceById(userId);
        Post post = findPostById(postVoteRequest.getPostId());
        PostVote postVote = new PostVote(currentUser, post, LocalDateTime.now(), postVoteValue);
        post.addVote(postVote);
        postsRepository.save(post);
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
