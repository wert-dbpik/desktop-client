package ru.wert.tubus.chogori.application.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BatchApiService {
    @GET("batch/init-data")
    Call<BatchResponse> getInitialData(
            @Query("includeDrafts") boolean includeDrafts,
            @Query("includeProducts") boolean includeProducts,
            @Query("includeFolders") boolean includeFolders,
            @Query("includePassports") boolean includePassports,
            @Query("includeAnyParts") boolean includeAnyParts,
            @Query("includePrefixes") boolean includePrefixes
    );

}
