package ru.wert.datapik.chogori.calculator;

import javafx.scene.control.TextField;

/**
 * Интерфейс контроллеров FormDetailController b FormAssmController
 * Эти формы вызываются из плашек ДЕТАЛЬ И СБОРКА соответствующе,
 * либо как самостоятельные формы из меню программы
 */
public interface IForm {

    void init(TextField tfName);
}
