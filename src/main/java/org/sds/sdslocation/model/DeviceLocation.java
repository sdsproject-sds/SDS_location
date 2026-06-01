package org.sds.sdslocation.model;

import java.util.Map;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import org.sds.sdslocation.model.enums.DeviceStatus;
import org.sds.sdslocation.model.enums.DeviceType;
import org.springframework.format.annotation.DateTimeFormat;
import static org.sds.sdslocation.model.enums.DeviceStatus.AVAILABLE;
import static org.sds.sdslocation.model.enums.DeviceStatus.ONLINE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceLocation {

    private String deviceId;
    private String userId;
    private Double lat;
    private Double lon;
    private String notificationToken;
    private DeviceType deviceType;
    private Double accuracy;
    private DeviceStatus status;
    private Map<String, Object> metadata;
    private List<String> supportedServices;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    // Convenience methods
    public boolean isAvailable() {
        return AVAILABLE.equals(status) || ONLINE.equals(status);
    }

    public boolean supportsService(String serviceType) {
        return supportedServices != null && supportedServices.contains(serviceType);
    }

    public boolean hasValidNotificationToken() {
        return notificationToken != null && !notificationToken.trim().isEmpty();
    }
}