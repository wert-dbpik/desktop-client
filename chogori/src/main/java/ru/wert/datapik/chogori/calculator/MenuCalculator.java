package ru.wert.datapik.chogori.calculator;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import ru.wert.datapik.chogori.calculator.controllers.*;

import java.io.IOException;

public class MenuCalculator extends ContextMenu {

    private IMenuCalculator calculator;
    private ObservableList<AbstractOpPlate> addedOperations;

    private ListView<VBox> listViewTechOperations;

    /**
     * Create a new ContextMenu
     */
    public MenuCalculator(IMenuCalculator calculator, ObservableList<AbstractOpPlate> addedOperations, ListView<VBox> listViewTechOperations) {
        this.calculator = calculator;
        this.addedOperations = addedOperations;
        this.listViewTechOperations = listViewTechOperations;
    }


    //ДОБАВИТЬ ДЕТАЛЬ
    public MenuItem getAddDetail(){
        MenuItem addDetail = new MenuItem("Добавить деталь");
        addDetail.setOnAction(event -> {
            addDetailOperation();
        });
        return addDetail;
    }


    //РАСКРОЙ И ЗАЧИСТКА
    public MenuItem getAddCutting(){
        MenuItem addCutting = new MenuItem("Резка и зачистка");
        addCutting.setOnAction(event -> {
            if(isDuplicate(PlateCuttingController.class.getSimpleName())) return ;
            addCattingOperation();
        });
        return addCutting;
    }

    //ГИБКА
    public MenuItem getAddBending(){
        MenuItem addBending = new MenuItem("Гибка");
        addBending.setOnAction(event -> {
            if(isDuplicate(PlateBendController.class.getSimpleName())) return ;
            addBendingOperation();
        });
        return addBending;
    }


    //СЛЕСАРНЫЕ РАБОТЫ
    public MenuItem getAddLocksmith(){
        MenuItem addLocksmith = new MenuItem("Слесарные операции");
        addLocksmith.setOnAction(event -> {
            if(isDuplicate(PlateLocksmithController.class.getSimpleName())) return ;
            addLocksmithOperation();
        });
        return addLocksmith;
    }

    //=======================================================================

    //СВАРКА НЕПРЕРЫВНАЯ
    public MenuItem getAddWeldingLongSeam(){
        MenuItem addWeldingLongSeam = new MenuItem("Сварка непрерывная");
        addWeldingLongSeam.setOnAction(event -> {
            addWeldingContinuousOperation();
        });
        return addWeldingLongSeam;
    }


    //СВАРКА ТОЧЕЧНАЯ
    public MenuItem getAddWeldingDotted(){
        MenuItem addWeldingDotted = new MenuItem("Сварка точечная");
        addWeldingDotted.setOnAction(event -> {
            if(isDuplicate(PlateWeldDottedController.class.getSimpleName())) return ;
            addWeldingDottedOperation();
        });
        return addWeldingDotted;
    }


    //=======================================================================

    //ПОКРАСКА
    public MenuItem getAddPainting(){
        MenuItem addPainting = new MenuItem("Покраска детали");
        addPainting.setOnAction(event -> {
            if(isDuplicate(PlatePaintController.class.getSimpleName())) return ;
            addPaintingOperation();
        });
        return addPainting;
    }



    //ПОКРАСКА СБОРОЧНОЙ ЕДИНИЦЫ
    public MenuItem getAddPaintingAssembling(){
        MenuItem addPaintingAssembling = new MenuItem("Покраска сборочной единицы");
        addPaintingAssembling.setOnAction(event -> {
            if(isDuplicate(PlatePaintAssmController.class.getSimpleName())) return ;
            addPaintingAssemblingOperation();
        });
        return addPaintingAssembling;
    }


    //=======================================================================
    //СБОРКА - КРЕПЕЖ
    public MenuItem getAddAssemblingNuts(){
        MenuItem addAssemblingNuts = new MenuItem("Сборка крепежа");
        addAssemblingNuts.setOnAction(event -> {
            if(isDuplicate(PlateAssmNutsController.class.getSimpleName())) return ;
            addAssemblingNutsOperation();
        });
        return addAssemblingNuts;
    }


    //СБОРКА - РАСКРОЙНЫЙ МАТЕРИАЛ
    public MenuItem getAddAssemblingCuttings(){
        MenuItem addAssemblingCuttings = new MenuItem("Сборка раскройного материала");
        addAssemblingCuttings.setOnAction(event -> {
            if(isDuplicate(PlateAssmCuttingsController.class.getSimpleName())) return ;
            addAssemblingCuttingsOperation();
        });
        return addAssemblingCuttings;
    }


