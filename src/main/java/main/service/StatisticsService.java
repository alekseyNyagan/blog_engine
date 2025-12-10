package main.service;

import main.api.response.StatisticsResponse;
import main.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final PostsRepository postsRepository;
    private final UserService userService;

    @Autowired
    public StatisticsService(PostsRepository postsRepository, UserService userService) {
        this.postsRepository = postsRepository;
        this.userService = userService;
    }

    public StatisticsResponse getMyStatistics(String email) {
        int userId = userService.getUserByEmail(email).getId();
        return postsRepository.getMyStatistic(userId);
    }

    public StatisticsResponse getAllStatistics() {
        return postsRepository.getAllStatistic();
    }
}
