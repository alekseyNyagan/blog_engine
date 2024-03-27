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
    Optional<PostVote> findPostVoteByUserAndPost(User user, Post post);

    @Transactional
    @Modifying
    @Query(value = "UPDATE PostVote pv SET pv.value = :value WHERE pv.user = :user AND pv.post = :post")
    void updatePostVote(@Param("value") int value, @Param("user") User user, @Param("post") Post post);
}
