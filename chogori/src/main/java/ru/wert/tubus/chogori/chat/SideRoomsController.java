package ru.wert.tubus.chogori.chat;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import ru.wert.tubus.client.entity.models.Room;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.chogori.statics.Comparators;
import ru.wert.tubus.chogori.images.BtnImages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.*;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

public class SideRoomsController {

    @FXML
    private Button btnAddNewChatGroup;

    @FXML
    private ListView<User> listOfUsers;

    @FXML
    private ListView<Room> listOfRooms;

    @FXML
    private Tab tabPaneRooms;

    @FXML
    private Tab tabPaneUsers;

    private SideChat chat;

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

    }

    public void init(SideChat chat){
        this.chat = chat;
    }

    /**
     * В методе создается пустой список, в который первым добавляется сам пользователь (написать себе)
     * Затем добавляется отсортированный список остальных пользователей за минусом Гостя
     */
    private void updateListOfUsers() {
        ArrayList<User > users = new ArrayList<>();
        users.add(CH_CURRENT_USER);

        List<User> allUsers = CH_USERS.findAll();

        for(int i=0; i< allUsers.size(); i++){
            User u = allUsers.get(i);
            if(u.getName().equals("Гость") || u.equals(CH_CURRENT_USER))
                allUsers.remove(u);
        }
        allUsers.sort(Comparators.usefulStringComparator());
        users.addAll(allUsers);
        listOfUsers.getItems().clear();
        listOfUsers.setItems(FXCollections.observableArrayList(users));
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

        listOfRooms.getItems().clear();
        listOfRooms.setItems(FXCollections.observableArrayList(rooms));

    }


    private void createListOfUsers() {
        tabPaneUsers.setGraphic(new ImageView(BtnImages.MAN_IMG));
        listOfUsers.setPlaceholder(new Label("Пользователей не найдено"));
        listOfUsers.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            public ListCell<User> call(ListView<User> param) {
                final Label userLabel = new Label();
                final ListCell<User> cell = new ListCell<User>() {
                    @Override
                    public void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        }  else {
                            setText(null);
                            if(item.equals(CH_CURRENT_USER))
                                userLabel.setText("Написать себе");
                            else
                                userLabel.setText(item.getName());

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
