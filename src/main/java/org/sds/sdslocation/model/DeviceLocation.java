package org.sds.sdslocation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceLocation {

    private String deviceId;
    private String userId; // Changed from specialistId to userId
    private Double latitude;
    private Double longitude;
    private String notificationToken; // Consolidated FCM/APN tokens
    private String deviceType; // ANDROID, IOS, WEB
    private Double accuracy;
    private String status; // AVAILABLE, OFFLINE, BUSY, ONLINE
    private String metadata; // JSON string for additional data
    private List<String> supportedServices; // Services this user/specialist supports

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Convenience methods
    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(status) || "ONLINE".equalsIgnoreCase(status);
    }

    public boolean supportsService(String serviceType) {
        return supportedServices != null && supportedServices.contains(serviceType);
    }

    public boolean hasValidNotificationToken() {
        return notificationToken != null && !notificationToken.trim().isEmpty();
    }
}