package com.piggymetrics.statistics.grpc;

import com.piggymetrics.Statistics;
import com.piggymetrics.StatisticsGrpcServiceGrpc;
import com.piggymetrics.statistics.Statistics;
import com.piggymetrics.statistics.StatisticsServiceGrpc;
import com.piggymetrics.statistics.domain.Account;
import com.piggymetrics.statistics.domain.timeseries.DataPoint;
import com.piggymetrics.statistics.service.StatisticsService;
import com.piggymetrics.statistics.service.StatisticsServiceImpl;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class StatisticsGrpcServiceImpl extends StatisticsGrpcServiceGrpc.StatisticsGrpcServiceImplBase {

    private final StatisticsService statisticsService;

     public StatisticsGrpcServiceImpl(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Override
    public void updateStatistics(Statistics.UpdateStatisticsRequest request, StreamObserver<Statistics.UpdateStatisticsResponse> responseObserver) {
        String accountName = request.getName();
        Statistics.Account account = request.getAccount();

        dataPoint.setIncomes(update.get());

//  update:    account.setIncomes(update.getIncomes());
//        account.setExpenses(update.getExpenses());
//        account.setSaving(update.getSaving());
//        account.setNote(update.getNote());
//        account.setLastSeen(new Date());





        statisticsService.save(accountName, update);

        Statistics.UpdateStatisticsResponse = Statistics.UpdateStatisticsResponse.newBuilder();
    }


}
