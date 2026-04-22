package main.service;

import lombok.extern.slf4j.Slf4j;
import main.api.response.StatisticsResponse;
import main.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatisticsService {

    private final PostsRepository postsRepository;
    private final UserService userService;

    @Autowired
    public StatisticsService(PostsRepository postsRepository, UserService userService) {
        this.postsRepository = postsRepository;
        this.userService = userService;
    }

    public StatisticsResponse getMyStatistics(String email) {
        log.info("Request for personal statistics for user {}", email);
        int userId = userService.getUserByEmail(email).getId();
        return postsRepository.getMyStatistic(userId);
    }

    public StatisticsResponse getAllStatistics() {
        log.info("Request for all statistics");
        return postsRepository.getAllStatistic();
    }
}
