package ru.wert.tubus.client.entity.api_interfaces;

import retrofit2.Call;
import retrofit2.http.*;
import ru.wert.tubus.client.entity.models.Room;

import java.util.List;

public interface RoomApiInterface {

    @GET("rooms/id/{id}")
    Call<Room> getById(@Path("id") Long id);

    @GET("rooms/name/{name}")
    Call<Room> getByName(@Path("name") String name);

    @GET("rooms/all")
    Call<List<Room>> getAll();

    @POST("rooms/create")
    Call<Room> create(@Body Room entity);

    @PUT("rooms/update")
    Call<Void> update(@Body Room entity);

    @DELETE("rooms/delete/{id}")
    Call<Void> deleteById(@Path("id") Long id);

    @PUT("users/add-roommate/{roomId}")
    Call<Room> addRoommates(@Body List<String> userIds, @Path("roomId") Long roomId);

    @PUT("users/remove-roommate/{roomId}")
    Call<Room> removeRoommates(@Body List<String> userIds, @Path("roomId") Long roomId);

    // Новые методы для управления видимостью и участием пользователей в чате

    /**
     * Устанавливает видимость чата для конкретного пользователя.
     *
     * @param roomId   ID комнаты
     * @param userId   ID пользователя
     * @param isVisible Флаг видимости (true/false)
     * @return Обновленная комната
     */
    @PUT("rooms/set-visibility/{roomId}/{userId}")
    Call<Room> setUserVisibility(
            @Path("roomId") Long roomId,
            @Path("userId") Long userId,
            @Query("isVisible") boolean isVisible
    );

    /**
     * Устанавливает статус участия пользователя в чате.
     *
     * @param roomId   ID комнаты
     * @param userId   ID пользователя
     * @param isMember Флаг участия (true/false)
     * @return Обновленная комната
     */
    @PUT("rooms/set-membership/{roomId}/{userId}")
    Call<Room> setUserMembership(
            @Path("roomId") Long roomId,
            @Path("userId") Long userId,
            @Query("isMember") boolean isMember
    );
}
