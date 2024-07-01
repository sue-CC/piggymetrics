package com.piggymetrics.account.grpc.server;

import com.piggymetrics.account.domain.*;
import com.piggymetrics.account.grpc.AccountProto;
import com.piggymetrics.account.grpc.AccountServiceGrpc;
import com.piggymetrics.account.service.AccountService;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AccountGrpcServiceImpl extends AccountServiceGrpc.AccountServiceImplBase {

    @Autowired
    private AccountService accountService;

    @Override
    public void getAccountByName(AccountProto.GetAccountRequest request, StreamObserver<AccountProto.GetAccountResponse> responseObserver) {
        Account account = accountService.findByName(request.getName());
        AccountProto.GetAccountResponse response = AccountProto.GetAccountResponse.newBuilder()
                .setAccount(convertToGrpcAccount(account))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getCurrentAccount(AccountProto.GetCurrentAccountRequest request, StreamObserver<AccountProto.GetAccountResponse> responseObserver) {
        String principal = request.getPrincipalName();
        Account account = accountService.findByName(principal);
        AccountProto.GetAccountResponse response = AccountProto.GetAccountResponse.newBuilder()
                .setAccount(convertToGrpcAccount(account))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void saveCurrentAccount(AccountProto.SaveAccountRequest request, StreamObserver<AccountProto.SuccessMessage> responseObserver) {
        String principal = request.getPrincipalName();
        Account account = accountService.findByName(principal);
        accountService.saveChanges(principal, account);
        AccountProto.SuccessMessage response = AccountProto.SuccessMessage.newBuilder().setSuccessMessage(principal + "updates have been saved!").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createNewAccount(AccountProto.CreateAccountRequest request, StreamObserver<AccountProto.GetAccountResponse> responseObserver) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        AccountProto.GetAccountResponse response = AccountProto.GetAccountResponse.newBuilder()
                .setAccount(convertToGrpcAccount(accountService.create(user)))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private AccountProto.Account convertToGrpcAccount(Account account) {
        return AccountProto.Account.newBuilder()
                .setName(account.getName())
                .setLastSeen(account.getLastSeen().toString())
                .addAllIncomes(account.getIncomes().stream().map(this::convertToGrpcItem).collect(Collectors.toList()))
                .addAllExpenses(account.getExpenses().stream().map(this::convertToGrpcItem).collect(Collectors.toList()))
                .setSaving(convertToGrpcSaving(account.getSaving()))
                .setNote(account.getNote())
                .build();
    }

    private AccountProto.Item convertToGrpcItem(Item item) {
        return AccountProto.Item.newBuilder()
                .setTitle(item.getTitle())
                .setAmount(item.getAmount().toPlainString())
                .setCurrency(convertToGrpcCurrency(item.getCurrency()))
                .setPeriod(convertToGrpcPeriod(item.getPeriod()))
                .build();
    }

    private AccountProto.Saving convertToGrpcSaving(Saving saving) {
        return AccountProto.Saving.newBuilder()
                .setAmount(saving.getAmount().toPlainString())
                .setCurrency(convertToGrpcCurrency(saving.getCurrency()))
                .setInterest(saving.getInterest().toPlainString())
                .setDeposit(saving.getDeposit())
                .setCapitalization(saving.getCapitalization())
                .build();
    }

    private AccountProto.TimePeriod convertToGrpcPeriod(TimePeriod period) {
        switch (period) {
            case YEAR:
                return AccountProto.TimePeriod.YEAR;
            case QUARTER:
                return AccountProto.TimePeriod.QUARTER;
            case MONTH:
                return AccountProto.TimePeriod.MONTH;
            case DAY:
                return AccountProto.TimePeriod.DAY;
            case HOUR:
                return AccountProto.TimePeriod.HOUR;
            default:
                throw new IllegalArgumentException("Unknown currency: " + period);
        }
    }

    private AccountProto.Currency convertToGrpcCurrency(Currency currency) {
        switch (currency) {
            case USD:
                return AccountProto.Currency.USD;
            case EUR:
                return AccountProto.Currency.EUR;
            case RUB:
                return AccountProto.Currency.RUB;
            default:
                throw new IllegalArgumentException("Unknown currency: " + currency);
        }
    }

}
