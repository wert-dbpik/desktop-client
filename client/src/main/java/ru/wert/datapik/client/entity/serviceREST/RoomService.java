package ru.wert.datapik.client.entity.serviceREST;


import retrofit2.Call;
import ru.wert.datapik.client.entity.api_interfaces.RoomApiInterface;
import ru.wert.datapik.client.entity.models.Room;
import ru.wert.datapik.client.entity.service_interfaces.IChatGroupService;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.retrofit.RetrofitClient;
import ru.wert.datapik.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class RoomService implements IChatGroupService, ItemService<Room> {

    private static RoomService instance;
    private RoomApiInterface api;

    private RoomService() {
        BLlinks.roomService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(RoomApiInterface.class);
    }

    public RoomApiInterface getApi() {
        return api;
    }

    public static RoomService getInstance() {
        if (instance == null)
            return new RoomService();
        return instance;
    }

    @Override
    public Room findById(Long id) {
        try {
            Call<Room> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Room findByName(String name) {
        try {
            Call<Room> call = api.getByName(name);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Room> findAll() {
        try {
            Call<List<Room>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Room> findAllByText(String text) {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public Room save(Room entity) {
        try {
            Call<Room> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Room entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Room entity) {
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
