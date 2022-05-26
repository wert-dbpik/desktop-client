package ru.wert.datapik.client.entity.serviceREST;


import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.ChatGroupApiInterface;
import ru.wert.datapik.client.entity.models.ChatGroup;
import ru.wert.datapik.client.entity.service_interfaces.IChatGroupService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class ChatGroupService implements IChatGroupService, ItemService<ChatGroup> {

    private static ChatGroupService instance;
    private ChatGroupApiInterface api;

    private ChatGroupService() {
        BLlinks.chatGroupService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(ChatGroupApiInterface.class);
    }

    public ChatGroupApiInterface getApi() {
        return api;
    }

    public static ChatGroupService getInstance() {
        if (instance == null)
            return new ChatGroupService();
        return instance;
    }

    @Override
    public ChatGroup findById(Long id) {
        try {
            Call<ChatGroup> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ChatGroup findByName(String name) {
        try {
            Call<ChatGroup> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChatGroup> findAll() {
        try {
            Call<List<ChatGroup>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChatGroup> findAllByText(String text) {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public ChatGroup save(ChatGroup entity) {
        try {
            Call<ChatGroup> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(ChatGroup entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(ChatGroup entity) {
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
