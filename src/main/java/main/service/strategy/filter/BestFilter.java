package main.service.strategy.filter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import main.model.Post;
import main.service.strategy.enums.FilterMode;
import main.repository.PostsRepository;
import main.service.PostServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BestFilter implements FilterStrategy {

    private final PostsRepository postsRepository;

    @PostConstruct
    @Override
    public void register() {
        PostServiceImpl.addFilterStrategy(FilterMode.BEST, this);
    }

    @Override
    public Page<Post> execute(int pageNumber, int limit) {
        Pageable page = PageRequest.of(pageNumber, limit);
        return postsRepository.findAllLikedPosts(page);
    }
}
