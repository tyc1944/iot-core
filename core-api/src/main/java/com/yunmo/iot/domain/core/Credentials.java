package com.yunmo.iot.domain.core;

import io.genrpc.annotation.ProtoMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ProtoMessage
public class Credentials implements Serializable {

    private CredentialFormat format;

    private String secret;

    public Credentials(CredentialFormat format, String secret) {
        this.format = format;
        this.secret = secret;
    }

    private String crt;
}
