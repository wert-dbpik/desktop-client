package ru.wert.datapik.chogori.application.common;

import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.utils.common.components.BtnDouble;
import ru.wert.datapik.utils.common.components.ChevronButton;
import ru.wert.datapik.utils.common.components.ExpandButton;
import ru.wert.datapik.utils.previewer.PreviewerPatch;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;

import static ru.wert.datapik.utils.images.BtnImages.*;
import static ru.wert.datapik.utils.images.BtnImages.BTN_CHEVRON_UP_IMG;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_PDF_VIEWER;
import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;

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

    public static ChevronButton createHorizontalDividerButton(SplitPane sppHorizontal){
        SplitPane.Divider horizontalDivider = sppHorizontal.getDividers().get(0);
        return new ChevronButton(
                "Развернуть", BTN_CHEVRON_RIGHT_IMG, 0.8,
                "Свернуть", BTN_CHEVRON_LEFT_IMG, 0.4,
                horizontalDivider);

    }

    public static ChevronButton createVerticalDividerButton(SplitPane sppVertical){
        SplitPane.Divider verticalDivider = sppVertical.getDividers().get(0);
        return new ChevronButton(
                "Развернуть", BTN_CHEVRON_DOWN_IMG, 0.8,
                "Свернуть", BTN_CHEVRON_UP_IMG, 0.4,
                verticalDivider);
    }

    /**
     * ПРЕДПРОСМОТРЩИК
     */
    public static PreviewerPatchController loadStpPreviewer(StackPane stpPreviewer, SplitPane sppHorizontal, SplitPane sppVertical) {
        PreviewerPatch previewerPatch = new PreviewerPatch().create();
        PreviewerPatchController previewerPatchController = previewerPatch.getController();
        previewerPatchController.initPreviewer(CH_PDF_VIEWER, CH_MAIN_STAGE.getScene());
        previewerPatchController.initPreviewerToolBar(true);
        previewerPatchController.getHboxPreviewerButtons().getChildren().add(CommonUnits.createExpandPreviewButton(sppHorizontal, sppVertical));

        stpPreviewer.getChildren().add(previewerPatch.getParent());

        return previewerPatchController;

    }
}
