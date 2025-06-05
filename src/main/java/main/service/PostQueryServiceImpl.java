package main.service;

import main.api.response.PostsResponse;
import main.dto.PostDto;
import main.dto.PostFlatDto;
import main.mapper.PostMapper;
import main.model.enums.ModerationStatus;
import main.repository.PostsRepository;
import main.service.strategy.enums.FilterMode;
import main.service.strategy.filter.FilterStrategy;
import main.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;

@Service
public class PostQueryServiceImpl implements PostQueryService {

    private static final EnumMap<FilterMode, FilterStrategy> filterStrategyMap = new EnumMap<>(FilterMode.class);
    private static final int POSTS_ON_PAGE = 10;
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String INACTIVE_POST_STATUS = "inactive";
    private static final String PENDING_POST_STATUS = "pending";
    private static final String DECLINED_POST_STATUS = "declined";
    private static final String PUBLISHED_POST_STATUS = "published";

    private final PostsRepository postsRepository;
    private final PostMapper postMapper;

    @Autowired
    public PostQueryServiceImpl(PostsRepository postsRepository, PostMapper postMapper) {
        this.postsRepository = postsRepository;
        this.postMapper = postMapper;
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

    @Override
    public PostsResponse getModerationPosts(int offset, int limit, ModerationStatus status) {
        Pageable page = PageRequest.of(getPageNumber(offset), limit);
        Page<PostFlatDto> posts = postsRepository.findPostsByModerationStatus(status, page);
        return new PostsResponse(posts.getTotalElements(), getPostDtosFromPosts(posts.getContent()));
    }

    @Override
    public PostsResponse getMyPosts(int offset, int limit, String status) {
        Page<PostFlatDto> posts = Page.empty();
        User user = SecurityUtils.getCurrentUser();
        String email = user.getUsername();
        Pageable page = PageRequest.of(getPageNumber(offset), limit);
        switch (status) {
            case INACTIVE_POST_STATUS -> posts = postsRepository.findPostsByUser(email, page);
            case PENDING_POST_STATUS -> posts = postsRepository.findPostsByUserAndModerationStatus(email, ModerationStatus.NEW, page);
            case DECLINED_POST_STATUS -> posts = postsRepository.findPostsByUserAndModerationStatus(email, ModerationStatus.DECLINED, page);
            case PUBLISHED_POST_STATUS -> posts = postsRepository.findPostsByUserAndModerationStatus(email, ModerationStatus.ACCEPTED, page);
            default -> Page.empty();
        }
        return new PostsResponse(posts.getTotalElements(), getPostDtosFromPosts(posts.getContent()));
    }

    private int getPageNumber(int offset) {
        return offset / POSTS_ON_PAGE;
    }

    private List<PostDto> getPostDtosFromPosts(List<PostFlatDto> posts) {
        return posts.stream().map(postMapper::toPostDto).toList();
    }
}
