package main.repository;

import main.dto.TagDTO;
import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TagsRepository extends JpaRepository<Tag, Integer> {
    @Query(nativeQuery = true, value = "WITH tag_temp AS(" +
            "SELECT t.name AS name, COUNT(tag_id) AS tag_count FROM posts " +
            "JOIN tag2post t2p on posts.id = t2p.post_id " +
            "JOIN tags t on t.id = t2p.tag_id WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= NOW() GROUP BY t.name), " +
            "post_temp AS (" +
            "SELECT COUNT(*) AS post_count FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= now()), " +
            "max_count_post_by_tag AS (" +
            "SELECT MAX(count) AS max_count FROM (SELECT COUNT(tag_id) AS count FROM posts " +
            "JOIN tag2post p on posts.id = p.post_id " +
            "JOIN tags t2 on t2.id = p.tag_id WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= now() GROUP BY t2.id) AS temp)" +
            "SELECT name, (1 / (max_count / post_count)) * (tag_count / post_count) AS weight FROM tag_temp JOIN post_temp JOIN max_count_post_by_tag")
    public Set<TagDTO> getTags();
}
