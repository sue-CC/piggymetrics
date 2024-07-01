package com.piggymetrics.account.grpc.client;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.User;
import com.piggymetrics.account.grpc.AccountServiceGrpc;
import com.piggymetrics.account.grpc.AccountProto;
import io.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.security.Principal;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

@Component
public class accountGrpcClientImpl implements AccountClient {

    private static final Logger logger = Logger.getLogger(accountGrpcClientImpl.class.getName());
    private final AccountServiceGrpc.AccountServiceBlockingStub accountService;
    private final ManagedChannel channel;

    @Autowired
    public accountGrpcClientImpl(@Value("${order.service.host:localhost}") String host,
                                 @Value("${order.service.port:9090}") int port, CallCredentials callCredentials) {
        this.channel = Grpc.newChannelBuilderForAddress(host, port, InsecureChannelCredentials.create())
                .build();
        this.accountService = AccountServiceGrpc.newBlockingStub(channel).withCallCredentials(callCredentials);
    }


    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.shutdown();
        }
    }

    @Override
    public Account getAccountByName(String accountName) {
        AccountProto.GetAccountRequest request = AccountProto.GetAccountRequest.newBuilder()
                .setName(accountName)
                .build();
        try {
            AccountProto.GetAccountResponse response = accountService.getAccountByName(request);
            return mapToAccount(response);
        } catch (StatusRuntimeException e) {
            // Handle gRPC exception
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account getCurrentAccount(Principal principal) {
        AccountProto.GetCurrentAccountRequest request = AccountProto.GetCurrentAccountRequest.newBuilder()
                .setPrincipalName(principal.getName())
                .build();
        try {
            AccountProto.GetAccountResponse response = accountService.getCurrentAccount(request);
            return mapToAccount(response);
        } catch (StatusRuntimeException e) {
            // Handle gRPC exception
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String saveCurrentAccount(Principal principal, Account account) {
        AccountProto.SaveAccountRequest request = AccountProto.SaveAccountRequest.newBuilder()
                .setPrincipalName(principal.getName())
                .setAccount(mapToGrpcAccount(account))
                .build();
        try {
            AccountProto.SuccessMessage response = accountService.saveCurrentAccount(request);
            return response.getSuccessMessage();
        } catch (StatusRuntimeException e) {
            // Handle gRPC exception
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Account createNewAccount(User user) {
        AccountProto.CreateAccountRequest request = AccountProto.CreateAccountRequest.newBuilder()
                .setPassword(user.getPassword())
                .setUsername(user.getUsername())
                .build();
        try {
            AccountProto.GetAccountResponse response = accountService.createNewAccount(request);
            return mapToAccount(response);
        } catch (StatusRuntimeException e) {
            // Handle gRPC exception
            e.printStackTrace();
            return null;
        }
    }

    private Account mapToAccount(AccountProto.GetAccountResponse response) {
        // Map the gRPC AccountResponse to your Account domain object
        // Implement this method based on your Account domain class structure
        return new Account();
    }

    private AccountProto.Account mapToGrpcAccount(Account account) {
        // Map your Account domain object to the gRPC Account message
        // Implement this method based on your Account domain class structure
        return AccountProto.Account.newBuilder().build();
    }

    // CallCredentials implementation for adding Bearer token
    public static class BearerTokenCallCredentials extends CallCredentials {
        private final String token;

        public BearerTokenCallCredentials(String token) {
            this.token = token;
        }

        @Override
        public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
            appExecutor.execute(() -> {
                try {
                    Metadata headers = new Metadata();
                    Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
                    headers.put(authKey, "Bearer " + token);
                    applier.apply(headers);
                } catch (Exception e) {
                    applier.fail(Status.UNAUTHENTICATED.withCause(e));
                }
            });
        }

        @Override
        public void thisUsesUnstableApi() {
            // Required method, can be left empty
        }
    }
}
