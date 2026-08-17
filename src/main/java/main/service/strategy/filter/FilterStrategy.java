package main.service.strategy.filter;

import main.repository.projection.PostProjection;
import org.springframework.data.domain.Page;

public interface FilterStrategy {
    void register();
    Page<PostProjection> execute(int pageNumber, int limit);
}
