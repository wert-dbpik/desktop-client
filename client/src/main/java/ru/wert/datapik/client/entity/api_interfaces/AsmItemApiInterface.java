package ru.wert.datapik.client.entity.api_interfaces;

import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.datapik.client.entity.models.AsmItem;

import java.util.List;

public interface AsmItemApiInterface {

    @GET("asm-items/id/{id}")
    Call<AsmItem> getById(@Path("id") Long id);

    @GET("asm-items/name/{name}")
    Call<AsmItem> getByName(@Path("name") String name);

    @GET("asm-items/all")
    Call<List<AsmItem>> getAll();

    @POST("asm-items/create")
    Call<AsmItem> create(@Body AsmItem entity);

    @PUT("asm-common/update")
    Call<Void> update(@Body AsmItem entity);

    @DELETE("asm-items/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);

}
