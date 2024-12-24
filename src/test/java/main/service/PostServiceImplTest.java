package main.service;

import main.api.response.PostsResponse;
import main.mapper.PostMapper;
import main.model.Post;
import main.repository.PostVotesRepository;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PostVotesRepository postVotesRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    @DisplayName("Should return recent posts")
    void getPostsShouldReturnRecentPosts() {
        int offset = 0;
        int limit = 10;
        String mode = "recent";
        Pageable page = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "time"));
        Page<Post> posts = new PageImpl<>(List.of(new Post()));
        when(postsRepository.findAllByIsActiveAndModerationStatus(page)).thenReturn(posts);

        PostsResponse response = postService.getPosts(offset, limit, mode);

        assertNotNull(response);
        assertEquals(posts.getTotalElements(), response.getCount());
        verify(postsRepository, times(1)).findAllByIsActiveAndModerationStatus(page);
    }

    @Test
    @DisplayName("Should return popular posts")
    void getPostsShouldReturnPopularPosts() {
        int offset = 0;
        int limit = 10;
        String mode = "popular";
        Pageable page = PageRequest.of(0, limit);
        Page<Post> posts = new PageImpl<>(List.of(new Post()));
        when(postsRepository.findAllByPostCommentCount(page)).thenReturn(posts);

        PostsResponse response = postService.getPosts(offset, limit, mode);

        assertNotNull(response);
        assertEquals(posts.getTotalElements(), response.getCount());
        verify(postsRepository, times(1)).findAllByPostCommentCount(page);
    }

    @Test
    @DisplayName("Should return earliest posts")
    void getPostsShouldReturnEarliestPosts() {
        int offset = 0;
        int limit = 10;
        String mode = "early";
        Pageable page = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "time"));
        Page<Post> posts = new PageImpl<>(List.of(new Post()));
        when(postsRepository.findAllByIsActiveAndModerationStatus(page)).thenReturn(posts);

        PostsResponse response = postService.getPosts(offset, limit, mode);

        assertNotNull(response);
        assertEquals(posts.getTotalElements(), response.getCount());
        verify(postsRepository, times(1)).findAllByIsActiveAndModerationStatus(page);
    }

    @Test
    @DisplayName("Should return best posts")
    void getPostsShouldReturnBestPosts() {
        int offset = 0;
        int limit = 10;
        String mode = "best";
        Pageable page = PageRequest.of(0, limit);
        Page<Post> posts = new PageImpl<>(List.of(new Post()));
        when(postsRepository.findAllLikedPosts(page)).thenReturn(posts);

        PostsResponse response = postService.getPosts(offset, limit, mode);

        assertNotNull(response);
        assertEquals(posts.getTotalElements(), response.getCount());
        verify(postsRepository, times(1)).findAllLikedPosts(page);
    }

    @Test
    @DisplayName("Should return posts that contain query")
    void testGetPostsByQuery() {
        int offset = 0;
        int limit = 10;
        String query = "test";
        Pageable page = PageRequest.of(0, limit);
        Page<Post> posts = new PageImpl<>(List.of(new Post()));
        when(postsRepository.findPostsByIsActiveAndModerationStatusAndTextLike(query, page)).thenReturn(posts);

        PostsResponse response = postService.getPostsByQuery(offset, limit, query);

        assertNotNull(response);
        assertEquals(posts.getTotalElements(), response.getCount());
        verify(postsRepository, times(1)).findPostsByIsActiveAndModerationStatusAndTextLike(query, page);
    }

    @Test
    @DisplayName("Should return posts that was published at current day")
    void testGetPostsByDate() {
        int offset = 0;
        int limit = 10;
        String date = "2022-01-01";
        LocalDateTime dayStart = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);
        Pageable page = PageRequest.of(0, limit);

        Page<Post> posts = new PageImpl<>(List.of(new Post()));
        when(postsRepository.findPostsByIsActiveAndModerationStatusAndTime(dayStart, dayEnd, page)).thenReturn(posts);

        PostsResponse response = postService.getPostsByDate(offset, limit, date);

        assertNotNull(response);
        assertEquals(posts.getTotalElements(), response.getCount());
        verify(postsRepository, times(1)).findPostsByIsActiveAndModerationStatusAndTime(dayStart, dayEnd, page);
    }
}
