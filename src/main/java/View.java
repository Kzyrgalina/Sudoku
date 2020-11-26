import model.Cell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class View {

    JFrame frame;
    Cell[][] field;
    JButton[] keyboard;
    JButton btnSolve;
    JButton btnClear;
    Color myBlue;
    Font font;
    Font btnFont;
    boolean myBluePosition = false;

    public View() {
        frame = new JFrame();
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.WHITE);


        myBlue = new Color(227, 243,255);
        font = new Font("TimesRoman", Font.BOLD, 13);
        btnFont = new Font("TimesRoman", Font.PLAIN, 20);



        field = new Cell[9][9];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                field[i][j] = new Cell(i, j);
                field[i][j].setBounds(85 + 41*i, 70 + 41*j, 41, 41);
                frame.add(field[i][j]);
            }
        }

        keyboard = new JButton[10];

        for (int i = 0; i < keyboard.length; i++) {
            keyboard[i] = new JButton("" + (i + 1));
            keyboard[i].setBounds(470, 70 + 41*i, 41, 41);
            keyboard[i].setBackground(myBlue);
            keyboard[i].setFont(font);
            if (i == 9) {
                keyboard[i].setText(" ");
                keyboard[i].setBounds(470, 85 + 41*i, 41, 41);
            }
            frame.add(keyboard[i]);

        }

        btnSolve = new JButton("Решить");
        btnSolve.setBounds(333, 455, 120, 41);
        btnSolve.setFont(btnFont);
        btnSolve.setBackground(myBlue);
        frame.add(btnSolve);

        btnClear = new JButton("Очистить");
        btnClear.setBounds(85, 455, 120, 41);
        btnClear.setFont(btnFont);
        btnClear.setBackground(myBlue);
        frame.add(btnClear);



        frame.setVisible(true);
    }

    public void listenField(ActionListener listener){
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                field[i][j].addActionListener(listener);
            }
        }
    }

    public void listenKeyboard(ActionListener listener){
        for (int i = 0; i < keyboard.length; i++) {
            keyboard[i].addActionListener(listener);
        }
    }

    public void listenSolve(ActionListener listener){
        btnSolve.addActionListener(listener);
    }

    public void listenClear(ActionListener listener){
        btnClear.addActionListener(listener);
    }

    private void fieldStartColor(){
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                paintField(i,j);
            }
        }
    }

    public boolean color(int i, int j){
        return myBluePosition = (i < 3 || i > 5) && ((j < 3) || (j > 5)) ||
                ((i > 2 && i < 6) && (j > 2 && j < 6));
    }

    public void paintField(int i, int j){
        if (color(i,j)) field[i][j].setBackground(myBlue);
        else field[i][j].setBackground(Color.WHITE);
    }

    public void restart(){
        fieldStartColor();
    }

    public Cell[][] getField() {
        return field;
    }
}
