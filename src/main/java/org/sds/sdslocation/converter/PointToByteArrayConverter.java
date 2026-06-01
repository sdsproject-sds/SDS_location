package org.sds.sdslocation.converter;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKBWriter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * @author Joseph Kibe
 * Created on 5/20/26
 */
@Component
@ReadingConverter
public class PointToByteArrayConverter implements Converter<Point, byte[]> {

    private final WKBWriter wkbWriter = new WKBWriter();

    @Override
    public byte[] convert(Point source) {
        if (source == null) {
            return null;
        }
        try {
            return wkbWriter.write(source);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Point to byte[]", e);
        }
    }
}
