package com.example.manager.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class TimeUtils {
    private static final long THRESHOLD_MILLIS = 300 * 1000;

    public static boolean isEqual(LocalDateTime dt1, LocalDateTime dt2) {
        ZoneId zone = ZoneId.systemDefault();

        long ts1 = dt1.atZone(zone).toInstant().toEpochMilli();
        long ts2 = dt2.atZone(zone).toInstant().toEpochMilli();

        return Math.abs(ts1 - ts2) <= THRESHOLD_MILLIS;
    }
}
