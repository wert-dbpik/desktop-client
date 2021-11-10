package ru.wert.datapik.utils.common.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LogicButton extends Button {

    private BooleanProperty logic = new SimpleBooleanProperty(false);
    //БОЛЬШИЕ КНОПКИ
    public static final String CSS_TOOL_BAR_BUTTON = "-fx-font-family: 'Arial Narrow'; -fx-pref-width: 25mm; -fx-pref-height: 20mm; ";
    public static final String CSS_BUTTON_PRESSED = "-fx-background-color: white; -fx-font-family: 'Arial Narrow'; -fx-pref-width: 25mm; -fx-pref-height: 20mm; ";
    public static final String CSS_BUTTON_RELEASED = "-fx-background-color: gray; -fx-text-fill: white; -fx-font-family: 'Arial Narrow';-fx-pref-width: 25mm; -fx-pref-height: 20mm;";
    public static final String CSS_FONT_ARIAL_NARROW = "-fx-font-family: 'Arial Narrow';";
    //МАЛЕНЬКИЕ КНОПКИ
    public static final String CSS_SMALL_BUTTON = "-fx-font-family: 'Arial Narrow';  ";
    public static final String CSS_SMALL_BUTTON_PRESSED = "-fx-background-color: white;";
    public static final String CSS_SMALL_BUTTON_RELEASED = "-fx-background-color: gray; -fx-text-fill: white;";

    /**
     * Большая кнопка
     * @param text String
     * @param image ImageView
     */
    public LogicButton(String text, ImageView image) {
        super(text, image);

        setId("patchButton");
        createBigButton();
    }

    /**
     * Маленькая кнопка
     * @param hint String
     * @param imageView ImageView
     * @param b boolean ничего не делает
     */
    public LogicButton(String hint, ImageView imageView, boolean b) {
        super("", imageView);

        setTooltip(new Tooltip(hint));

        createSmallButton();
    }

    /**
     * Создается большая кнопка
     */
    private void createBigButton(){
        //Кнопку нажали
        setOnMousePressed(e->{
            setStyle(CSS_BUTTON_PRESSED);
        });

        //Кнопку отпустили
        setOnMouseReleased(e->{
            //инвертируем значение logic
            logic.set(!logic.get());
            //меняем стиль кнопки
            if(logic.get())
                setStyle(CSS_BUTTON_RELEASED);
            else
                setStyle(CSS_TOOL_BAR_BUTTON + CSS_FONT_ARIAL_NARROW);

            setFocused(false);
        });
    }

    /**
     * Создается маленькая кнопка
     */
    private void createSmallButton(){
        //Кнопку нажали
        setOnMousePressed(e->{
            setStyle(CSS_SMALL_BUTTON_PRESSED);
        });

        //Кнопку отпустили
        setOnMouseReleased(e->{
            //инвертируем значение logic
            logic.set(!logic.get());
            //меняем стиль кнопки
            if(logic.get())
                setStyle(CSS_SMALL_BUTTON_RELEASED);
            else
                setStyle(CSS_SMALL_BUTTON);

            setFocused(false);
        });
    }

    public BooleanProperty getLogicProperty(){
        return logic;
    }


}
