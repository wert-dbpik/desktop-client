package ru.wert.datapik.utils.common.tableView;

import javafx.scene.control.ScrollBar;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdatePositions {

    private ScrollBar vertScroll; //Вертикальный сколл
    private ScrollBar horScroll; //Горизонтальный сколл
    private double vertPos; //Вертикальная позиция скролла
    private double horPos; //Горизонтальная позиция скролла

}
