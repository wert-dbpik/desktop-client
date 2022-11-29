package ru.wert.datapik.chogori.calculator;

import javafx.scene.control.TextField;
import ru.wert.datapik.chogori.calculator.entities.OpAssm;
import ru.wert.datapik.chogori.calculator.entities.OpData;

/**
 * Интерфейс контроллеров FormDetailController и FormAssmController
 * Эти формы вызываются из плашек ДЕТАЛЬ И СБОРКА соответствующе,
 * либо как самостоятельные формы из меню программы
 */
public interface IForm {

    void init(IFormController controller, TextField tfName, OpData opData);
}
