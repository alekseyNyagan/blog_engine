package main.service;

import main.api.response.PostsResponse;

public interface PostService {
    public PostsResponse getPosts(int offset, int limit, String mode);
    public long postCount();
}
