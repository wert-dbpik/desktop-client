package ru.wert.tubus.chogori.setteings;

import javafx.scene.input.KeyCode;
import ru.wert.tubus.client.entity.models.AppSettings;
import ru.wert.tubus.client.entity.models.Prefix;
import ru.wert.tubus.client.entity.models.User;
import ru.wert.tubus.client.entity.models.UserGroup;
import ru.wert.tubus.winform.enums.EPDFViewer;

import java.io.File;
import java.util.List;

public class ChogoriSettings {

    public static boolean CHECK_ENTERED_NUMBER = true; //Проверять правильность введенных номеров

    //НАСТРОЙКИ ПРИЛОЖЕНИЯ
    public static File CH_DEFAULT_PATH_TO_NORMY_MK; //Путь к папке НОРМЫ МК
    public static boolean CH_SHOW_PREFIX; //Показывать префикс
    public static boolean CH_VALIDATE_DEC_NUMBERS; //Показывать префикс
    public static Prefix CH_DEFAULT_PREFIX; //Префикс предприятия

    //НАСТРАИВАЕМЫЕ ПЕРЕМЕННЫЕ
    public static String CH_DEFAULT_MAT_TYPE = "Листовой"; //Расчетный тип материала
    public static String CH_DEFAULT_DENSITY = "сталь"; //Плотность стали
    public static String CH_DEFAULT_PRODUCT_GROUP = "Разное"; //Префикс предприятия
    public static EPDFViewer CH_PDF_VIEWER; //Просмотрщик PDF файлов
    public static int MAX_COUNT_TEMP_FILES = 20; //Просмотрщик PDF файлов


    //ПЕРЕМЕННЫЕ ПРОГРАММЫ
    public static User CH_CURRENT_USER; //текущий пользователь
    public static UserGroup CH_CURRENT_USER_GROUP;//группа текущего пользователя
    public static AppSettings CH_CURRENT_USER_SETTINGS;//группа текущего пользователя
    public static List<KeyCode> CH_KEYS_NOW_PRESSED; //Массив хранения нажатых клавиш

    //ПОСЛЕДНИЕ ВЫБРАННЫЕ ДАННЫЕ




}
