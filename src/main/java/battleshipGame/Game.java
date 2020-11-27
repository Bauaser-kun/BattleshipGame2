package battleshipGame;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Random;

public class Game extends Application {
    Image imageback = new Image("file:src/main/resources/waterBackground.jpg");
    private Label turnCounter;
    private int turnCount = 0;
    private Board enemyBoard;
    private Board playerBoard;
    private boolean gameRunning = false;
    private boolean enemyTurn;
    private Random random = new Random();
    private Battleship currentPlayerShip;
    private int oneMastShips = 4;
    private int twoMastShips = 3;
    private int threeMastShips = 2;
    private int fourMastShips = 1;
    private int totalships = oneMastShips + twoMastShips + threeMastShips + fourMastShips;
    private LinkedList<Battleship> playerShips  = new LinkedList<>();
    private LinkedList<Battleship> enemyShips  = new LinkedList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createGame(primaryStage));

        primaryStage.setTitle("Battleship");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private Parent createGame(Stage stage) {
        BackgroundSize backgroundSize = new BackgroundSize(100, 100,
                true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(imageback,
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        addShips(playerShips);
        currentPlayerShip = getNextShip(playerShips);
        addShips(enemyShips);

        BorderPane pane = new BorderPane();
        pane.setBackground(background);
        pane.setPrefSize(800, 640);

        Button rotateBtn = new Button("Rotate Ship");
        rotateBtn.setOnAction(event -> {
            rotateShip(currentPlayerShip);
        });

        Button newGameBtn = new Button("Start new game");
        newGameBtn.setOnAction(event -> {
            startNewGame(stage);
        });

        Button exitBtn = new Button("Exit game");
        exitBtn.setOnAction(event -> {
            exitGame();
        });

        Button randomizeShipBtn = new Button("Place ships randomly");
        randomizeShipBtn.setOnAction(event -> {
            setShipsRandomly(playerBoard, playerShips);
        });

        FlowPane bottomButtons = new FlowPane();
        bottomButtons.getChildren().addAll(newGameBtn, randomizeShipBtn, exitBtn, rotateBtn);

        pane.setTop(turnCounter);
        pane.setBottom(bottomButtons);

        pane.setRight(new Text("checking if this works"));

        enemyBoard = new Board(false, enemyBoardClickHandler());
        playerBoard = new Board(true, playerBoardClickHandler(),
                playerBoardEnteredHandler(), playerBoardExitedHandler());

        VBox gameBoards = new VBox(50, enemyBoard, playerBoard);
        gameBoards.setAlignment(Pos.CENTER);

        pane.setCenter(gameBoards);

        return pane;
    }

    public void resetSetting() {
        playerShips.clear();
        enemyShips.clear();
        oneMastShips = 4;
        twoMastShips = 3;
        threeMastShips = 2;
        fourMastShips = 1;
        turnCount = 0;
        totalships = oneMastShips + twoMastShips + threeMastShips + fourMastShips;
        gameRunning = false;
    }

    private EventHandler<? super MouseEvent> playerBoardExitedHandler() {
        return event -> {
          if (gameRunning) {
              return;
          }
          playerBoard.removeHiglightFromCellsToSetShipOn();
        };
    }

    private EventHandler<? super MouseEvent> playerBoardEnteredHandler() {
        return event -> {
            if (gameRunning) {
                return;
            }
            Board.Cell currentCell = (Board.Cell) event.getSource();
            playerBoard.highlitCellsTosetShipOn(currentPlayerShip, currentCell);
        };
    }

    private EventHandler<? super MouseEvent> playerBoardClickHandler() {
        return event -> {
          if (gameRunning)
              return;

            Board.Cell cell = (Board.Cell) event.getSource();

            boolean shipSetProperly = playerBoard.placeShip(currentPlayerShip, cell.columns, cell.rows);

            if (shipSetProperly) {
                playerBoard.placeShip(currentPlayerShip, cell.columns, cell.rows);
                if (playerShips.size() == 0) {
                    startGame();
                }
                currentPlayerShip = getNextShip(playerShips);
            }
        };
    }

    private Battleship getNextShip(LinkedList<Battleship> ships) {
        return ships.pollFirst();
    }

    public EventHandler<MouseEvent> enemyBoardClickHandler() {
        return event -> {
            if (!gameRunning) {
                return;
            }

            Board.Cell cell = (Board.Cell) event.getSource();
            if(cell.wasAimed)
                return;

            enemyTurn = !cell.shoot();

            if(enemyBoard.shipcount == 0) {
                System.out.println("You win");
                gameRunning = false;
            }

            if (enemyTurn && gameRunning)
                enemyMove();
        };
    }

    private void enemyMove() {
        while (enemyTurn) {
            int col = random.nextInt(10);
            int row = random.nextInt(10);

            Board.Cell cell = playerBoard.getCell(col, row);
            if (cell.wasAimed)
                continue;

            enemyTurn = cell.shoot();

            if (playerBoard.shipcount == 0) {
                System.out.println("You Lose");
                gameRunning = false;
            }
        }
    }

    private void exitGame() {
        System.exit(0);
    }

    private void startGame() {
        gameRunning = true;
        setShipsRandomly(enemyBoard, enemyShips);

    }

    private void startNewGame(Stage stage){
        resetSetting();
        Scene scene = new Scene(createGame(stage));
        stage.setTitle("Battleship");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

    }

    private void setShipsRandomly(Board board, LinkedList<Battleship> ships) {
        totalships = ships.size();
        Battleship currentship;
        if (ships == playerShips) {
            currentship = currentPlayerShip;
        } else {
            currentship = getNextShip(ships);
        }

        while (totalships > 0) {
            int col = random.nextInt(10);
            int row = random.nextInt(10);
            if (random.nextBoolean()) {
                currentship.rotate();
            }
            boolean shipSetProperly = board.placeShip(currentship, col, row);

            if (shipSetProperly) {
                board.placeShip(currentship, col, row);
                if (ships.size() == 0) {
                    if (board == playerBoard) {
                        startGame();
                    }
                }
                currentship = getNextShip(ships);
            }
        }
    }

    private void rotateShip(Battleship ship) {
        if(!gameRunning) {
            ship.rotate();
        }
    }

    private void addShips (LinkedList<Battleship> ships){
        for (int i = 0; i < oneMastShips; i++) {
            ships.add(new Battleship(1, true));
        }

        for (int i = 0; i < twoMastShips; i++) {
            ships.add(new Battleship(2, true));
        }

        for (int i = 0; i < threeMastShips; i++) {
            ships.add(new Battleship(3, true));
        }

        for (int i = 0; i < fourMastShips; i++) {
            ships.add(new Battleship(4, true));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
