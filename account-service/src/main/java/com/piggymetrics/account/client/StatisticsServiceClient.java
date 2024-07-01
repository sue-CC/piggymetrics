package com.piggymetrics.account.client;

import com.piggymetrics.account.domain.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "statistics-service", fallback = StatisticsServiceClientFallback.class)
public interface StatisticsServiceClient {

	@RequestMapping(method = RequestMethod.PUT, value = "/statistics/{accountName}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	void updateStatistics(@PathVariable("accountName") String accountName, Account account);
	// request: string name; Account
	// response: empty
	// logic: from the account side, send a PUT request contains accountName, as well as account,
	// the statistics will update the account information using the account as well as the request body of account
	// sever: statistics
	// client: account
	// define statistics.proto
	// implement statisticsGrpcService (updateStatistics) and statisticsGrcpConfig
	// define accountClient class includes the methods offers by statistics
	// implement usage of the offered methods in the account side
}
