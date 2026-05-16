package org.sds.sdslocation.converter;


import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKBWriter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

/**
 * @author Joseph.Kibe. Created On 16 May 2026 02:09
 */

@Component
@WritingConverter
public class GeometryToBytesConverter implements Converter<Geometry, byte[]> {
    private final WKBWriter wkbWriter = new WKBWriter();

    @Override
    public byte[] convert(Geometry source) {
        if (source == null) {
            return null;
        }
        try {
            return wkbWriter.write(source);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert geometry to bytes", e);
        }
    }
}