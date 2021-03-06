package ru.wert.datapik.utils.statics;

import com.twelvemonkeys.io.FileUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.AppLog;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.entity.models.VersionDesktop;
import ru.wert.datapik.utils.common.components.FileFwdSlash;
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.previewer.PreviewerPatch;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.utils.views.pdf.PDFReader;
import ru.wert.datapik.utils.views.pdf.readers.PdfJSNewReader;
import ru.wert.datapik.utils.views.pdf.readers.PdfJSOldReader;
import ru.wert.datapik.winform.enums.EDraftType;
import ru.wert.datapik.winform.enums.EPDFViewer;
import ru.wert.datapik.winform.warnings.Warning1;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_TAB_PANE;
import static ru.wert.datapik.winform.statics.WinformStatic.*;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

//import ru.wert.datapik.utils.views.pdf.readers.temp.PdfIcepdfReader;

@Slf4j
public class AppStatic {
    public static String CURRENT_PROJECT_VERSION = "1.4.1"; //???????????? ???????????????????? ?????????????????????? ??????????????
    public static String LAST_VERSION_IN_DB; //?????????????????? ?????????????????? ???????????? ?? ???????? ????????????

    public static final String DEC_NUMBER = "\\d{6}[.]\\d{3}";// XXXXXX.XXX
    public static final String DEC_NUMBER_WITH_EXT = "\\d{6}[.]\\d{3}[-]\\d{2,3}";// XXXXXX.XXX-????(??)
    public static final String SKETCH_NUMBER = "??\\d{5}";// ????????????
    public static final String SKETCH_NUM_WITH_EXT = "[??]\\d{5}[-]\\d{2}";// ????????????-????

    public static final String UPWARD = "< . . . >";
    public static final char TILDA = '\u02F7';

    public static FileChooser.ExtensionFilter ALLOWED_EXTENSIONS = new FileChooser.ExtensionFilter(
            "PDF, PNG, JPEG, EASM",
            "*.pdf", "*.png", "*.jpg", "*.eprt", "*.easm");

    public static List<String> PDF_EXTENSIONS = Collections.singletonList("pdf");
    public static List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    public static List<String> SOLID_EXTENSIONS = Arrays.asList("eprt", "easm");
    public static List<String> DRAW_EXTENSIONS = Arrays.asList("prt", "sldprt", "asm", "sldasm", "drw", "sldrw", "dxf");


