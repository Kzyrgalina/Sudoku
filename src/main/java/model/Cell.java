package model;

import javax.swing.*;
import java.awt.*;

public class Cell extends JButton {
    private Point locate;
    private CellState state;

    public Cell(int x, int y){
        locate = new Point(x, y);
        state = CellState.EMPTY;
    }
    public void setState(CellState state) {
        this.state = state;
        switch (state) {
            case SELECTED:
                setBackground(new Color(125, 196,250));
                break;
            case BUSY:
                setBackground(new Color(204, 204, 255));
                break;
        }
    }

    public CellState getState() {
        return state;
    }

    public Point getLocate() { return locate; }
}
