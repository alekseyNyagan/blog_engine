package main.service;

import main.api.response.PostResponse;
import main.api.response.PostsResponse;
import main.repository.projection.PostProjection;
import main.mapper.PostMapper;
import main.model.enums.ModerationStatus;
import main.repository.PostsRepository;
import main.service.strategy.enums.FilterMode;
import main.service.strategy.filter.FilterStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceTest {
    @Mock
    private PostsRepository postsRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private FilterStrategy filterStrategy;

    @Mock
    private PostProjection postProjection;

    @Mock
    private PostResponse postResponse;

    @InjectMocks
    private PostQueryService postQueryService;

    private List<PostProjection> flatDtos;

    @BeforeEach
    void setUp() {
        PostQueryService.addFilterStrategy(FilterMode.BEST, filterStrategy);
        flatDtos = List.of(postProjection);
    }

    @Test
    void getPosts_ShouldReturnFilteredPosts() {
        Page<PostProjection> page = new PageImpl<>(flatDtos);
        when(filterStrategy.execute(0, 10)).thenReturn(page);
        when(postMapper.toPostResponse(postProjection)).thenReturn(postResponse);

        PostsResponse response = postQueryService.getPosts(0, 10, FilterMode.BEST);

        assertEquals(1, response.count());
        assertEquals(List.of(postResponse), response.posts());
    }

    @Test
    void getPostsByQuery_ShouldReturnMatchingPosts() {
        Page<PostProjection> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByTextLike("test", PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostResponse(postProjection)).thenReturn(postResponse);

        PostsResponse response = postQueryService.getPostsByQuery(0, 10, "test");

        assertEquals(1, response.count());
        assertEquals(List.of(postResponse), response.posts());
    }

    @Test
    void getPostsByDate_ShouldReturnMatchingPosts() {
        Page<PostProjection> page = new PageImpl<>(flatDtos);
        String date = "2024-06-06";
        LocalDate localDate = LocalDate.parse(date);
        Instant from = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(postsRepository.findPostsByTime(from, to, PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostResponse(postProjection)).thenReturn(postResponse);

        PostsResponse response = postQueryService.getPostsByDate(0, 10, date);

        assertEquals(1, response.count());
        assertEquals(List.of(postResponse), response.posts());
    }

    @Test
    void getPostsByTag_ShouldReturnTaggedPosts() {
        Page<PostProjection> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByTag("spring", PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostResponse(postProjection)).thenReturn(postResponse);

        PostsResponse response = postQueryService.getPostsByTag(0, 10, "spring");

        assertEquals(1, response.count());
        assertEquals(List.of(postResponse), response.posts());
    }

    @Test
    void getModerationPosts_ShouldReturnPostsWithStatus() {
        Page<PostProjection> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByModerationStatus(ModerationStatus.NEW, PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostResponse(postProjection)).thenReturn(postResponse);

        PostsResponse response = postQueryService.getModerationPosts(0, 10, ModerationStatus.NEW);

        assertEquals(1, response.count());
        assertEquals(List.of(postResponse), response.posts());
    }

    @Test
    void getMyPosts_ShouldReturnUserPosts_WhenStatusInactive() {
        Page<PostProjection> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByUser("test@example.com", PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostResponse(postProjection)).thenReturn(postResponse);

        PostsResponse response = postQueryService.getMyPosts(0, 10, "inactive", "test@example.com");

        assertEquals(1, response.count());
        assertEquals(List.of(postResponse), response.posts());
    }

    @Test
    void getMyPosts_ShouldReturnUserPosts_WhenStatusPending() {
        Page<PostProjection> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByUserAndModerationStatus("test@example.com", ModerationStatus.NEW, PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostResponse(postProjection)).thenReturn(postResponse);

        PostsResponse response = postQueryService.getMyPosts(0, 10, "pending", "test@example.com");

        assertEquals(1, response.count());
        assertEquals(List.of(postResponse), response.posts());
    }

    @Test
    void getMyPosts_ShouldReturnUserPosts_WhenStatusDeclined() {
        Page<PostProjection> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByUserAndModerationStatus("test@example.com", ModerationStatus.DECLINED, PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostResponse(postProjection)).thenReturn(postResponse);

        PostsResponse response = postQueryService.getMyPosts(0, 10, "declined", "test@example.com");

        assertEquals(1, response.count());
        assertEquals(List.of(postResponse), response.posts());
    }

    @Test
    void getMyPosts_ShouldReturnUserPosts_WhenStatusPublished() {
        Page<PostProjection> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByUserAndModerationStatus("test@example.com", ModerationStatus.ACCEPTED, PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostResponse(postProjection)).thenReturn(postResponse);

        PostsResponse response = postQueryService.getMyPosts(0, 10, "published", "test@example.com");

        assertEquals(1, response.count());
        assertEquals(List.of(postResponse), response.posts());
    }

    @Test
    void getMyPosts_ShouldReturnEmpty_WhenStatusUnknown() {
        PostsResponse response = postQueryService.getMyPosts(0, 10, "unknown_status", "test@example.com");

        assertEquals(0, response.count());
        assertTrue(response.posts().isEmpty());
    }

}