    public static void closeWindow(Event event){
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public static VersionDesktop findCurrentLastAppVersion(){
        List<VersionDesktop> versions = CH_VERSIONS_DESKTOP.findAll();
        return versions.get(versions.size() - 1);
    }

    public static PDFReader createPDFViewer(StackPane stackPanePreviewer, Scene scene, EPDFViewer viewer){
        PDFReader pdfReader = null;
        switch(viewer){
            case NEW_PDF_JS: pdfReader = new PdfJSNewReader(stackPanePreviewer); break; //???????????????? ?????????? pdf.js
            case OLD_PDF_JS: pdfReader = new PdfJSOldReader(stackPanePreviewer); break; //???????????????? ???????????? pdf.js
//            default : pdfReader = new PdfIcepdfReader(stackPanePreviewer, scene); //???????????????? IcePDF
        }
        return pdfReader;
    }

    /**
     * ?????????????????? ???????????? ?? ????????????????????????????????
     * @param draft Draft
     * @param previewerController PreviewerController
     */
    public static void openDraftInPreviewer(Draft draft, PreviewerPatchController previewerController) {
        if (draft != null) {
            //?????? ?????????? id ???????????????????????? ??????????, ?????? ?????? ???? ?????????????????? ?? ?????? ????????????
            Long fileId = draft.getId();
            //?? ????????????????????
            String ext = draft.getExtension();

            //???????? ???????? ?????????????????????? ?? ?????????? temp/bddrafts, ???? ???????? ???????? ?????????????????????? ???? ????
            if(!draftInTempDir(fileId, ext)) {
                boolean res = CH_QUICK_DRAFTS.download("drafts", //???????????????????? ?????????? ?? ???????????????? ?????? ????????????????
                        String.valueOf(fileId), //???????????????? ???????????????????????? ??????????
                        "." + ext, //???????????????????? ???????????????????????? ??????????
                        WF_TEMPDIR.toString()); //?????????????????? ??????????, ???????? ???????????????????? ?????????????? ????????
                if(res) {
                    log.info("openDraftInPreviewer : ???????? '{}' ???????????????? c ?????????????? ???? ?????????????????? ??????????", String.valueOf(fileId) + "." + ext);
                } else {
                    log.error("openDraftInPreviewer : ???????? '{}' ???? ?????? ???????????????? ?? ??????????????", String.valueOf(fileId) + "." + ext);
                    Platform.runLater(()->Warning1.create($ATTENTION, $DRAFT_IS_NOT_AVAILABLE, $MAYBE_IT_IS_CORRUPTED));
                    return;
                }

            }
            //?? ??????????, ?????????????????? ???????? ???? ?????????????????? ??????????
            Platform.runLater(()->{
//                if(previewerController != null)
                previewerController.showDraft(draft, new FileFwdSlash(WF_TEMPDIR.toString() + "/" + fileId + "." + ext));
                log.debug("openDraftInPreviewer : " +
                                "???? ?????????????????? ?????????? ???????????????? ???????? {}",
                        new FileFwdSlash(WF_TEMPDIR.toString() + "/" + fileId + "." + ext).toStrong());
            });

        } else { //???????? ?????????????? ??????, ???????????????????? NO IMAGE ????????????????
            Platform.runLater(()->{
                try {
                    if(previewerController != null)
                    previewerController.showDraft(null, new File(AppStatic.class.getResource("/utils-pics/NO_IMAGE.pdf").toURI()));
                } catch (URISyntaxException e) {

                }
            });

        }
    }


    /**
     * ?????????? ?????????????????? ???????????? ???????????????? ???????????? ?? ?????????????????? ??????????????
     * ???????????????????? ????????????, ?????????????????? ???? ???????????? ??????????????
     */
    public static void openDraftsInNewTabs(List<Draft> chosenDrafts, Draft_TableView tableView){

        if(chosenDrafts.isEmpty()) return;
        for(Draft d : chosenDrafts){
            PreviewerPatch previewerPatch = new PreviewerPatch().create();
            PreviewerPatchController previewerController = previewerPatch.getController();
            previewerController.initPreviewer(CH_PDF_VIEWER, WF_MAIN_STAGE.getScene());
            previewerController.initPreviewerToolBar(true, false, true, true, false);

            String tabName = d.toUsefulString() +
                    " (" + EDraftType.getDraftTypeById(d.getDraftType()) + "-" + d.getPageNumber() + ")";
            boolean showTabNow = false;
            if(chosenDrafts.size() == 1) showTabNow = true;

            CH_TAB_PANE.createNewTab(tabName, previewerPatch.getParent(), showTabNow, null, null);

            AppStatic.openDraftInPreviewer(d, previewerController);

        }
    }

    /**
     * ?????????????????? ?????????????? ?????????? ???? ?????????????????? ??????????
     * @param fileId Long id ?????????? ?????????????????? ?? ?????? ????????????
     * @param ext String ???????????????????? ??????????
     * @return boolean, true - ???????? ???????? ???????? ?? ??????????
     */
    private static boolean draftInTempDir(Long fileId, String ext) {
        String searchedFileName = fileId.toString() + "." + ext;
        log.debug("draftInTempDir: ?????????????????? ?????????????? ?????????????? {} ???? ?????????????????? ??????????", searchedFileName);
        try {
            Path drafts = WF_TEMPDIR.toPath();
            List<Path> filesInFolder = Files.walk(drafts)
                    .filter(Files::isRegularFile).collect(Collectors.toList());

            for(Path p : filesInFolder){
                if(p.getFileName().toString().contains(searchedFileName)) {
                    log.debug("draftInTempDir : ???? ?????????????????? ?????????? ???????? {} ????????????", searchedFileName);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("draftInTempDir : ???? ?????????????????? ?????????? ???????? {} ???? ????????????", searchedFileName);
        return false;
    }

    public static void setNodeInAnchorPane(Node node){
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
    }

    /**
     * ???????????????? ?????????????? ???? ?????????????? ???????????????????????? ????????????
     * @param fullDecNumber String
     */
    public static Prefix getPrefixInDecNumber(String fullDecNumber){
        if(fullDecNumber.contains(".")){
            String[] str = fullDecNumber.split(Pattern.quote("."), 2);
            List<Prefix> list = CH_QUICK_PREFIXES.findAll();
            for(Prefix p : list)
                if(p.getName().equals(str[0]))
                    return p;

        }
        return null;
    }

    /**
     * ?????????????? ?????????????? ???? ???????????? ?? ???????????????? ???????????? ?????????? ?? ?????? ????????????
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
     * ?????????? ?????????????? ???????????? ?? ProgressIndicator
     * @param spIndicator StackPane
     */
    public static void createSpIndicator(StackPane spIndicator){
        //?????????????? ???????????????????? ???????????? ?? ??????????????????????
        spIndicator.setAlignment(Pos.CENTER);
        //?????????????? ?????? ??????????????????
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
        File newFile = null;
        if(initialDirectory.exists() && initialDirectory.isDirectory()) {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(initialDirectory);
            try {
                newFile = chooser.showOpenDialog(WF_MAIN_STAGE);
            } catch (Exception e) {
                chooser.setInitialDirectory(new File("C:\\"));
                newFile = chooser.showOpenDialog(WF_MAIN_STAGE);
            }
        }

        return newFile;
    }

    /**
     * ?????????? ?????????????? ???????????? ???????? ?? ???????? ????????????
     * CURRENT_PROJECT_VERSION ???? ???????????????????????? ?????? ?????????????? ???????????????????? ????-?????? IDE
     */
    public static void createLog(boolean forAdminOnly, String text) {

        if (forAdminOnly && !CH_CURRENT_USER.isLogging()) return;

        CH_LOGS.save(new AppLog(
                LocalDateTime.now().toString(),
                forAdminOnly,
                CH_CURRENT_USER,
                0,
                CURRENT_PROJECT_VERSION,
                text
        ));

    }

    /**
     * ?????????? ?????????????????? ???????? ???? ?????????????? ????????????????????
     * @param myFile
     */
    public static void openInOuterApplication(File myFile) {
        String executingFile = null;
        String ext = FileUtil.getExtension(myFile);
        if (PDF_EXTENSIONS.contains(ext) && !CH_CURRENT_USER_SETTINGS.getPathToOpenPDFWith().equals(""))
            executingFile = CH_CURRENT_USER_SETTINGS.getPathToOpenPDFWith();
        else if (IMAGE_EXTENSIONS.contains(ext) && !CH_CURRENT_USER_SETTINGS.getPathToOpenImageWith().equals(""))
            executingFile = CH_CURRENT_USER_SETTINGS.getPathToOpenImageWith();
        else if (SOLID_EXTENSIONS.contains(ext) && !CH_CURRENT_USER_SETTINGS.getPathToOpenSolidWith().equals(""))
            executingFile = CH_CURRENT_USER_SETTINGS.getPathToOpenSolidWith();

        if (executingFile != null)
            try {
                Runtime.getRuntime().exec(executingFile + " " + myFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("openInOuterApp : ???? ?????????????? ?????????????? ???????? '{}' ???? ?????????????? ???????????????????? '{}'", myFile.getAbsolutePath(), executingFile);
                Warning1.create("????????????",
                        "???? ?????????????? ?????????????? ???????? ???? ?????????????? ????????????????????",
                        "????????????????, ?????????????????? ???? ?????????????????????????? ?????? ????????????????\n" +
                                "?????????????????? ??????????, ?????? ???????? ??????????????????");
                e.printStackTrace();
            }
        else {
            //?????????????????? ?? ?????????????????????? ????????????????????
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(myFile);
                } catch (IOException ex) {
                    log.error("openInOuterApp : ???? ?????????????? ?????????????? ???????? '{}' ?? ?????????????????????? ????????????????????", myFile.getAbsolutePath());
                    Warning1.create("????????????",
                            "???? ?????????????? ?????????????? ???????? ?? ?????????????????????? ????????????????????",
                            "???????????????? ???? ???????? ???????????????????? ???? ?????????????????????????? ??\n " +
                                    "?? ???????????? ????????????, ?????? ???????? ??????????????????");
                    ex.printStackTrace();
                }
            }
        }
    }

}
