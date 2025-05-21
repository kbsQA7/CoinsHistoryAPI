
package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceHistoryResponse {
    private String status;
    private Map<String, Double> data;
    private boolean failure;
    private String failureDetails;
    private long timestamp;
}
