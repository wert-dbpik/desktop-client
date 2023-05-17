package ru.wert.datapik.chogori.excel.poi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import ru.wert.datapik.chogori.excel.enums.EColName;
import ru.wert.datapik.chogori.excel.enums.EColor;
import ru.wert.datapik.chogori.excel.model.EditorRow;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ru.wert.datapik.chogori.excel.enums.EColName.*;

@Log
@Getter
public class POIReader {

    private final File file;
    private final Workbook book;
    private final Sheet sheet;
    private final List<String> colNamesList; //Список сформированный из enum класса
    private final int headRowIndex;          // Номер строки заголовков таблицы
    private final int firstRowIndex;         // Номер первой строки с данными таблицы
    private int lastRowIndex;          // Номер последней строки с данными таблицы

    private HashMap<Integer, String> hashMapHeader; // Map содержащий пары [индекс колонки + заголовок]
    private final Set<Integer> setOfColIndexes;
    private List<String> setOfColNames;
    private boolean colLaсquerExist; // Есть ли в таблице стобец Лак
    private boolean colZpcExist;// Есть ли в таблице стобец ЦСГ
    private boolean colAmountPerAssembleExist; // Есть ли в таблице стобец (кол)
    private HashMap<Integer, Integer> executions;
    private HashMap<String, Integer> modelColNames; // Map содержащий пары [название столбца + индекс столбца] без Кол и (кол)
    private List<List<String>> data;


    public POIReader(File file) throws IOException {
        this.file = file;
        this.book = WorkbookFactory.create(file);
        this.sheet = book.getSheetAt(0);
        this.headRowIndex = findHeadRowIndex(sheet);
        this.firstRowIndex = headRowIndex +1;
        this.lastRowIndex = findLastRowInTable();
        this.colNamesList = EColName.getColNamesList();
        this.hashMapHeader = findHashMapHeader();
        this.setOfColIndexes = hashMapHeader.keySet();
        this.setOfColNames = findSetOfColNames();

        this.colLaсquerExist = setOfColNames.contains(LACQUER.toString());
        this.colZpcExist = setOfColNames.contains(ZCP.toString());
        this.colAmountPerAssembleExist = setOfColNames.contains(AMOUNT.toString());

        this.modelColNames = findModelColNames();
        this.executions = findExecutions();
        this.data = findData();

        book.close(); //Закрываем книгу
    }

    /**
     * Метод определяет тип ячейки таблицы и после выдает ее строковое значение
     */
    private String getDataFromCell(int rowIndex, int cellIndex){
        Cell cell = this.sheet.getRow(rowIndex).getCell(cellIndex);
        if(cell.getCellType().equals(CellType.NUMERIC)) {
            return String.valueOf(cell.getNumericCellValue());
        }
        else {
            return cell.getStringCellValue();
        }
    }

    /**
     * Создаем строку заголовков таблицы. Добавляем первый столбец со статусом строки.
     * @return setOfColNames
     */
    private List<String> findSetOfColNames() {
        setOfColNames = new ArrayList<>();
        setOfColNames.add("Цвет");
        setOfColNames.addAll(hashMapHeader.values());
        return setOfColNames;
    }

    /**
     * Находим номер строки заголовков headRow
     * @param sheet
     * @return headRowIndex
     */
    private int findHeadRowIndex(Sheet sheet){
        for(int i = 0; ; i++){
            if(getDataFromCell(i, 0).equals(ROW_NUM.toString()) ||
                    getDataFromCell(i, 1).equals(KRP.toString()))
                return sheet.getRow(i).getRowNum();
        }
    }

    /**
     * Находим индекс последней строки в таблице
     * @return lastRowIndex
     */
    private int findLastRowInTable(){
        for(lastRowIndex = headRowIndex;  ; lastRowIndex++){
            Row row = sheet.getRow(lastRowIndex);
            if(row == null)
                return lastRowIndex;
            else{
                Cell cell = row.getCell(0);
                if (cell == null || cell.getCellType() == CellType.BLANK)
                    return lastRowIndex;
            }
        }
    }

    /**
     * Находим HashMap строки заголовков
     * [ключ = индекс столбца, значение = имя столбца]
     * @return hashMapHeader
     */
    private HashMap<Integer, String> findHashMapHeader(){
        hashMapHeader = new HashMap<>();
        for(int colNum = 0; ; colNum++){
                Cell cell = sheet.getRow(headRowIndex).getCell(colNum);
                if(cell == null) return hashMapHeader;
                if(colNamesList.contains(cell.getStringCellValue()))
                    hashMapHeader.put(cell.getColumnIndex(), cell.getStringCellValue());
        }
    }

    /**
     * Готовим массив массивов STRING для искомой таблицы.
     * Добавляем нулевой столбец со статусом строки.
     */
    private List<List<String>> findData(){
        data = FXCollections.observableArrayList();
        for(int rowNum = firstRowIndex; rowNum < lastRowIndex; rowNum++){
            Iterator<Integer> it = setOfColIndexes.iterator();
            List<String> oneRowData = new ArrayList<>();

            String color = findRowStatus(rowNum);
            oneRowData.add(color);

            while (it.hasNext()){
                oneRowData.add(getDataFromCell(rowNum, it.next()));
            }
            data.add(oneRowData);
        }
        return data;
    }

