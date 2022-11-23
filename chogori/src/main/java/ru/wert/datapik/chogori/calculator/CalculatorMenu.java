package ru.wert.datapik.chogori.calculator;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.datapik.chogori.calculator.controllers.*;

import java.io.IOException;

public class CalculatorMenu extends ContextMenu {

    private ICalculatorController calculator;
    private ObservableList<AbstractNormsCounter> addedOperations;

    private ListView<VBox> listViewTechOperations;

    /**
     * Create a new ContextMenu
     */
    public CalculatorMenu(ICalculatorController calculator, ObservableList<AbstractNormsCounter> addedOperations, ListView<VBox> listViewTechOperations) {
        this.calculator = calculator;
        this.addedOperations = addedOperations;
        this.listViewTechOperations = listViewTechOperations;
    }



    //РАСКРОЙ И ЗАЧИСТКА
    public MenuItem getAddCutting(){
        MenuItem addCutting = new MenuItem("Резка и зачистка");
        addCutting.setOnAction(event -> {
            if(isDuplicate(CuttingController.class.getSimpleName())) return ;
            addCattingOperation();
        });
        return addCutting;
    }

    //ГИБКА
    public MenuItem getAddBending(){
        MenuItem addBending = new MenuItem("Гибка");
        addBending.setOnAction(event -> {
            if(isDuplicate(BendingController.class.getSimpleName())) return ;
            addBendingOperation();
        });
        return addBending;
    }


    //СЛЕСАРНЫЕ РАБОТЫ
    public MenuItem getAddLocksmith(){
        MenuItem addLocksmith = new MenuItem("Слесарные операции");
        addLocksmith.setOnAction(event -> {
            if(isDuplicate(LocksmithController.class.getSimpleName())) return ;
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
            if(isDuplicate(WeldingDottedController.class.getSimpleName())) return ;
            addWeldingDottedOperation();
        });
        return addWeldingDotted;
    }


    //=======================================================================

    //ПОКРАСКА
    public MenuItem getAddPainting(){
        MenuItem addPainting = new MenuItem("Покраска детали");
        addPainting.setOnAction(event -> {
            if(isDuplicate(PaintingController.class.getSimpleName())) return ;
            addPaintingOperation();
        });
        return addPainting;
    }



    //ПОКРАСКА СБОРОЧНОЙ ЕДИНИЦЫ
    public MenuItem getAddPaintingAssembling(){
        MenuItem addPaintingAssembling = new MenuItem("Покраска сборочной единицы");
        addPaintingAssembling.setOnAction(event -> {
            if(isDuplicate(PaintingAssemblingController.class.getSimpleName())) return ;
            addPaintingAssemblingOperation();
        });
        return addPaintingAssembling;
    }


    //=======================================================================
    //СБОРКА - КРЕПЕЖ
    public MenuItem getAddAssemblingNuts(){
        MenuItem addAssemblingNuts = new MenuItem("Сборка крепежа");
        addAssemblingNuts.setOnAction(event -> {
            if(isDuplicate(AssemblingNutsController.class.getSimpleName())) return ;
            addAssemblingNutsOperation();
        });
        return addAssemblingNuts;
    }


    //СБОРКА - РАСКРОЙНЫЙ МАТЕРИАЛ
    public MenuItem getAddAssemblingCuttings(){
        MenuItem addAssemblingCuttings = new MenuItem("Сборка раскройного материала");
        addAssemblingCuttings.setOnAction(event -> {
            if(isDuplicate(AssemblingCuttingsController.class.getSimpleName())) return ;
            addAssemblingCuttingsOperation();
        });
        return addAssemblingCuttings;
    }


    //СБОРКА СТАНДАРТНЫХ УЗЛОВ
    public MenuItem getAddAssemblingNodes(){
        MenuItem addAssemblingNodes = new MenuItem("Сборка стандартных узлов");
        addAssemblingNodes.setOnAction(event -> {
            if(isDuplicate(AssemblingNodesController.class.getSimpleName())) return ;
            addAssemblingNodesOperation();
        });
        return addAssemblingNodes;
    }


    //НАНЕСЕНИЕ НАЛИВНОГО УПЛОТНИТЕЛЯ
    public MenuItem getAddLevelingSealer(){
        MenuItem addLevelingSealer = new MenuItem("Нанесение наливного утеплителя");
        addLevelingSealer.setOnAction(event -> {
            if(isDuplicate(LevelingSealerController.class.getSimpleName())) return ;
            addLevelingSealerOperation();
        });
        return addLevelingSealer;
    }

    /**
     * Ищем дубликат операции в списке addedOperations по clazz
     */
    private boolean isDuplicate(String clazz){
        for(AbstractNormsCounter cn: addedOperations){
            if(cn.getClass().getSimpleName().equals(clazz))
                return true;
        }
        return false;
    }

    /**
     * РАСКРОЙ И ЗАЧИСТКА
     */
    private void addCattingOperation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/cutting.fxml"));
            VBox cutting = loader.load();
            cutting.setId("calculator");
            CuttingController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/bending.fxml"));
            VBox bending = loader.load();
            bending.setId("calculator");
            BendingController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/locksmith.fxml"));
            VBox locksmith = loader.load();
            locksmith.setId("calculator");
            LocksmithController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/painting.fxml"));
            VBox painting = loader.load();
            painting.setId("calculator");
            PaintingController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/paintingAssembling.fxml"));
            VBox paintingAssembling = loader.load();
            paintingAssembling.setId("calculator");
            PaintingAssemblingController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/weldingContinuous.fxml"));
            VBox weldingLongSeam = loader.load();
            weldingLongSeam.setId("calculator");
            WeldingContinuousController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/weldingDotted.fxml"));
            VBox weldingDotted = loader.load();
            weldingDotted.setId("calculator");
            WeldingDottedController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/assemblingNuts.fxml"));
            VBox assemblingNuts = loader.load();
            assemblingNuts.setId("calculator");
            AssemblingNutsController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/assemblingCuttings.fxml"));
            VBox assemblingCuttings = loader.load();
            assemblingCuttings.setId("calculator");
            AssemblingCuttingsController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/assemblingNodes.fxml"));
            VBox assemblingNodes = loader.load();
            assemblingNodes.setId("calculator");
            AssemblingNodesController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/calculator/levelingSealer.fxml"));
            VBox levelingSealer = loader.load();
            levelingSealer.setId("calculator");
            LevelingSealerController controller = loader.getController();
            controller.init(calculator);
            listViewTechOperations.getItems().add(levelingSealer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
