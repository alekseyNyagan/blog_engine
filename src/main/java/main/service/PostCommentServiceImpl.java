package main.service;

import main.api.request.CommentRequest;
import main.api.response.CommentResponse;
import main.error.AbstractError;
import main.error.TextError;
import main.exceptions.NoSuchCommentException;
import main.exceptions.NoSuchPostException;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PostCommentServiceImpl implements PostCommentService {

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
        PostComment postComment = new PostComment();
        Optional<Post> post = postsRepository.findById(commentRequest.getPostId());
        Map<AbstractError, String> errors = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        main.model.User currentUser = usersRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user" + email + "not found"));
        String text = commentRequest.getText();
        String htmlTagRegexp = "\\<.*?\\>";
        if (commentRequest.getParentId() instanceof Integer) {
            Optional<PostComment> parentPostComment = postCommentsRepository.findById((Integer) commentRequest.getParentId());
            if (text.replaceAll(htmlTagRegexp, "").length() > 50 && post.isPresent() &&
                    (parentPostComment.isPresent())) {
                postComment.setPost(post.get());
                postComment.setUser(currentUser);
                postComment.setTime(LocalDateTime.now());
                postComment.setText(text);
                postComment.setParentID((Integer) commentRequest.getParentId());
                postCommentsRepository.save(postComment);
                commentResponse.setId(postComment.getId());
            } else {
                createErrors(post, errors, text, htmlTagRegexp, parentPostComment.isEmpty());
                commentResponse.setResult(false);
                commentResponse.setErrors(errors);
            }
        } else {
            if (text.replaceAll(htmlTagRegexp, "").length() > 50 && post.isPresent()) {
                postComment.setPost(post.get());
                postComment.setUser(currentUser);
                postComment.setTime(LocalDateTime.now());
                postComment.setText(text);
                postCommentsRepository.save(postComment);
                commentResponse.setId(postComment.getId());
            } else {
                createErrors(post, errors, text, htmlTagRegexp, false);
                commentResponse.setResult(false);
                commentResponse.setErrors(errors);
            }
        }
        return commentResponse;
    }

    private void createErrors(Optional<Post> post, Map<AbstractError, String> errors, String text, String htmlTagRegexp, boolean isParentPostCommentEmpty) {
        if (text.replaceAll(htmlTagRegexp, "").isEmpty()) {
            TextError textError = new TextError("Текст комментария пустой");
            errors.put(textError, textError.getMessage());
        } else if (text.replaceAll(htmlTagRegexp, "").length() <= 50) {
            TextError textError = new TextError("Текст комментария слишком короткий");
            errors.put(textError, textError.getMessage());
        }
        if (isParentPostCommentEmpty) {
            throw new NoSuchCommentException("Комментарий не найден");
        }
        if (post.isEmpty()) {
            throw new NoSuchPostException("Публикация не найдена");
        }
    }
}
