package com.yunmo.iot.domain.core.service;

import com.yunmo.iot.domain.core.MessageSchema;
import com.yunmo.iot.domain.core.Product;
import com.yunmo.iot.domain.core.ProductCreatedEvent;
import com.yunmo.iot.domain.core.repository.MessageSchemaRepository;
import com.yunmo.iot.domain.core.value.MessageSchemaValue;
import com.yunmo.iot.pipe.core.Topics;
import com.yunmo.iot.schema.Field;
import com.yunmo.iot.schema.RecordFormat;
import com.yunmo.iot.schema.Schema;
import com.yunmo.iot.types.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;


@Service
public class ProductChannelService {
    @Autowired
    MessageSchemaRepository schemaRepository;

    public static final Schema BINARY_DEFAULT = new Schema(new Field[]  {
       new Field(0,"hex",new StringType())
    });


    public MessageSchema upsertChannel(Product product, String channel, MessageSchemaValue value) {
        schemaRepository.override(product.getId(), channel);
        SchemaValidator.validate(value.getRecordSchema());

        MessageSchema schema = value.create();
        schema.setChannel(channel);
        schema.setProductId(product.getId());
        schema.setOverrideTime(Instant.EPOCH);

        if(schema.getRecordFormat().equals(RecordFormat.BINARY)) {
            schema.setRecordSchema(BINARY_DEFAULT );
        }
        return schemaRepository.save(schema);
    }


    @Transactional(propagation = REQUIRES_NEW)
    @EventListener
    public void createDefaultChannel(ProductCreatedEvent event) {
        schemaRepository.save(new MessageSchema()
        .setChannel(Topics.DEFAULT_CHANNEL)
                .setProductId(event.getProduct().getId())
                .setRecordFormat(RecordFormat.BINARY)
                .setRecordSchema(BINARY_DEFAULT)
                .setDeleted(false));
    }
}
