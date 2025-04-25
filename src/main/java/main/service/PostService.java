package main.service;

import main.api.request.ModerationRequest;
import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.*;
import main.dto.PostDetailsDto;
import main.model.enums.ModerationStatus;
import main.service.strategy.enums.FilterMode;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PostService {
    PostsResponse getPosts(int offset, int limit, FilterMode mode);
    PostsResponse getPostsByQuery(int offset, int limit, String query);
    PostsResponse getPostsByDate(int offset, int limit, String date);
    PostsResponse getPostsByTag(int offset, int limit, String tag);
    PostsResponse getModerationPosts(int offset, int limit, ModerationStatus status);
    CalendarResponse getCalendar(int year);
    PostDetailsDto getPostById(int id);
    PostsResponse getMyPosts(int offset, int limit, String status);
    ResultResponse addPost(PostRequest postRequest);
    ResultResponse updatePost(int id, PostRequest postRequest);
    ResultResponse makePostVote(PostVoteRequest postVoteRequest, byte postVoteValue);
    StatisticsResponse getMyStatistic();
    StatisticsResponse getAllStatistic();
    ResultResponse moderation(ModerationRequest moderationRequest);
    Object image(MultipartFile multipartFile) throws IOException;
}
