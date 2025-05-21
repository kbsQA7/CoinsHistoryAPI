package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    // Случайная дата между двумя годами
    public static String randomDate(int startYear, int endYear) {
        LocalDate start = LocalDate.of(startYear, 1, 1);
        LocalDate end = LocalDate.of(endYear, 12, 31);
        long startEpoch = start.toEpochDay();
        long endEpoch = end.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch);
        return LocalDate.ofEpochDay(randomDay).format(DATE);
    }


    // Рандом интервал дат между now и now.minusDays
    public static String[] randomDateTimeInterval(int daysBack) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startLimit = now.minusDays(daysBack);

        long startEpoch = startLimit.toEpochSecond(ZoneOffset.UTC);
        long endEpoch = now.toEpochSecond(ZoneOffset.UTC);

        long randomStart = ThreadLocalRandom.current().nextLong(startEpoch, endEpoch - 3600);
        long randomEnd = ThreadLocalRandom.current().nextLong(randomStart + 1800, endEpoch);

        String from = LocalDateTime.ofEpochSecond(randomStart, 0, ZoneOffset.UTC).format(DATE_TIME);
        String to = LocalDateTime.ofEpochSecond(randomEnd, 0, ZoneOffset.UTC).format(DATE_TIME);

        return new String[]{from, to};
    }


    public static String randomTimeFrame() {
        String[] frames = {"1H", "1D", "1W", "1M", "3M", "6M", "YTD", "1Y", "ALL"};
        int idx = ThreadLocalRandom.current().nextInt(frames.length);
        return frames[idx];
    }


    public static String randomCoin() {
        String[] coins = {
                "bitcoin",
                "ethereum",
                "litecoin",
                "ripple",
                "dogecoin",
                "cardano",
                "solana"
        };
        int idx = ThreadLocalRandom.current().nextInt(coins.length);
        return coins[idx];
    }
}


