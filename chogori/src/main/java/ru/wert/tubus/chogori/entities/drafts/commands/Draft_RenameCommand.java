package ru.wert.tubus.chogori.entities.drafts.commands;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.entities.drafts.Draft_RenameController;
import ru.wert.tubus.winform.window_decoration.WindowDecoration;

import java.io.IOException;

import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class Draft_RenameCommand implements ICommand {

    private Draft_TableView tableView;
    private Passport passport = null;

    /**
     * Конструктор вызывается из контекстного меню таблицы
     * @param tableView Draft_TableView
     */
    public Draft_RenameCommand(Draft_TableView tableView) {
        this.tableView = tableView;
    }

    /**
     * Конструктор вызывается из контекстного меню таблицы
     * @param tableView Draft_TableView
     */
    public Draft_RenameCommand(Draft_TableView tableView, Passport passport) {
        this.tableView = tableView;
        this.passport = passport;
    }


    @Override
    public void execute() {
        Draft renamedDraft;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/drafts/renameDraft.fxml"));
            Parent parent = loader.load();
            Draft_RenameController controller = loader.getController();

            String title;
            if(passport == null) {
                renamedDraft = tableView.getSelectionModel().getSelectedItem();
                controller.init(tableView, renamedDraft);
                title = renamedDraft.getPassport().getName();
            } else {
                controller.init(tableView, passport);
                title = passport.getName();
            }

            new WindowDecoration("Переимнование " + title,
                    parent, false, WF_MAIN_STAGE, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
