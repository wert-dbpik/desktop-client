package ru.wert.datapik.utils.statics;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.utils.common.components.FileFwdSlash;
import ru.wert.datapik.utils.previewer.PreviewerPatch;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.utils.views.pdf.PDFReader;
import ru.wert.datapik.utils.views.pdf.readers.PdfJSNewReader;
import ru.wert.datapik.utils.views.pdf.readers.PdfJSOldReader;
import ru.wert.datapik.winform.enums.EDraftType;
import ru.wert.datapik.winform.enums.EPDFViewer;
import ru.wert.datapik.winform.warnings.Warning1;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_PREFIXES;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_PDF_VIEWER;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_TEMPDIR;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_TAB_PANE;
import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

//import ru.wert.datapik.utils.views.pdf.readers.temp.PdfIcepdfReader;

@Slf4j
public class AppStatic {

    public static final char TILDA = '\u02F7';
    public static String CURRENT_ROJECT_VERSION;
    public static String NEWER_PROJECT_VERSION;

    public static void closeWindow(Event event){
        ((Node) event.getSource()).getScene().getWindow().hide();
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
     */
    public static void openDraftInPreviewer(Draft draft, PreviewerPatchController previewerController) {
        if (draft != null) {
            //Нам нужен id загружаемого файла, так как он совпадает с его именем
            Long fileId = draft.getId();
            //и расширение
            String ext = draft.getExtension();

            //Если файл отсутствует в папке temp/bddrafts, то файл туда загружается из БД
            if(!draftInTempDir(fileId, ext)) {
                boolean res = CH_QUICK_DRAFTS.download("drafts", //Постоянная папка в каталоге для чертежей
                        String.valueOf(fileId), //название скачиваемого файла
                        "." + ext, //расширение скачиваемого файла
                        CH_TEMPDIR.toString()); //временная папка, куда необходимо скачать файл
                if(res) {
                    log.info("createSelectionListener : файл '{}' загружен c сервера во временную папку", String.valueOf(fileId) + "." + ext);
                } else {
                    log.error("createSelectionListener : файл '{}' не был загружен с сервера", String.valueOf(fileId) + "." + ext);
                    Warning1.create($ATTENTION, $DRAFT_IS_NOT_AVAILABLE, $MAYBE_IT_IS_CORRUPTED);
                    return;
                }

            }
            //В итоге, загружаем файл из временной папки
            Platform.runLater(()->{
//                if(previewerController != null)
                previewerController.showDraft(draft, new FileFwdSlash(CH_TEMPDIR.toString() + "/" + fileId + "." + ext));
                log.debug("createSelectionListener : " +
                                "Из временной папки загружен файл {}",
                        new FileFwdSlash(CH_TEMPDIR.toString() + "/" + fileId + "." + ext).toStrong());
            });

        } else { //Если чертежа нет, показываем NO IMAGE заглушку
            Platform.runLater(()->{
                try {
                    if(previewerController != null)
                    previewerController.showDraft(null, new File(AppStatic.class.getResource("/utils-pics/NO_IMAGE.pdf").toURI()));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
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
            previewerController.initPreviewer(CH_PDF_VIEWER, CH_MAIN_STAGE.getScene());

            String tabName = d.toUsefulString() +
                    " (" + EDraftType.getDraftTypeById(d.getDraftType()) + "-" + d.getPageNumber() + ")";
            boolean showTabNow = false;
            if(chosenDrafts.size() == 1) showTabNow = true;

            CH_TAB_PANE.createNewTab(tabName, previewerPatch.getParent(), showTabNow, null, null);

            AppStatic.openDraftInPreviewer(d, previewerController);

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
            Path drafts = CH_TEMPDIR.toPath();
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

    public static void centerWindow(Stage window, Boolean fullScreen, int mainMonitor){

        List<Screen> screenList = Screen.getScreens();
        //Если всего один монитор, то открываем на нем
        int monitor = Math.min(mainMonitor, screenList.size() - 1);

        if(fullScreen) {
            window.setWidth(screenList.get(monitor).getBounds().getWidth());
            window.setHeight(screenList.get(monitor).getBounds().getHeight());
        }
        double screenMinX = screenList.get(monitor).getBounds().getMinX();
        double screenMinY = screenList.get(monitor).getBounds().getMinY();
        double screenWidth = screenList.get(monitor).getBounds().getWidth();
        double screenHeight = screenList.get(monitor).getBounds().getHeight();

        window.setX(screenMinX + ((screenWidth - window.getWidth()) / 2));
        window.setY(screenMinY + ((screenHeight - window.getHeight()) / 2));

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
            ObservableList<Prefix> list = CH_QUICK_PREFIXES.findAll();
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

}
