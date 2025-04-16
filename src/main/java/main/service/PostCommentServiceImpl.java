package main.service;

import main.api.request.CommentRequest;
import main.api.response.CommentResponse;
import main.model.Post;
import main.model.PostComment;
import main.repository.PostCommentsRepository;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.NoSuchElementException;

@Service
public class PostCommentServiceImpl implements PostCommentService {

    private static final String POST_NOT_FOUND_ERROR_MESSAGE = "Публикация не найдена";
    private static final String USER_NOT_FOUND_ERROR_PATTERN = "user {0} not found";
    private static final String POST_COMMENT_NOT_FOUND_ERROR_MESSAGE = "user {0} not found";
    private final PostsRepository postsRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public PostCommentServiceImpl(PostsRepository postsRepository, PostCommentsRepository postCommentsRepository, UsersRepository usersRepository) {
        this.postsRepository = postsRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public CommentResponse addComment(CommentRequest commentRequest) {
        CommentResponse commentResponse = new CommentResponse();
        Post post = postsRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_ERROR_MESSAGE));
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        main.model.User currentUser = usersRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format(USER_NOT_FOUND_ERROR_PATTERN, email)));
        PostComment postComment = new PostComment();
        postComment.setPost(post);
        postComment.setUser(currentUser);
        postComment.setText(commentRequest.getText());
        if (commentRequest.getParentId() instanceof Integer parentId) {
            postCommentsRepository.findById(parentId).orElseThrow(() -> new NoSuchElementException(POST_COMMENT_NOT_FOUND_ERROR_MESSAGE));
            postComment.setParentID(parentId);
        }
        postCommentsRepository.save(postComment);
        commentResponse.setId(postComment.getId());
        return commentResponse;
    }
}
