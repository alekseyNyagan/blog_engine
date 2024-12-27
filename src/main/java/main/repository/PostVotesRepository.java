package main.repository;

import main.model.Post;
import main.model.PostVote;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVotesRepository extends JpaRepository<PostVote, Integer> {
    Optional<PostVote> findPostVoteByUserAndPost(User user, Post post);
}
