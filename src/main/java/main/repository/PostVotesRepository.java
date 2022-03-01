package main.repository;

import main.model.Post;
import main.model.PostVote;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PostVotesRepository extends JpaRepository<PostVote, Integer> {
    public Optional<PostVote> findPostVoteByUserAndPost(User user, Post post);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE post_votes SET value = :value WHERE user_id = :user_id AND post_id = :post_id")
    public void updatePostVote(@Param("value") int value, @Param("user_id") int userId, @Param("post_id") int postId);
}
