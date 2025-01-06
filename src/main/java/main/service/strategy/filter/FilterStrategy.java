package main.service.strategy.filter;

import main.model.Post;
import org.springframework.data.domain.Page;

public interface FilterStrategy {
    void register();
    Page<Post> execute(int pageNumber, int limit);
}
