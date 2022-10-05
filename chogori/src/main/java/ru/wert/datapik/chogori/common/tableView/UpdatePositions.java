package ru.wert.datapik.chogori.common.tableView;

import javafx.scene.control.ScrollBar;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatePositions {

    private ScrollBar vertScroll; //Вертикальный скролл
    private ScrollBar horScroll; //Горизонтальный скролл
    private double vertPos; //Вертикальная позиция скролла
    private double horPos; //Горизонтальная позиция скролла

}
