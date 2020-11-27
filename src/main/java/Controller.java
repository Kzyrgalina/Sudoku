import model.Cell;
import model.Model;
import model.SolveState;

import javax.swing.*;
import java.awt.*;

import static model.CellState.*;
import static model.SolveState.ENTER_THE_DATA;


public class Controller {
    private View view;
    private Model model;
    private Cell selectedCell = null;
    private boolean wasSelected = false;


    public Controller(final View view, final Model model) {
        this.view = view;
        this.model = model;

        restart();

        view.listenField(e -> {
            if (model.getState() == ENTER_THE_DATA && !wasSelected) {
                Cell cell = (Cell) e.getSource();
                model.setSelectedCell(cell.getLocate());
                if (selectedCell != null) selectedCell.setState(EMPTY);
                selectedCell = cell;
                selectedCell.setState(SELECTED);
                wasSelected = true;
            }
        });

        view.listenKeyboard(e -> {
            if (model.getState() == ENTER_THE_DATA && wasSelected) {
                wasSelected = false;
                String str = ((JButton) e.getSource()).getText();
                model.setChar(str.charAt(0));
                if (selectedCell != null) {
                    if (str.equals(" ")) {
                        Point locate = selectedCell.getLocate();
                        view.paintCell(locate.x, locate.y);
                    }
                    init();
                    selectedCell = null;
                }
                view.showMessage(" ");
                if (!model.checkData(model.getField())) {
                    view.showMessage("Invalid data, please try again :)");
                }
            }
        });

        view.listenClear(e -> restart());

        view.listenCancel(e -> {
            if (model.getState() == ENTER_THE_DATA) {
                String str = ((JButton) e.getSource()).getText();
                model.setChar('\u0000');
                if (selectedCell != null) {
                    if (!str.equals(" ")) {
                        Point locate = selectedCell.getLocate();
                        view.paintCell(locate.x, locate.y);
                    }
                    init();
                    selectedCell = null;
                }
                view.showMessage("");
            }
        });

        view.listenSolve(e -> {
            if (model.getState() == ENTER_THE_DATA) {
                if (model.solve() == SolveState.HAVE_ANSWER) {
                    Cell[][] field = view.getField();
                    for (int i = 0; i < field.length; i++) {
                        for (int j = 0; j < field[0].length; j++) {
                            Cell cell = field[i][j];
                            view.setDigit(cell, model.getField()[i][j] + "");
                        }
                    }
                    view.showMessage("Sudoku solution: ");
                } else view.showMessage("The solution is not found :(");
            }
        });

    }

    public void restart() {
        model.start();
        view.restart();
        init();
    }

    public void init() {
        Cell[][] field = view.getField();
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                Cell cell = field[i][j];
                if (model.getField()[i][j] != 0) {
                    cell.setState(BUSY);
                    cell.setText(model.getField()[i][j] + "");
                } else {
                    cell.setState(EMPTY);
                    cell.setText("");
                }
            }
        }
    }
}

