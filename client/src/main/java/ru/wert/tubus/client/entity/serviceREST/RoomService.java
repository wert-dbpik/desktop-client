package ru.wert.tubus.client.entity.serviceREST;

import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.RoomApiInterface;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.service_interfaces.IRoomService;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.List;

public class RoomService implements IRoomService, ItemService<Room> {

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
            instance = new RoomService();
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
    public Room addRoommates(List<String> userIds, Room room) {
        try {
            Call<Room> call = api.addRoommates(userIds, room.getId());
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Room removeRoommates(List<String> userIds, Room room) {
        try {
            Call<Room> call = api.removeRoommates(userIds, room.getId());
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

    // Новые методы для управления видимостью и участием пользователей в чате

    /**
     * Устанавливает видимость чата для конкретного пользователя.
     *
     * @param roomId   ID комнаты
     * @param userId   ID пользователя
     * @param isVisible Флаг видимости (true/false)
     * @return Обновленная комната
     */
    public Room setUserVisibility(Long roomId, Long userId, boolean isVisible) {
        try {
            Call<Room> call = api.setUserVisibility(roomId, userId, isVisible);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Устанавливает статус участия пользователя в чате.
     *
     * @param roomId   ID комнаты
     * @param userId   ID пользователя
     * @param isMember Флаг участия (true/false)
     * @return Обновленная комната
     */
    public Room setUserMembership(Long roomId, Long userId, boolean isMember) {
        try {
            Call<Room> call = api.setUserMembership(roomId, userId, isMember);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
