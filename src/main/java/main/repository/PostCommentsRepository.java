package main.repository;

import main.model.Post;
import main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentsRepository extends JpaRepository<PostComment, Integer> {
    public int countPostCommentsByPost(Post post);
    public List<PostComment> findPostCommentsByPost(Post post);
}
