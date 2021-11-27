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

    @Query(nativeQuery = true, value = "WITH tag_temp AS(" +
            "SELECT COUNT(tag_id) AS tag_count FROM posts " +
            "JOIN tag2post t2p on posts.id = t2p.post_id " +
            "JOIN tags t on t.id = t2p.tag_id WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= NOW() AND t.id = :id), " +
            "post_temp AS (" +
            "SELECT COUNT(*) AS post_count FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= now()), " +
            "max_count_post_by_tag AS (" +
            "SELECT MAX(count) AS max_count FROM (SELECT COUNT(tag_id) AS count FROM posts " +
            "JOIN tag2post p on posts.id = p.post_id " +
            "JOIN tags t2 on t2.id = p.tag_id WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= now() GROUP BY t2.id) AS temp)" +
            "SELECT (1 / (max_count / post_count)) * (tag_count / post_count) FROM tag_temp JOIN post_temp JOIN max_count_post_by_tag")
    public double getTagWeight(@Param("id") int id);
}
