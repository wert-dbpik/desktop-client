package ru.wert.tubus.chogori.chat.util;

import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.Roommate;
import ru.wert.tubus.client.entity.models.User;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_ROOMS;
import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

public class ChatMaster {

    public static List<Message> UNREAD_MESSAGES = new ArrayList<>();

    public static String getRoomName(String roomNameDB){
        String finalName = "";
        if(roomNameDB.startsWith("one-to-one:")){
            roomNameDB = roomNameDB.replace("one-to-one:#", "");
            String[] usersId = roomNameDB.split("#", -1);
            for(String id : usersId){
                User user = CH_USERS.findById(Long.parseLong(id));
                if(!user.getId().equals(CH_CURRENT_USER.getId())) {
                    finalName = user.getName();
                    break;
                }
            }
        } else
            finalName = roomNameDB;

        return finalName;

    }

    public static User getSecondUserInOneToOneChat(Room oneToOneRoom) {
        // Проверяем, что имя комнаты не null
        if (oneToOneRoom.getName() == null) {
            throw new IllegalArgumentException("Имя комнаты не может быть null.");
        }

        // Убираем префикс "one-to-one:" из имени комнаты
        String str = oneToOneRoom.getName().replaceAll("one-to-one:", "");

        // Разделяем строку по символу '#'
        String[] parts = str.split("#");

        // Проверяем, что строка содержит два ID после разделения
        if (parts.length != 3) {
            throw new IllegalArgumentException("Некорректный формат имени комнаты.");
        }

        // Парсим ID пользователей
        Long id1 = Long.parseLong(parts[1]);
        Long id2 = Long.parseLong(parts[2]);

        // Получаем ID текущего пользователя
        Long currentUserId = CH_CURRENT_USER.getId();

        // Сравниваем ID и возвращаем пользователя, чей ID не равен ID текущего пользователя
        if (id1.equals(currentUserId)) {
            return CH_USERS.findById(id2); // Возвращаем второго пользователя
        } else if (id2.equals(currentUserId)) {
            return CH_USERS.findById(id1); // Возвращаем первого пользователя
        } else {
            throw new IllegalArgumentException("Ни один из ID в комнате не соответствует ID текущего пользователя.");
        }
    }

    /**
     * Создает комнату для индивидуального чата между текущим пользователем и указанным собеседником
     * @param secondUser Второй участник чата
     * @return Созданная или найденная комната
     * @throws IllegalArgumentException если второй пользователь null или равен текущему пользователю
     */
    public static Room fetchOneToOneRoom(User secondUser) {
        if (secondUser == null) {
            throw new IllegalArgumentException("Второй пользователь не может быть null");
        }

        if (secondUser.getId().equals(CH_CURRENT_USER.getId())) {
            throw new IllegalArgumentException("Нельзя создать чат с самим собой");
        }

        // Формируем имя комнаты по шаблону one-to-one:#minId#maxId
        Long user1Id = CH_CURRENT_USER.getId();
        Long user2Id = secondUser.getId();
        String roomName = "one-to-one:#" + Math.min(user1Id, user2Id) + "#" + Math.max(user1Id, user2Id);

        // Проверяем существование комнаты
        Room existingRoom = CH_ROOMS.findByName(roomName);
        if (existingRoom != null) {
            return existingRoom;
        }

        // Создаем новую комнату
        Room newRoom = new Room();
        newRoom.setName(roomName);
        newRoom.setCreatorId(CH_CURRENT_USER.getId());

        // Создаем и настраиваем участников чата
        List<Roommate> roommates = new ArrayList<>();

        // Участник 1 - текущий пользователь
        Roommate currentUserRoommate = new Roommate();
        currentUserRoommate.setUserId(user1Id);
        currentUserRoommate.setVisibleForUser(true);
        currentUserRoommate.setMember(true);
        roommates.add(currentUserRoommate);

        // Участник 2 - второй пользователь
        Roommate secondUserRoommate = new Roommate();
        secondUserRoommate.setUserId(user2Id);
        secondUserRoommate.setVisibleForUser(true);
        secondUserRoommate.setMember(true);
        roommates.add(secondUserRoommate);

        newRoom.setRoommates(roommates);

        // Сохраняем комнату на сервере
        Room createdRoom = CH_ROOMS.save(newRoom);
        if (createdRoom == null) {
            throw new RuntimeException("Не удалось создать комнату чата");
        }

        return createdRoom;
    }

}
