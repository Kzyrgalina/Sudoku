package algorithmX;

import java.util.*;
import java.util.stream.Collectors;

public class AlgorithmX {
    LinkedHashMap<Triple, LinkedList<Triple>> rows;
    LinkedHashMap<Triple, LinkedHashSet<Triple>> cols;
    LinkedHashMap<Triple, LinkedHashSet<Triple>> remains;
    ArrayList<LinkedHashMap<Triple, LinkedHashSet<Triple>>> megaList = new ArrayList<>();
    int algCount = 0;
    private int[][] output;
    private int[][] input = new int[][]{
            {0, 0, 0,   3, 0, 2,    0, 0, 8}, //54
            {8, 0, 5,   1, 0, 0,    3, 0, 0},
            {0, 3, 0,   0, 0, 5,    0, 0, 2},

            {0, 0, 6,   0, 0, 0,    0, 0, 0},
            {0, 0, 0,   7, 0, 4,    5, 0, 0},
            {0, 0, 0,   0, 2, 0,    0, 0, 3},

            {7, 0, 1,   0, 5, 0,    0, 0, 0},
            {0, 0, 0,   0, 0, 0,    0, 0, 0},
            {3, 0, 0,   0, 0, 0,    0, 2, 0}};
    Stack<Triple> s = new Stack<>();

    public AlgorithmX() {
        rows = new LinkedHashMap();
        cols = new LinkedHashMap();
        print(input);
        System.out.println();
        System.out.println("Ршение:");
        int[][] answer = xsudoku(input);
        print(answer);
        System.out.println();
        checkAnswer(input, answer);
    }

    private void print(int[][] res) {
        if (res == null) {
            System.out.println("null in res");
            return;
        }
        for (int i = 0; i < res.length; i++) {
            System.out.println("");
            for (int j = 0; j < res[0].length; j++) {
                System.out.print(res[i][j] + " ");
            }
        }
    }

