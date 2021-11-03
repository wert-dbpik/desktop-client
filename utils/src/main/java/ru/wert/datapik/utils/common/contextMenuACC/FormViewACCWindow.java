package ru.wert.datapik.utils.common.contextMenuACC;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.winform.enums.EOperation;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;

import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;


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

    public FormViewACCWindow() {
    }

    public Parent create(EOperation op, IFormView<P> formView, ItemCommands<P> commands, String res) {

        Parent parent = null;
        try {
            FXMLLoader loader = new FXMLLoader(FormViewACCWindow.class.getResource(res));
            parent = loader.load();

            parent.getStylesheets().add(FormViewACCWindow.class.getResource("/utils-css/modal-windows-dark.css").toString());

            accController = loader.getController();

            windowCreationAllowed = true;
            accController.init(op, formView, commands);

            if(windowCreationAllowed) new WindowDecoration(op.getName(), parent, false, CH_MAIN_STAGE);



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
