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
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.chogori.statics.Comparators;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.client.entity.serviceQUICK.UserQuickService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.*;
import static ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler.printUsersOnline;
import static ru.wert.tubus.chogori.chat.socketwork.ServerMessageHandler.sideRoomsController;
import static ru.wert.tubus.chogori.images.BtnImages.*;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

public class SideRoomsController {

    @FXML
    private Button btnAddNewChatGroup;

    @FXML
    private ListView<UserOnline> listOfUsers;

    @FXML
    private ListView<Room> listOfRooms;

    @FXML
    private Tab tabPaneRooms;

    @FXML
    private Tab tabPaneUsers;

    private SideChat chat;

    @Getter
    public ObservableList<UserOnline> usersOnline = FXCollections.observableArrayList();

    @FXML
    void initialize(){

        createListOfRooms();
        updateListOfRooms();
        tabPaneRooms.setOnSelectionChanged(e->{
            if(tabPaneRooms.isSelected()){
                updateListOfRooms();
            }
        });

        createListOfUsers();
        updateListOfUsers();
        setCellFactory();

        printUsersOnline("SideRoomController #1");

    }

    private void setCellFactory() {
        listOfUsers.setCellFactory(new Callback<ListView<UserOnline>, ListCell<UserOnline>>() {
            @Override
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
                    protected void updateItem(UserOnline userOnline, boolean empty) {
                        super.updateItem(userOnline, empty);

                        if (empty || userOnline == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            // Устанавливаем изображение в зависимости от статуса пользователя
                            if (userOnline.isOnline()) {
                                dotImageView.setImage(DOT_BLUE_IMG);
                            } else {
                                dotImageView.setImage(SPACE_IMG);
                            }

                            // Устанавливаем имя пользователя
                            nameLabel.setText(userOnline.getUser().getName());
                            nameLabel.setStyle("-fx-text-fill: #000001");

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

    public void refreshListOfUsers(){
        if(listOfUsers != null){
            listOfUsers.refresh();
        }
    }


    public void init(SideChat chat){
        this.chat = chat;
        sideRoomsController = this;
    }

    /**
     * В методе создается пустой список, в который первым добавляется сам пользователь (написать себе)
     * Затем добавляется отсортированный список остальных пользователей за минусом Гостя
     */
    private void updateListOfUsers() {
        usersOnline.clear();
        for (User user : UserQuickService.users) {
            usersOnline.add(new UserOnline(user, user.isOnline()));
        }

        sortUsersOnlineByName();
        listOfUsers.setItems(usersOnline);

    }

    private void sortUsersOnlineByName() {
        usersOnline.sort((userOnline1, userOnline2) -> {
            String name1 = userOnline1.getUser().getName();
            String name2 = userOnline2.getUser().getName();
            return name1.compareToIgnoreCase(name2); // Сортировка без учета регистра
        });
    }

    /**
     * В методе создается пустой список, который выводится в ListView, если в базе еще нет чатов
     */
    private void updateListOfRooms() {
        List<Room> rooms = new ArrayList<>();
        List<Room> allRooms = CH_ROOMS.findAll();
        for(Room room : allRooms){
            if(room.getRoommates().contains(CH_CURRENT_USER.getId()))
                rooms.add(room);
        }

        listOfRooms.setItems(FXCollections.observableArrayList(rooms));

    }


    private void createListOfUsers() {
        tabPaneUsers.setGraphic(new ImageView(BtnImages.MAN_IMG));
        listOfUsers.setPlaceholder(new Label("Пользователей не найдено"));
        listOfUsers.setCellFactory(new Callback<ListView<UserOnline>, ListCell<UserOnline>>() {
            public ListCell<UserOnline> call(ListView<UserOnline> param) {
                final Label userLabel = new Label();
                final ListCell<UserOnline> cell = new ListCell<UserOnline>() {
                    @Override
                    public void updateItem(UserOnline item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        }  else {
                            setText(null);
                            if(item.equals(CH_CURRENT_USER))
                                userLabel.setText("Написать себе");
                            else
                                userLabel.setText(item.getUser().getName());

                            userLabel.setContextMenu(createContextMenu());
                            userLabel.setOnMouseClicked(e->{
                                if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                                    openOneToOneChat(userLabel);
                                    e.consume();
                                }
                            });
                            userLabel.setStyle("-fx-font-size:14; -fx-text-fill:black; -fx-font-style: italic");
                            setGraphic(userLabel);
                        }
                    }
                };
                return cell;
            }
        });
    }

    private void openOneToOneChat(Label userLabel){
        Long user1 = CH_CURRENT_USER.getId();
        Long user2 = CH_USERS.findByName(userLabel.getText()).getId();
        String roomName = "one-to-one:#" + Math.min(user1, user2) + "#" + Math.max(user1, user2);
        Room room = createNewRoomIfNeeded(roomName);
        chat.showChatDialog(room);
    }

    private ContextMenu createContextMenu(){
        ContextMenu chatGroupContextMenu = new ContextMenu();
        MenuItem openChat = new MenuItem("Написать сообщение");
        openChat.setOnAction(e->{
            Label userLabel = (Label) ((MenuItem)e.getSource()).getParentPopup().getUserData();
            openOneToOneChat(userLabel);
        });
        chatGroupContextMenu.getItems().add(openChat);

        return chatGroupContextMenu;
    }

    /**
     *
     */
    private Room createNewRoomIfNeeded(String roomName) {
        Room room = null;
        room = CH_ROOMS.findByName(roomName);
        if (room == null) {
            Room newRoom = new Room();
            newRoom.setName(roomName);
            newRoom.setCreatorId(CH_CURRENT_USER.getId());
            newRoom.setRoommates(Collections.singletonList(CH_CURRENT_USER.getId()));

            room = CH_ROOMS.save(newRoom);
        }
        return room;
    }


    private void createListOfRooms() {
        tabPaneRooms.setGraphic(new ImageView(BtnImages.CHATS_IMG));
        Label placeholder = new Label("Ни одного чата\nеще не создано");
        placeholder.setStyle("-fx-text-fill: saddlebrown; -fx-font-size: 14; -fx-font-style: italic; -fx-font-weight: bold");
        listOfRooms.setPlaceholder(placeholder);

        listOfRooms.setCellFactory(new Callback<ListView<Room>, ListCell<Room>>() {
            public ListCell<Room> call(ListView<Room> param) {
                final Label userLabel = new Label();
                final ListCell<Room> cell = new ListCell<Room>() {
                    @Override
                    public void updateItem(Room item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(null);
                            userLabel.setText(ChatMaster.getRoomName(item.getName()));
                            userLabel.setStyle("-fx-font-style: italic; -fx-text-fill: black");
                            userLabel.setOnMouseClicked(e->{
                                if(e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                                    openOneToOneChat(userLabel);
                                    e.consume();
                                }
                            });
                            setGraphic(userLabel);
                        }
                    }
                };
                return cell;
            }
        });
    }

}
