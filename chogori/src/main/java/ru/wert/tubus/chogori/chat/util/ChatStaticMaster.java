package ru.wert.tubus.chogori.chat.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.dialog.dialogController.DialogController;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.Roommate;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.retrofit.GsonConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_ROOMS;
import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

@Slf4j
public class ChatStaticMaster {

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

    /**
     * Обновляет сообщение во всех открытых диалогах в пользовательском интерфейсе.
     * Выполняется в потоке JavaFX через Platform.runLater().
     *
     * @param updateCommand сообщение-команда с обновленными данными (не должно быть null)
     * @throws IllegalArgumentException если updateCommand или его текст равен null
     */
    public static void updateMessageInOpenRooms(Message updateCommand) {
        if (updateCommand == null) {
            throw new IllegalArgumentException("Не удалось обработать null команду обновления");
        }
        if (updateCommand.getText() == null) {
            throw new IllegalArgumentException("Команда обновления содержит null текст");
        }

        Platform.runLater(() -> {
            try {
                Gson gson = GsonConfiguration.createGson();
                Message updatedMessage = gson.fromJson(updateCommand.getText(), Message.class);

                if (updatedMessage == null) {
                    log.error("Не удалось десериализовать сообщение из JSON: {}", updateCommand.getText());
                    return;
                }

                // Безопасная обработка списка открытых комнат
                Optional.ofNullable(DialogController.openRooms)
                        .ifPresent(rooms -> rooms.stream()
                                .filter(Objects::nonNull)
                                .filter(dialog -> dialog.getRoom() != null)
                                .filter(dialog -> updatedMessage.getRoomId() != null)
                                .filter(dialog -> updatedMessage.getRoomId().equals(dialog.getRoom().getId()))
                                .findFirst()
                                .ifPresent(dialog -> {
                                    if (dialog.getRoomMessages() != null) {
                                        dialog.getRoomMessages().replaceAll(msg -> {
                                            if (msg != null && msg.getId() != null &&
                                                    msg.getId().equals(updatedMessage.getId())) {
                                                return updatedMessage; // Заменяем старое сообщение на обновленное
                                            }
                                            return msg;
                                        });

                                        log.debug("Сообщение ID {} обновлено в комнате {}",
                                                updatedMessage.getId(), updatedMessage.getRoomId());
                                    }
                                }));
            } catch (JsonSyntaxException e) {
                log.error("Ошибка синтаксиса JSON при разборе сообщения: {}\nОригинальный JSON: {}",
                        e.getMessage(), updateCommand.getText(), e);
            } catch (Exception e) {
                log.error("Неожиданная ошибка при обработке обновления сообщения: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Удаляет сообщение из всех открытых диалогов в пользовательском интерфейсе.
     * Выполняется в потоке JavaFX через Platform.runLater().
     *
     * @param deleteMessage сообщение-команда с данными для удаления (не должно быть null)
     * @throws IllegalArgumentException если deleteMessage или его текст равен null
     */
    public static void deleteMessageFromOpenRooms(Message deleteMessage) {
        if (deleteMessage == null) {
            throw new IllegalArgumentException("Не удалось обработать null сообщение");
        }
        if (deleteMessage.getText() == null) {
            throw new IllegalArgumentException("Сообщение для удаления содержит null текст");
        }

        Platform.runLater(() -> {
            try {
                Gson gson = GsonConfiguration.createGson();
                Message messageToBeDeleted = gson.fromJson(deleteMessage.getText(), Message.class);

                if (messageToBeDeleted == null) {
                    log.error("Не удалось десериализовать сообщение из JSON: {}", deleteMessage.getText());
                    return;
                }

                // Безопасная обработка списка открытых комнат
                Optional.ofNullable(DialogController.openRooms)
                        .ifPresent(rooms -> rooms.stream()
                                .filter(Objects::nonNull)
                                .filter(dialog -> dialog.getRoom() != null)
                                .filter(dialog -> messageToBeDeleted.getRoomId() != null)
                                .filter(dialog -> messageToBeDeleted.getRoomId().equals(dialog.getRoom().getId()))
                                .findFirst()
                                .ifPresent(dialog -> {
                                    if (dialog.getRoomMessages() != null) {
                                        boolean removed = dialog.getRoomMessages()
                                                .removeIf(msg -> msg != null &&
                                                        msg.getId() != null &&
                                                        msg.getId().equals(messageToBeDeleted.getId()));

                                        if (removed) {
                                            log.debug("Сообщение ID {} удалено из комнаты {}",
                                                    messageToBeDeleted.getId(), messageToBeDeleted.getRoomId());
                                        }
                                    }
                                }));
            } catch (JsonSyntaxException e) {
                log.error("Ошибка синтаксиса JSON при разборе сообщения: {}\nОригинальный JSON: {}",
                        e.getMessage(), deleteMessage.getText(), e);
            } catch (Exception e) {
                log.error("Неожиданная ошибка при обработке удаления сообщения: {}", e.getMessage(), e);
            }
        });
    }
}
