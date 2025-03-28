package ru.wert.tubus.chogori.application.services;

import retrofit2.Response;
import ru.wert.tubus.client.retrofit.RetrofitClient;

import java.io.IOException;

public class BatchService {
    private static BatchApiService apiService;

    public static BatchResponse loadInitialData() throws IOException {
        if(apiService == null) {
            apiService = RetrofitClient.getInstance().getRetrofit().create(BatchApiService.class);
        }

        Response<BatchResponse> response = apiService.getInitialData(true, true,true,true,true,true,true).execute();
        if(response.isSuccessful()) {
            return response.body();
        }
        throw new IOException("Batch request failed");
    }
}

