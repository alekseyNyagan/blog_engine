package main.repository;

import main.api.response.StatisticsResponse;
import main.dto.CalendarDTO;
import main.model.Post;
import main.model.User;
import main.model.enums.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT COUNT(p.id) FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = :status")
    int countPostsByModerationStatus(@Param("status") ModerationStatus moderationStatus);

    @Query(value = """
            SELECT p FROM Post p
            WHERE p.isActive = 1
            AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
            AND p.time <= CURRENT_TIME
            """)
    Page<Post> findAllByIsActiveAndModerationStatus(Pageable pageable);

    @Query(value = """
            SELECT p FROM Post p LEFT JOIN p.votes pv
            WHERE p.isActive = 1
            AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
            AND p.time <= CURRENT_TIME
            GROUP BY p.id
            ORDER BY SUM(CASE WHEN pv.value = 1 THEN 1 ELSE 0 END) DESC
            """)
    Page<Post> findAllLikedPosts(Pageable pageable);

    @Query(value = """
            SELECT p FROM Post p LEFT JOIN p.comments c
            WHERE p.isActive = 1
            AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
            AND p.time <= CURRENT_TIME
            GROUP BY p ORDER BY COUNT(c) DESC
            """)
    Page<Post> findAllByPostCommentCount(Pageable pageable);

    @Query(value = """
            SELECT p FROM Post p
            WHERE p.isActive = 1
            AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
            AND p.time <= CURRENT_TIME
            AND p.text LIKE CONCAT('%', :text, '%')
            """)
    Page<Post> findPostsByIsActiveAndModerationStatusAndTextLike(@Param("text") String text, Pageable pageable);

    @Query(value = """
            SELECT p FROM Post p
            WHERE p.isActive = 1
            AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
            AND p.time BETWEEN :dateFrom AND :dateTo
            """)
    Page<Post> findPostsByIsActiveAndModerationStatusAndTime(@Param("dateFrom") LocalDateTime dateFrom
            , @Param("dateTo") LocalDateTime dateTo
            , Pageable pageable);

    @Query(value = """
            SELECT p FROM Post p JOIN p.tags t
            WHERE p.isActive = 1
            AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
            AND t.name = :name
            """)
    Page<Post> findPostsByTag(@Param("name") String name, Pageable pageable);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = :status")
    Page<Post> findPostByModerationStatus(@Param("status") ModerationStatus status, Pageable pageable);

    @Query(value = """
            SELECT FUNCTION('YEAR', p.time) FROM Post p
            WHERE p.isActive = 1
            AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
            AND p.time <= CURRENT_TIME
            GROUP BY FUNCTION('YEAR', p.time)
            """)
    List<Integer> findYearsWithCreatedPosts();

    @Query(value = """
            SELECT FUNCTION('DATE_FORMAT', p.time, '%Y-%m-%d') AS date, COUNT(p.id) AS count FROM Post p
            WHERE p.isActive = 1
            AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
            AND FUNCTION('YEAR', p.time) = :year
            GROUP BY p.time
            ORDER BY p.time
            """)
    List<CalendarDTO> countPostsByYear(@Param("year") int year);

    Page<Post> findPostsByUserAndIsActive(User user, byte isActive, Pageable pageable);

    Page<Post> findPostsByUserAndIsActiveAndModerationStatus(User user, byte isActive, ModerationStatus moderationStatus, Pageable pageable);

    @NativeQuery(value = """
            WITH post_temp AS (
            SELECT IFNULL(COUNT(id), 0) AS postsCount, IFNULL(SUM(view_count), 0) AS viewsCount, UNIX_TIMESTAMP(MIN(time)) AS firstPublication FROM posts
            WHERE user_id = :user_id AND is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= NOW()),
            post_votes_temp AS (
            SELECT IFNULL(SUM(IF(value = 1, 1, 0)), 0) AS likesCount, IFNULL(SUM(IF(value = -1, 1, 0)), 0) AS dislikesCount FROM post_votes WHERE user_id = :user_id)
            SELECT postsCount, viewsCount, firstPublication, likesCount, dislikesCount FROM post_temp JOIN post_votes_temp
            """)
    StatisticsResponse getMyStatistic(@Param("user_id") int userId);

    @NativeQuery(value = """
            WITH post_temp AS (
            SELECT IFNULL(COUNT(id), 0) AS postsCount, IFNULL(SUM(view_count), 0) AS viewsCount, UNIX_TIMESTAMP(MIN(time)) AS firstPublication FROM posts
            WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= NOW()),
            post_votes_temp AS (
            SELECT IFNULL(SUM(IF(value = 1, 1, 0)), 0) AS likesCount, IFNULL(SUM(IF(value = -1, 1, 0)), 0) AS dislikesCount FROM post_votes
            JOIN posts p on post_votes.post_id = p.id WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND p.time <= NOW())
            SELECT postsCount, viewsCount, firstPublication, likesCount, dislikesCount FROM post_temp JOIN post_votes_temp
            """)
    StatisticsResponse getAllStatistic();
}
