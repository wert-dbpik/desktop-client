package ru.wert.tubus.chogori.chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.serviceQUICK.UserQuickService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_ROOMS;
import static ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler.sideRoomsController;
import static ru.wert.tubus.chogori.images.BtnImages.DOT_BLUE_IMG;
import static ru.wert.tubus.chogori.images.BtnImages.SPACE_IMG;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

@Slf4j
public class SideRoomsController {

    @FXML
    private Button btnAddNewChatGroup;

    @FXML
    private ListView<UserOnline> listOfUsers;

    @FXML
    @Getter
    private ListView<Room> listOfRooms;

    @FXML
    private Tab tabPaneRooms;

    @FXML
    private Tab tabPaneUsers;

    private SideChat chat;

    @Getter
    public ObservableList<UserOnline> usersOnline = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        log.info("Инициализация SideRoomsController");

        createListOfRooms();
        updateListOfRooms();
        tabPaneRooms.setOnSelectionChanged(e -> {
            if (tabPaneRooms.isSelected()) {
                log.debug("Вкладка 'Комнаты' выбрана, обновление списка комнат");
                updateListOfRooms();
            }
        });

        createListOfUsers();
        updateListOfUsers();

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
        if (listOfUsers != null) {
            log.debug("Обновление списка пользователей");
            listOfUsers.refresh();
        }
    }

    public void init(SideChat chat) {
        log.info("Инициализация SideRoomsController с SideChat");
        this.chat = chat;
        sideRoomsController = this;
    }

    /**
     * В методе создается пустой список, в который первым добавляется сам пользователь (написать себе)
     * Затем добавляется отсортированный список остальных пользователей за минусом Гостя
     */
    private void updateListOfUsers() {
        log.debug("Обновление списка пользователей");
        usersOnline.clear();
        for (User user : UserQuickService.users) {
            usersOnline.add(new UserOnline(user, user.isOnline()));
        }

        sortUsersOnlineByName();
        listOfUsers.setItems(usersOnline);
    }

    private void sortUsersOnlineByName() {
        log.debug("Сортировка пользователей по имени");
        String currentUserName = CH_CURRENT_USER.getName(); // Имя текущего пользователя

        usersOnline.sort((userOnline1, userOnline2) -> {
            String name1 = userOnline1.getUser().getName();
            String name2 = userOnline2.getUser().getName();

            // Если первый пользователь - это текущий пользователь, он должен быть выше
            if (name1.equals(currentUserName)) {
                return -1; // userOnline1 будет выше
            }
            // Если второй пользователь - это текущий пользователь, он должен быть выше
            if (name2.equals(currentUserName)) {
                return 1; // userOnline2 будет выше
            }
            // В остальных случаях сортируем по имени без учета регистра
            return name1.compareToIgnoreCase(name2);
        });
    }

    /**
     * В методе создается пустой список, который выводится в ListView, если в базе еще нет чатов
     */
    private void updateListOfRooms() {
        log.debug("Обновление списка комнат");
        List<Room> rooms = new ArrayList<>();
        List<Room> allRooms = CH_ROOMS.findAll();
        for (Room room : allRooms) {
            if (room.getRoommates().contains(CH_CURRENT_USER.getId()))
                rooms.add(room);
        }

        listOfRooms.setItems(FXCollections.observableArrayList(rooms));
    }

    private void createListOfUsers() {
        log.debug("Создание списка пользователей");
        tabPaneUsers.setGraphic(new ImageView(BtnImages.MAN_IMG));
        listOfUsers.setPlaceholder(new Label("Пользователей не найдено"));
        listOfUsers.setCellFactory(new Callback<ListView<UserOnline>, ListCell<UserOnline>>() {
            public ListCell<UserOnline> call(ListView<UserOnline> param) {
                return new ListCell<UserOnline>() {
                    private final ImageView dotImageView = new ImageView();
                    private final Label nameLabel = new Label();

                    {
                        // Устанавливаем размеры для ImageView
                        dotImageView.setFitWidth(10);
                        dotImageView.setFitHeight(10);
                    }

                    @Override
                    public void updateItem(UserOnline userOnline, boolean empty) {
                        super.updateItem(userOnline, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);

                            // Устанавливаем имя пользователя
                            if (userOnline.getUser().equals(CH_CURRENT_USER))
                                nameLabel.setText("Написать себе");
                            else
                                nameLabel.setText(userOnline.getUser().getName());

                             nameLabel.setContextMenu(createContextMenu(userOnline.getUser()));
                             nameLabel.setOnMouseClicked(e -> {
                                if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                                    log.debug("Открытие чата с пользователем: {}", userOnline.getUser().getName());
                                    openOneToOneChat(userOnline.getUser());
                                    e.consume();
                                }
                            });
                            nameLabel.setStyle("-fx-font-size:14; -fx-text-fill:black; -fx-font-style: italic");
                            // Устанавливаем изображение в зависимости от статуса пользователя
                            if (userOnline.isOnline()) {
                                dotImageView.setImage(DOT_BLUE_IMG);
                            } else {
                                dotImageView.setImage(SPACE_IMG);
                            }

                            // Создаем контейнер для изображения и текста
                            HBox hbox = new HBox(dotImageView, nameLabel);
                            hbox.setStyle("-fx-alignment: center-left");
                            hbox.setSpacing(5); // Расстояние между изображением и текстом

                            // Устанавливаем контейнер в ячейку
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    private void openChat(Room room){
        log.debug("Открытие чата {}", room.getName());
        chat.showChatDialog(room);
    }

    private void openOneToOneChat(User interlocutor) {
        Long user1 = CH_CURRENT_USER.getId();
        Long user2 = interlocutor.getId();
        String roomName = "one-to-one:#" + Math.min(user1, user2) + "#" + Math.max(user1, user2);
        Room room = createNewRoomIfNeeded(roomName);
        log.debug("Открытие чата с пользователем: {}", interlocutor.getName());
        chat.showChatDialog(room);
    }

    private ContextMenu createContextMenu(User interlocutor) {
        ContextMenu chatGroupContextMenu = new ContextMenu();
        MenuItem openChat = new MenuItem("Написать сообщение");
        openChat.setOnAction(e -> {
            log.debug("Открытие чата через контекстное меню с пользователем: {}", interlocutor.getName());
            openOneToOneChat(interlocutor);
        });
        chatGroupContextMenu.getItems().add(openChat);

        return chatGroupContextMenu;
    }

    /**
     * Создает новую комнату, если она не существует.
     *
     * @param roomName Имя комнаты.
     * @return Созданная или найденная комната.
     */
    private Room createNewRoomIfNeeded(String roomName) {
        Room room = CH_ROOMS.findByName(roomName);
        if (room == null) {
            log.debug("Создание новой комнаты: {}", roomName);
            Room newRoom = new Room();
            newRoom.setName(roomName);
            newRoom.setCreatorId(CH_CURRENT_USER.getId());
            newRoom.setRoommates(Collections.singletonList(CH_CURRENT_USER.getId()));

            room = CH_ROOMS.save(newRoom);
        }
        return room;
    }

    private void createListOfRooms() {
        log.debug("Создание списка комнат");
        tabPaneRooms.setGraphic(new ImageView(BtnImages.CHATS_IMG));
        Label placeholder = new Label("Ни одного чата\nеще не создано");
        placeholder.setStyle("-fx-text-fill: saddlebrown; -fx-font-size: 14; -fx-font-style: italic; -fx-font-weight: bold");
        listOfRooms.setPlaceholder(placeholder);

        listOfRooms.setCellFactory(new Callback<ListView<Room>, ListCell<Room>>() {
            public ListCell<Room> call(ListView<Room> param) {
                final Label chatLabel = new Label();
                final ListCell<Room> cell = new ListCell<Room>() {
                    @Override
                    public void updateItem(Room room, boolean empty) {
                        super.updateItem(room, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);
                            chatLabel.setText(ChatMaster.getRoomName(room.getName()));
                            chatLabel.setStyle("-fx-font-style: italic; -fx-text-fill: black");
                            chatLabel.setOnMouseClicked(e -> {
                                if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                                    log.debug("Открытие чата в комнате: {}", room.getName());
                                    openChat(room);
                                    e.consume();
                                }
                            });
                            setGraphic(chatLabel);
                        }
                    }
                };
                return cell;
            }
        });
    }
}
