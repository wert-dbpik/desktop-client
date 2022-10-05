package ru.wert.datapik.chogori.pdf.readers;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.util.FontPropertiesManager;
import org.icepdf.ri.util.PropertiesManager;
import ru.wert.datapik.chogori.pdf.PDFReader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class PdfIcepdfReader implements PDFReader {

    private SwingController swingController;
    private JComponent viewerPanel;
    private final StackPane stackPaneForPDF;
    public PdfIcepdfReader(StackPane stackPaneForPDF, Scene scene) {
        this.stackPaneForPDF = stackPaneForPDF;

        createViewer(stackPaneForPDF);
        createResizeListeners(scene, viewerPanel);

    }

    private void createResizeListeners(Scene scene, JComponent viewerPanel) {

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                viewerPanel.setSize(new Dimension(newValue.intValue(), (int) scene.getHeight()));
                viewerPanel.setPreferredSize(new Dimension(newValue.intValue(), (int) scene.getHeight()));
                viewerPanel.repaint();
            });
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            SwingUtilities.invokeLater(() -> {
                viewerPanel.setSize(new Dimension((int) scene.getWidth(), newValue.intValue()));
                viewerPanel.setPreferredSize(new Dimension((int) scene.getWidth(), newValue.intValue()));
                viewerPanel.repaint();
            });
        });
    }

    private void createViewer(StackPane stackPaneForPDF) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                // create the viewer ri components.
                swingController = new SwingController();
                swingController.setIsEmbeddedComponent(true);

                // read/store the font cache.
                FontPropertiesManager.getInstance().loadOrReadSystemFonts();

                PropertiesManager properties = PropertiesManager.getInstance();
                properties.checkAndStoreFloatProperty(PropertiesManager.PROPERTY_DEFAULT_ZOOM_LEVEL, 1.0f);
                properties.checkAndStoreFloatProperty(PropertiesManager.PROPERTY_VIEWPREF_FITWINDOW, 1.0f);
                properties.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_OPEN, true);
                properties.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_SAVE, true);
                properties.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_UTILITY_PRINT, true);
                // hide the status bar
                properties.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_STATUSBAR, false);
                // hide a few toolbars, just to show how the prefered size of the viewer changes.
                properties.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FIT, true);
                properties.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ROTATE, false);
                properties.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_TOOL, false);
                properties.checkAndStoreBooleanProperty(PropertiesManager.PROPERTY_SHOW_TOOLBAR_FORMS, false);


                swingController.getDocumentViewController().setAnnotationCallback(
                        new org.icepdf.ri.common.MyAnnotationCallback(swingController.getDocumentViewController()));

                SwingViewBuilder factory = new SwingViewBuilder(swingController, properties);


                viewerPanel = factory.buildViewerPanel();
                viewerPanel.revalidate();



                SwingNode swingNode = new SwingNode();
                swingNode.setContent(viewerPanel);

//                JToggleButton b1 = factory.buildFitPageButton();
//                JToggleButton b2 = factory.buildFitWidthButton();
//                b2.setSelected(true);

                Platform.runLater(()->{
                    stackPaneForPDF.getChildren().add(swingNode);
                });

            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showPDF(File file) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                swingController.openDocument(file.toString());
                viewerPanel.revalidate();
            }
        });

    }

    private void buildButton(FlowPane flowPane, AbstractButton jButton){
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(jButton);
        flowPane.getChildren().add(swingNode);
    }

    private void buildJToolBar(FlowPane flowPane, JToolBar jToolBar){
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(jToolBar);
        flowPane.getChildren().add(swingNode);
    }
}
