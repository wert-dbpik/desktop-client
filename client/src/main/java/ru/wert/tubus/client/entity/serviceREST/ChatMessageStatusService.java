package ru.wert.tubus.client.entity.serviceREST;


import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.ChatMessageStatusApiInterface;
import ru.wert.tubus.client.entity.models.ChatMessageStatus;
import ru.wert.tubus.client.entity.service_interfaces.IChatMessageStatusService;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class ChatMessageStatusService implements IChatMessageStatusService, ItemService<ChatMessageStatus> {

    private static ChatMessageStatusService instance;
    private ChatMessageStatusApiInterface api;

    private ChatMessageStatusService() {
        BLlinks.chatMessageStatusService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(ChatMessageStatusApiInterface.class);
    }

    public ChatMessageStatusApiInterface getApi() {
        return api;
    }

    public static ChatMessageStatusService getInstance() {
        if (instance == null)
            return new ChatMessageStatusService();
        return instance;
    }

    @Override
    public ChatMessageStatus findById(Long id) {
        try {
            Call<ChatMessageStatus> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<ChatMessageStatus> findAll() {
        try {
            Call<List<ChatMessageStatus>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChatMessageStatus> findAllByText(String text) {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public ChatMessageStatus save(ChatMessageStatus entity) {
        try {
            Call<ChatMessageStatus> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(ChatMessageStatus entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(ChatMessageStatus entity) {
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
