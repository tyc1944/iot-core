package com.yunmo.iot.domain.core.service;

import com.google.common.base.Preconditions;
import com.yunmo.iot.schema.Field;
import com.yunmo.iot.schema.Schema;
import com.yunmo.iot.schema.SemanticCharacter;

import java.util.Optional;
import java.util.stream.Stream;

public class SchemaValidator {
    public static void validate(Schema schema) {
        schema.validate();
        Stream.of(schema.getFields())
                .forEach(field -> {
                    switch (field.getType().getKind()) {
                        case STRUCT:
                            validate(field.getType().getSchema());
                        default:
                            Preconditions.checkArgument(geoTypeIsNumber(field), "坐标字段的类型类型必须是数值");
                            Preconditions.checkArgument(stringAndBoolOnlyCanBeDiscrete(field), "字符串和布尔类型只能是离散值");
                    }
                });
    }


    public static boolean geoTypeIsNumber(Field field) {
        return Optional.ofNullable(field.getCharacter())
                .map(SemanticCharacter::getGeo)
                .map(geo -> field.getType().getKind().isNumber())
                .orElse(true);
    }

    public static boolean stringAndBoolOnlyCanBeDiscrete(Field field) {
        switch (field.getType().getKind()) {
            case STRING:
            case BOOLEAN:
                return Optional.ofNullable(field.getCharacter())
                        .map(SemanticCharacter::getGather)
                        .map(gather -> SemanticCharacter.Gather.DISCRETE.equals(gather))
                        .orElse(true);
            default:
                return true;
        }
    }
}
