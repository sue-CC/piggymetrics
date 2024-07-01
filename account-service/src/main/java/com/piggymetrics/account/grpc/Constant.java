package com.piggymetrics.account.grpc;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import io.grpc.Context;
import io.grpc.Metadata;

/**
 * Constants definition
 */
final class Constant {

//    static final String REFRESH_SUFFIX = "+1";
    static final String ACCESS_TOKEN = "access-token";
    static final Context.Key<String> CLIENT_ID_CONTEXT_KEY = Context.key("clientId");
    static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER);

    private Constant() {
    }
}
