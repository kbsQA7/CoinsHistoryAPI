package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FearIndexResponse {
    private String status;
    private List<FearIndexPoint> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FearIndexPoint {
        private long time;
        private double value;
    }

    public long getFirstTime() {
        if (data == null || data.isEmpty()) {
            throw new IllegalStateException("Список данных пуст, невозможно получить первую метку времени.");
        }
        return data.get(0).getTime();
    }

    public long getLastTime() {
        if (data == null || data.isEmpty()) {
            throw new IllegalStateException("Список данных пуст, невозможно получить вторую метку времени.");
        }
        return data.get(data.size() - 1).getTime();
    }
}
