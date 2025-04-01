package ru.wert.tubus.chogori.chat.roomsController;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.chat.SideChat;
import ru.wert.tubus.chogori.chat.util.UserOnline;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_ROOMS;
import static ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler.roomsController;
import static ru.wert.tubus.chogori.chat.util.ChatStaticMaster.fetchOneToOneRoom;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

@Slf4j
public class RoomsController {

    public static final String WRIGHT_YOURSELF = "Написать себе";

    @FXML
    private Button btnAddNewChatGroup;

    @FXML
    @Getter
    private ListView<UserOnline> listOfUsers;

    @FXML
    @Getter
    private ListView<Room> listOfRooms;

    @FXML
    @Getter
    private Tab tabPaneRooms;

    @FXML
    @Getter
    private Tab tabPaneUsers;

    private SideChat chat;
    @Getter
    private ObservableList<UserOnline> usersOnline = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        log.info("Инициализация SideRoomsController");

        TabRooms tabRooms = new TabRooms(this);
        tabRooms.createListOfRooms();
        tabRooms.updateListOfRooms();

        TabUsers tabUsers = new TabUsers(this);
        tabUsers.createListOfUsers();
        tabUsers.updateListOfUsers();

        tabPaneRooms.setOnSelectionChanged(e -> {
            if (tabPaneRooms.isSelected()) {
                log.debug("Вкладка 'Комнаты' выбрана, обновление списка комнат");
                tabRooms.updateListOfRooms();
            }
        });
    }

    /**
     * Добавляет комнату в список listOfRooms, если она отсутствует.
     *
     * @param room Комната, которую нужно добавить.
     */
    public void addRoomIfAbsent(Room room) {
        if (room != null && !listOfRooms.getItems().contains(room)) {
            log.debug("Добавление комнаты в список: {}", room.getName());
            listOfRooms.getItems().add(room);
        }
    }

    public void refreshListOfUsers() {
        Platform.runLater(()->{
            if (listOfUsers != null) {
                log.debug("Обновление списка пользователей");
                listOfUsers.refresh();
            }
        });
    }

    public void init(SideChat chat) {
        log.info("Инициализация SideRoomsController с SideChat");
        this.chat = chat;
        roomsController = this;
    }


    public void openChat(Room room){
        log.debug("Открытие чата {}", room.getName());

        chat.showChatDialog(room);
    }

    public void openOneToOneChat(User interlocutor) {
        Room room = fetchOneToOneRoom(interlocutor);

        // Делаем чат снова видимым для текущего пользователя
        Room updatedRoom = CH_ROOMS.setUserVisibility(room.getId(), CH_CURRENT_USER.getId(), true);
        if (updatedRoom != null) {
            log.debug("Чат с пользователем {} снова видим для текущего пользователя.", interlocutor.getName());
        } else {
            log.error("Не удалось сделать чат видимым для текущего пользователя.");
        }

        log.debug("Открытие чата с пользователем: {}", interlocutor.getName());
        chat.showChatDialog(room);
    }


    public void deleteRoomFromList(Room room) {
        // Логика для удаления комнаты из списка
        if (room != null) {
            // Получаем ID текущего пользователя
            Long currentUserId = CH_CURRENT_USER.getId();

            // Скрываем комнату для пользователя
            Room updatedRoom = CH_ROOMS.setUserVisibility(room.getId(), currentUserId, false);
            if (updatedRoom != null) {
                // Удаляем комнату из списка
                listOfRooms.getItems().remove(room); // Удаляем комнату из ObservableList
            } else {
                log.error("Ошибка при скрытии комнаты.");
            }
        }
    }

    public void exitFromChat(Room room) {
        // Логика для выхода из чата
        if (room != null) {
            // Предположим, что currentUserId — это ID текущего пользователя
            Long currentUserId = CH_CURRENT_USER.getId(); // Нужно реализовать метод для получения ID текущего пользователя
            Room updatedRoom = CH_ROOMS.setUserMembership(room.getId(), currentUserId, false);
            if (updatedRoom != null) {
                System.out.println("Пользователь вышел из чата: " + room.getName());
                // Дополнительные действия, например, обновление UI
            } else {
                System.out.println("Ошибка при выходе из чата.");
            }
        }
    }
}
