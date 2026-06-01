package org.sds.sdslocation.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request an object for notifying nearby service providers
 * @author AI Assistant
 * Created on 5/19/2026
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyNearbyRequest {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotBlank(message = "Request ID is required")
    private String requestId;

    @NotBlank(message = "Request details are required")
    @JsonProperty("requestDetails")
    private String requestDetails;

    @Positive(message = "Radius must be positive")
    private Double radius;

    private List<String> serviceTypes;

    @JsonProperty("additionalMetadata")
    private Map<String, Object> additionalMetadata;

    /**
     * Get radius with a default value if not provided
     */
    public Double getRadiusOrDefault() {
        return radius != null ? radius : 5000.0; // 5km default
    }

    /**
     * Get additional metadata or empty map if not provided
     */
    @JsonIgnore
    public Map<String, Object> getAdditionalMetadataOrEmpty() {
        return additionalMetadata != null ? additionalMetadata : Map.of();
    }
}