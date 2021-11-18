package main.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagsRepository extends JpaRepository<Tag, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM posts JOIN tag2post t2p on posts.id = t2p.post_id " +
            "JOIN tags t on t2p.tag_id = t.id WHERE posts.id = :id")
    public List<Tag> findTagsByPostId(@Param("id") int id);
}
