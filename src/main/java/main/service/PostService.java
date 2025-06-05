package main.service;

import main.api.request.ModerationRequest;
import main.api.request.PostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.CalendarResponse;
import main.api.response.ResultResponse;
import main.dto.PostDetailsDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PostService {
    CalendarResponse getCalendar(int year);
    PostDetailsDto getPostById(int id);
    ResultResponse addPost(PostRequest postRequest);
    ResultResponse updatePost(int id, PostRequest postRequest);
    ResultResponse makePostVote(PostVoteRequest postVoteRequest, byte postVoteValue);
    ResultResponse moderation(ModerationRequest moderationRequest);
    Object image(MultipartFile multipartFile) throws IOException;
}
