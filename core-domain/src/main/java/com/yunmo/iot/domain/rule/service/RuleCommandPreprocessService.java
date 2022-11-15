package com.yunmo.iot.domain.rule.service;

import com.google.common.base.Strings;
import com.squareup.javapoet.JavaFile;
import com.yunmo.domain.common.Asserts;
import com.yunmo.domain.common.Problems;
import com.yunmo.iot.domain.core.MessageSchema;
import com.yunmo.iot.domain.core.StringFormat;
import com.yunmo.iot.domain.core.repository.MessageSchemaRepository;
import com.yunmo.iot.domain.rule.*;
import com.yunmo.iot.domain.rule.repository.CommandTemplateRepository;
import com.yunmo.iot.dsl.RuleEvaluatorGenerator;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.apache.flink.table.runtime.generated.CompileUtils;
import org.apache.flink.util.FlinkRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class RuleCommandPreprocessService {
    @Autowired
    MessageSchemaRepository schemaRepository;

    @Autowired
    CommandTemplateRepository commandTemplateRepository;




    @SneakyThrows
    public CommandTemplate preprocess(CommandTemplate template) {
        if (!Strings.isNullOrEmpty(template.getCommand())) {
            byte[] encoded;
            if (template.getFormat().equals(StringFormat.HEX)) {
                encoded = Hex.decodeHex(template.getCommand().replaceAll("\\s",""));
            } else {
                encoded = template.getCommand().getBytes(StandardCharsets.UTF_8);
            }
            template.setCommandEncoded(Base64.getEncoder().encodeToString(encoded));
        }

        return template;
    }

    public RuleSpec preprocess(RuleSpec rule) {
        MessageSchema schema = schemaRepository.findDeletedAlsoById(rule.getSchemaId()).get();

        rule.setGenerated(compileExpression(rule.getExpression(), schema));
        rule.setProductId(schema.getProductId());
        rule.setTelemetryChannel(schema.getChannel());

        if(rule.getAction() != ActionType.NOTIFICATION) {
            Asserts.found(rule.getCommandTemplateId());
            Asserts.found(rule.getDeviceId(), "批量规则只可用于触发通知");
            CommandTemplate template = commandTemplateRepository.findById(rule.getCommandTemplateId()).get();
            rule.setCommandChannel(template.getChannel());
            rule.setCommandEncoded(template.getCommandEncoded());
        }
        return rule;
    }

    public FilterRule preprocess(FilterRule rule) {
        MessageSchema schema = schemaRepository.findDeletedAlsoById(rule.getSchemaId()).get();

        rule.setGenerated(compileExpression(rule.getExpression(), schema));

        rule.setProductId(schema.getProductId());
        rule.setTelemetryChannel(schema.getChannel());
        return rule;
    }

    private GeneratedClassCode compileExpression(String expression, MessageSchema schema) {
        Problems.ensure(!schema.isDeleted(), "通道结构已修改，如若修改规则请删除后重建");
        JavaFile generated = RuleEvaluatorGenerator.generate(schema.getRecordSchema(), expression,
                false);
        String className = generated.packageName + "." + generated.typeSpec.name;

        try {
            CompileUtils.compile(this.getClass().getClassLoader(), className, generated.toString()).getConstructor()
                    .newInstance();
        } catch (FlinkRuntimeException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Problems.hint("表达式编译出错，请检查语法或类型错误");
        }

        return new GeneratedClassCode(generated.toString(), className);
    }

}
