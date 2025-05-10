package main.repository;

import main.api.response.StatisticsResponse;
import main.dto.CalendarDTO;
import main.dto.PostDetailsFlatDto;
import main.dto.PostFlatDto;
import main.model.Post;
import main.model.User;
import main.model.enums.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT COUNT(p.id) FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = :status")
    int countPostsByModerationStatus(@Param("status") ModerationStatus moderationStatus);

    @Query(value = """
            SELECT new main.dto.PostFlatDto(
                    p.id,
                    p.time,
                    u.id,
                    u.name,
                    p.title,
                    p.text,
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = 1) AS likesCount,
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = -1),
                    (SELECT COUNT(c) FROM PostComment c WHERE c.post = p),
                    p.viewCount
                )
                FROM Post p
                JOIN p.user u
                WHERE
                    p.isActive = 1
                    AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
                    AND p.time <= CURRENT_TIME
                GROUP BY p.id, u.id, u.name, p.title, p.text, p.time, p.viewCount
                ORDER BY likesCount DESC
            """)
    Page<PostFlatDto> findAllLikedPosts(Pageable pageable);

    @Query(value = """
            SELECT new main.dto.PostFlatDto(
                    p.id,
                    p.time,
                    u.id,
                    u.name,
                    p.title,
                    p.text,
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = 1),
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = -1),
                    (SELECT COUNT(c) FROM PostComment c WHERE c.post = p) AS commentCount,
                    p.viewCount
                )
                FROM Post p
                JOIN p.user u
                WHERE
                    p.isActive = 1
                    AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
                    AND p.time <= CURRENT_TIME
                GROUP BY p.id, u.id, u.name, p.title, p.text, p.time, p.viewCount
                ORDER BY commentCount DESC
            """)
    Page<PostFlatDto> findAllByPostCommentCount(Pageable pageable);

    @Query(value = """
            SELECT new main.dto.PostFlatDto(
                    p.id,
                    p.time,
                    u.id,
                    u.name,
                    p.title,
                    p.text,
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = 1),
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = -1),
                    (SELECT COUNT(c) FROM PostComment c WHERE c.post = p),
                    p.viewCount
                )
                FROM Post p
                JOIN p.user u
                WHERE
                    p.isActive = 1
                    AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
                    AND p.time <= CURRENT_TIME
                    AND p.text LIKE CONCAT('%', :text, '%')
                GROUP BY p.id, u.id, u.name, p.title, p.text, p.time, p.viewCount
            
            """)
    Page<PostFlatDto> findPostsByTextLike(@Param("text") String text, Pageable pageable);

    @Query(value = """
            SELECT new main.dto.PostFlatDto(
                    p.id,
                    p.time,
                    u.id,
                    u.name,
                    p.title,
                    p.text,
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = 1),
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = -1),
                    (SELECT COUNT(c) FROM PostComment c WHERE c.post = p),
                    p.viewCount
                )
                FROM Post p
                JOIN p.user u
                WHERE
                    p.isActive = 1
                    AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
                    AND p.time BETWEEN :dateFrom AND :dateTo
                GROUP BY p.id, u.id, u.name, p.title, p.text, p.time, p.viewCount
            """)
    Page<PostFlatDto> findPostsByTime(@Param("dateFrom") Instant dateFrom
            , @Param("dateTo") Instant dateTo
            , Pageable pageable);

    @Query(value = """
            SELECT new main.dto.PostFlatDto(
                    p.id,
                    p.time,
                    u.id,
                    u.name,
                    p.title,
                    p.text,
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = 1),
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = -1),
                    (SELECT COUNT(c) FROM PostComment c WHERE c.post = p),
                    p.viewCount
                )
                FROM Post p
                JOIN p.user u
                JOIN p.tags t
                WHERE
                    p.isActive = 1
                    AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
                    AND p.time <= CURRENT_TIME
                    AND t.name = :name
                GROUP BY p.id, u.id, u.name, p.title, p.text, p.time, p.viewCount
            """)
    Page<PostFlatDto> findPostsByTag(@Param("name") String name, Pageable pageable);

    @Query(value = """
            SELECT new main.dto.PostFlatDto(
                    p.id,
                    p.time,
                    u.id,
                    u.name,
                    p.title,
                    p.text,
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = 1),
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = -1),
                    (SELECT COUNT(c) FROM PostComment c WHERE c.post = p),
                    p.viewCount
                )
                FROM Post p
                JOIN p.user u
                WHERE
                    p.isActive = 1
                    AND p.moderationStatus = :status
                GROUP BY p.id, u.id, u.name, p.title, p.text, p.time, p.viewCount
            """)
    Page<PostFlatDto> findPostsByModerationStatus(@Param("status") ModerationStatus status, Pageable pageable);

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

    @Query("""
            SELECT new main.dto.PostFlatDto(
                p.id,
                p.time,
                u.id,
                u.name,
                p.title,
                p.text,
                (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = 1),
                (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = -1),
                (SELECT COUNT(c) FROM PostComment c WHERE c.post = p),
                p.viewCount
            )
            FROM Post p
            JOIN p.user u
            WHERE
                p.isActive = 0
                AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
                AND p.time <= CURRENT_TIME
                AND p.user.email = :email
                GROUP BY p.id, u.id, u.name, p.title, p.text, p.time, p.viewCount
            """)
    Page<PostFlatDto> findPostsByUser(String email, Pageable pageable);

    @Query("""
            SELECT new main.dto.PostFlatDto(
                p.id,
                p.time,
                u.id,
                u.name,
                p.title,
                p.text,
                (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = 1),
                (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = -1),
                (SELECT COUNT(c) FROM PostComment c WHERE c.post = p),
                p.viewCount
            )
            FROM Post p
            JOIN p.user u
            WHERE
                p.isActive = 1
                AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
                AND p.time <= CURRENT_TIME
                AND p.user.email = :email
                AND p.moderationStatus = :moderationStatus
                GROUP BY p.id, u.id, u.name, p.title, p.text, p.time, p.viewCount
            """)
    Page<PostFlatDto> findPostsByUserAndModerationStatus(String email, ModerationStatus moderationStatus, Pageable pageable);

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

    @Modifying
    @Query("update Post set viewCount = viewCount + 1 where id = :id")
    void updateViewCount(int viewCount, int id);

    @Query("""
                SELECT new main.dto.PostFlatDto(
                    p.id,
                    p.time,
                    u.id,
                    u.name,
                    p.title,
                    p.text,
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = 1),
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = -1),
                    (SELECT COUNT(c) FROM PostComment c WHERE c.post = p),
                    p.viewCount
                )
                FROM Post p
                JOIN p.user u
                WHERE
                    p.isActive = 1
                    AND p.moderationStatus = main.model.enums.ModerationStatus.ACCEPTED
                    AND p.time <= CURRENT_TIME
                GROUP BY p.id, u.id, u.name, p.title, p.text, p.time, p.viewCount
            """)
    Page<PostFlatDto> findPosts(Pageable pageable);

    @Query("""
            SELECT new main.dto.PostDetailsFlatDto(
                    p.id,
                    p.time,
                    p.isActive = 1,
                    u.id,
                    u.name,
                    u.photo,
                    p.title,
                    p.text,
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = 1),
                    (SELECT COUNT(v) FROM PostVote v WHERE v.post = p AND v.value = -1),
                    p.viewCount,
                    u.email
                )
                FROM Post p
                JOIN p.user u
                WHERE p.id = :postId
            """)
    Optional<PostDetailsFlatDto> findPostDetailsById(@Param("postId") int postId);
}
