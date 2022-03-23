package ru.wert.datapik.utils.entities.drafts.commands;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.entities.drafts.Draft_RenameController;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;

import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/drafts/renameDraft.fxml"));
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
                    parent, false, CH_MAIN_STAGE, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
