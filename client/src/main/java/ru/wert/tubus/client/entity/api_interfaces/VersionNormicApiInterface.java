package ru.wert.tubus.client.entity.api_interfaces;

import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.tubus.client.entity.models.VersionNormic;

import java.util.List;

public interface VersionNormicApiInterface {

    @GET("versions_normic/id/{id}")
    Call<VersionNormic> getById(@Path("id") Long id);

    @GET("versions_normic/name/{name}")
    Call<VersionNormic> getByName(@Path("name") String name);

    @GET("versions_normic/all")
    Call<List<VersionNormic>> getAll();

    @POST("versions_normic/create")
    Call<VersionNormic> create(@Body VersionNormic p);

    @PUT("versions_normic/update")
    Call<Void> update(@Body VersionNormic p);

    @DELETE("versions_normic/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);

}
