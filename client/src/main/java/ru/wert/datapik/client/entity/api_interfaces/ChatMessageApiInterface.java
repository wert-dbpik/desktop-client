package ru.wert.datapik.client.entity.api_interfaces;

import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.datapik.client.entity.models.ChatMessage;
import ru.wert.datapik.client.entity.models.Coat;

import java.util.List;

public interface ChatMessageApiInterface {

    @GET("chat-messages/id/{id}")
    Call<ChatMessage> getById(@Path("id") Long id);

    @GET("chat-messages/all")
    Call<List<ChatMessage>> getAll();
    
    @POST("chat-messages/create")
    Call<ChatMessage> create(@Body ChatMessage entity);

    @PUT("chat-messages/update")
    Call<Void> update(@Body ChatMessage entity);

    @DELETE("chat-messages/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);

}
