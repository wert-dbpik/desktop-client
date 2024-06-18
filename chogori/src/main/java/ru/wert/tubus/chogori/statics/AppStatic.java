package ru.wert.tubus.chogori.statics;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import ru.wert.tubus.chogori.components.FileFwdSlash;
import ru.wert.tubus.chogori.pdf.PDFReader;
import ru.wert.tubus.chogori.pdf.readers.PdfJSNewReader;
import ru.wert.tubus.chogori.pdf.readers.PdfJSOldReader;
import ru.wert.tubus.chogori.previewer.PreviewerPatch;
import ru.wert.tubus.chogori.previewer.PreviewerPatchController;
import ru.wert.tubus.client.entity.models.AppLog;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Prefix;
import ru.wert.tubus.client.entity.models.VersionDesktop;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;
import ru.wert.tubus.winform.enums.EDraftStatus;
import ru.wert.tubus.winform.enums.EDraftType;
import ru.wert.tubus.winform.enums.EPDFViewer;
import ru.wert.tubus.winform.statics.WinformStatic;
import ru.wert.tubus.winform.warnings.Warning1;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER_GROUP;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;
import static ru.wert.tubus.winform.statics.WinformStatic.*;
import static ru.wert.tubus.winform.warnings.WarningMessages.*;

//import ru.wert.datapik.chogori.pdf.readers.temp.PdfIcepdfReader;

@Slf4j
public class AppStatic {

//    public static boolean SEARCH_PRO = true;

    public static final String KOMPLEKT = "компл: ";
    public static final double CHAT_WIDTH = 250; //первоначальная ширина чата
    public static String BASE_URL = RetrofitClient.baseUrl;

    public static final String DEC_NUMBER = "\\d{6}[.]\\d{3}";// XXXXXX.XXX
    public static final String DEC_NUMBER_WITH_EXT = "\\d{6}[.]\\d{3}[-]\\d{2,3}";// XXXXXX.XXX-ХХ(Х)
    public static final String SKETCH_NUMBER = "Э\\d{5}";// ЭХХХХХ
    public static final String SKETCH_NUM_WITH_EXT = "[Э]\\d{5}[-]\\d{2}";// ЭХХХХХ-ХХ

    public static final String UPWARD = "< . . . >";
    public static final char TILDA = '\u02F7';

    public static FileChooser.ExtensionFilter ALLOWED_EXTENSIONS = new FileChooser.ExtensionFilter(
            "PDF, PNG, JPEG, EASM",
            "*.pdf", "*.png", "*.jpg", "*.eprt", "*.easm");

    public static List<String> PDF_EXTENSIONS = Collections.singletonList("pdf");
    public static List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    public static List<String> SOLID_EXTENSIONS = Arrays.asList("eprt", "easm");
    public static List<String> DRAW_EXTENSIONS = Arrays.asList("prt", "sldprt", "asm", "sldasm", "drw", "sldrw", "dxf");

