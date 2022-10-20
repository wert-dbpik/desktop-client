package ru.wert.datapik.client.entity.api_interfaces;

import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.datapik.client.entity.models.Message;

import java.util.List;

public interface MessageApiInterface {

    @GET("chat-messages/id/{id}")
    Call<Message> getById(@Path("id") Long id);

    @GET("chat-messages/all")
    Call<List<Message>> getAll();
    
    @POST("chat-messages/create")
    Call<Message> create(@Body Message entity);

    @PUT("chat-messages/update")
    Call<Void> update(@Body Message entity);

    @DELETE("chat-messages/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);

}
