package ru.wert.tubus.client.entity.api_interfaces;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import ru.wert.tubus.client.entity.models.CrashReport;

import java.util.List;

public interface CrashReportApiInterface {

    @GET("crash-reports/id/{id}")
    Call<CrashReport> getById(@Path("id") Long id);

    @GET("crash-reports/all")
    Call<List<CrashReport>> getAll();

    @GET("crash-reports/user/{userId}")
    Call<List<CrashReport>> getAllByUserId(@Path("userId") Long userId);

    @GET("crash-reports/device/{device}")
    Call<List<CrashReport>> getAllByDevice(@Path("device") String device);

    @POST("crash-reports/create")
    Call<CrashReport> create(@Body CrashReport entity);

    @PUT("crash-reports/update")
    Call<CrashReport> update(@Body CrashReport entity);

    @DELETE("crash-reports/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);
}
