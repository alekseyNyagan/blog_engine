package main.repository;

import main.model.ModerationStatus;
import main.model.Post;
import main.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Post, Integer> {

    public int countPostsByModerationStatus(ModerationStatus moderationStatus);

    public List<Post> findAllByIsActiveAndModerationStatus(byte isActive, ModerationStatus moderationStatus, Pageable pageable);

    public int countPostsByIsActiveAndModerationStatus(byte isActive, ModerationStatus moderationStatus);

    @Query(nativeQuery = true, value = "SELECT * FROM posts LEFT JOIN post_votes pv on posts.id = pv.post_id " +
            "WHERE posts.is_active = 1 AND posts.moderation_status = 'ACCEPTED'" +
            "GROUP BY posts.id ORDER BY SUM(IF(pv.value = 1, 1, 0)) DESC")
    public List<Post> findAllLikedPosts(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM posts LEFT JOIN post_comments pc on posts.id = pc.post_id " +
            "WHERE posts.is_active = 1 AND posts.moderation_status = 'ACCEPTED' GROUP BY posts.id ORDER BY COUNT(pc.post_id) DESC")
    public List<Post> findAllByPostCommentCount(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT MAX(count) FROM(SELECT COUNT(tag_id) AS count from posts join tag2post t2p on posts.id = t2p.post_id " +
            "join tags t on t2p.tag_id = t.id group by t.id) AS temp")
    public long maxCountPostsByTag();

    @Query(nativeQuery = true, value = "SELECT COUNT(tag_id) FROM posts join tag2post t2p on posts.id = t2p.post_id " +
            "join tags t on t.id = t2p.tag_id WHERE t.id = :id")
    public long countPostsByTag(@Param("id") int id);
}
