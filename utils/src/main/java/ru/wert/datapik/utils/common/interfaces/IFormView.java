package ru.wert.datapik.utils.common.interfaces;

import javafx.scene.layout.StackPane;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;

import java.util.List;

public interface IFormView<P extends Item> {
    /**
     * Very common function to update view
     */
    void updateView();

    /**
     * Method looks like classic getSelectedItems()
     * @return List<P>
     */
    List<P> getAllSelectedItems();

    /**
     * Связь с контроллером окна добавления/изменения
     * @param accController FormView_ACCController<P>
     */
    void setAccController(FormView_ACCController<P> accController);

    /**
     * Связь с контроллером окна добавления/изменения
     */
    FormView_ACCController<P> getAccController();

    /**
     * Меод возвращает панель с вращающимся progressIndicator
     */
//    StackPane getSpIndicator();



}
