package main.service;

import main.api.response.PostsResponse;
import main.dto.PostDto;
import main.dto.PostFlatDto;
import main.mapper.PostMapper;
import main.model.enums.ModerationStatus;
import main.repository.PostsRepository;
import main.service.strategy.enums.FilterMode;
import main.service.strategy.filter.FilterStrategy;
import main.utils.SecurityUtilsTestHelper;
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
    private PostFlatDto postFlatDto;

    @Mock
    private PostDto postDto;

    @InjectMocks
    private PostQueryService postQueryService;

    private List<PostFlatDto> flatDtos;

    @BeforeEach
    void setUp() {
        PostQueryService.addFilterStrategy(FilterMode.BEST, filterStrategy);
        flatDtos = List.of(postFlatDto);
    }

    @Test
    void getPosts_ShouldReturnFilteredPosts() {
        Page<PostFlatDto> page = new PageImpl<>(flatDtos);
        when(filterStrategy.execute(0, 10)).thenReturn(page);
        when(postMapper.toPostDto(postFlatDto)).thenReturn(postDto);

        PostsResponse response = postQueryService.getPosts(0, 10, FilterMode.BEST);

        assertEquals(1, response.getCount());
        assertEquals(List.of(postDto), response.getPosts());
    }

    @Test
    void getPostsByQuery_ShouldReturnMatchingPosts() {
        Page<PostFlatDto> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByTextLike("test", PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostDto(postFlatDto)).thenReturn(postDto);

        PostsResponse response = postQueryService.getPostsByQuery(0, 10, "test");

        assertEquals(1, response.getCount());
        assertEquals(List.of(postDto), response.getPosts());
    }

    @Test
    void getPostsByDate_ShouldReturnMatchingPosts() {
        Page<PostFlatDto> page = new PageImpl<>(flatDtos);
        String date = "2024-06-06";
        LocalDate localDate = LocalDate.parse(date);
        Instant from = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant to = localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(postsRepository.findPostsByTime(from, to, PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostDto(postFlatDto)).thenReturn(postDto);

        PostsResponse response = postQueryService.getPostsByDate(0, 10, date);

        assertEquals(1, response.getCount());
        assertEquals(List.of(postDto), response.getPosts());
    }

    @Test
    void getPostsByTag_ShouldReturnTaggedPosts() {
        Page<PostFlatDto> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByTag("spring", PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostDto(postFlatDto)).thenReturn(postDto);

        PostsResponse response = postQueryService.getPostsByTag(0, 10, "spring");

        assertEquals(1, response.getCount());
        assertEquals(List.of(postDto), response.getPosts());
    }

    @Test
    void getModerationPosts_ShouldReturnPostsWithStatus() {
        Page<PostFlatDto> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByModerationStatus(ModerationStatus.NEW, PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostDto(postFlatDto)).thenReturn(postDto);

        PostsResponse response = postQueryService.getModerationPosts(0, 10, ModerationStatus.NEW);

        assertEquals(1, response.getCount());
        assertEquals(List.of(postDto), response.getPosts());
    }

    @Test
    void getMyPosts_ShouldReturnUserPosts_WhenStatusInactive() {
        SecurityUtilsTestHelper.setAuthenticatedUser("test@example.com", List.of("ROLE_USER"));

        Page<PostFlatDto> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByUser("test@example.com", PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostDto(postFlatDto)).thenReturn(postDto);

        PostsResponse response = postQueryService.getMyPosts(0, 10, "inactive");

        assertEquals(1, response.getCount());
        assertEquals(List.of(postDto), response.getPosts());
    }

    @Test
    void getMyPosts_ShouldReturnUserPosts_WhenStatusPending() {
        SecurityUtilsTestHelper.setAuthenticatedUser("test@example.com", List.of("ROLE_USER"));

        Page<PostFlatDto> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByUserAndModerationStatus("test@example.com", ModerationStatus.NEW, PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostDto(postFlatDto)).thenReturn(postDto);

        PostsResponse response = postQueryService.getMyPosts(0, 10, "pending");

        assertEquals(1, response.getCount());
        assertEquals(List.of(postDto), response.getPosts());
    }

    @Test
    void getMyPosts_ShouldReturnUserPosts_WhenStatusDeclined() {
        SecurityUtilsTestHelper.setAuthenticatedUser("test@example.com", List.of("ROLE_USER"));

        Page<PostFlatDto> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByUserAndModerationStatus("test@example.com", ModerationStatus.DECLINED, PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostDto(postFlatDto)).thenReturn(postDto);

        PostsResponse response = postQueryService.getMyPosts(0, 10, "declined");

        assertEquals(1, response.getCount());
        assertEquals(List.of(postDto), response.getPosts());
    }

    @Test
    void getMyPosts_ShouldReturnUserPosts_WhenStatusPublished() {
        SecurityUtilsTestHelper.setAuthenticatedUser("test@example.com", List.of("ROLE_USER"));

        Page<PostFlatDto> page = new PageImpl<>(flatDtos);
        when(postsRepository.findPostsByUserAndModerationStatus("test@example.com", ModerationStatus.ACCEPTED, PageRequest.of(0, 10))).thenReturn(page);
        when(postMapper.toPostDto(postFlatDto)).thenReturn(postDto);

        PostsResponse response = postQueryService.getMyPosts(0, 10, "published");

        assertEquals(1, response.getCount());
        assertEquals(List.of(postDto), response.getPosts());
    }

    @Test
    void getMyPosts_ShouldReturnEmpty_WhenStatusUnknown() {
        SecurityUtilsTestHelper.setAuthenticatedUser("test@example.com", List.of("ROLE_USER"));

        PostsResponse response = postQueryService.getMyPosts(0, 10, "unknown_status");

        assertEquals(0, response.getCount());
        assertTrue(response.getPosts().isEmpty());
    }

}
