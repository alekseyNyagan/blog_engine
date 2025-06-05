package main.service;

import main.api.response.StatisticsResponse;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import main.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public StatisticsServiceImpl(PostsRepository postsRepository, UsersRepository usersRepository) {
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public StatisticsResponse getMyStatistics() {
        String email = SecurityUtils.getCurrentUserEmail();
        int userId = usersRepository.findUserIdByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(MessageFormat.format("User {0} not found", email)));
        return postsRepository.getMyStatistic(userId);
    }

    @Override
    public StatisticsResponse getAllStatistics() {
        return postsRepository.getAllStatistic();
    }
}
