package com.yunmo.iot.domain.core;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int code;
    private double lat;
    private double lng;
    private String address;
}
