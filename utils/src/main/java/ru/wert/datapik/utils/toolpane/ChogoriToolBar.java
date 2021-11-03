package ru.wert.datapik.utils.toolpane;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.utils.views.BaseCommands;


public class ChogoriToolBar extends ToolBar {

    //ДОБАВИТЬ
    public static Button BTN_ADD;
    public static final Image BTN_ADD_IMG = new Image(ru.wert.datapik.utils.images.BtnImages.class.getResourceAsStream("/chogori-pics/toolpane/add(24x24).png"));
    public static final String BTN_ADD_TOOLTIP = "Добавить чертежи";
    //ДОБАВИТЬ ПАПКУ
    public static Button BTN_ADD_FEW;
    public static final Image BTN_ADD_FEW_IMG = new Image(ru.wert.datapik.utils.images.BtnImages.class.getResourceAsStream("/chogori-pics/toolpane/addFolder(24x24).png"));
    public static final String BTN_ADD_FEW_TOOLTIP = "Добавить папку";
    //ДОБАВИТЬ КОПИРОВАНИЕМ
    public static Button BTN_COPY;
    public static final Image BTN_COPY_IMG = new Image(ru.wert.datapik.utils.images.BtnImages.class.getResourceAsStream("/chogori-pics/toolpane/copy(24x24).png"));
    public static final String BTN_COPY_TOOLTIP = "Добавить копированием";
    //ИЗМЕНИТЬ
    public static Button BTN_CHANGE;
    public static final Image BTN_CHANGE_IMG = new Image(ru.wert.datapik.utils.images.BtnImages.class.getResourceAsStream("/chogori-pics/toolpane/change(24x24).png"));
    public static final String BTN_CHANGE_TOOLTIP = "Изменить";
    //УДАЛИТЬ
    public static Button BTN_DELETE;
    public static final Image BTN_DELETE_IMG = new Image(ru.wert.datapik.utils.images.BtnImages.class.getResourceAsStream("/chogori-pics/toolpane/delete(24x24).png"));
    public static final String BTN_DELETE_TOOLTIP = "Удалить";

    @Getter@Setter
    private BaseCommands editableView;


    public ChogoriToolBar() {
        ADD(); ADD_FEW(); COPY(); CHANGE(); DELETE();
    }

    /**
     * ДОБАВИТЬ ПО ОДНОМУ ЭЛЕМЕНТУ
     */
    public void ADD(){
        BTN_ADD = new Button();
        BTN_ADD.setGraphic(new ImageView(BTN_ADD_IMG));
        BTN_ADD.setTooltip(new Tooltip(BTN_ADD_TOOLTIP));
        BTN_ADD.setOnAction(event -> {
//            new AddItemCommand(editableView).execute();
        });
    }

    /**
     * ДОБАВИТЬ НЕСКОЛЬКО ЭЛЕМЕНТОВ СРАЗУ ПАПКОЙ
     */
    public void ADD_FEW(){
        BTN_ADD_FEW = new Button();
        BTN_ADD_FEW.setGraphic(new ImageView(BTN_ADD_FEW_IMG));
        BTN_ADD_FEW.setTooltip(new Tooltip(BTN_ADD_FEW_TOOLTIP));
        BTN_ADD_FEW.setOnAction(event -> {
//            new AddFolderCommand(editableView).execute();
        });
    }

    /**
     * ДОБАВИТЬ ЭЛЕМЕНТ КОПИРОВАНИЕМ
     */
    public void COPY(){
        BTN_COPY = new Button();
        BTN_COPY.setGraphic(new ImageView(BTN_COPY_IMG));
        BTN_COPY.setTooltip(new Tooltip(BTN_COPY_TOOLTIP));
        BTN_COPY.setOnAction(event -> {
//            new CopyItemCommand(editableView).execute();
        });
    }

    /**
     * ИЗМЕНИТЬ ЭЛЕМЕНТ
     */
    public void CHANGE(){
        BTN_CHANGE = new Button();
        BTN_CHANGE.setGraphic(new ImageView(BTN_CHANGE_IMG));
        BTN_CHANGE.setTooltip(new Tooltip(BTN_CHANGE_TOOLTIP));
        BTN_CHANGE.setOnAction(event -> {
//            new ChangeItemCommand(editableView).execute();
        });
    }

    /**
     * УДАЛИТь ЭЛЕМЕНТЫ
     */
    public void DELETE(){
        BTN_DELETE = new Button();
        BTN_DELETE.setGraphic(new ImageView(BTN_DELETE_IMG));
        BTN_DELETE.setTooltip(new Tooltip(BTN_DELETE_TOOLTIP));
        BTN_DELETE.setOnAction(event -> {
//            new DeleteItemCommand(editableView).execute();
        });
    }
}
