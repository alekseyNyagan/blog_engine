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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EarlyFilter implements FilterStrategy {

    private final PostsRepository postsRepository;


    @PostConstruct
    @Override
    public void register() {
        PostQueryService.addFilterStrategy(FilterMode.EARLY, this);
    }

    @Override
    public Page<PostFlatDto> execute(int pageNumber, int limit) {
        Sort dateSort = Sort.by(Sort.Direction.ASC, "time");
        Pageable page = PageRequest.of(pageNumber, limit, dateSort);
        return postsRepository.findPosts(page);
    }
}
