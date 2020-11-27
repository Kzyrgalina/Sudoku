package model;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;

import static model.SolveState.ENTER_THE_DATA;
import static model.SolveState.HAVE_ANSWER;

public class Model {
    private int[][] field;
    private Point selectedCell;
    int[][] answer;
    Stack<Triplet> solution = new Stack<>();
    LinkedHashMap<Triplet, LinkedList<Triplet>> rows;
    LinkedHashMap<Triplet, LinkedHashSet<Triplet>> cols;
    HashSet<Triplet> data;
    SolveState state;

    public Model() {
        field = new int[9][9];
        rows = new LinkedHashMap();
        cols = new LinkedHashMap();
        data = new HashSet<>();
    }

    public void start() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                field[i][j] = 0;
            }
        }
        selectedCell = new Point(-1, -1);
        state = ENTER_THE_DATA;
    }

    public SolveState solve() {
        answer = sudokuSolver(field);
        if (answer != null) {
            state = HAVE_ANSWER;
            if (checkData(answer)) {
                field = answer;
                return SolveState.HAVE_ANSWER;
            }
            return SolveState.INCORRECT_ANSWER;
        }
        return SolveState.NO_SOLUTION;
    }

    //выбор клетки для установки новой буквы
    public boolean setSelectedCell(int x, int y) {
        if (selectedCell != null) {
            selectedCell.x = x;
            selectedCell.y = y;
            return true;
        }
        return false;
    }

    //установка буквы
    public void setChar(char c) {
        if (selectedCell.x >= 0 && selectedCell.y >= 0) {
            try {
                field[selectedCell.x][selectedCell.y] = Integer.parseInt(c + "");
            } catch (Exception e) {
                field[selectedCell.x][selectedCell.y] = 0;
            }
        }
    }

    //проверка корректности входных данных
    public boolean checkData(int[][] solution) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                //(row, col, num)
                data.add(new Triplet(i + 1, j + 1, solution[i][j]));
            }
        }
        for (Triplet current : data) {
            int quadCurrent = ((current.first - 1) / 3) * 3 + (current.second - 1) / 3 + 1;
            for (Triplet other : data) {
                if (!current.equals(other)) {
                    int quadOther = ((other.first - 1) / 3) * 3 + (other.second - 1) / 3 + 1;

                    boolean numberEquality;
                    if (state == HAVE_ANSWER) { //проверка для ответа
                        numberEquality = current.third == other.third;
                    } else { //проверка данных при вводе
                        numberEquality = (current.third != 0 && (current.third == other.third));
                    }

                    if (((current.first == other.first) && (numberEquality)) ||
                            ((current.second == other.second) && (numberEquality)) ||
                            (quadCurrent == quadOther) && (numberEquality)) {
                        data.clear();
                        return false;
                    }
                }
            }
        }
        data.clear();
        return true;
    }

    ////////////// Sudoku solver

    private int[][] sudokuSolver(int[][] matrix) {

        //заполняем строки
        for (int row = 1; row < 10; row++) {
            for (int col = 1; col < 10; col++) {
                for (int num = 1; num < 10; num++) {
                    LinkedList<Triplet> r = new LinkedList<>();
                    int quad = ((row - 1) / 3) * 3 + (col - 1) / 3 + 1;
                    r.addLast(new Triplet(0, row, col));
                    r.addLast(new Triplet(1, row, num));
                    r.addLast(new Triplet(2, col, num));
                    r.addLast(new Triplet(3, quad, num));
                    rows.put(new Triplet(row, col, num), r);
                }
            }
        }

        //заполняем столбцы
        for (int i = 0; i < 4; i++) {
            for (int n1 = 1; n1 < 10; n1++) {
                for (int n2 = 1; n2 < 10; n2++) {
                    cols.put(new Triplet(i, n1, n2), new LinkedHashSet<>());
                }
            }
        }
        for (Map.Entry<Triplet, LinkedList<Triplet>> elementOfRow : rows.entrySet()) {
            for (Triplet v : elementOfRow.getValue()) {
                cols.get(v).add(elementOfRow.getKey());
            }
        }

        //первичное покрытие на основе входных данных
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                if (matrix[i - 1][j - 1] > 0) {
                    Triplet triplet = new Triplet(i, j, matrix[i - 1][j - 1]);
                    solution.push(triplet);
                    extractIntersects(rows, cols, triplet);
                }
            }
        }

        //заготовка для ответа и вход в рекурсию
        Stack<Triplet> answer = algorithmX(rows, cols, solution);

        if (answer == null) {
            return null;
        } else {
            //ответ в виде матрицы
            for (Triplet triplet : solution) {
                matrix[triplet.first - 1][triplet.second - 1] = triplet.third;
            }
            return matrix;
        }
    }

    private Stack<Triplet> algorithmX(LinkedHashMap<Triplet, LinkedList<Triplet>> rows,
                                      LinkedHashMap<Triplet, LinkedHashSet<Triplet>> columns, Stack<Triplet> cover) {
        if (columns.isEmpty()) return cover; //основное условие выхода
        else {
            //ищем столбец с минимальным числом элементов
            Map.Entry<Triplet, Integer> min = columns.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, row -> row.getValue().size()))
                    .entrySet()
                    .stream()
                    .min(Map.Entry.comparingByValue()).orElse(null);

            Triplet c = min.getKey();

            LinkedHashSet<Triplet> current = columns.get(c);
            int check = 0;
            while (current.size() - check > 0) {
                ArrayList<Triplet> newCurrent = new ArrayList<>(current);
                Triplet triplet = (newCurrent).get(0);
                cover.push(triplet);
                //удаляем пересекающиеся подмножества и содержащиеся в triplet элементы
                Stack<LinkedHashSet<Triplet>> buf = extractIntersects(rows, columns, triplet);
                newCurrent.remove(0);
                solution = algorithmX(rows, columns, cover);
                //есть решение - выходим, нет - восстанавливаем столбцы на места
                if (solution != null) return solution;
                restoreIntersects(rows, columns, triplet, buf);
                check++;
                cover.pop();
            }
            //рекурсивный поиск не нашёл решения
            return null;
        }
    }

    private Stack<LinkedHashSet<Triplet>> extractIntersects(LinkedHashMap<Triplet, LinkedList<Triplet>> rows,
                                                            LinkedHashMap<Triplet, LinkedHashSet<Triplet>> columns,
                                                            Triplet baseRow) {
        Stack<LinkedHashSet<Triplet>> buf = new Stack<>();
        for (Triplet element : rows.get(baseRow)) {
            //вынимаем текущий столбец из таблицы в буфер
            buf.add(columns.remove(element));
            //удаляем все пересекающиеся строки из всех оставшихся столбцов
            for (Triplet intersectingRow : buf.peek()) {
                for (Triplet otherElement : rows.get(intersectingRow)) {
                    if (!otherElement.equals(element)) {
                        columns.get(otherElement).remove(intersectingRow);
                    }
                }
            }
        }
        return buf;
    }

    private void restoreIntersects(LinkedHashMap<Triplet, LinkedList<Triplet>> rows,
                                   LinkedHashMap<Triplet, LinkedHashSet<Triplet>> columns,
                                   Triplet baseRow, Stack<LinkedHashSet<Triplet>> buf) {
        LinkedList<Triplet> reverse = new LinkedList<>(rows.get(baseRow));
        Collections.reverse(reverse);
        for (Triplet element : reverse) {
            columns.put(element, buf.pop());
            for (Triplet addedRow : columns.get(element)) {
                for (Triplet col : rows.get(addedRow)) {
                    columns.get(col).add(addedRow);
                }
            }
        }
    }

    //////////////

    public int[][] getField() {
        return field;
    }

    public boolean setSelectedCell(Point point) {
        return setSelectedCell(point.x, point.y);
    }

    public void setField(int[][] field) {
        this.field = field;
    }

    public SolveState getState() {
        return state;
    }
}
