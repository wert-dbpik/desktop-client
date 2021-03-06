package ru.wert.datapik.utils.common.contextMenuACC;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EOperation;
import ru.wert.datapik.winform.warnings.Warning1;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.common.components.BXPrefix.LAST_PREFIX;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_PREFIXES;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CHECK_ENTERED_NUMBER;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_DEFAULT_PREFIX;
import static ru.wert.datapik.utils.statics.AppStatic.closeWindow;
import static ru.wert.datapik.winform.enums.EOperation.*;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

public abstract class FormView_ACCController<P extends Item>{

    protected EOperation operation;
    protected ItemCommands<P> commands;
    protected IFormView<P> formView;
    protected ItemService<P> service;
    protected P oldItem;
    private Task<Void> manipulation;
    private StackPane spIndicator;
    private boolean multiple;


    public abstract void init(EOperation operation, IFormView<P> formView, ItemCommands<P> commands);
    public abstract ArrayList<String> getNotNullFields();
    public abstract P getNewItem();
    public abstract P getOldItem();
//    public abstract void setChosenItem(TreeItem<P> chosenItem);
    public abstract void fillFieldsOnTheForm(P oldItem);
    public abstract void changeOldItemFields(P oldItem);
    public abstract void showEmptyForm();
    public abstract boolean enteredDataCorrect();


//    public abstract void setFocusedItem(P focusedItem);

    protected void initSuper(EOperation operation, IFormView<P> formView, ItemCommands<P> commands, ItemService<P> service) {
        this.operation = operation;
        this.formView = formView;
        this.commands = commands;
        this.service = service;
//        this.spIndicator = formView.getSpIndicator();

        formView.setAccController(this);

    }

    protected void setInitialValues() {
        if (operation.equals(CHANGE) || operation.equals(COPY) || operation.equals(EOperation.REPLACE)) {
                oldItem = getOldItem();

                fillFieldsOnTheForm(oldItem);
        }
        if (operation.equals(ADD) || operation.equals(EOperation.ADD_FOLDER)) {
            showEmptyForm();
        }
    }

    protected void cancelPressed(Event event){
        closeWindow(event);
    }

    protected boolean okPressed(Event event, StackPane spIndicator, Button btnOk, boolean multiple){

        okPressed(event, spIndicator, btnOk);

        return true;
    }

    protected void okPressed(Event event, StackPane spIndicator, Button btnOk){
        P newItem;
        if(notNullFieldEmpty()) {
            Warning1.create($ATTENTION, "?????????????????? ???????? ???? ??????????????????!", "?????????????????? ?????? ????????");
            return;
        }

        if(CHECK_ENTERED_NUMBER && enteredDataCorrect()){
            newItem = getNewItem();
        } else
            return;

        manipulation = manipulationTask(operation, event, spIndicator, btnOk, newItem);
        if(spIndicator != null) spIndicator.setVisible(true);
        if(btnOk != null) btnOk.setDisable(true);
        new Thread(manipulation).start();
    }



    @NotNull
    private Task<Void> manipulationTask(EOperation operation, Event event, StackPane spIndicator, Button btnOk, P newItem) {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                //????????????????????
                if(operation.equals(ADD)){
                    //???????????????? ???????????????? ?????????????? ?? ???????????????? ????????????????
                    if(formView instanceof Draft_TableView){
                        if(((Draft_TableView) formView).getAccController().draftIsDuplicated((Draft) newItem, null))
                            return null;
                    }
                    else if(isDuplicated(newItem, null)){
                        Platform.runLater(()-> Warning1.create($ATTENTION, $ITEM_EXISTS,$USE_ORIGINAL_ITEM));
                        return null;
                    }
                    commands.add(event, newItem);
                }
                //??????????????????????
                if(operation.equals(COPY)) {
                    if(isDuplicated(newItem, null)) {
                        Platform.runLater(()->AppStatic.closeWindow(event));
                        return null; //?????????????????? ????????, ???????? ?????????? ???????????? ?????????????????? ????????????
                    }
                    commands.add(event, newItem);
                }
                //????????????
                else if(operation.equals(CHANGE)) {
                    if(isDuplicated(newItem, oldItem)){
                        Platform.runLater(()-> Warning1.create($ATTENTION, $ITEM_EXISTS,$USE_ORIGINAL_ITEM));
                        return null;
                    }
                    changeOldItemFields(oldItem);
                    commands.change(event, oldItem);
                }

                Platform.runLater(()->AppStatic.closeWindow(event));
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if(spIndicator != null) spIndicator.setVisible(false);
                if(btnOk != null) btnOk.setDisable(false);
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                if(spIndicator != null) spIndicator.setVisible(false);
                if(btnOk != null) btnOk.setDisable(false);
            };

            @Override
            protected void failed() {
                super.failed();
                if(spIndicator != null) spIndicator.setVisible(false);
                if(btnOk != null) btnOk.setDisable(false);
            }
        };
    }


    /**
     * ???????????????? ???? ???????????????????????? ?????? ?????????????????? ????????????
     * ???????????? ?????????????????????? ???? ?????????????? ?????? ???????????????????????? ??????????????
     * ?????? ???????????????????? ???????????? ???? ???????????????? ??????????????????????
     */
    protected boolean isDuplicated(P newItem, P oldItem){

        //???? ?????????? ?????????????????? ???????????????????? ???????????? (????????????)
        List<P> items = service.findAll();
        if(oldItem != null)items.remove(oldItem);

        //???????????? ?????????? ???????????? ???????????????????????? ???????????? ?? ?????????????????????? ????????????????
        for (Object u : items)
            if (newItem.equals(u)) return true;
        return false;
    }

    /**
     * ???????????????? ???? ?????????????????????????? ?????????????????? ??????????
     */
    public boolean notNullFieldEmpty(){
        ArrayList<String> notNullFields = getNotNullFields();
        for(Object s : notNullFields){
            if (s.toString().equals("")){
                return true;
            }
        }
        return false;
    }

    protected void setComboboxPrefixValue(ComboBox<Prefix> bxPrefix){
        bxPrefix.setValue(LAST_PREFIX == null ? CH_DEFAULT_PREFIX : LAST_PREFIX);
    }

    protected void setComboboxProductGroupValue(ComboBox<ProductGroup> bxGroup){
        //???????????????????? ?????????????????? ???????????? ?????????????? ???? ??????????????
        ProductGroup defaultGroup = CH_PRODUCT_GROUPS.findByName("????????????");
        //???????????????????? ?????????????? ???????????? ?? ?????????? ????????????, ??.??. ?????? ?????? ?????????? ?????? ?????????????? ???? ???????????? GLOBE
        ProductGroup rootGroup = ((CatalogableTable<ProductGroup>)formView).getRootItem().getValue();

        //?????????? ???????????????????? ?????????????? ???????????????????? ????????????, ???????? ?????? ??????????????
        ProductGroup chosenGroup = null;
        TreeItem<ProductGroup> productGroupTreeItem = ((CatalogableTable<ProductGroup>)formView).getUpwardRow();
        if(productGroupTreeItem != null)
            chosenGroup = productGroupTreeItem.getValue();

        ProductGroup finalGroup = null;
        if (chosenGroup == null || chosenGroup.equals(rootGroup)) {
            finalGroup = defaultGroup; //???????? ???? ???????????????? ???? ???????? ????????????
        } else
            finalGroup = chosenGroup;

        bxGroup.setValue(finalGroup);
    }


}
