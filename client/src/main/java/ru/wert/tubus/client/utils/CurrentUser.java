package ru.wert.tubus.client.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Tab;
import ru.wert.tubus.client.entity.models.User;

public class CurrentUser {

    private static CurrentUser instance;
    private Tab currentTab;

    private ObjectProperty<User> currentUser = new SimpleObjectProperty(null);

    private CurrentUser() {
    }

    public static CurrentUser getInstance(){
        if(instance == null){
            instance = new CurrentUser();
        }
        return instance;
    }

    public static void setAppSettingsForNewClient(User User){

    }

    public static void saveAppSettingsForOldClient(User User){

    }

    public User getCurrentUser() {
        return currentUser.get();
    }

    public ObjectProperty<User> currentUserProperty() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser.set(currentUser);
    }

    public Tab getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
    }
}
