package ru.wert.datapik.utils.common.tableView;

import javafx.scene.control.ScrollBar;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdatePositions {

    private ScrollBar vertScroll; //Вертикальный скролл
    private ScrollBar horScroll; //Горизонтальный скролл
    private double vertPos; //Вертикальная позиция скролла
    private double horPos; //Горизонтальная позиция скролла

}
