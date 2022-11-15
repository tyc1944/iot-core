package com.yunmo.iot.domain.core;

import io.genrpc.annotation.ProtoEnum;

@ProtoEnum
public enum CredentialFormat {
    RSA_PEM,
    RAW
}