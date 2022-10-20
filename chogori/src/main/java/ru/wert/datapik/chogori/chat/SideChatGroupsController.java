package ru.wert.datapik.chogori.chat;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import ru.wert.datapik.client.entity.models.Room;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.chogori.statics.Comparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.wert.datapik.chogori.images.BtnImages.CHATS_IMG;
import static ru.wert.datapik.chogori.images.BtnImages.MAN_IMG;
import static ru.wert.datapik.chogori.application.services.ChogoriServices.*;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;

public class SideChatGroupsController {

    @FXML
    private Button btnAddNewChatGroup;

    @FXML
    private ListView<User> listOfUsers;

    @FXML
    private ListView<Room> listOfChats;

    @FXML
    private Tab tabPaneChats;

    @FXML
    private Tab tabPaneUsers;

    private SideChat chat;

    @FXML
    void initialize(){
        createListOfUsers();
        createListOfChatGroups();

        updateListOfUsers();
        updateListOfGroups();

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
    private void updateListOfGroups() {
        List<Room> groups = new ArrayList<>();
        List<Room> allGroups = CH_ROOMS.findAll();

        if(allGroups != null && !allGroups.isEmpty())
            groups.addAll(allGroups);
        listOfChats.getItems().clear();
        listOfChats.setItems(FXCollections.observableArrayList(groups));

    }


    private void createListOfUsers() {
        tabPaneUsers.setGraphic(new ImageView(MAN_IMG));
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
                                    openChat(userLabel);
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

    private void openChat(Label userLabel){
        Long user1 = CH_CURRENT_USER.getId();
        Long user2 = CH_USERS.findByName(userLabel.getText()).getId();
        String groupName = "#" + Math.min(user1, user2) + "#" + Math.max(user1, user2);
        Room room = createNewChatGroupIfNeeded(groupName);
        chat.showChatTalk(room);
    }

    private ContextMenu createContextMenu(){
        ContextMenu chatGroupContextMenu = new ContextMenu();
        MenuItem openChat = new MenuItem("Написать сообщение");
        openChat.setOnAction(e->{
            Label userLabel = (Label) ((MenuItem)e.getSource()).getParentPopup().getUserData();
            openChat(userLabel);
        });
        chatGroupContextMenu.getItems().add(openChat);

        return chatGroupContextMenu;
    }

    /**
     *
     */
    private Room createNewChatGroupIfNeeded(String groupName) {
        Room room = null;
        room = CH_ROOMS.findByName(groupName);
        if (room == null) {
            Room newRoom = new Room();
            newRoom.setName(groupName);
            newRoom.setCreator(CH_CURRENT_USER);
            newRoom.setRoommates(Collections.singletonList(CH_CURRENT_USER));

            room = CH_ROOMS.save(newRoom);
        }
        return room;
    }


    private void createListOfChatGroups() {
        tabPaneChats.setGraphic(new ImageView(CHATS_IMG));
        Label placeholder = new Label("Ни одного чата\nеще не создано");
        placeholder.setStyle("-fx-text-fill: saddlebrown; -fx-font-size: 14; -fx-font-style: italic; -fx-font-weight: bold");
        listOfChats.setPlaceholder(placeholder);
        listOfChats.setCellFactory(new Callback<ListView<Room>, ListCell<Room>>() {
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
                            userLabel.setText(item.getName());
                            userLabel.setStyle("-fx-font-style: italic");
                            setGraphic(userLabel);
                        }
                    }
                };
                return cell;
            }
        });
    }

}
