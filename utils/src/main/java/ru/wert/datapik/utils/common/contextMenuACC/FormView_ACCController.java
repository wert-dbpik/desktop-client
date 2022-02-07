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


    public abstract void init(EOperation operation, IFormView<P> formView, ItemCommands<P> commands);
    public abstract ArrayList<String> getNotNullFields();
    public abstract P getNewItem();
    public abstract P getOldItem();
//    public abstract void setChosenItem(TreeItem<P> chosenItem);
    public abstract void fillFieldsOnTheForm(P oldItem);
    public abstract void changeOldItemFields(P oldItem);
    public abstract void showEmptyForm();

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

                System.out.println("old Item = " + oldItem.getName());
                fillFieldsOnTheForm(oldItem);
        }
        if (operation.equals(ADD) || operation.equals(EOperation.ADD_FOLDER)) {
            showEmptyForm();
        }
    }

    protected void cancelPressed(Event event){
        closeWindow(event);
    }

    protected void okPressed(Event event, StackPane spIndicator, Button btnOk){

        if(notNullFieldEmpty()) {
            Warning1.create($ATTENTION, "Некоторые поля не заполнены!", "Заполните все поля");
            return;
        }
        P newItem = getNewItem();

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
                //ДОБАВЛЕНИЕ
                if(operation.equals(ADD)){
                    //Проверка чертежей сложнее и вынесена отдельно
                    if(formView instanceof Draft_TableView){
                        if(((Draft_TableView) formView).getAccController().draftIsDuplicated((Draft) newItem))
                            return null;
                    }
                    else if(isDuplicated(newItem, null)){
                        Platform.runLater(()-> Warning1.create($ATTENTION, $ITEM_EXISTS,$USE_ORIGINAL_ITEM));
                        return null;
                    }
                    commands.add(event, newItem);
                }
                //КОПИРОВАНИЕ
                if(operation.equals(COPY)) {
                    if(isDuplicated(newItem, null)) {
                        Platform.runLater(()->AppStatic.closeWindow(event));
                        return null; //Закрываем окно, если новая запись повторяет старую
                    }
                    commands.add(event, newItem);
                }
                //ЗАМЕНА
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
     * Проверка на дублирование при изменении записи
     * Запись проверяется со списком уже существующих записей
     * Имя изменяемой записи из проверки исключается
     */
    private boolean isDuplicated(P newItem, P oldItem){

        //Из листа удаляется выделенная запись (старая)
        ObservableList<P> items = service.findAll();
        if(oldItem != null)items.remove(oldItem);

        //Теперь новая запись сравнивается только с оставшимися записями
        for (Object u : items)
            if (newItem.equals(u)) return true;
        return false;
    }

    /**
     * Проверка на заполненность ненулевых полей
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
        Prefix defaultPrefix = CH_QUICK_PREFIXES.findByName(CH_DEFAULT_PREFIX);
        bxPrefix.setValue(LAST_PREFIX == null ? defaultPrefix : LAST_PREFIX);
    }

    protected void setComboboxProductGroupValue(ComboBox<ProductGroup> bxGroup){
        //Определяем дефолтную группу изделий на будущее
        ProductGroup defaultGroup = CH_PRODUCT_GROUPS.findByName("Разное");
        //Определяем нулевую группу в корне дерева, т.к. она нам нужна при нажатии на кнопку GLOBE
        ProductGroup rootGroup = ((CatalogableTable<ProductGroup>)formView).getRootItem().getValue();

        //Потом определяем текущую выделенную группу, если она выбрана
        ProductGroup chosenGroup = null;
        TreeItem<ProductGroup> productGroupTreeItem = ((CatalogableTable<ProductGroup>)formView).getChosenCatalogItem();
        if(productGroupTreeItem != null)
            chosenGroup = productGroupTreeItem.getValue();

        ProductGroup finalGroup = null;
        if (chosenGroup == null || chosenGroup.equals(rootGroup)) {
            finalGroup = defaultGroup; //Если не выделена ни одна группа
        } else
            finalGroup = chosenGroup;

        bxGroup.setValue(finalGroup);
    }


}
