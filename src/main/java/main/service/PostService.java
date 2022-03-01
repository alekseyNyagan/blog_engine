package main.service;

import main.api.request.ModerationRequest;
import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.*;
import main.dto.CurrentPostDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PostService {
    public PostsResponse getPosts(int offset, int limit, String mode);
    public PostsResponse getPostsByQuery(int offset, int limit, String query);
    public PostsResponse getPostsByDate(int offset, int limit, String date);
    public PostsResponse getPostsByTag(int offset, int limit, String tag);
    public PostsResponse getModerationPosts(int offset, int limit, String status);
    public CalendarResponse getCalendar(int year);
    public CurrentPostDTO getPostById(int id);
    public PostsResponse getMyPosts(int offset, int limit, String status);
    public ErrorsResponse addPost(PostRequest postRequest, boolean postPremoderation);
    public ErrorsResponse updatePost(int id, PostRequest postRequest, boolean postPremoderation);
    public ResultResponse like(PostVoteRequest postVoteRequest);
    public ResultResponse dislike(PostVoteRequest postVoteRequest);
    public StatisticsResponse getMyStatistic();
    public StatisticsResponse getAllStatistic();
    public ResultResponse moderation(ModerationRequest moderationRequest);
    public Object image(MultipartFile multipartFile) throws IOException;
}
