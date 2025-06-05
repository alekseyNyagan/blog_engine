package main.service;

import main.api.response.PostsResponse;
import main.model.enums.ModerationStatus;
import main.service.strategy.enums.FilterMode;

public interface PostQueryService {
    PostsResponse getPosts(int offset, int limit, FilterMode mode);
    PostsResponse getPostsByQuery(int offset, int limit, String query);
    PostsResponse getPostsByDate(int offset, int limit, String date);
    PostsResponse getPostsByTag(int offset, int limit, String tag);
    PostsResponse getModerationPosts(int offset, int limit, ModerationStatus status);
    PostsResponse getMyPosts(int offset, int limit, String status);
}
