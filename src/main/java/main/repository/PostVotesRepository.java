package main.repository;

import main.model.Post;
import main.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVotesRepository extends JpaRepository<PostVote, Integer> {
    public int countPostVoteByPostAndValue(Post post, byte value);
}
