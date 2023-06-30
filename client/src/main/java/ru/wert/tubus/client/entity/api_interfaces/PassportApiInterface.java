package ru.wert.tubus.client.entity.api_interfaces;

import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.tubus.client.entity.models.Passport;

import java.util.List;

public interface PassportApiInterface {

    @GET("passports/id/{id}")
    Call<Passport> getById(@Path("id") Long id);

    @GET("passports/prefix-and-number/{id}/{number}")
    Call<Passport> getByPrefixIdAndNumber(@Path("id") Long id, @Path("number") String number);

    @GET("passports/all")
    Call<List<Passport>> getAll();

    @GET("passports/all-by-name/{name}")
    Call<List<Passport>> getAllByName(@Path("name") String name);

    @GET("passports/all-by-text/{text}")
    Call<List<Passport>> getAllByText(@Path("text") String text);

    @POST("passports/create")
    Call<Passport> create(@Body Passport entity);

    @PUT("passports/update")
    Call<Void> update(@Body Passport entity);

    @DELETE("passports/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);

}
