package utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class IntervalUtils {
    public final String from;
    public final String to;
    public final long expectedFrom;
    public final long expectedTo;

    private IntervalUtils(String from, String to, long expectedFrom, long expectedTo) {
        this.from = from;
        this.to = to;
        this.expectedFrom = expectedFrom;
        this.expectedTo = expectedTo;
    }


    // Генерация случайного интервала за последние `daysBack` дней
    public static IntervalUtils random(int daysBack) {
        String[] interval = RandomUtils.randomDateTimeInterval(daysBack);
        String from = interval[0];
        String to = interval[1];

        long fromMs = LocalDateTime.parse(from).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
        long toMs = LocalDateTime.parse(to).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();

        return new IntervalUtils(from, to, fromMs, toMs);
    }
}