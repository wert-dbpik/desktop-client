package ru.wert.datapik.utils.entities.drafts.commands;

import com.twelvemonkeys.io.FileUtil;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.entities.drafts.info.DraftInfoPatch;
import ru.wert.datapik.utils.remarks.RemarksController;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.statics.WinformStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER_SETTINGS;
import static ru.wert.datapik.utils.statics.AppStatic.*;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_TAB_PANE;

@Slf4j
public class _Draft_Commands implements ItemCommands<Draft> {

    private String TAG = "_Draft_Commands";
    private final Draft_TableView tableView;

    public _Draft_Commands(Draft_TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void add(Event event, Draft newItem){
        ICommand command = new Draft_AddCommand(newItem, tableView);
        command.execute();
    }

    @Override
    public void copy(Event event){
        System.out.println("added with copy");
    }

    @Override
    public void delete(Event event, List<Draft> items){
        ICommand command = new Draft_DeleteCommand(items, tableView);
        command.execute();
    }

    @Override
    public void change(Event event, Draft item){
        ICommand command = new Draft_ChangeCommand(item, tableView);
        command.execute();
    }

    public void addFromFolder(Event event){
        ICommand command = new Draft_AddFolderCommand(tableView);
        command.execute();
    }

    public void renameDraft(Event event){
        ICommand command = new Draft_RenameCommand(tableView);
        command.execute();
    }

    public void replaceDraft(Event event){
        ICommand command = new Draft_ReplaceCommand(tableView);
        command.execute();
    }

    public void nullifyDraft(Event event){
        ICommand command = new Draft_NullifyCommand(tableView);
        command.execute();
    }

    public void openInTab(Event event){
        AppStatic.openDraftsInNewTabs(tableView.getSelectionModel().getSelectedItems(), tableView);
    }



    public void showInfo(Event event) {
        Draft draft = tableView.getSelectionModel().getSelectedItem();
        new DraftInfoPatch().create(draft, null);
    }

    public void openInOuterApp2(Event event){
        if (Desktop.isDesktopSupported()) {
            Draft draft = tableView.getSelectionModel().getSelectedItem();
            try {
                File myFile = new File(WinformStatic.WF_TEMPDIR + File.separator + draft.getId() + "." + draft.getExtension());
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
            }
        }
    }

    public void openInOuterApp(Event event) {
        Draft draft = tableView.getSelectionModel().getSelectedItem();
        File myFile = new File(WinformStatic.WF_TEMPDIR + File.separator + draft.getId() + "." + draft.getExtension());
        AppStatic.openInOuterApplication(myFile);
    }


    public void showRemarks(ActionEvent actionEvent) {
        Passport draftPassport = tableView.getSelectionModel().getSelectedItem().getPassport();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/remarks/remarks.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            RemarksController controller = loader.getController();
            controller.init(draftPassport);
            CH_TAB_PANE.createNewTab("> " + draftPassport.toUsefulString(), parent, true, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
