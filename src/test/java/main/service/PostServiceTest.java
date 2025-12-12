package main.service;

import main.api.request.ModerationRequest;
import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.CalendarResponse;
import main.api.response.ResultResponse;
import main.dto.CalendarDTO;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
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

    @Test
    @DisplayName("getPostDetails should return flat DTO when post exists")
    void getPostDetails_ShouldReturnDto_WhenPostExists() {
        int postId = 1;
        PostDetailsFlatDto expectedDto = mock(PostDetailsFlatDto.class);
        when(postsRepository.findPostDetailsById(postId)).thenReturn(Optional.of(expectedDto));

        PostDetailsFlatDto actualDto = postService.getPostDetails(postId);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("getPostDetails should throw exception when post does not exist")
    void getPostDetails_ShouldThrowException_WhenPostNotFound() {
        int postId = 1;
        when(postsRepository.findPostDetailsById(postId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> postService.getPostDetails(postId));
    }

    @Test
    @DisplayName("incrementViewCount should increment when user is not authenticated")
    void incrementViewCount_ShouldIncrement_WhenUserIsNull() {
        PostDetailsFlatDto post = new PostDetailsFlatDto(1, Instant.now(), true, 1, "User", null, "Title", "Text", 10, 2, 5, "author@example.com");
        postService.incrementViewCount(post, null);
        verify(postsRepository, times(1)).updateViewCount(6, 1);
    }

    @Test
    @DisplayName("incrementViewCount should not increment when user is the author")
    void incrementViewCount_ShouldNotIncrement_WhenUserIsAuthor() {
        PostDetailsFlatDto post = new PostDetailsFlatDto(1, Instant.now(), true, 1, "User", null, "Title", "Text", 10, 2, 5, "author@example.com");
        UserDetails author = mock(UserDetails.class);
        when(author.getUsername()).thenReturn("author@example.com");

        postService.incrementViewCount(post, author);

        verify(postsRepository, never()).updateViewCount(anyInt(), anyInt());
    }

    @Test
    @DisplayName("incrementViewCount should not increment when user is a moderator")
    void incrementViewCount_ShouldNotIncrement_WhenUserIsModerator() {
        PostDetailsFlatDto post = new PostDetailsFlatDto(1, Instant.now(), true, 1, "User", null, "Title", "Text", 10, 2, 5, "author@example.com");
        UserDetails moderator = mock(UserDetails.class);
        doReturn(List.of(new SimpleGrantedAuthority("user:moderate"))).when(moderator).getAuthorities();

        postService.incrementViewCount(post, moderator);

        verify(postsRepository, never()).updateViewCount(anyInt(), anyInt());
    }

    @Test
    @DisplayName("incrementViewCount should increment for regular user who is not the author")
    void incrementViewCount_ShouldIncrement_ForOtherUser() {
        PostDetailsFlatDto post = new PostDetailsFlatDto(1, Instant.now(), true, 1, "User", null, "Title", "Text", 10, 2, 5, "author@example.com");
        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("other@example.com");
        doReturn(List.of(new SimpleGrantedAuthority("user:write"))).when(otherUser).getAuthorities();


        postService.incrementViewCount(post, otherUser);

        verify(postsRepository, times(1)).updateViewCount(6, 1);
    }

    @Test
    void addPost_ShouldReturnSuccessResponse() {
        int userId = 1;
        PostRequest request = new PostRequest();
        User user = mock(User.class);

        when(usersRepository.getReferenceById(userId)).thenReturn(user);
        when(postMapper.fromPostRequestToPost(request, user)).thenReturn(mock(Post.class));

        ResultResponse response = postService.addPost(request, userId);

        assertTrue(response.isResult());
        verify(postsRepository).save(any(Post.class));
    }

    @Test
    void makePostVote_ShouldReturnSuccessResponse() {
        int userId = 1;
        PostVoteRequest voteRequest = new PostVoteRequest();
        voteRequest.setPostId(42);
        Post post = mock(Post.class);
        User user = mock(User.class);

        when(usersRepository.getReferenceById(userId)).thenReturn(user);
        when(postsRepository.findById(42)).thenReturn(Optional.of(post));

        ResultResponse response = postService.makePostVote(voteRequest, (byte) 1, userId);

        assertTrue(response.isResult());
        verify(post).addVote(any(PostVote.class));
        verify(postsRepository).save(post);
    }

    @Test
    void moderation_ShouldAcceptPost() {
        int moderatorId = 101;
        ModerationRequest request = new ModerationRequest();
        request.setPostId(7);
        request.setDecision("accept");

        Post post = new Post();

        when(postsRepository.findById(7)).thenReturn(Optional.of(post));

        ResultResponse response = postService.moderation(request, moderatorId);

        assertTrue(response.isResult());
        assertEquals(ModerationStatus.ACCEPTED, post.getModerationStatus());
        assertEquals(moderatorId, post.getModeratorId());
        verify(postsRepository).save(post);
    }

    @Test
    void updatePost_ShouldReturnSuccessResponse() {
        int userId = 1;
        int postId = 123;
        PostRequest request = new PostRequest();
        User user = mock(User.class);
        Post post = mock(Post.class);

        when(usersRepository.getReferenceById(userId)).thenReturn(user);
        when(postMapper.fromPostRequestToPost(postId, request, user)).thenReturn(post);

        ResultResponse result = postService.updatePost(postId, request, userId);

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
