import model.Cell;
import model.CellState;
import model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static model.CellState.*;
import static model.CellState.SELECTED;


public class Controller {
    private View view;
    private Model model;
    private Cell selectedCell = null;

    public Controller(final View view, final Model model) {
        this.view = view;
        this.model = model;

        restart();

        view.listenField(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Cell cell = (Cell) e.getSource();
                CellState state;

                model.setSelectedCell(cell.getLocate());
                    if (selectedCell != null) selectedCell.setState(EMPTY);
                    selectedCell = cell;
                    selectedCell.setState(SELECTED);

            }
        });

        view.listenKeyboard(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String str = ((JButton) e.getSource()).getText();
                    model.setChar(str.charAt(0));
                    if (selectedCell != null) {
                        if (str.equals(" ")){
                            Point locate = selectedCell.getLocate();
                            view.paintField(locate.x, locate.y);
                            //selectedCell.setState(EMPTY);
                        } //else selectedCell.setState(BUSY);
                        init();
                        selectedCell = null;
                    }

            }
        });

        view.listenClear(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restart();
                Cell[][] field = view.getField();
                for (int i = 0; i < field.length; i++) {
                    System.out.println("строка");
                    for (int j = 0; j < field[0].length; j++) {
                        field[i][j].setText("");
                    }
                }
            }
        });

        view.listenSolve(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.solve();
                /*int[][] res = model.getRes();
                Cell[][] field = view.getField();
                for (int i = 0; i < res.length; i++) {
                    for (int j = 0; j < res[0].length; j++) {
                        Cell cell = field[i][j];
                        if (res[i][j] != 0){
                            cell.setState(BUSY);
                            cell.setText(res[i][j] + "");
                        } else {
                            cell.setState(EMPTY);
                            cell.setText("");
                        }
                    }
                }*/
                int[][] res = model.getRes();
                for (int i = 0; i < res.length; i++) {
                    for (int j = 0; j < res[0].length; j++) {
                        System.out.print(res[i][j] + " ");
                    }
                }
            }
        });

    }


    public void restart(){
        model.start();
        view.restart();
        init();
    }

    public void init(){
        Cell[][] field = view.getField();
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                Cell cell = field[i][j];
                if (model.getField()[i][j] != 0){
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

