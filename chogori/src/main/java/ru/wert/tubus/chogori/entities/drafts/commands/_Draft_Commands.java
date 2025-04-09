package ru.wert.tubus.chogori.entities.drafts.commands;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.drafts.DraftsEditorController;
import ru.wert.tubus.chogori.previewer.PreviewerPatchController;
import ru.wert.tubus.chogori.tabs.AppTab;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.entities.drafts.info.DraftInfoPatch;
import ru.wert.tubus.chogori.remarks.RemarksController;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.winform.statics.WinformStatic;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;

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
        AppStatic.openDraftsInNewTabs(tableView.getSelectionModel().getSelectedItems());
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
//        File myFile = new File(WinformStatic.WF_TEMPDIR + File.separator + draft.getId() + "." + draft.getExtension());
        AppStatic.openInOuterApplication(draft);
    }


    public void showRemarks(ActionEvent actionEvent) {
        Passport draftPassport = tableView.getSelectionModel().getSelectedItem().getPassport();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/remarks/remarks.fxml"));
            Parent parent = loader.load();
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            RemarksController controller = loader.getController();
            controller.init(draftPassport);

            String tabName = "> " + draftPassport.toUsefulString();
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName , parent, true,  null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void goToFolderWithTheDraft(ActionEvent event) {
        Draft selectedDraft = tableView.getSelectionModel().getSelectedItems().get(0);
        Folder folder = selectedDraft.getFolder();
        AppTab pane = CH_TAB_PANE.tabIsAvailable("Чертежи");
        DraftsEditorController controller = (DraftsEditorController) pane.getTabController();
        controller.openFolderByName(folder, selectedDraft);
    }
}
