package com.piggymetrics.account.grpc;

import io.grpc.*;
import org.springframework.stereotype.Component;

@Component
public class AuthInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {

        String authHeader = metadata.get(Constant.AUTHORIZATION_METADATA_KEY);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Authorization token is missing or invalid"), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        // Validate token
        if (!isValidToken(token)) {
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Invalid token"), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }

        // Continue with the request
        return Contexts.interceptCall(Context.current(), serverCall, metadata, serverCallHandler);
    }

    private boolean isValidToken(String token) {
        // Implement token validation logic here, such as:
        // - Check if the token is in a list of valid tokens
        // - Verify token expiration
        // - Check if the token is properly formatted

        // Example validation (you need to replace this with your actual validation logic):
        // For this example, assume that we have a method to get token info
        // from a token store or authentication server.

        // Check token expiration
        // Retrieve token info from the token store or authentication server
        // Example:
        // TokenInfo tokenInfo = tokenStore.getTokenInfo(token);
        // return tokenInfo != null && !tokenInfo.isExpired();

        return true; // Replace with actual validation
    }
}
