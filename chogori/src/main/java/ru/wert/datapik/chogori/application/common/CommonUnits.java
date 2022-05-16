package ru.wert.datapik.chogori.application.common;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.utils.common.components.ChevronButton;
import ru.wert.datapik.utils.common.components.ExpandButton;
import ru.wert.datapik.utils.previewer.PreviewerPatch;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;

import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.images.BtnImages.BTN_CHEVRON_UP_IMG;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_PDF_VIEWER;
import static ru.wert.datapik.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class CommonUnits {

    /**
     * кнопочки с шевроном
     */
    public static ExpandButton createExpandPreviewButton(SplitPane sppHorizontal, SplitPane sppVertical){
        SplitPane.Divider horizontalDivider = sppHorizontal.getDividers().get(0);
        SplitPane.Divider verticalDivider = sppVertical.getDividers().get(0);
        return new ExpandButton(
                "Развернуть", BTN_EXPAND_IMG,
                "Свернуть", BTN_REDUCE_IMG, horizontalDivider,
                verticalDivider);

    }

    public static ChevronButton createHorizontalDividerButton(SplitPane sppHorizontal, double posMax, double pozStart){
        SplitPane.Divider horizontalDivider = sppHorizontal.getDividers().get(0);
        return new ChevronButton(
                "Развернуть", BTN_CHEVRON_RIGHT_IMG, posMax, //0.8
                "Свернуть", BTN_CHEVRON_LEFT_IMG, pozStart, //0.4
                horizontalDivider);

    }

    public static ChevronButton createVerticalDividerButton(SplitPane sppVertical, double posMax, double pozStart){
        SplitPane.Divider verticalDivider = sppVertical.getDividers().get(0);
        return new ChevronButton(
                "Развернуть", BTN_CHEVRON_DOWN_IMG, posMax, //0.8
                "Свернуть", BTN_CHEVRON_UP_IMG, pozStart, //0.4
                verticalDivider);
    }

    /**
     * ПРЕДПРОСМОТРЩИК
     */
    public static PreviewerPatchController loadStpPreviewer(StackPane stpPreviewer, SplitPane sppHorizontal, SplitPane sppVertical, boolean useBrackets) {
        PreviewerPatch previewerPatch = new PreviewerPatch().create();
        PreviewerPatchController previewerPatchController = previewerPatch.getController();
        previewerPatchController.initPreviewer(CH_PDF_VIEWER, WF_MAIN_STAGE.getScene());
        previewerPatchController.initPreviewerToolBar(true, true, true, true, useBrackets);
        previewerPatchController.getHboxPreviewerButtons().getChildren().add(CommonUnits.createExpandPreviewButton(sppHorizontal, sppVertical));

        stpPreviewer.getChildren().add(previewerPatch.getParent());

        return previewerPatchController;

    }
}
