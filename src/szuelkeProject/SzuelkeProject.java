/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szuelkeProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
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
    Boolean Paused = true;
    MenuItem Pause, Go, Save, Open, Settings;
    Stage Stage;
    
    @Override
    public void start(Stage primaryStage) {
        Timer = new AnimationTimer(){
            @Override
            public void handle(long now){
                onTimer(now);
            }
        };
        Board = new Board(1);
        Board.addPlayer();
        Board.addGhosts(2, .5);
        BorderPane root = new BorderPane();
        root.setCenter(Board);
        //add the menus
        root.setTop(buildMenuBar());
        //add mStatus
        mStatus = new Label("New Game");
        ToolBar toolBar = new ToolBar(mStatus);
        root.setBottom(toolBar);
        Scene scene = new Scene(root, 600, 500);
        Stage = primaryStage;
        Stage.setTitle("Slab Game");
        Stage.setScene(scene);
        scene.addEventHandler(KeyEvent.KEY_PRESSED , e -> pauseGo(e));
        Stage.show();
        Timer.start();
    }
    
    private void readBoard(SavableBoard sboard){
        Board.Board = sboard.Board;
        Board.Score.set(sboard.Score);
        Board.Ghosts = sboard.Ghosts;
        Board.Level.set(sboard.Level);
        Board.draw();
    }
    
    private void openHandler(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Game Files", "*.slab"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(Stage);
        if(selectedFile == null){
            return;
        }
        try{
            FileInputStream fileInput = new FileInputStream(selectedFile);
            ObjectInputStream objInput = new ObjectInputStream(fileInput);
            SavableBoard openBoard = (SavableBoard) objInput.readObject();
            readBoard(openBoard);
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
    
    private void saveHandler(){
        //SaveAs
        File selectedFile = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(  
            new FileChooser.ExtensionFilter("Game Files", "*.slab"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        selectedFile = fileChooser.showSaveDialog(Stage);
        
        if(selectedFile != null){
            try{
                //Save File Here  
                FileOutputStream fileStream = new FileOutputStream(selectedFile);
                ObjectOutputStream objStream = new ObjectOutputStream(fileStream);
                objStream.writeObject(new SavableBoard(Board));
            }catch(Exception e){
                e.printStackTrace();
                return;
            }
        }
    }
    
    private void newHandler(){
        Board.resetBoard();
        Board.Score.set(0);
        Board.Ghosts.clear();
        Board.addGhosts(2, .5);
        Board.addPlayer();
        Board.Death.set(false);
        Paused = true;
        Board.draw();
    }
    
    private void pauseGo(KeyEvent e)
    {
        if(e.getCode() == KeyCode.SPACE){
            if(Paused) goHandler();
            else pauseHandler();
        }else Board.keyPressed(e);
    }
    
    private void goHandler(){
        Paused = false;
        Go.setDisable(true);
        Save.setDisable(true);
        Open.setDisable(true);
        Pause.setDisable(false);
    }
    
    private void pauseHandler(){
        Paused = true;
        Pause.setDisable(true);
        Go.setDisable(false);
        Save.setDisable(false);
        Open.setDisable(false);
    }
    
    private void settingsHandler(){
        
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
        Open = new MenuItem("Open");
        Open.setOnAction(e -> openHandler());
        Save = new MenuItem("Save");
        Save.setOnAction(e -> saveHandler());
        MenuItem quitMenuItem = new MenuItem("_Quit");
        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                KeyCombination.CONTROL_DOWN));
        quitMenuItem.setOnAction(actionEvent -> Platform.exit());
        fileMenu.getItems().addAll(Open, Save, quitMenuItem);
        //Game menu with new pause go and settings
        Menu gameMenu = new Menu("Game");
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setOnAction(e -> newHandler());
        Go = new MenuItem("Go");
        Go.setOnAction(e -> goHandler());
        Pause = new MenuItem("Pause");
        Pause.setOnAction(e -> pauseHandler());
        Settings = new MenuItem("Settings");
        Settings.setOnAction(e -> settingsHandler());
        gameMenu.getItems().addAll(newMenuItem,(new SeparatorMenuItem()), Go, Pause, Settings);
        // Help menu with just an about item for now
        Menu helpMenu = new Menu("_Help");
        MenuItem aboutMenuItem = new MenuItem("_About");
        aboutMenuItem.setOnAction(actionEvent -> onAbout());
        helpMenu.getItems().add(aboutMenuItem);
        menuBar.getMenus().addAll(fileMenu, gameMenu, helpMenu);
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
