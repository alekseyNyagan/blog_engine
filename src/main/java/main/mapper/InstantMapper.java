package main.mapper;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class InstantMapper {
    public long fromInstantToLong(Instant instant) {
        return instant.getEpochSecond();
    }

    public Instant fromLongToInstant(long timestamp) {
        return Instant.ofEpochSecond(timestamp);
    }
}