    private void checkAnswer(int[][] input, int[][] solve){
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (input[i][j] != 0 && (input[i][j] != output[i][j])) {
                    System.out.println("Неверно");
                    return;
                }
            }
        }
        System.out.println("Верно!!!!");
    }

    //Для работы алгоритма нужна функция, вынимающая из матрицы строки, пересекающиеся с заданной,
    // и функция, возвращающая эти строки на место.

    private int[][] xsudoku(int[][] input) {
        //заполняем строки
        for (int row = 1; row < 10; row++) {    //начинаем с 1 и идем до 9
            for (int col = 1; col < 10; col++) {
                for (int num = 1; num < 10; num++) {
                    LinkedList<Triple> r = new LinkedList<>();
                    int quad = ((row - 1) / 3) * 3 + (col - 1) / 3 + 1;
                    r.addLast(new Triple(0, row, col));
                    r.addLast(new Triple(1, row, num));
                    r.addLast(new Triple(2, col, num));
                    r.addLast(new Triple(3, quad, num));
                    rows.put(new Triple(row, col, num), r);
                }
            }
        }

        //заполняем столбцы
        for (int i = 0; i < 4; i++) {
            for (int n1 = 1; n1 < 10; n1++) {
                for (int n2 = 1; n2 < 10; n2++) {
                    cols.put(new Triple(i, n1, n2), new LinkedHashSet<>());
                }
            }
        }
        for (Map.Entry<Triple, LinkedList<Triple>> elementOfRow : rows.entrySet()) {
            for (Triple v : elementOfRow.getValue()) {
                cols.get(v).add(elementOfRow.getKey());
            }
        }


        //s - заготовка для ответа (КАКОЙ У НЕЕ ТИП ДАННЫХ АААААААА)
        //для начала, туда надо внести те цифры, которые уже заполнены
        /**/     //Stack<Triple> s = new Stack<>();
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                if (input[i - 1][j - 1] > 0) {
                    Triple elt = new Triple(i, j, input[i - 1][j - 1]);
                    s.push(elt); //23 числа
                    extract_intersects(rows, cols, elt);
                }
                //if (input[i-1][j-1] > 0) extract_intersects(rows, cols, elt);
            }
        }
        //23 числа, s = 23, cols = 324 - 23*4 = 232
        //всё, что осталось - найти покрытие
        Stack<Triple> success = algorithm_x(rows, cols, s);

        if (success == null) return null;
            //ответ выдадим в виде матрицы
        else {
            output = input;
            for (Triple triple : s) { //?????
                output[triple.first - 1][triple.second - 1] = triple.third;
            }
            return output;
        }
    }

    private Stack<Triple> algorithm_x(LinkedHashMap<Triple, LinkedList<Triple>> rows,
                                      LinkedHashMap<Triple, LinkedHashSet<Triple>> columns, Stack<Triple> cover) {
        Map<Triple, LinkedHashSet<Triple>> collect = columns.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        //columns =  collect;

        if (columns.isEmpty()) return cover; //основное условие выхода
        else {
            //ищем столбец с минимальным числом элементов

            Map.Entry<Triple, Integer> min = columns.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, row -> row.getValue().size()))
                    .entrySet()
                    .stream()
                    //.filter(tripleIntegerEntry -> !remains.containsKey(tripleIntegerEntry.getKey()))
                    //.filter(entry -> entry.getValue() > 0)
                    .min(Map.Entry.comparingByValue()).orElse(null);

           // remains.put(min.getKey(), null);

            Triple c = min.getKey();

            if (columns.get(c) != null) {
                LinkedHashSet <Triple> current = columns.get(c);
                int check = 0;
                while (current.size() - check > 0) {
                    ArrayList<Triple> newCurrent = new ArrayList<>(current);
                    Triple subset = (newCurrent).get(0);
                    cover.push(subset);
                    //удаляем пересекающиеся подмножества и содержащиеся в subset элементы
                    Stack<LinkedHashSet<Triple>> buf_cols = extract_intersects(rows, columns, subset);
                    newCurrent.remove(0);
                    s = algorithm_x(rows, columns, cover);
                    //если нашлось непустое решение - готово, выходим
                    if (s != null) return s;
                    restore_intersects(rows, columns, subset, buf_cols);
                    check++;
                    cover.pop();
                }
            } else System.out.println("columns.get(c) == null");

            //сюда дойдём либо если в columns[c] пусто,
            //либо когда рекурсивный поиск не нашёл решения
            //System.out.println("columns[c] пусто или рекурсивный поиск не нашёл решения");
            return null;
        }
    }

    private Stack<LinkedHashSet<Triple>> extract_intersects(LinkedHashMap<Triple, LinkedList<Triple>> rows,
                                                            LinkedHashMap<Triple, LinkedHashSet<Triple>> columns,
                                                            Triple base_row) {
        Stack<LinkedHashSet<Triple>> buf = new Stack<>();
        LinkedList<Triple> triples = rows.get(base_row);
        Iterator iteratorForExtract = rows.get(base_row).iterator();
        while (iteratorForExtract.hasNext()) {
            Triple elt = (Triple) iteratorForExtract.next();
            //вынимаем текущий столбец из таблицы в буфер
            // push!(buf, pop!(columns, elt))
/***/LinkedHashSet<Triple> set = columns.remove(elt);
            buf.add(set);
            //удаляем все пересекающиеся строки из всех оставшихся столбцов

            for (Triple intersecting_row : buf.peek()) {
                for (Triple other_elt : rows.get(intersecting_row)) {
                    if (!other_elt.equals(elt)) {
                        columns.get(other_elt).remove(intersecting_row);
                    }
                }
            }
            /*Iterator<Triple> first = buf.peek().iterator();
            while (first.hasNext()) {
                Triple intersecting_row = first.next();
                Iterator second = rows.get(intersecting_row).iterator();
                while (second.hasNext()) {
                    Object other_elt = second.next();

                    if (!other_elt.equals(elt)) {
                        LinkedHashSet<Triple> linkedHashSet = columns.get(other_elt);
*//***//*                        linkedHashSet.remove(intersecting_row);
                    }
                }
            }*/

        }
        return buf;
    }

    private void restore_intersects(LinkedHashMap<Triple, LinkedList<Triple>> rows,
                                    LinkedHashMap<Triple, LinkedHashSet<Triple>> columns,
                                    Triple base_row, Stack<LinkedHashSet<Triple>> buf) {
        //удаляли столбцы от первого пересечения с base_row к последнему,
        // восстанавливать надо в обратном порядке
        LinkedList<Triple> reverse = new LinkedList<>(rows.get(base_row));
        Collections.reverse(reverse);
        Triple check = new Triple(0, 6, 8);
        for (Triple elt: reverse){
            columns.put(elt, buf.pop());

            for (Triple added_row : columns.get(elt)){
                for (Triple col : rows.get(added_row)){

                    columns.get(col).add(added_row);
                    if (col.equals(check)){

                    }
                }
            }
        }

        /*Iterator<Triple> iteratorReverse = reverse.iterator();
        while (iteratorReverse.hasNext()) {
            Triple elt = iteratorReverse.next();

            columns.put(elt, buf.pop());

            Iterator<Triple> second = columns.get(elt).iterator();
            while (second.hasNext()) {
                Triple added_row = second.next();

                Iterator<Triple> third = rows.get(added_row).iterator();
                while (third.hasNext()) {
                    Triple col = third.next();
                    LinkedHashSet<Triple> hashSet = columns.get(col);
                    hashSet.add(added_row);
                }
            }
        }

        reverse.stream()
                .forEachOrdered(elt -> {
            columns.put(elt, buf.pop());
            columns.get(elt).stream()
                    .forEachOrdered(added_row -> {
                        rows.get(added_row).stream()
                                .forEachOrdered(col -> {
                                    LinkedHashSet<Triple> triples = columns.get(col);
                                    triples.add(added_row);
                                    System.out.println("\n columns[" + col + "] : ");
                                    triples.forEach(System.out::print);
                                    System.out.println("");
                                });
                    });
        });*/

    }
}
