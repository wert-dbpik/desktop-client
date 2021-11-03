package ru.wert.datapik.client.entity.api_interfaces;


import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.datapik.client.entity.models.AnyPartGroup;

import java.util.List;

public interface AnyPartGroupApiInterface {

    @GET("part-groups/id/{id}")
    Call<AnyPartGroup> getById(@Path("id") Long id);

    @GET("part-groups/name/{name}")
    Call<AnyPartGroup> getByName(@Path("name") String name);

    @GET("part-groups/all")
    Call<List<AnyPartGroup>> getAll();

    @GET("part-groups/all-by-text/{text}")
    Call<List<AnyPartGroup>> getAllByText(@Path("text") String text);

    @POST("part-groups/create")
    Call<AnyPartGroup> create(@Body AnyPartGroup entity);

    @PUT("part-groups/update")
    Call<Void> update(@Body AnyPartGroup entity);

    @DELETE("part-groups/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);

}
