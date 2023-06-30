package ru.wert.tubus.client.entity.api_interfaces;

import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.tubus.client.entity.models.ChatMessageStatus;

import java.util.List;

public interface ChatMessageStatusApiInterface {

    @GET("chat-messages-statuses/id/{id}")
    Call<ChatMessageStatus> getById(@Path("id") Long id);
    
    @GET("chat-messages-statuses/all")
    Call<List<ChatMessageStatus>> getAll();
    
    @POST("chat-messages-statuses/create")
    Call<ChatMessageStatus> create(@Body ChatMessageStatus entity);

    @PUT("chat-messages-statuses/update")
    Call<Void> update(@Body ChatMessageStatus entity);

    @DELETE("chat-messages-statuses/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);

}
