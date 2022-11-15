package com.yunmo.iot.repository.tile38;

import io.lettuce.core.protocol.ProtocolKeyword;

import java.nio.charset.StandardCharsets;

public enum Tile38Command implements ProtocolKeyword {
    SETCHAN,
    DELCHAN,
    SETHOOK,
    DELHOOK
    ;

    public final byte[] bytes;

    Tile38Command() {
        bytes = name().getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }
}