    /**
     * Определим цвет строки по переданному индексу строки
     * @param rowNum
     * @return цвет строки
     */
    private String findRowStatus(int rowNum) {
        Cell cell = sheet.getRow(rowNum).getCell(3);
        XSSFColor color = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();

        if(color == null)
            return "WHITE";
        else
            return EColor.byHEX(color.getARGBHex()).toString();
    }

    /**
     * Возвращает HashMap с координатами именного столбца. Столбцы Кол и (кол) не учитываются.
     * @return
     */
    private HashMap<String, Integer> findModelColNames() {
        modelColNames = new HashMap<>();

        for(int i = 0; i < setOfColNames.size(); i++){
            modelColNames.put(setOfColNames.get(i), i);
        }

        return modelColNames;
    }

    /**
     * Возвращает HashMap [порядковый номер испонения + индекс столбца равный ключу hashMapHeader]
     * @return
     */
    private HashMap<Integer, Integer> findExecutions() {
        executions = new HashMap<>();
        int ex = 0;//порядковый номер исполнения
        for (int i = 0; i < setOfColNames.size(); i++) {
            if (setOfColNames.get(i).equals(TOTAL_AMOUNT.toString())) {
                executions.put(ex, i);
                ex++;
            }
        }

        return executions;
    }

    /**
     * Возвращает массив данных из таблицы. Числовые данные усекаются до общего формата без .0
     */
    public ObservableList<EditorRow> findModelData(){
        ObservableList<EditorRow> editorRowData = FXCollections.observableArrayList();
        for(int i = 0; i < data.size(); i ++){
            List<String> row = new ArrayList<>(data.get(i));
            EditorRow mc = new EditorRow();
            mc.setColor(row.get(0));
            mc.setRowNumber(splitDotZero(row.get(modelColNames.get(ROW_NUM.toString()))));
            mc.setKrp(row.get(modelColNames.get(KRP.toString())));
            mc.setDecNumber(row.get(modelColNames.get(DEC_NUM.toString())));
            mc.setName(row.get(modelColNames.get(NAME.toString())));
            if (colLaсquerExist)
                mc.setLacquer(row.get(modelColNames.get(LACQUER.toString())));
            mc.setCoat(row.get(modelColNames.get(COAT.toString())));
            if(colZpcExist)
                mc.setZcp(row.get(modelColNames.get(ZCP.toString())));

            //Прописываем количество элементов для всех исполнений таблицы
            ArrayList<EditorRow.Execution> exs = new ArrayList<>();
            for(Integer ex : executions.keySet()){
//                String exName = (ex == 0) ? "-" : ((ex < 10) ? ("0" + ex) : String.valueOf(ex));
                String am = splitDotZero(row.get(executions.get(ex))); //Кол
                String amAs = ""; //(кол)
                if(colAmountPerAssembleExist) amAs = row.get(executions.get(ex) + 1);
                String exId = "ex" + ex;
                EditorRow.Execution exx = new EditorRow.Execution(exId, am, amAs);
                exs.add(exx);
            }
            mc.setExecutions(exs);

            mc.setFolder(row.get(modelColNames.get(FOLDER.toString())));
            mc.setMaterial(row.get(modelColNames.get(MATERIAL.toString())));
            mc.setParamA(splitDotZero(row.get(modelColNames.get(A.toString()))));
            mc.setParamB(splitDotZero(row.get(modelColNames.get(B.toString()))));

            editorRowData.add(mc);
        }
        return editorRowData;
    }

    /**
     * Метод усекает числовую строку до нормального вида без .0
     * @param initStr
     * @return
     */
    private String splitDotZero(String initStr){
        String val = Arrays.asList(initStr.split("\\.")).get(0);
        return val;
    }

    /**
     * Метод возвращает наименование исполнения: -, 01, 02 и т.д.
     * Наименование исполнения не всегда совпадает с его порядковым номер
     */
    public String getExecutionName(int ex) {
        String exName = "";
        int rowExName = 0; //Строка с именем исполнения

        //Если сторока заголовков следует сразу за заголовком таблицы, то строки с исполнениями не существует
        if (headRowIndex == 1) return "";
        //В прочих случаях эта строка всегда первая
        if (headRowIndex > 1) rowExName = 1;

        return getDataFromCell(rowExName, executions.get(ex));

    }

    /**
     * Метод возвращает описание исполнения - это несколько обозначений в отдельной строке
     */
    public String getExecutionDescription(int ex) {
        int rowExDesc = 0; //Строка с описанием

        //Если сторока заголовков следует сразу за заголовком таблицы либо сразу после строки с исполнениями,
        // то строки с описанием не существует
        if (headRowIndex == 1 || headRowIndex == 2) return "";

        //В прочих случаях эта строка следует за строкой с исполнением и может состоять из двух строк
        if (headRowIndex == 3) {
            rowExDesc = 2;
            return getDataFromCell(rowExDesc, executions.get(ex));
        } else {
            rowExDesc = 2;
            String desc = "";
            return getDataFromCell(rowExDesc, executions.get(ex)) +
                    getDataFromCell(rowExDesc + 1, executions.get(ex));
        }

    }



}
