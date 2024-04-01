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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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
        Optional<Post> post = postsRepository.findById(commentRequest.getPostId());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = user.getUsername();
        main.model.User currentUser = usersRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user" + email + "not found"));
        String htmlTagRegexp = "<.*?>";
        String text = commentRequest.getText().replaceAll(htmlTagRegexp, "");

        Map<String, String> errors = validateComment(text);

        if (!errors.isEmpty()) {
            commentResponse.setResult(false);
            commentResponse.setErrors(errors);
            return commentResponse;
        } else {
            post.orElseThrow(() -> new NoSuchElementException("Публикация не найдена"));
            PostComment postComment = new PostComment(
                    null
                    , post.get()
                    , currentUser
                    , LocalDateTime.now()
                    , text);
            if (commentRequest.getParentId() instanceof Integer) {
                Optional<PostComment> parentPostComment = postCommentsRepository.findById((Integer) commentRequest.getParentId());
                parentPostComment.orElseThrow(() -> new NoSuchElementException("Комментарий не найден"));
                postComment.setParentID((Integer) commentRequest.getParentId());
            }
            postCommentsRepository.save(postComment);
            commentResponse.setId(postComment.getId());
        }
        return commentResponse;
    }

    private Map<String, String> validateComment(String text) {
        Map<String, String> errorsMap = new HashMap<>();
        if (text.isEmpty()) {
            errorsMap.put("text", "Текст комментария пустой");
        } else if (text.length() <= 50) {
            errorsMap.put("text", "Текст комментария слишком короткий");
        }
        return errorsMap;
    }
}