    //СБОРКА СТАНДАРТНЫХ УЗЛОВ
    public MenuItem getAddAssemblingNodes(){
        MenuItem addAssemblingNodes = new MenuItem("Сборка стандартных узлов");
        addAssemblingNodes.setOnAction(event -> {
            if(isDuplicate(PlateAssmNodesController.class.getSimpleName())) return ;
            addAssemblingNodesOperation();
        });
        return addAssemblingNodes;
    }


    //НАНЕСЕНИЕ НАЛИВНОГО УПЛОТНИТЕЛЯ
    public MenuItem getAddLevelingSealer(){
        MenuItem addLevelingSealer = new MenuItem("Нанесение наливного утеплителя");
        addLevelingSealer.setOnAction(event -> {
            if(isDuplicate(PlateLevelingSealerController.class.getSimpleName())) return ;
            addLevelingSealerOperation();
        });
        return addLevelingSealer;
    }

    /**
     * Ищем дубликат операции в списке addedOperations по clazz
     */
    private boolean isDuplicate(String clazz){
        for(AbstractOpPlate cn: addedOperations){
            if(cn.getClass().getSimpleName().equals(clazz))
                return true;
        }
        return false;
    }

    /*==================================================================================================================
    *                                                М Е Т О Д Ы
    * ==================================================================================================================*/

    /**
     * ДОБАВЛЕНИЕ ДЕТАЛИ
     */
    private void addDetailOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/plateDetail.fxml"));
            VBox detail = loader.load();
            detail.setId("calculator");
            PlateDetailController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(detail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * РАСКРОЙ И ЗАЧИСТКА
     */
    private void addCattingOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/plateCutting.fxml"));
            VBox cutting = loader.load();
            cutting.setId("calculator");
            PlateCuttingController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(cutting);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ГИБКА
     */
    private void addBendingOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/plateBend.fxml"));
            VBox bending = loader.load();
            bending.setId("calculator");
            PlateBendController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(bending);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * СЛЕСАРНЫЕ ОПЕРАЦИИ
     */
    private void addLocksmithOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/plateLocksmith.fxml"));
            VBox locksmith = loader.load();
            locksmith.setId("calculator");
            PlateLocksmithController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(locksmith);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //==================================================================================================================

    /**
     * ПОКРАСКА ДЕТАЛИ
     */
    private void addPaintingOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/platePaint.fxml"));
            VBox painting = loader.load();
            painting.setId("calculator");
            PlatePaintController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(painting);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ПОКРАСКА СБОРОЧНОЙ ЕДИНИЦЫ
     */
    private void addPaintingAssemblingOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/platePaintAssm.fxml"));
            VBox paintingAssembling = loader.load();
            paintingAssembling.setId("calculator");
            PlatePaintAssmController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(paintingAssembling);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //==================================================================================================================

    /**
     * СВАРКА НЕПРЕРЫВНАЯ
     */
    private void addWeldingContinuousOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/plateWeldContinuous.fxml"));
            VBox weldingLongSeam = loader.load();
            weldingLongSeam.setId("calculator");
            PlateWeldContinuousController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(weldingLongSeam);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * СВАРКА ТОЧЕЧНАЯ
     */
    private void addWeldingDottedOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/plateWeldDotted.fxml"));
            VBox weldingDotted = loader.load();
            weldingDotted.setId("calculator");
            PlateWeldDottedController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(weldingDotted);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //==================================================================================================================

    /**
     * СБОРКА КРЕПЕЖА
     */
    private void addAssemblingNutsOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/plateAssmNuts.fxml"));
            VBox assemblingNuts = loader.load();
            assemblingNuts.setId("calculator");
            PlateAssmNutsController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(assemblingNuts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * СБОРКА РАСКРОЙНОГО МАТЕРИАЛА
     */
    private void addAssemblingCuttingsOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/plateAssmCuttings.fxml"));
            VBox assemblingCuttings = loader.load();
            assemblingCuttings.setId("calculator");
            PlateAssmCuttingsController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(assemblingCuttings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * СБОРКА СТАНДАРТНЫХ УЗЛОВ
     */
    private void addAssemblingNodesOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/plateAssmNodes.fxml"));
            VBox assemblingNodes = loader.load();
            assemblingNodes.setId("calculator");
            PlateAssmNodesController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(assemblingNodes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //==================================================================================================================

    /**
     * НАНЕСЕНИЕ НАЛИВНОГО УПЛОТНИТЕЛЯ
     */
    private void addLevelingSealerOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/plateLevelingSealer.fxml"));
            VBox levelingSealer = loader.load();
            levelingSealer.setId("calculator");
            PlateLevelingSealerController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(levelingSealer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
