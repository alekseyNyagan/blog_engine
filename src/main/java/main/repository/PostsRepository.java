package main.repository;

import main.dto.CalendarDTO;
import main.model.ModerationStatus;
import main.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


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

    @Query(nativeQuery = true, value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' " +
            "AND time <= now() AND text LIKE CONCAT('%', :text, '%')")
    public List<Post> findPostsByIsActiveAndModerationStatusAndTextLike(@Param("text") String text, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND DATE(time) = :date")
    public List<Post> findPostsByIsActiveAndModerationStatusAndTime(@Param("date") String date, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM posts JOIN tag2post t2p on posts.id = t2p.post_id JOIN tags t on t.id = t2p.tag_id " +
            "WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND t.name = :name")
    public List<Post> findPostsByTag(@Param("name") String name, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = :status")
    public List<Post> findPostByModerationStatus(@Param("status") ModerationStatus moderationStatus, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT YEAR(posts.time) FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' GROUP BY YEAR(posts.time)")
    public List<Integer> findYearsByPostCount();

    @Query(nativeQuery = true, value = "SELECT DATE_FORMAT(time, '%Y-%m-%d') AS date, count(*) AS count FROM posts " +
            "WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND YEAR(time) = :year GROUP BY time ORDER BY time")
    public List<CalendarDTO> countPostsByYear(@Param("year") int year);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' " +
            "AND time <= now() AND text LIKE CONCAT('%', :text, '%')")
    public int countPostsByIsActiveAndModerationStatusAndTextLike(@Param("text") String text);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND DATE(time) = :date")
    public int countPostsByIsActiveAndModerationStatusAndTime(@Param("date") String date);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM posts JOIN tag2post t2p on posts.id = t2p.post_id JOIN tags t on t.id = t2p.tag_id " +
            "WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND t.name = :name")
    public int countPostsByTagName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE posts SET view_count = view_count + 1 WHERE id = :id")
    public void incrementViewCount(@Param("id") int id);

}
