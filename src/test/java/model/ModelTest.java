package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private final int[][] input = new int[][]{
            {0, 0, 0,   3, 0, 2,    0, 0, 8},
            {8, 0, 5,   1, 0, 0,    3, 0, 0},
            {0, 3, 0,   0, 0, 5,    0, 0, 2},

            {0, 0, 6,   0, 0, 0,    0, 0, 0},
            {0, 0, 0,   7, 0, 4,    5, 0, 0},
            {0, 0, 0,   0, 2, 0,    0, 0, 3},

            {7, 0, 1,   0, 5, 0,    0, 0, 0},
            {0, 0, 0,   0, 0, 0,    0, 0, 0},
            {3, 0, 0,   0, 0, 0,    0, 2, 0}};

    private final int[][] wrongInput = new int[][]{
            {0, 0, 0,   3, 0, 2,    2, 0, 8},
            {8, 0, 5,   1, 0, 0,    3, 0, 0},
            {0, 3, 0,   0, 0, 5,    0, 0, 2},

            {0, 0, 6,   0, 4, 0,    0, 0, 0},
            {0, 0, 0,   7, 0, 4,    5, 0, 0},
            {0, 0, 0,   0, 2, 0,    0, 0, 3},

            {7, 0, 1,   0, 5, 0,    0, 0, 0},
            {0, 0, 0,   0, 0, 0,    0, 0, 0},
            {3, 0, 0,   0, 5, 0,    0, 2, 0}};

    private final int[][] answer = new int[][]{
            {1, 4, 9,   3, 7, 2,    6, 5, 8},
            {8, 2, 5,   1, 9, 6,    3, 7, 4},
            {6, 3, 7,   8, 4, 5,    9, 1, 2},

            {4, 7, 6,   5, 3, 9,    2, 8, 1},
            {2, 1, 3,   7, 8, 4,    5, 6, 9},
            {5, 9, 8,   6, 2, 1,    7, 4, 3},

            {7, 8, 1,   2, 5, 3,    4, 9, 6},
            {9, 5, 2,   4, 6, 8,    1, 3, 7},
            {3, 6, 4,   9, 1, 7,    8, 2, 5}};

    Model model = new Model();

    @Test
    void solve() {
        model.setField(input);
        assertTrue(model.checkData(input));    //проверка, что данные корректны
        assertEquals(SolveState.HAVE_ANSWER, model.solve());
        assertTrue(model.checkData(model.getField()));  //проверка корректности ответа
        assertArrayEquals(answer, model.getField());    //соответсвие ответа с ожидаемым
    }

    @Test
    void checkData() {
        model.start();
        model.setField(wrongInput);
        assertFalse(model.checkData(wrongInput));   //некорректные данные
    }
}