package main.repository;

import main.dto.PostCommentFlatDto;
import main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentsRepository extends JpaRepository<PostComment, Integer> {
    @Query("""
                SELECT new main.dto.PostCommentFlatDto(
                        c.id,
                        c.time,
                        c.text,
                        u.id,
                        u.name,
                        u.photo
                    )
                    FROM PostComment c
                    JOIN c.user u
                    WHERE c.post.id = :postId
                    ORDER BY c.time ASC
            """)
    List<PostCommentFlatDto> findCommentsByPostId(@Param("postId") int postId);

}
