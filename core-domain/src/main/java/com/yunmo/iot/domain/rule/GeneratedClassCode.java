package com.yunmo.iot.domain.rule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneratedClassCode {
    @Column(length = 4000)
    private String source;

    private String className;
}
