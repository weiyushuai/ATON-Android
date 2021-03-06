package com.juzix.wallet.engine.service;

import com.juzhen.framework.network.ApiResponse;
import com.juzix.wallet.entity.BatchVoteSummaryEntity;
import com.juzix.wallet.entity.BatchVoteTransactionEntity;

import java.util.List;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author matrixelement
 */
public interface VoteService {

    /**
     * 获取统计统计信息
     *
     * @param requestBody
     * @return
     */
    @POST("getBatchVoteSummary")
    Single<Response<ApiResponse<List<BatchVoteSummaryEntity>>>> getBatchVoteSummary(@Body RequestBody requestBody);

    /**
     * 批量获选票交易
     *
     * @param requestBody
     * @return
     */
    @POST("getBatchVoteTransaction")
    Single<Response<ApiResponse<List<BatchVoteTransactionEntity>>>> getBatchVoteTransaction(@Body RequestBody requestBody);

}
