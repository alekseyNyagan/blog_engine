package main.service;

import main.api.response.StatisticsResponse;

public interface StatisticsService {
    StatisticsResponse getMyStatistics();
    StatisticsResponse getAllStatistics();
}
