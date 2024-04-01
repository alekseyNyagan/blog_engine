package main.service;

import main.api.request.ModerationRequest;
import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.*;
import main.dto.CurrentPostDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PostService {
    PostsResponse getPosts(int offset, int limit, String mode);
    PostsResponse getPostsByQuery(int offset, int limit, String query);
    PostsResponse getPostsByDate(int offset, int limit, String date);
    PostsResponse getPostsByTag(int offset, int limit, String tag);
    PostsResponse getModerationPosts(int offset, int limit, String status);
    CalendarResponse getCalendar(int year);
    CurrentPostDTO getPostById(int id);
    PostsResponse getMyPosts(int offset, int limit, String status);
    ErrorsResponse addPost(PostRequest postRequest, boolean postPremoderation);
    ErrorsResponse updatePost(int id, PostRequest postRequest, boolean postPremoderation);
    ResultResponse like(PostVoteRequest postVoteRequest);
    ResultResponse dislike(PostVoteRequest postVoteRequest);
    StatisticsResponse getMyStatistic();
    StatisticsResponse getAllStatistic();
    ResultResponse moderation(ModerationRequest moderationRequest);
    Object image(MultipartFile multipartFile) throws IOException;
}
