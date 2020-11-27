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

    Model model = new Model();

    @Test
    void solve() {
        model.setField(input); //корректные входные данные
        assertTrue(model.checkData(input)); //проверка, что они корректны
        assertEquals(SolveState.HAVE_ANSWER, model.solve());
        assertTrue(model.checkData(model.getField()));//проверка корректности ответа
    }

    @Test
    void checkData() {
        model.start();
        model.setField(wrongInput);
        assertFalse(model.checkData(wrongInput));//некорректные данные
    }
}