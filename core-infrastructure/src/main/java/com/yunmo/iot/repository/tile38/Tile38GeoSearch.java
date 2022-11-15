package com.yunmo.iot.repository.tile38;

import java.nio.charset.StandardCharsets;

public enum  Tile38GeoSearch {
    NEARBY, INTERSECTS,  WITHIN;

    public final byte[] bytes;

    Tile38GeoSearch() {
        bytes = name().getBytes(StandardCharsets.US_ASCII);
    }

    public byte[] getBytes() {
        return bytes;
    }
}
