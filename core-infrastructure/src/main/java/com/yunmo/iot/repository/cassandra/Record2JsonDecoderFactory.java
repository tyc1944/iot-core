package com.yunmo.iot.repository.cassandra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunmo.iot.decode.*;
import com.yunmo.iot.schema.RecordFormat;
import com.yunmo.iot.schema.Schema;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class Record2JsonDecoderFactory implements DecoderFactory<JsonNode> {
    private static final long serialVersionUID = 4642270927555539201L;
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Decoder create(RecordFormat format, Schema schema) {
        switch (format) {
            case JSON:
                return new Decoder() {
                    @SneakyThrows
                    @Override
                    @SuppressWarnings("unchecked")
                    public <T> T decode(byte[] data, WriterFactory<T> factory) {
                        return (T) objectMapper.readTree(data);
                    }
                };
            case PROTO_BUF:
                return new ProtoDecoder(schema);
            case BINARY:
                return SimpleBinaryDecoder.INSTANCE;
            case CSV_ROW:
                return new CSVDecoder(schema);
            case BIT_FIELD:
                return new BitFieldDecoder(schema);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public Decoder raw() {
        return create(RecordFormat.BINARY, null);
    }
}
