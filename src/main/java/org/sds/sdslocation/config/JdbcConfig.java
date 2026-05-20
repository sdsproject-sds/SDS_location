package org.sds.sdslocation.config;

import org.sds.sdslocation.converter.ByteArrayToPointConverter;
import org.sds.sdslocation.converter.BytesToGeometryConverter;
import org.sds.sdslocation.converter.GeometryToBytesConverter;
import org.sds.sdslocation.converter.PointToByteArrayConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;

import java.util.Arrays;

@Configuration
public class JdbcConfig extends AbstractJdbcConfiguration {

    @Bean
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(Arrays.asList(
                new BytesToGeometryConverter(),
                new GeometryToBytesConverter(),
                new ByteArrayToPointConverter(),
                new PointToByteArrayConverter()
        ));
    }
}