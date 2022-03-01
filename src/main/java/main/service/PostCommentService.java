package main.service;

import main.api.request.CommentRequest;
import main.api.response.CommentResponse;

public interface PostCommentService {
    public CommentResponse addComment(CommentRequest commentRequest);
}
