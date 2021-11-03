package ru.wert.datapik.login;

import ru.wert.datapik.utils.modal.ModalWindow;



public class LoginWindow extends ModalWindow {
/*
    private static PasswordField passwordField;
    private static BooleanProperty agreevation = new SimpleBooleanProperty();

    public static boolean create(){
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(LoginWindow.class.getResource("/login-fxml/login.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);

            passwordField = (PasswordField) parent.lookup("#pasFldPassword");
            Button btnCancel = (Button) parent.lookup("#btnCancel");
            Button btnEnter = (Button) parent.lookup("#btnEnter");

            btnCancel.setOnAction(LoginWindow::cancel);
            btnEnter.setOnAction(LoginWindow::enter);

            setMovingPane(parent);

            Platform.runLater(() -> {
                centerWindow(stage);
            });
            stage.isAlwaysOnTop();

            stage.showAndWait();
        } catch (IOException e){
            e.printStackTrace();
        }
        return agreevation.get();
    }


    *//**
     * Метод закрывает окно при нажатии на ОТМЕНА
     * @param event Event
     *//*
    static void cancel(Event event){
        agreevation.set(false);
        closeWindow(event);
    }

    *//**
     * Метод обрабатывает нажатие кнопки ВОЙТИ
     * @param event Event
     *//*
    static void enter(Event event){
        String pass = passwordField.getText();
        //Вход без регистрации личности
        // 1 - Используется зарезервированный пароль разработчика
        if(pass.equals(DEV_PASS)){
            CURRENT_USER = new User();
            CURRENT_USER.setName("Разработчик");
            CURRENT_USER_GROUP = CURRENT_USER.getUserGroup();
            agreevation.set(true);
            closeWindow(event);
        }
        //Вход с регистрацией личности
        else {
            User user = USERS.findByPassword(pass);
            if(user != null){
                CURRENT_USER = user;
                CURRENT_USER_GROUP = user.getUserGroup();
                agreevation.set(true);
                closeWindow(event);

            } else
                Warning1.create($ATTENTION, $NO_SUCH_USER, $TRY_MORE);
        }

    }*/
}
