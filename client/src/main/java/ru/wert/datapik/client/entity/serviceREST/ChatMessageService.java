package ru.wert.datapik.client.entity.serviceREST;


import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.ChatMessageApiInterface;
import ru.wert.datapik.client.entity.models.ChatMessage;
import ru.wert.datapik.client.entity.service_interfaces.IChatMessageService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class ChatMessageService implements IChatMessageService, ItemService<ChatMessage> {

    private static ChatMessageService instance;
    private ChatMessageApiInterface api;

    private ChatMessageService() {
        BLlinks.chatMessageService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(ChatMessageApiInterface.class);
    }

    public ChatMessageApiInterface getApi() {
        return api;
    }

    public static ChatMessageService getInstance() {
        if (instance == null)
            return new ChatMessageService();
        return instance;
    }

    @Override
    public ChatMessage findById(Long id) {
        try {
            Call<ChatMessage> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChatMessage> findAll() {
        try {
            Call<List<ChatMessage>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChatMessage> findAllByText(String text) {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public ChatMessage save(ChatMessage entity) {
        try {
            Call<ChatMessage> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(ChatMessage entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(ChatMessage entity) {
        Long id = entity.getId();
        try {
            Call<Void> call = api.deleteById(id);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}
