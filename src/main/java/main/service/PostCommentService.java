package main.service;

import main.api.request.CommentRequest;
import main.api.response.CommentResponse;
import main.model.Post;
import main.model.PostComment;
import main.repository.PostCommentsRepository;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.NoSuchElementException;

@Service
public class PostCommentService {

    private static final String POST_NOT_FOUND_ERROR_MESSAGE = "Публикация не найдена";
    private static final String USER_NOT_FOUND_ERROR_PATTERN = "user {0} not found";
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
    public CommentResponse addComment(CommentRequest commentRequest, UserDetails userDetails) {
        CommentResponse commentResponse = new CommentResponse();
        Post post = postsRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
        String email = userDetails.getUsername();
        main.model.User currentUser = usersRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format(USER_NOT_FOUND_ERROR_PATTERN, email)));
        PostComment postComment = new PostComment();
        postComment.setUser(currentUser);
        postComment.setText(commentRequest.getText());
        if (commentRequest.getParentId() instanceof Integer parentId) {
            postCommentsRepository.findById(parentId).orElseThrow(() -> new NoSuchElementException(POST_COMMENT_NOT_FOUND_ERROR_MESSAGE));
            postComment.setParentID(parentId);
        }
        post.addComment(postComment);
        postsRepository.save(post);
        commentResponse.setId(postComment.getId());
        return commentResponse;
    }
}
