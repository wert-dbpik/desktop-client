package ru.wert.datapik.utils.setteings;

import javafx.scene.input.KeyCode;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.client.entity.models.UserGroup;
import ru.wert.datapik.winform.enums.EPDFViewer;

import java.io.File;
import java.util.List;

public class ChogoriSettings {

    //НАСТРАИВАЕМЫЕ ПЕРЕМЕННЫЕ
    public static int CH_MONITOR;//0 - первый монитор,  1 - второй монитор
    public static String CH_DEFAULT_PREFIX = "ПИК"; //Префикс предприятия
    public static String CH_DEFAULT_MAT_TYPE = "Листовой"; //Расчетный тип материала
    public static String CH_DEFAULT_DENSITY = "сталь"; //Плотность стали
    public static String CH_DEFAULT_PRODUCT_GROUP = "Разное"; //Префикс предприятия
    public static EPDFViewer CH_PDF_VIEWER = EPDFViewer.ICE_PDF; //Просмотрщик PDF файлов

    //ПЕРЕМЕННЫЕ ПРОГРАММЫ
    public static final String CH_DEV_PASS = "888"; //пароль разработчика
    public static User CH_CURRENT_USER; //текущий пользователь
    public static UserGroup CH_CURRENT_USER_GROUP;//группа текущего пользователя
    public static File CH_TEMPDIR; //Директория временного хранения
    public static List<KeyCode> CH_KEYS_NOW_PRESSED; //Массив хранения нажатых клавиш

    //ПОСЛЕДНИЕ ВЫБРАННЫЕ ДАННЫЕ




}
