package main.service.strategy.filter;

import main.dto.PostFlatDto;
import org.springframework.data.domain.Page;

public interface FilterStrategy {
    void register();
    Page<PostFlatDto> execute(int pageNumber, int limit);
}
