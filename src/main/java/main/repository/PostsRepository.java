package main.repository;

import main.model.ModerationStatus;
import main.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Post, Integer> {

    public int countPostsByModerationStatus(ModerationStatus moderationStatus);

    @Query(nativeQuery = true, value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= now()")
    public List<Post> findAllByIsActiveAndModerationStatus(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= now()")
    public int countPostsByIsActiveAndModerationStatus();

    @Query(nativeQuery = true, value = "SELECT * FROM posts LEFT JOIN post_votes pv on posts.id = pv.post_id " +
            "WHERE posts.is_active = 1 AND posts.moderation_status = 'ACCEPTED' AND posts.time <= now()" +
            "GROUP BY posts.id ORDER BY SUM(IF(pv.value = 1, 1, 0)) DESC")
    public List<Post> findAllLikedPosts(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM posts LEFT JOIN post_comments pc on posts.id = pc.post_id " +
            "WHERE posts.is_active = 1 AND posts.moderation_status = 'ACCEPTED' AND posts.time <= now() GROUP BY posts.id ORDER BY COUNT(pc.post_id) DESC")
    public List<Post> findAllByPostCommentCount(Pageable pageable);
}
