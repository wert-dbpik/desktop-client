package ru.wert.tubus.client.entity.serviceREST;


import retrofit2.Call;
import ru.wert.tubus.client.entity.api_interfaces.MessageApiInterface;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.service_interfaces.IMessageService;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.client.utils.BLlinks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements IMessageService, ItemService<Message> {

    private static MessageService instance;
    private MessageApiInterface api;

    private MessageService() {
        BLlinks.messageService = this;
        api = RetrofitClient.getInstance().getRetrofit().create(MessageApiInterface.class);
    }

    public MessageApiInterface getApi() {
        return api;
    }

    public static MessageService getInstance() {
        if (instance == null)
            instance = new MessageService();
        return instance;
    }

    @Override
    public Message findById(Long id) {
        try {
            Call<Message> call = api.getById(id);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        try {
            Call<List<Message>> call = api.getAll();
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Message> findAllByRoom(Room room) {
        try {
            Call<List<Message>> call = api.getAllByRoomId(room.getId());
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void markMessagesAsDelivered(Room room, Long secondUserId) {
        List<Message> undeliveredMessages = findUndeliveredByRoomAndUser(room, secondUserId);
        undeliveredMessages.forEach(message -> {
            message.setStatus(Message.MessageStatus.DELIVERED);
            update(message);
        });
    }

    @Override
    public List<Message> findUndeliveredByRoomAndUser(Room room, Long secondUserId) {
        List<Message> undeliveredMessages = new ArrayList<>();
        List<Message> messagesByRoom = findAllByRoom(room);
        for(Message m : messagesByRoom){
            if(m.getSenderId().equals(secondUserId) && m.getStatus().equals(Message.MessageStatus.RECEIVED)){
                undeliveredMessages.add(m);
            }
        }
        return undeliveredMessages;
    }

    @Override
    public List<Message> findAllByText(String text) {
        //НЕ ИСПОЛЬЗУЕТСЯ
        return null;
    }

    @Override
    public Message save(Message entity) {
        try {
            Call<Message> call = api.create(entity);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Message entity) {
        try {
            Call<Void> call = api.update(entity);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Message entity) {
        Long id = entity.getId();
        try {
            Call<Void> call = api.deleteById(id);
            return call.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Message> findUndeliveredMessages(Long roomId, Long userId) {
        try {
            // Создаем временный вызов к API сообщений
            MessageApiInterface messageApi = RetrofitClient.getInstance()
                    .getRetrofit()
                    .create(MessageApiInterface.class);

            Call<List<Message>> call = messageApi.findUndeliveredMessages(roomId, userId);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
