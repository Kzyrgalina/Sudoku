package model;

import test.Triple;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
        fillRows();
        fillCols();
        res = fillPuzzle();
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

    private void fillRows(){
        ArrayList<Triple> r = new ArrayList<>();
        int quad;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                for (int num = 1; num < 10; num++) {
                    quad = ((row-1)/3)*3 + (col-1)/3 + 1;
                    r.add(new Triple(0, row, col));
                    r.add(new Triple(1, row, num));
                    r.add(new Triple(2, col, num));
                    r.add(new Triple(3, quad, num));
                    ArrayList<Triple> collect =
                            (ArrayList<Triple>)
                                    r.stream()
                                        .sorted(Comparator.comparingInt(s -> s.first))
                                        .collect(Collectors.toList());
                    rows.put(new Triple(row, col, num), collect);
                    r.clear();
                }
            }
        }
    }

    private void fillCols(){
        for(Map.Entry<Triple,
                ArrayList<Triple>> pair: rows.entrySet()){
            for (Triple triple: pair.getValue()){
                Set<Triple> set = cols.computeIfAbsent(
                        triple, k -> new HashSet<>());
                set.add(pair.getKey());
            }
        }
    }

    private int[][] fillPuzzle(){
        ArrayList<Triple> solve = new ArrayList<>();
        for (int i = 0; i < test.length; i++) {
            for (int j = 0; j < test[0].length; j++) {
                if (test[i][j] > 0){
                    Triple elt = new Triple(i, j, test[i][j]);
                    solve.add(elt);
                    extractIntersects(rows, cols, elt);
                }
            }
        }
        solve = algorithm_x(rows, cols, solve);
        if (solve == null) return null;
        int[][] ret = new int[9][9];
        for (Triple elt: solve) {
            ret[elt.first][elt.second] = elt.third;
        }
        return ret;
    }

    ////////////// Algorithm X

    private ArrayList<Triple> algorithm_x(HashMap<Triple, ArrayList<Triple>> rows,
                                          HashMap<Triple, Set<Triple>> columns, ArrayList<Triple> cover) {
        if (columns.isEmpty()) return cover;
        else {
            ArrayList<Set<Triple>> buf_cols = new ArrayList<>();
            ArrayList<Triple> s = new ArrayList<>();
            //ищем столбец с минимальным числом элементов
            Collection<Set<Triple>> values = columns.values();
            Integer min = values.stream().map(Set::size).min(Integer::compareTo).orElse(0);
            List<Triple> collect = columns.entrySet()
                    .stream()
                    .filter(e -> e.getValue().size() == min)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            Triple tripleMin = collect.get(0);
            for (Triple subset : columns.get(tripleMin)) {
                cover.add(subset);
                //удаляем пересекающиеся подмножества и содержащиеся в subset элементы
                buf_cols = extractIntersects(rows, columns, subset);
                if (buf_cols == null) continue;
                //if (!s.isEmpty() && s.size() == 24) return s;
                //if (s.size() == 24) return s;

                    s = algorithm_x(rows, columns, cover);

                    if (s != null) return s;
                    restore_intersects(rows, columns, subset, buf_cols);
                    cover.remove(cover.size() - 1);

                //если нашлось непустое решение - готово, выходим
            }
            return null;
        }

    }

    private void restore_intersects(HashMap<Triple, ArrayList<Triple>> rows,
                                    HashMap<Triple, Set<Triple>> columns,
                                    Triple base_row, ArrayList<Set<Triple>> buf) {
        //удаляли столбцы от первого пересечения с base_row к последнему,
        // восстанавливать надо в обратном порядке
        ArrayList<Triple> reverse = new ArrayList<>();
        ArrayList<Triple> triples = rows.get(base_row);
        for (int i = triples.size() - 1; i > -1; i--) {
            reverse.add(triples.get(i));
        }
        try {
            for (Triple elt : reverse) {
                columns.put(elt,buf.remove(buf.size() - 1));
                for (Triple added_row : columns.get(elt)) {
                    for (Triple col : rows.get(added_row)){
                        Set<Triple> set = columns.computeIfAbsent(col, k -> new HashSet<>());
                        set.add(added_row);
                    }
                }
            }
        } catch (Exception e){
            return;
        }

    }

    private ArrayList<Set<Triple>> extractIntersects(HashMap<Triple, ArrayList<Triple>> rows,
                                                     HashMap<Triple, Set<Triple>> columns, Triple baseRow) {

        ArrayList<Set<Triple>> buf = new ArrayList<>();
        ArrayList<Triple> triples = rows.get(baseRow);
        try {
            if (rows.get(baseRow) != null){
                for (Triple elt : rows.get(baseRow)) {
                    if (columns.get(elt) != null) buf.add(columns.remove(elt));
                    //else System.out.println("columns.remove(elt) = null");
                    for (Triple intersecting_row : buf.get(buf.size() - 1)){
                        for (Triple other_elt : rows.get(intersecting_row)){
                            //Triple other_elt1 = other_elt;
                            if (other_elt.equals(intersecting_row)) {
                                if (columns.get(other_elt) != null) columns.get(other_elt).remove(intersecting_row);
                            }
                        }
                    }
                }
                System.out.println("Цикл прошел упешно" + count);
                count++;
            } else System.out.println("null");
        } catch (Exception e){
            return null;
        }
        return buf;
    }

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
