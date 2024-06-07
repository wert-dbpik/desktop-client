package ru.wert.tubus.chogori.common.utils;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import ru.wert.tubus.chogori.components.ChevronButton;
import ru.wert.tubus.chogori.components.ExpandButton;
import ru.wert.tubus.chogori.previewer.PreviewerPatch;
import ru.wert.tubus.chogori.previewer.PreviewerPatchController;
import ru.wert.tubus.chogori.images.BtnImages;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

import static ru.wert.tubus.winform.statics.WinformStatic.WF_MAIN_STAGE;

public class CommonUnits {

    /**
     * кнопочки с шевроном
     */
    public static ExpandButton createExpandPreviewButton(SplitPane sppHorizontal, SplitPane sppVertical){
        SplitPane.Divider horizontalDivider = sppHorizontal.getDividers().get(0);
        SplitPane.Divider verticalDivider = sppVertical.getDividers().get(0);
        return new ExpandButton(
                "Развернуть", BtnImages.BTN_EXPAND_IMG,
                "Свернуть", BtnImages.BTN_REDUCE_IMG, horizontalDivider,
                verticalDivider);

    }

    public static ChevronButton createHorizontalDividerButton(SplitPane sppHorizontal, double posMax, double pozStart){
        SplitPane.Divider horizontalDivider = sppHorizontal.getDividers().get(0);
        return new ChevronButton(
                "Развернуть", BtnImages.BTN_CHEVRON_RIGHT_IMG, posMax, //0.8
                "Свернуть", BtnImages.BTN_CHEVRON_LEFT_IMG, pozStart, //0.4
                horizontalDivider);

    }

    public static ChevronButton createVerticalDividerButton(SplitPane sppVertical, double posMax, double pozStart){
        SplitPane.Divider verticalDivider = sppVertical.getDividers().get(0);
        return new ChevronButton(
                "Развернуть", BtnImages.BTN_CHEVRON_DOWN_IMG, posMax, //0.8
                "Свернуть", BtnImages.BTN_CHEVRON_UP_IMG, pozStart, //0.4
                verticalDivider);
    }

    /**
     * ПРЕДПРОСМОТРЩИК
     */
    public static PreviewerPatchController loadStpPreviewer(StackPane stpPreviewer, SplitPane sppHorizontal, SplitPane sppVertical, boolean useBrackets) {
        PreviewerPatch previewerPatch = new PreviewerPatch().create();
        PreviewerPatchController previewerPatchController = previewerPatch.getController();
        previewerPatchController.initPreviewer(ChogoriSettings.CH_PDF_VIEWER, WF_MAIN_STAGE.getScene());
        previewerPatchController.initPreviewerToolBar(true, true, true, true, useBrackets);
        previewerPatchController.getHboxPreviewerButtons().getChildren().add(CommonUnits.createExpandPreviewButton(sppHorizontal, sppVertical));

        stpPreviewer.getChildren().add(previewerPatch.getParent());

        return previewerPatchController;

    }
}
