package main.service.strategy.filter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import main.dto.PostFlatDto;
import main.repository.PostsRepository;
import main.service.PostQueryService;
import main.service.strategy.enums.FilterMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularFilter implements FilterStrategy {

    private final PostsRepository postsRepository;

    @PostConstruct
    @Override
    public void register() {
        PostQueryService.addFilterStrategy(FilterMode.POPULAR, this);
    }

    @Override
    public Page<PostFlatDto> execute(int pageNumber, int limit) {
        Pageable page = PageRequest.of(pageNumber, limit);
        return postsRepository.findAllByPostCommentCount(page);
    }
}
