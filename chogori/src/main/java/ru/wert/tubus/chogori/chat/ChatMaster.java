package ru.wert.tubus.chogori.chat;

import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

public class ChatMaster {

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

}
