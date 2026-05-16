package org.sds.sdslocation.converter;


import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * @author Joseph.Kibe. Created On 16 May 2026 02:08
 */
@Component
@ReadingConverter
public class BytesToGeometryConverter implements Converter<byte[], Geometry> {
    private final WKBReader wkbReader = new WKBReader();

    @Override
    public Geometry convert(byte[] source) {
        if (source == null || source.length == 0) {
            return null;
        }
        try {
            return wkbReader.read(source);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse geometry", e);
        }
    }
}