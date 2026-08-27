package main.repository;

import main.api.response.TagResponse;
import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface TagsRepository extends JpaRepository<Tag, Integer> {
    @Query(nativeQuery = true, value = """
    WITH tag_counts AS (
        SELECT
            t.name AS name,
            COUNT(t2p.tag_id) AS tag_count,
            MAX(COUNT(t2p.tag_id)) OVER() AS max_count
        FROM posts p
        JOIN tag2post t2p ON p.id = t2p.post_id
        JOIN tags t ON t.id = t2p.tag_id
        WHERE p.is_active = 1
          AND p.moderation_status = 'ACCEPTED'
          AND p.time <= NOW()
        GROUP BY t.name
    )
    SELECT
        name,
        (CAST(tag_count AS DOUBLE) / max_count) AS weight
    FROM tag_counts
    """)
    Set<TagResponse> getTags();

    @Query("""
                SELECT t.name AS name
                FROM Post p
                JOIN p.tags t
                WHERE p.id = :postId
            """)
    Set<String> findTagNamesByPostId(@Param("postId") int postId);

    Set<Tag> findByNameIn(Collection<String> names);
}
