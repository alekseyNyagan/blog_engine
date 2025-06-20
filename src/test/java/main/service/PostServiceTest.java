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
import main.utils.SecurityUtilsTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostsRepository postsRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private PostCommentsRepository postCommentsRepository;
    @Mock
    private TagsRepository tagsRepository;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        SecurityUtilsTestHelper.setAuthenticatedUser("test@example.com", List.of("ROLE_USER"));
    }

    @AfterEach
    void tearDown() {
        SecurityUtilsTestHelper.clearAuthentication();
    }

    @Test
    void getPostById_ShouldReturnPostDetailsDto_WhenPostExists() {
        int postId = 1;
        PostDetailsFlatDto flatDto = mock(PostDetailsFlatDto.class);

        when(flatDto.viewCount()).thenReturn(5);
        when(flatDto.email()).thenReturn("author@example.com");
        when(postsRepository.findPostDetailsById(postId)).thenReturn(Optional.of(flatDto));
        when(postCommentsRepository.findCommentsByPostId(postId)).thenReturn(List.of());
        when(tagsRepository.findTagNamesByPostId(postId)).thenReturn(List.of());
        when(postMapper.toCurrentPostDto(flatDto, List.of(), List.of()))
                .thenReturn(mock(PostDetailsDto.class));

        PostDetailsDto result = postService.getPostById(postId);

        assertNotNull(result);
        verify(postsRepository).updateViewCount(6, postId);
    }

    @Test
    void addPost_ShouldReturnSuccessResponse() {
        PostRequest request = new PostRequest();
        main.model.User user = mock(main.model.User.class);

        when(usersRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(postMapper.fromPostRequestToPost(request, user)).thenReturn(mock(Post.class));

        ResultResponse response = postService.addPost(request);

        assertTrue(response.isResult());
        verify(postsRepository).save(any(Post.class));
    }

    @Test
    void makePostVote_ShouldReturnSuccessResponse() {
        PostVoteRequest voteRequest = new PostVoteRequest();
        voteRequest.setPostId(42);
        Post post = mock(Post.class);
        main.model.User user = mock(main.model.User.class);

        when(usersRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(postsRepository.findById(42)).thenReturn(Optional.of(post));

        ResultResponse response = postService.makePostVote(voteRequest, (byte) 1);

        assertTrue(response.isResult());
        verify(post).addVote(any(PostVote.class));
        verify(postsRepository).save(post);
    }

    @Test
    void moderation_ShouldAcceptPost() {
        ModerationRequest request = new ModerationRequest();
        request.setPostId(7);
        request.setDecision("accept");

        Post post = new Post();

        when(postsRepository.findById(7)).thenReturn(Optional.of(post));
        when(usersRepository.findUserIdByEmail("test@example.com")).thenReturn(Optional.of(101));

        ResultResponse response = postService.moderation(request);

        assertTrue(response.isResult());
        assertEquals(ModerationStatus.ACCEPTED, post.getModerationStatus());
        assertEquals(101, post.getModeratorId());
        verify(postsRepository).save(post);
    }

    @Test
    void updatePost_ShouldReturnSuccessResponse() {
        int postId = 123;
        PostRequest request = new PostRequest();
        main.model.User user = mock(main.model.User.class);
        Post post = mock(Post.class);

        when(usersRepository.findUserByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(postMapper.fromPostRequestToPost(postId, request, user)).thenReturn(post);

        ResultResponse result = postService.updatePost(postId, request);

        assertTrue(result.isResult());
        verify(postsRepository).save(post);
    }

    @Test
    void getCalendar_ShouldReturnCorrectResponse() {
        int year = 2024;
        List<Integer> years = List.of(2023, 2024);

        CalendarDTO dto1 = mock(CalendarDTO.class);
        when(dto1.getDate()).thenReturn("2024-06-06");
        when(dto1.getCount()).thenReturn(5);

        CalendarDTO dto2 = mock(CalendarDTO.class);
        when(dto2.getDate()).thenReturn("2024-06-07");
        when(dto2.getCount()).thenReturn(2);

        when(postsRepository.findYearsWithCreatedPosts()).thenReturn(years);
        when(postsRepository.countPostsByYear(year)).thenReturn(List.of(dto1, dto2));

        CalendarResponse response = postService.getCalendar(year);

        assertEquals(years, response.getYears());
        assertEquals(2, response.getPosts().size());
        assertEquals(5, response.getPosts().get("2024-06-06"));
        assertEquals(2, response.getPosts().get("2024-06-07"));
    }

}
