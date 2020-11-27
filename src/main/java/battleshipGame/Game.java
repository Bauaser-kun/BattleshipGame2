package battleshipGame;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.Random;

public class Game extends Application {
    Image imageback = new Image("file:src/main/resources/waterBackground.jpg");
    private Label turnCounter;
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
    private int shipsToPlace = oneMastShips + twoMastShips + threeMastShips + fourMastShips;
    private LinkedList<Battleship> playerShips  = new LinkedList<>();
    private LinkedList<Battleship> enemyShips  = new LinkedList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createGame());

        primaryStage.setTitle("Battleship");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private Parent createGame() {
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
            rotateShip();
        });

        Button newGameBtn = new Button("Start new game");
        newGameBtn.setOnAction(event -> {
            startGame();
        });

        Button exitBtn = new Button("Exit game");
        exitBtn.setOnAction(event -> {
            exitGame();
        });

        FlowPane bottomButtons = new FlowPane();
        bottomButtons.getChildren().addAll(newGameBtn, exitBtn, rotateBtn);

        pane.setTop(turnCounter);
        pane.setBottom(bottomButtons);

        pane.setRight(new Text("checking if this works"));
        enemyBoard = new Board(false, enemyBoardClickHandler());

        playerBoard = new Board(true, playerBoardClickHandler());


        VBox gameBoards = new VBox(50, enemyBoard, playerBoard);
        gameBoards.setAlignment(Pos.CENTER);

        pane.setCenter(gameBoards);

        return pane;
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

            if(enemyBoard.ships == 0) {
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

            if (playerBoard.ships == 0) {
                System.out.println("You Lose");
                gameRunning = false;
            }
        }
    }

    private void exitGame() {
        System.exit(0);
    }

    private void startGame() {
        setShipsRandomly(enemyBoard, enemyShips);
        gameRunning = true;
    }

    private void setShipsRandomly(Board board, LinkedList<Battleship> ships) {
        shipsToPlace = ships.size();
        Battleship currentship = getNextShip(ships);

        while (shipsToPlace > 0) {
            int col = random.nextInt(10);
            int row = random.nextInt(10);
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

    private void rotateShip() {
        if(!gameRunning) {

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
