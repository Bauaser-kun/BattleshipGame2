package battleshipGame;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board extends Parent {
    private final int rows = 10;
    private final int columns = 10;
    public boolean playerBoard;
    public int shipcount = 10;

    private VBox boardRows = new VBox();
    private boolean enemy = false;

    public Board(boolean playerBoard, EventHandler <? super MouseEvent> clickHandler) {
        this.playerBoard = playerBoard;
        for (int row = 0; row < rows; row++) {
            HBox r =  new HBox();
            for (int column = 0; column < columns; column++) {
                Cell cell = new Cell(column, row, this);
                cell.setOnMouseClicked(clickHandler);
                r.getChildren().add(cell);
            }
            boardRows.getChildren().add(r);
        }
        getChildren().add(boardRows);
    }

    public Cell getCell(int col, int row) {
        return (Cell)((HBox)boardRows.getChildren().get(row)).getChildren().get(col);
    }


    public boolean placeShip(Battleship ship, int columns, int rows) {
        if (canPlaceShip(ship, columns, rows)) {
            int length = ship.size;

            if (ship.vertical) {
                for (int i = rows; i < rows + length; i++) {
                    Cell cell = getCell(columns, i);
                    cell.ship = ship;
                    if (!enemy) {
                        cell.setFill(Color.GRAY);
                        cell.setStroke(Color.WHITE);
                    }
                    if (enemy) {
                        cell.setFill(Color.GRAY);
                        cell.setStroke(Color.WHITE);
                    }
                }
            } else {
                for (int i = columns; i < columns + length; i++) {
                    Cell cell = getCell(i, rows);
                    cell.ship = ship;
                    if (!enemy) {
                        cell.setFill(Color.GRAY);
                        cell.setStroke(Color.WHITE);
                    }
                    if (enemy) {
                        cell.setFill(Color.GRAY);
                        cell.setStroke(Color.WHITE);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean canPlaceShip(Battleship ship, int columns, int rows) {
        int length = ship.size;

        if (ship.vertical) {
            for (int i = rows; i < rows + length; i++) {
                if (!isValidPoint(columns, i))
                    return false;

                Cell cell = getCell(columns, i);
                if (cell.ship != null)
                    return false;

                for (Cell neighbor : getNeighbors(columns, i)) {
                    if (!isValidPoint(columns, i))
                        return false;

                    if (neighbor.ship != null)
                        return false;
                }
            }
        } else {
            for (int i = columns; i < columns + length; i++) {
                if (!isValidPoint(i, rows))
                    return false;

                Cell cell = getCell(i, rows);
                if (cell.ship != null)
                    return false;

                for (Cell neighbor : getNeighbors(i, rows)) {
                    if (!isValidPoint(i, rows))
                        return false;

                    if (neighbor.ship != null)
                        return false;
                }
            }
        }
        return true;
    }

    private Cell[] getNeighbors(int columns, int rows) {
        Point2D[] points = new Point2D[] {
                new Point2D(columns - 1, rows),
                new Point2D(columns + 1, rows),
                new Point2D(columns, rows - 1),
                new Point2D(columns, rows + 1)
        };

        List<Cell> neighbors = new ArrayList<Cell>();

        for ( Point2D p : points) {
            if(isValidPoint(p)) {
                neighbors.add(getCell((int)p.getX(), (int)p.getY()));
            }
        }
        return neighbors.toArray(new Cell[0]);
    }

    private boolean isValidPoint(Point2D p) {
        return isValidPoint(p.getX(), p.getY());
    }

    private boolean isValidPoint(double columns, double rows) {
        return columns >= 0 && columns < 10 && rows >= 0 && rows < 10;
    }

    public class Cell extends Rectangle {
        public int columns;
        public int rows;
        public Battleship ship = null;
        public boolean wasAimed = false;

        private Board board;

        public Cell (int columns, int rows, Board board) {
            super(20, 20);
            this.columns = columns;
            this.rows = rows;
            this.board = board;
            setFill(Color.AQUAMARINE);
            setStroke(Color.GRAY);
        }

        public boolean shoot() {
            wasAimed = true;
            setFill(Color.BLACK);

            if (ship != null) {
                ship.wasShot();
                setFill(Color.RED);
                if (!ship.isNotSinked()) {
                    board.shipcount--;
                }
                return true;
            }
        return false;
        }
    }

    public void setShipsRandomly (Battleship randomlyPlacedShip, Board board) {
        boolean shipPlaced;
        Random random = new Random();

        do {
            int row = random.nextInt(rows);
            int column = random.nextInt(columns);

            if (random.nextBoolean()) {
                randomlyPlacedShip.rotate();
            }
            placeShip(randomlyPlacedShip, row, column);

            shipPlaced = placeShip(randomlyPlacedShip, column, row);

        } while (!shipPlaced);
    }
}
