package model;

import algorithmX.Triple;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

// судоку задаётся матрицей 9×9, на месте неизвестных чисел нули
// идентификаторы строк - кортежи вида (row, col, num)
// идентификаторы столбцов:
// (0, row, col) - на пересечении row и col стоит число
// (1, row, num) - в строке row есть число num
// (2, col, num) - в столбце col есть число num
// (3, q, num) - в квадранте q есть число num

public class Model {

    int count = 1;

    private int[][] field;
    private Point selectedCell;
    boolean digitWasSet;
    boolean charWasSet;
    HashMap<Triple, ArrayList<Triple>> rows;
    HashMap<Triple, Set<Triple>> cols;
    private int[][] res;
    private int[][] test = new int[][] {{0, 0, 0, 0, 0, 0, 4, 0, 0}, //54
                                        {3, 0, 6, 0, 0, 0, 0, 0, 0},
                                        {0, 0, 0, 1, 9, 6, 0, 3, 0},
                                        {0, 7, 0, 0, 0, 0, 0, 1, 0},
                                        {8, 0, 0, 2, 5, 0, 0, 9, 0},
                                        {0, 4, 0, 0, 0, 0, 8, 0, 0},
                                        {0, 6, 0, 4, 0, 9, 0, 0, 8},
                                        {0, 0, 5, 0, 0, 0, 0, 2, 0},
                                        {0, 0, 0, 5, 0, 0, 0, 0, 7}};

    public Model() {
        field = new int[9][9];
        rows = new HashMap<>();
        cols = new HashMap<>();
        res = new int[9][9];
    }


    public void start(){
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                field[i][j] = 0;
            }
        }
        selectedCell = new Point(-1, -1);
        digitWasSet = false;
    }
    public void solve(){

    }

    //выбор клетки для установки новой буквы
    public boolean setSelectedCell(int x, int y) {
        if (selectedCell != null){
            selectedCell.x = x;
            selectedCell.y = y;
            return true;
        }
                return false;
    }

    //установка буквы
    public void setChar(char c){
        if (selectedCell.x >= 0 && selectedCell.y >= 0) {
            try {
                field[selectedCell.x][selectedCell.y] = Integer.parseInt(c + "");
            } catch (Exception e){
                field[selectedCell.x][selectedCell.y] = 0;
            }
        }
    }

    ////////////// Sudoku solver
    //////////////

    public int[][] getField() {
        return field;
    }

    public boolean isCharWasSet() { return charWasSet; }

    public boolean setSelectedCell(Point point) {
        return setSelectedCell(point.x, point.y);
    }

    public int[][] getRes() {
        return res;
    }
}
