package ru.wert.tubus.chogori.chat.roomsController;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

/**
 * Контроллер для управления списком комнат и пользователей в чате.
 * Обеспечивает взаимодействие между интерфейсом и логикой чата.
 */
@Slf4j
public class RoomsController {

    public static final String WRITE_YOURSELF = "Написать себе";

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
    private TabPane chatTabPane;

    @FXML
    @Getter
    private Tab tabPaneRooms;

    @FXML
    @Getter
    private Tab tabPaneUsers;

    private SideChat chat;
    @Getter
    private ObservableList<UserOnline> usersOnline = FXCollections.observableArrayList();

    /**
     * Инициализация контроллера. Создает и обновляет списки комнат и пользователей.
     */
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
                tabRooms.getListOfRooms().refresh();
            }
        });
    }

    /**
     * Добавляет комнату в список, если она отсутствует.
     *
     * @param room Комната для добавления.
     */
    public void addRoomIfAbsent(Room room) {
        if (room != null && !listOfRooms.getItems().contains(room)) {
            log.debug("Добавление комнаты в список: {}", room.getName());
            listOfRooms.getItems().add(room);
        }
    }

    /**
     * Обновляет список пользователей и комнат, а также индикаторы статусов.
     */
    public void refreshListOfUsers() {
        Platform.runLater(()->{
            if (listOfUsers != null) {
                log.debug("Обновление списка пользователей и комнат");
                listOfUsers.refresh();
                listOfRooms.refresh();

                // Явно триггерим обновление индикаторов
                updateAllRoomNameIndicators();
            }
        });
    }

    /**
     * Инициализирует контроллер с указанным чатом.
     *
     * @param chat Объект SideChat для взаимодействия.
     */
    public void init(SideChat chat) {
        log.info("Инициализация SideRoomsController с SideChat");
        this.chat = chat;
        roomsController = this;

        // Добавляем слушатель изменений списка пользователей
        usersOnline.addListener((ListChangeListener<UserOnline>) c -> {
            while (c.next()) {
                if (c.wasUpdated() || c.wasAdded() || c.wasRemoved()) {
                    updateAllRoomNameIndicators();
                }
            }
        });
    }

    /**
     * Обновляет индикаторы статусов для всех комнат.
     */
    private void updateAllRoomNameIndicators() {
        if (chat != null && chat.getDialogController() != null) {
            Room currentRoom = chat.getDialogController().getCurrentOpenRoom();
            if (currentRoom != null) {
                Platform.runLater(() -> {
                    chat.getDialogController().setRoomNameWithOnlineStatus(currentRoom);
                });
            }
        }
    }

    /**
     * Открывает чат для указанной комнаты.
     *
     * @param room Комната, для которой открывается чат.
     */
    public void openChat(Room room){
        log.debug("Открытие чата {}", room.getName());
        chat.showChatDialog(room);
    }

    /**
     * Открывает индивидуальный чат с указанным пользователем.
     *
     * @param secondUser Пользователь, с которым открывается чат.
     */
    public void openOneToOneChat(User secondUser) {
        Room room = fetchOneToOneRoom(secondUser);

        // Делаем чат снова видимым для текущего пользователя
        Room updatedRoom = CH_ROOMS.setUserVisibility(room.getId(), CH_CURRENT_USER.getId(), true);
        if (updatedRoom != null) {
            log.debug("Чат с пользователем {} снова видим для текущего пользователя.", secondUser.getName());
        } else {
            log.error("Не удалось сделать чат видимым для текущего пользователя.");
        }

        log.debug("Открытие чата с пользователем: {}", secondUser.getName());
        chat.showChatDialog(room);
    }

    /**
     * Удаляет комнату из списка и скрывает её для текущего пользователя.
     *
     * @param room Комната для удаления.
     */
    public void deleteRoomFromList(Room room) {
        if (room != null) {
            Long currentUserId = CH_CURRENT_USER.getId();
            Room updatedRoom = CH_ROOMS.setUserVisibility(room.getId(), currentUserId, false);
            if (updatedRoom != null) {
                listOfRooms.getItems().remove(room);
            } else {
                log.error("Ошибка при скрытии комнаты.");
            }
        }
    }

    /**
     * Обрабатывает выход пользователя из чата.
     *
     * @param room Комната, из которой выходит пользователь.
     */
    public void exitFromChat(Room room) {
        if (room != null) {
            Long currentUserId = CH_CURRENT_USER.getId();
            Room updatedRoom = CH_ROOMS.setUserMembership(room.getId(), currentUserId, false);
            if (updatedRoom != null) {
                System.out.println("Пользователь вышел из чата: " + room.getName());
            } else {
                System.out.println("Ошибка при выходе из чата.");
            }
        }
    }
}