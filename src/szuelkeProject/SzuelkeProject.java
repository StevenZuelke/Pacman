/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szuelkeProject;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author StevenZuelke
 */
public class SzuelkeProject extends Application {

    //member status
    Label mStatus;
    Board Board;
    AnimationTimer Timer;
    Boolean Paused = false;
    
    @Override
    public void start(Stage primaryStage) {
        Timer = new AnimationTimer(){
            @Override
            public void handle(long now){
                onTimer(now);
            }
        };
        
        Board = new Board();
        Board.addPlayer();
        Board.addGhosts(2, .5);
        BorderPane root = new BorderPane();
        root.setCenter(Board);
        Board.drawBoard();
        //add the menus
        root.setTop(buildMenuBar());
        //add mStatus
        mStatus = new Label("New Game");
        ToolBar toolBar = new ToolBar(mStatus);
        root.setBottom(toolBar);
        Scene scene = new Scene(root, 600, 500);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        scene.addEventHandler(KeyEvent.KEY_PRESSED , e -> Board.keyPressed(e));
        primaryStage.show();
        Timer.start();
    }

    private void onTimer(long now){
        Board.onTimer(now, Paused);
    }
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Steven Zuelke, CSCD 370 Lab Template, Wtr 2020");
        alert.showAndWait();
    }

    private MenuBar buildMenuBar() {
        // build a menu bar
        MenuBar menuBar = new MenuBar();
        // File menu with just a quit item for now
        Menu fileMenu = new Menu("_File");
        MenuItem quitMenuItem = new MenuItem("_Quit");
        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                KeyCombination.CONTROL_DOWN));
        quitMenuItem.setOnAction(actionEvent -> Platform.exit());
        fileMenu.getItems().add(quitMenuItem);
        // Help menu with just an about item for now
        Menu helpMenu = new Menu("_Help");
        MenuItem aboutMenuItem = new MenuItem("_About");
        aboutMenuItem.setOnAction(actionEvent -> onAbout());
        helpMenu.getItems().add(aboutMenuItem);
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private void setStatus(String status){
        mStatus.setText(status);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
