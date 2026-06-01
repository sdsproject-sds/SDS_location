package org.sds.sdslocation.converter;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKBReader;
import org.sds.sdslocation.exeption.SdsLocationException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * @author Joseph Kibe
 * Created on 5/20/26
 */

@Component
@ReadingConverter
public class ByteArrayToPointConverter implements Converter<byte[], Point> {
    private final WKBReader wkbReader = new WKBReader();

    @Override
    public Point convert(byte[] source) {
        if (source == null || source.length == 0) {
            return null;
        }
        try {
            return (Point) wkbReader.read(source);
        } catch (Exception e) {
            throw new SdsLocationException("Failed to convert byte[] to Point", e);
        }
    }

}
