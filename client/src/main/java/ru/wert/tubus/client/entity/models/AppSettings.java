package ru.wert.tubus.client.entity.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class AppSettings extends _BaseEntity implements Item{

    private String name; //Наименование группы настроек
    private User user;   //Пользователь, к которому относятся эти настройки
    private Integer monitor; //монитор
    private Integer pdfViewer; //просмотрщик
    private String pathToNormyMK; //путь до папки НормыМК
    private String pathToOpenPDFWith; //путь до папки НормыМК
    private String pathToOpenImageWith; //путь до папки НормыМК
    private String pathToOpenSolidWith; //путь до папки НормыМК
    private boolean showPrefixes; //показывать префиксы
    private boolean showNotificationLine; //показывать строку уведомлений
    private boolean correctDetToAssm; //Исправлять DET на ASSM если номер начинается на 3 или 4
    private Prefix defaultPrefix; //Префикс по умолчанию
    private boolean openDraftsTabOnStart; //Открывать вкладку чертежи при старте приложения
    private boolean validateDecNumbers; //Проверять вводимый децимальный номер
    private String lastPathToDrafts; //Последний использованный путь до папки с чертежами


    @Override
    public String toUsefulString() {
        return null; //Пока нет применения
    }

    public AppSettings makeCopy(){
        AppSettings newSettings = new AppSettings();

        newSettings.setName("");
        newSettings.setUser(null);
        newSettings.setMonitor(getMonitor());
        newSettings.setPdfViewer(getPdfViewer());
        newSettings.setPathToNormyMK(getPathToNormyMK());
        newSettings.setPathToOpenPDFWith(getPathToOpenPDFWith());
        newSettings.setPathToOpenImageWith(getPathToOpenImageWith());
        newSettings.setPathToOpenSolidWith(getPathToOpenSolidWith());
        newSettings.setShowPrefixes(isShowPrefixes());
        newSettings.setShowNotificationLine(isShowNotificationLine());
        newSettings.setCorrectDetToAssm(isCorrectDetToAssm());
        newSettings.setDefaultPrefix(getDefaultPrefix());
        newSettings.setOpenDraftsTabOnStart(isOpenDraftsTabOnStart());
        newSettings.setValidateDecNumbers(isValidateDecNumbers());
        newSettings.setLastPathToDrafts(getLastPathToDrafts());

        return newSettings;
    }
}
