package main.service;

import main.api.request.CommentRequest;
import main.api.response.CommentResponse;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.repository.PostCommentsRepository;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommentServiceTest {

    @Mock
    private PostsRepository postsRepository;
    @Mock
    private PostCommentsRepository postCommentsRepository;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private PostCommentService postCommentService;

    private static final int USER_ID = 1;
    private static final int POST_ID = 21;
    private static final int PARENT_COMMENT_ID = 31;
    private static final int SAVED_COMMENT_ID = 100;
    private static final String COMMENT_TEXT = "текст комментария";

    // --- Вспомогательные методы ---

    private CommentRequest buildRequest(Object parentId, String text) {
        CommentRequest request = new CommentRequest();
        request.setParentId(parentId);
        request.setPostId(POST_ID);
        request.setText(text);
        return request;
    }

    private Post mockPost() {
        Post post = mock(Post.class);
        when(postsRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        return post;
    }

    private void mockSavedComment() {
        when(postCommentsRepository.save(any(PostComment.class))).thenAnswer(invocation -> {
            PostComment comment = invocation.getArgument(0);
            comment.setId(SAVED_COMMENT_ID);
            return comment;
        });
    }

    // --- Тесты ---

    @Test
    @DisplayName("addComment: успешное добавление без родителя (parent_id = пустая строка)")
    void addComment_ShouldSaveComment_WhenParentIdIsEmptyString() {
        mockPost();
        mockSavedComment();
        when(usersRepository.getReferenceById(USER_ID)).thenReturn(mock(User.class));

        CommentRequest request = buildRequest("", COMMENT_TEXT);
        CommentResponse response = postCommentService.addComment(request, USER_ID);

        assertEquals(SAVED_COMMENT_ID, response.getId());

        ArgumentCaptor<PostComment> captor = ArgumentCaptor.forClass(PostComment.class);
        verify(postCommentsRepository).save(captor.capture());
        assertNull(captor.getValue().getParentId(), "parentId должен быть null при пустом parent_id");
        verify(postCommentsRepository, never()).findById(any());
    }

    @Test
    @DisplayName("addComment: успешное добавление с родительским комментарием (parent_id = Integer)")
    void addComment_ShouldSaveComment_WhenParentIdIsInteger() {
        mockPost();
        mockSavedComment();
        when(usersRepository.getReferenceById(USER_ID)).thenReturn(mock(User.class));
        PostComment parentComment = mock(PostComment.class);
        when(postCommentsRepository.findById(PARENT_COMMENT_ID)).thenReturn(Optional.of(parentComment));

        CommentRequest request = buildRequest(PARENT_COMMENT_ID, COMMENT_TEXT);
        CommentResponse response = postCommentService.addComment(request, USER_ID);

        assertEquals(SAVED_COMMENT_ID, response.getId());

        ArgumentCaptor<PostComment> captor = ArgumentCaptor.forClass(PostComment.class);
        verify(postCommentsRepository).save(captor.capture());
        assertEquals(PARENT_COMMENT_ID, captor.getValue().getParentId(), "parentId должен быть установлен");
    }

    @Test
    @DisplayName("addComment: пост не найден → NoSuchElementException")
    void addComment_ShouldThrow_WhenPostNotFound() {
        when(postsRepository.findById(POST_ID)).thenReturn(Optional.empty());

        CommentRequest request = buildRequest("", COMMENT_TEXT);

        assertThrows(NoSuchElementException.class,
                () -> postCommentService.addComment(request, USER_ID));

        verify(postCommentsRepository, never()).save(any());
    }

    @Test
    @DisplayName("addComment: родительский комментарий не найден → NoSuchElementException")
    void addComment_ShouldThrow_WhenParentCommentNotFound() {
        mockPost();
        when(usersRepository.getReferenceById(USER_ID)).thenReturn(mock(User.class));
        when(postCommentsRepository.findById(PARENT_COMMENT_ID)).thenReturn(Optional.empty());

        CommentRequest request = buildRequest(PARENT_COMMENT_ID, COMMENT_TEXT);

        assertThrows(NoSuchElementException.class,
                () -> postCommentService.addComment(request, USER_ID));

        verify(postCommentsRepository, never()).save(any());
    }
}
