package main.service;

import main.api.response.CalendarResponse;
import main.api.response.PostsResponse;
import main.dto.PostDTO;

public interface PostService {
    public PostsResponse getPosts(int offset, int limit, String mode);
    public PostsResponse getPostsByQuery(int offset, int limit, String query);
    public PostsResponse getPostsByDate(int offset, int limit, String date);
    public PostsResponse getPostsByTag(int offset, int limit, String tag);
    public PostsResponse getModerationPosts(int offset, int limit, String status);
    public CalendarResponse getCalendar(int year);
    public PostDTO getPostById(int id);
}
