package ru.wert.tubus.client.entity.api_interfaces;

import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.tubus.client.entity.models.AppSettings;

import java.util.List;

public interface AppSettingsApiInterface {

    @GET("settings/id/{id}")
    Call<AppSettings> getById(@Path("id") Long id);

    @GET("settings/name/{name}")
    Call<AppSettings> getAllByName(@Path("name") String name);

    @GET("settings/all")
    Call<List<AppSettings>> getAll();

    @GET("settings/all-by/user-id/{id}")
    Call<List<AppSettings>> getAllByUserId(@Path("id") Long id);

    @GET("settings/all-by-text/{text}")
    Call<List<AppSettings>> getAllByText(@Path("text") String text);

    @POST("settings/create")
    Call<AppSettings> create(@Body AppSettings p);

    @PUT("settings/update")
    Call<Void> update(@Body AppSettings p);

    @DELETE("settings/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);

}
