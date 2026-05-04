package org.sds.sdslocation.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

/**
 * @author Joseph Kibe
 * Created on March 28, 2026,
 * Timezone Configuration - Sets application default timezone to UTC
 */
@Configuration
@Slf4j
public class TimezoneConfiguration {

    @PostConstruct
    public void configureTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
        System.setProperty("user.timezone", "UTC");
    }

    @Bean
    @Primary
    public Clock utcClock() {
        Clock clock = Clock.systemUTC();
        log.info("UTC Clock bean created: {}", clock.getZone());
        return clock;
    }

    @Bean("utcZoneId")
    public ZoneId utcZoneId() {
        return ZoneId.of("UTC");
    }


    @Bean("systemZoneId")
    public ZoneId systemZoneId() {
        return ZoneId.systemDefault();
    }
}