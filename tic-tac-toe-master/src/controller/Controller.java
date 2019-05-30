package controller;

import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import model.Board;
import model.EasyAI;
import model.Player;
import view.GUI;

public class Controller extends Application {
    
    private GUI gui;
    private Board board;
    private EasyAI ai;
    private String mode = "twoplayer";
    
    private Alert alert;
    
    private ArrayList<Button> buttons;
    
    public Controller() {
        
        gui = new GUI();
        board = new Board();
        alert = new Alert(AlertType.CONFIRMATION);
        
        ai = new EasyAI();
        
        buttons = gui.addButtons(new EventHandler<ActionEvent>() {
            
            private void handleTwoPlayerGame(ActionEvent event) {
                for (Button b : buttons) {
                    if (event.getSource().equals(b)) {
                        
                        if (b.getGraphic() == null) {
                            if (board.getTurn() > 0) {
                                b.setGraphic(new ImageView(new Image("image/x.png")));
                                board.addMove(Integer.parseInt(b.getId()), new Player("X"));
                            } else {
                                b.setGraphic(new ImageView(new Image("image/o.png")));
                                board.addMove(Integer.parseInt(b.getId()), new Player("O"));
                            }
                            board.switchTurn();
                        }
                        
                        board.addXOPositions();
                        board.printBoard();
                        checkWin();
                    }
                }
            }

            @Override
            public void handle(ActionEvent event) {
                
                switch (mode) {
                case "twoplayer":
                    handleTwoPlayerGame(event);
                    break;
                default:
                    break;
                }
            
            }
            
        });
    }
    
    private void setAlert(Player player) {
        String playerWon;
        
        if (player == null) {
            playerWon = "No player";
        } else {
            playerWon = player.getID();
        }
        
        alert.setTitle("Tic Tac Toe");
        alert.setHeaderText("Game Over - " + playerWon + " wins!");
        alert.setContentText(
                "X Wins: " + board.getXWins() + "\n" +
                "O Wins: " + board.getOWins() + "\n" +
                "Draws: " + board.getDraws() + "\n\n" +
                "Play Again?"
        );
        
        getAlert(alert);
    }
    
    private void getAlert(Alert alert) {
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.get() == ButtonType.OK) {
            resetGame();
        } else {
            System.exit(0);
        }
    }
    
    private void checkWin() {
        if (board.isXWin()) {
            setAlert(new Player("X"));
        } else if (board.isOWin()) {
            setAlert(new Player("O"));
        } else if (board.isDraw()) {
            setAlert(null);
        }
    }
    
   private void resetGame() {
       board.resetBoard();
       for (int i = 0; i < buttons.size(); i++) {
           buttons.get(i).setGraphic(null);
       }
   }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(gui.getFrameTitle());
        
        TilePane pane = new TilePane();
        pane.getChildren().addAll(buttons);
        
        Scene scene = new Scene(pane, gui.getFrameHeight(), gui.getFrameWidth());
        scene.getStylesheets().add("view/style.css");
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
