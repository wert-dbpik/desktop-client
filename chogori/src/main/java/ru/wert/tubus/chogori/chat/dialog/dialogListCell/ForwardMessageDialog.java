package ru.wert.tubus.chogori.chat.dialog.dialogListCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ForwardMessageDialog {

    private final Stage dialog;
    private final ListView<User> usersListView;
    private final Button btnForward;
    private final Button btnCancel;
    public static List<User> selectedUsers = new ArrayList<>();

    /**
     * Создает и показывает модальное окно для пересылки сообщений
     * @param owner родительское окно
     * @param messagesToForward список сообщений для пересылки
     * @param allUsers список всех пользователей системы
     * @param onForward обработчик нажатия кнопки "Переслать"
     */
    public static void show(Window owner, List<Message> messagesToForward, List<User> allUsers, Runnable onForward) {
        new ForwardMessageDialog(owner, messagesToForward, allUsers, onForward).show();
    }

    private ForwardMessageDialog(Window owner, List<Message> messagesToForward, List<User> allUsers, Runnable onForward) {
        dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Переслать сообщения");

        // Фильтруем пользователей: исключаем неактивных и текущего пользователя
        User currentUser = ChogoriSettings.CH_CURRENT_USER;
        ObservableList<User> availableUsers = FXCollections.observableArrayList(
                allUsers.stream()
                        .filter(user -> user.isActive() && !user.equals(currentUser))
                        .collect(Collectors.toList())
        );

        // Создаем ListView с поддержкой множественного выбора
        usersListView = new ListView<>(availableUsers);
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        usersListView.setCellFactory(lv -> new UserListCell());

        // Создаем кнопки
        btnForward = new Button("Переслать");
        btnForward.setOnAction(e -> {
            selectedUsers = new ArrayList<>(usersListView.getSelectionModel().getSelectedItems());
            if (!selectedUsers.isEmpty()) {
                // Здесь должна быть логика пересылки сообщений выбранным пользователям
                onForward.run();
                dialog.close();
            } else {
                showAlert("Не выбраны получатели", "Пожалуйста, выберите хотя бы одного получателя.");
            }
        });

        btnCancel = new Button("Отмена");
        btnCancel.setOnAction(e -> dialog.close());

        // Настраиваем layout
        HBox buttonBox = new HBox(10, btnForward, btnCancel);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-alignment: center-right;");

        Label titleLabel = new Label("Выберите получателей:");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 5 0;");

        VBox content = new VBox(10, titleLabel, usersListView, buttonBox);
        content.setPadding(new Insets(15));
        content.setPrefSize(400, 500);

        dialog.setScene(new javafx.scene.Scene(content));
    }

    private void show() {
        dialog.showAndWait();
    }

    /**
     * Показывает предупреждающее сообщение
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(dialog);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Кастомная ячейка для отображения пользователей в списке
     */
    private static class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            if (empty || user == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(user.toUsefulString());
                // Можно добавить иконку или другую визуализацию
            }
        }
    }
}
