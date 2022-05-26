package ru.wert.datapik.client.entity.api_interfaces;

import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.datapik.client.entity.models.ChatGroup;
import ru.wert.datapik.client.entity.models.Coat;

import java.util.List;

public interface ChatGroupApiInterface {

    @GET("chat-groups/id/{id}")
    Call<ChatGroup> getById(@Path("id") Long id);

    @GET("chat-groups/name/{name}")
    Call<ChatGroup> getByName(@Path("name") String name);

    @GET("chat-groups/all")
    Call<List<ChatGroup>> getAll();

    @POST("chat-groups/create")
    Call<ChatGroup> create(@Body ChatGroup entity);

    @PUT("chat-groups/update")
    Call<Void> update(@Body ChatGroup entity);

    @DELETE("chat-groups/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);

}
