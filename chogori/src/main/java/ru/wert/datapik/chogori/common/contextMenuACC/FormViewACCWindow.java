package ru.wert.datapik.chogori.common.contextMenuACC;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import ru.wert.datapik.chogori.common.treeView.Item_TreeView;
import ru.wert.datapik.chogori.entities.folders.Folder_ACCController;
import ru.wert.datapik.chogori.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.interfaces.IFormView;
import ru.wert.datapik.chogori.entities.drafts.Draft_TableView;
import ru.wert.datapik.chogori.entities.product_groups.ProductGroup_ACCController;
import ru.wert.datapik.winform.enums.EOperation;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;

import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;


/**
 * Класс описывает общие свойства модального окна ДОБАВИТЬ-ИЗМЕНИТЬ
 * @param <P> соответствует классу сущности
 */
public class FormViewACCWindow<P extends Item> {

    //Переменная нужна только для таблицы с чертежами,
    //перед созданием окна ДОБАВИТЬ осуществляется выбор добавляемых чертежей
    //И если выбор чертежей не произведен, нажато ОТМЕНА, то окно не должно создаваться
    public static boolean windowCreationAllowed = true;

    //Контроллер диалогового окна ДОБАВИТЬ/ИЗМЕНИТЬ
    private FormView_ACCController<P> accController;
    private TableView<Item> draftsTableView = null;
    private Item_TreeView treeView = null;
    private boolean changesAreInTableView;

    public Parent create(EOperation op, IFormView<P> formView, ItemCommands<P> commands, String res,
                         Item_TreeView treeView){
        this.treeView = treeView;

        return create(op, formView, commands, res);
    }

    public Parent create(EOperation op, IFormView<P> formView, ItemCommands<P> commands, String res,
                         TableView<Item> draftsTableView, boolean changesAreInTableView){
        this.draftsTableView = draftsTableView;
        this.changesAreInTableView = changesAreInTableView;


        return create(op, formView, commands, res);
    }

    public Parent create(EOperation op, IFormView<P> formView, ItemCommands<P> commands, String res) {

        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(FormViewACCWindow.class.getResource(res));
            parent = loader.load();

            parent.getStylesheets().add(FormViewACCWindow.class.getResource("/chogori-css/modal-windows-dark.css").toString());

            accController = loader.getController();

            windowCreationAllowed = true;

            //Если изменения происходят в таблице, не в дереве
            if(accController instanceof ProductGroup_ACCController && changesAreInTableView) {
                ((ProductGroup_ACCController) accController).setChangesInTableView(changesAreInTableView);
                ((ProductGroup_ACCController) accController).setTableView(draftsTableView);
            }
            if (accController instanceof Folder_ACCController) {
                ProductGroup selectedGroup = ((TreeItem<ProductGroup>) treeView.getSelectionModel().getSelectedItem()).getValue();
                ((Folder_ACCController) accController).setSelectedGroup(selectedGroup);
            }

            accController.init(op, formView, commands);

            if(windowCreationAllowed){
                boolean resizable = false;
                if(formView instanceof Draft_TableView) resizable = true;  //Окно добавления чертежей на весь экран
                new WindowDecoration(op.getName(), parent, resizable, WF_MAIN_STAGE);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
        return parent;
    }

    /**
     * Возвращает ссылку на контроллер с диалоговым окном
     * Ссылка необходима для получения переменных данных, используемых далее в методе execute() класса Command
     */
    public FormView_ACCController<P> getAccController(){
        return accController;
    }

}