    /**
     * Метод парсит строку формата "yyyy-MM-dd'T'HH:mm:ss" в фотрмат dd.MM.yyyy
     */
    public static  String parseStringToDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date date = format.parse(dateString);
            SimpleDateFormat myFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            return myFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    /**
     * Метод парсит строку формата "yyyy-MM-dd'T'HH:mm:ss" в фотрмат HH:mm"
     */
    public static  String parseStringToTime(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date date = format.parse(dateString);
            SimpleDateFormat myFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return myFormat.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    /**
     * Возвращает текущее время с миллисекундами
     */
    public static String getCurrentTime(){
        Date date = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return df.format(date);
    }


    public static void closeWindow(Event event){
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public static VersionDesktop findCurrentLastAppVersion(){
        List<VersionDesktop> versions = ChogoriServices.CH_VERSIONS_DESKTOP.findAll();
        return versions.get(versions.size() - 1);
    }

    public static PDFReader createPDFViewer(StackPane stackPanePreviewer, Scene scene, EPDFViewer viewer){
        PDFReader pdfReader = null;
        switch(viewer){
            case NEW_PDF_JS: pdfReader = new PdfJSNewReader(stackPanePreviewer); break; //Просмотр новым pdf.js
            case OLD_PDF_JS: pdfReader = new PdfJSOldReader(stackPanePreviewer); break; //Просмотр старым pdf.js
//            default : pdfReader = new PdfIcepdfReader(stackPanePreviewer, scene); //Просмотр IcePDF
        }
        return pdfReader;
    }

    /**
     * Открывает чертеж в предпросмотрщике
     * @param draft Draft
     * @param previewerController PreviewerController
     * @param fixSearchHistory boolean, флаг указывающий, что необходимо обновить историю поиска
     */
    public static void openDraftInPreviewer(Draft draft, PreviewerPatchController previewerController, boolean fixSearchHistory) {
        if (draft != null) {
            PreviewerPatchController.addPreviewToHistory(draft);
//            if(fixSearchHistory)  CH_SEARCH_FIELD.updateSearchHistoryWithPassport(draft.getPassport());
            //Нам нужен id загружаемого файла, так как он совпадает с его именем
            Long fileId = draft.getId();
            //и расширение
            String ext = draft.getExtension();

            //Если файл отсутствует в папке temp/bddrafts, то файл туда загружается из БД
            if(!draftInTempDir(fileId, ext)) {
                boolean res = ChogoriServices.CH_FILES.download("drafts", //Постоянная папка в каталоге для чертежей
                        String.valueOf(fileId), //название скачиваемого файла
                        "." + ext, //расширение скачиваемого файла
                        WF_TEMPDIR.toString(),  //временная папка, куда необходимо скачать файл
                        null); //префикс
                if(res) {
                    log.info("openDraftInPreviewer : файл '{}' загружен c сервера во временную папку", String.valueOf(fileId) + "." + ext);
                } else {
                    log.error("openDraftInPreviewer : файл '{}' не был загружен с сервера", String.valueOf(fileId) + "." + ext);
                    Platform.runLater(()->Warning1.create($ATTENTION, $DRAFT_IS_NOT_AVAILABLE, $MAYBE_IT_IS_CORRUPTED));
                    return;
                }

            }
            //В итоге, загружаем файл из временной папки
            Platform.runLater(()->{
//                if(previewerController != null)
                previewerController.showDraft(draft, new FileFwdSlash(WF_TEMPDIR.toString() + "/" + fileId + "." + ext));
                log.debug("openDraftInPreviewer : " +
                                "Из временной папки загружен файл {}",
                        new FileFwdSlash(WF_TEMPDIR.toString() + "/" + fileId + "." + ext).toStrong());
            });

        } else { //Если чертежа нет, показываем NO IMAGE заглушку
            Platform.runLater(()->{
                try {
                    if(previewerController != null)
                    previewerController.showDraft(null, new File(AppStatic.class.getResource("/chogori-pics/NO_IMAGE.pdf").toURI()));
                } catch (URISyntaxException e) {

                }
            });

        }
    }


    /**
     * Метод открывает список чертежей каждый в отдельной вкладке
     * Допукается список, состоящий из одного чертежа
     */
    public static void openDraftsInNewTabs(List<Draft> chosenDrafts){

        if(chosenDrafts.isEmpty()) return;
        for(Draft d : chosenDrafts){
            PreviewerPatch previewerPatch = new PreviewerPatch().create();
            PreviewerPatchController previewerController = previewerPatch.getController();
            previewerController.initPreviewer(ChogoriSettings.CH_PDF_VIEWER, WF_MAIN_STAGE.getScene());
            previewerController.initPreviewerToolBar(true, false, true, true, false);

            String tabName = d.toUsefulString() +
                    " (" + EDraftType.getDraftTypeById(d.getDraftType()) + "-" + d.getPageNumber() + ")";

            String draftStatus = EDraftStatus.getStatusById(d.getStatus()).getStatusName();
            String statusTime = d.getStatusTime();

            String tabId = d.toUsefulString() +
                    " (" + EDraftType.getDraftTypeById(d.getDraftType()) + "-" + d.getPageNumber() + "/" + draftStatus + " c " + statusTime + ")";

            boolean showTabNow = false;
            if(chosenDrafts.size() == 1) showTabNow = true;

            CH_TAB_PANE.createNewTab(tabId, tabName, previewerPatch.getParent(), showTabNow, null);

            AppStatic.openDraftInPreviewer(d, previewerController, false);

        }
    }



    /**
     * Проверяет наличие файла во временной папке
     * @param fileId Long id файла совпадает с его именем
     * @param ext String расширение файла
     * @return boolean, true - если файл есть в папке
     */
    private static boolean draftInTempDir(Long fileId, String ext) {
        String searchedFileName = fileId.toString() + "." + ext;
        log.debug("draftInTempDir: проверяем наличие чертежа {} во временной папке", searchedFileName);
        try {
            Path drafts = WF_TEMPDIR.toPath();
            List<Path> filesInFolder = Files.walk(drafts)
                    .filter(Files::isRegularFile).collect(Collectors.toList());

            for(Path p : filesInFolder){
                if(p.getFileName().toString().contains(searchedFileName)) {
                    log.debug("draftInTempDir : во временной папке файл {} найден", searchedFileName);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("draftInTempDir : во временной папке файл {} НЕ найден", searchedFileName);
        return false;
    }

    public static void setNodeInAnchorPane(Node node){
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
    }

    /**
     * Получаем префикс из полного децимального номера
     * @param fullDecNumber String
     */
    public static Prefix getPrefixInDecNumber(String fullDecNumber){
        if(fullDecNumber.contains(".")){
            String[] str = fullDecNumber.split(Pattern.quote("."), 2);
            List<Prefix> list = ChogoriServices.CH_QUICK_PREFIXES.findAll();
            for(Prefix p : list)
                if(p.getName().equals(str[0]))
                    return p;

        }
        return null;
    }

    /**
     * Убираем префикс из номера и получаем только цифры в дец номере
     * @param fullDecNumber String
     */
    public static String getShortDecNumber(String fullDecNumber){
        if(fullDecNumber.contains(".")){
            String[] str = fullDecNumber.split(Pattern.quote("."), 2);
            return str[1];
        }
        return fullDecNumber;
    }

    /**
     * Метод создает панель с ProgressIndicator
     * @param spIndicator StackPane
     */
    public static void createSpIndicator(StackPane spIndicator){
        //Создаем прозрачную панель с индикатором
        spIndicator.setAlignment(Pos.CENTER);
        //создаем сам индикатор
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(35.0, 35.0);
        spIndicator.getChildren().addAll(progressIndicator);
        spIndicator.setVisible(false);
    }

    public static File chooseDirectory(Event event, File initialDirectory){
        File newDirectory = new File("C:\\");
        if(initialDirectory.exists() && initialDirectory.isDirectory()) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setInitialDirectory(initialDirectory);
            try {
                newDirectory = chooser.showDialog(WF_MAIN_STAGE);
            } catch (Exception e) {
                chooser.setInitialDirectory(new File("C:\\"));
                newDirectory = chooser.showDialog(WF_MAIN_STAGE);
            }
        }

        return newDirectory;
    }

    public static File chooseFile(Event event, File initialDirectory){
        File file = null;
        if(initialDirectory.exists() && initialDirectory.isDirectory()) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Выберите файл");
            chooser.setInitialDirectory(initialDirectory);
            try {
                file = chooser.showOpenDialog(WF_MAIN_STAGE);
            } catch (Exception e) {
                chooser.setInitialDirectory(new File("C:\\"));
                file = chooser.showOpenDialog(WF_MAIN_STAGE);
            }
        }

        return file;
    }

    public static List<File> chooseManyFile(Event event, File initialDirectory, FileChooser.ExtensionFilter filter){
        List<File> chosenList = new ArrayList<>();
        if(initialDirectory.exists() && initialDirectory.isDirectory()) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Выберите файлы");
            chooser.setInitialDirectory(initialDirectory);
            chooser.setSelectedExtensionFilter(filter);
            try {
                chosenList = chooser.showOpenMultipleDialog(WF_MAIN_STAGE);
            } catch (Exception e) {
                chooser.setInitialDirectory(new File("C:\\"));
                chosenList = chooser.showOpenMultipleDialog(WF_MAIN_STAGE);
            }
        }

        return chosenList;
    }

    /**
     * Метод создает запись лога в базе данных
     * CURRENT_PROJECT_VERSION не определяется при запуске приложения из-под IDE
     */
    public static void createLog(boolean forAdminOnly, String text) {

        if (forAdminOnly && !ChogoriSettings.CH_CURRENT_USER.isLogging()) return;

        ChogoriServices.CH_LOGS.save(new AppLog(
                LocalDateTime.now().toString(),
                forAdminOnly,
                ChogoriSettings.CH_CURRENT_USER,
                0,
                CURRENT_PROJECT_VERSION,
                text
        ));

    }

    /**
     * Метод открывает файл во внешнем приложении
     */
    public static void openInOuterApplication(Draft draft) {
        if (draft == null) return;
        if (!CH_CURRENT_USER_GROUP.isReadDrafts() && //Если нет разрешения на печать файлов
                !draft.getDraftType().equals(EDraftType.IMAGE_3D.getTypeId())) //Если чертеж не 3D
            return;

        AppStatic.createLog(true, format("%s открыл чертеж '%s' во внешней программе",
                CH_CURRENT_USER.toUsefulString(), draft.toUsefulString()));


        File myFile;
        //Если отображается чертеж из БД

        //Если отображается вновь добавляемый файл
        myFile = new File(WinformStatic.WF_TEMPDIR + File.separator +
                draft.getId() + "." + draft.getExtension());

        if (!myFile.exists() || !myFile.isFile()) return;

        String executingFile = null;
        String ext = FilenameUtils.getExtension(myFile.getName());
        if (PDF_EXTENSIONS.contains(ext) && !ChogoriSettings.CH_CURRENT_USER_SETTINGS.getPathToOpenPDFWith().equals(""))
            executingFile = ChogoriSettings.CH_CURRENT_USER_SETTINGS.getPathToOpenPDFWith();
        else if (IMAGE_EXTENSIONS.contains(ext) && !ChogoriSettings.CH_CURRENT_USER_SETTINGS.getPathToOpenImageWith().equals(""))
            executingFile = ChogoriSettings.CH_CURRENT_USER_SETTINGS.getPathToOpenImageWith();
        else if (SOLID_EXTENSIONS.contains(ext) && !ChogoriSettings.CH_CURRENT_USER_SETTINGS.getPathToOpenSolidWith().equals(""))
            executingFile = ChogoriSettings.CH_CURRENT_USER_SETTINGS.getPathToOpenSolidWith();

        if (executingFile != null)
            try {
                Runtime.getRuntime().exec(executingFile + " " + myFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("openInOuterApp : не удалось открыть файл '{}' во внешнем приложении '{}'", myFile.getAbsolutePath(), executingFile);
                Warning1.create("Ошибка",
                        "Не удалось открыть файл во внешнем приложении",
                        "Возможно, программа не предназначена для открытия\n" +
                                "подобного файла, или файл поврежден");
                e.printStackTrace();
            }
        else {
            //Открываем в стандартном приложении
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(myFile);
                } catch (IOException ex) {
                    log.error("openInOuterApp : не удалось открыть файл '{}' в стандартном приложении", myFile.getAbsolutePath());
                    Warning1.create("Ошибка",
                            "Не удалось открыть файл в стандартном приложении",
                            "Возможно ни одно приложение не ассоциировано с\n " +
                                    "с данным файлом, или файл поврежден");
                    ex.printStackTrace();
                }
            }
        }
    }

}
