package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChartResponse {
    private String status;
    private List<Point> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Point {
        private long timestamp;
        private Map<String, Double> price;
        private double marketCap;
        private double volume24h;
        private double funding;
    }
}
