package main.service;

import lombok.extern.slf4j.Slf4j;
import main.api.request.CommentRequest;
import main.api.response.CommentResponse;
import main.model.Post;
import main.model.PostComment;
import main.repository.PostCommentsRepository;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Slf4j
public class PostCommentService {

    private static final String POST_NOT_FOUND_ERROR_MESSAGE = "Публикация не найдена";
    private static final String POST_COMMENT_NOT_FOUND_ERROR_MESSAGE = "Комментарий не найден";
    private final PostsRepository postsRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public PostCommentService(PostsRepository postsRepository, PostCommentsRepository postCommentsRepository, UsersRepository usersRepository) {
        this.postsRepository = postsRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional
    public CommentResponse addComment(CommentRequest commentRequest, int userId) {
        log.info("User {} is adding a comment to post {}", userId, commentRequest.getPostId());
        CommentResponse commentResponse = new CommentResponse();
        Post post = postsRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));

        main.model.User currentUser = usersRepository.getReferenceById(userId);

        PostComment postComment = new PostComment();
        postComment.setUser(currentUser);
        postComment.setText(commentRequest.getText());
        if (commentRequest.getParentId() instanceof Integer parentId) {
            postCommentsRepository.findById(parentId).orElseThrow(() -> new NoSuchElementException(POST_COMMENT_NOT_FOUND_ERROR_MESSAGE));
            postComment.setParentId(parentId);
        }
        postComment.setPost(post);
        postCommentsRepository.save(postComment);
        commentResponse.setId(postComment.getId());
        log.info("Comment {} added successfully to post {}", postComment.getId(), commentRequest.getPostId());
        return commentResponse;
    }
}
