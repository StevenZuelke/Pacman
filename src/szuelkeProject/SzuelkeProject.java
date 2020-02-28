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
import java.util.Optional;
import java.util.prefs.Preferences;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    MenuItem Pause, Go, Save, Open, Settings;
    Stage Stage;
    VBox Vbox;
    
    @Override
    public void start(Stage primaryStage) {
        Timer = new AnimationTimer(){
            @Override
            public void handle(long now){
                onTimer(now);
            }
        };
        ChangeListener winListener = new ChangeListener<Integer>(){
            @Override
            public synchronized void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue){
                win(newValue);
            }
        };
        ChangeListener loseListener = new ChangeListener<Integer>(){
            @Override
            public synchronized void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue){
                lose(newValue);
            }
        };
        Board = new Board(1);
        newHandler();
        //GameDisplay
        Label lvlLabel = new Label("Level: " );
        lvlLabel.textProperty().bind(Board.LvlString);
        Label scoreLabel = new Label("Score: " );
        scoreLabel.textProperty().bind(Board.ScoreString);
        Label cakesLabel = new Label("Cakes Left: ");
        cakesLabel.textProperty().bind(Board.CakeString);
        Vbox = new VBox();
        Vbox.getChildren().addAll(lvlLabel, Board, scoreLabel);
        VBox.setVgrow(Board, Priority.ALWAYS);
        BorderPane root = new BorderPane();
        root.setCenter(Vbox);
        //add the menus
        root.setTop(buildMenuBar());
        //Cakes on the status bar
        ToolBar toolBar = new ToolBar(cakesLabel);
        root.setBottom(toolBar);
        Scene scene = new Scene(root, 600, 500);
        Stage = primaryStage;
        Stage.setTitle("Slab Game");
        Stage.setScene(scene);
        scene.addEventHandler(KeyEvent.KEY_PRESSED , e -> pauseGo(e));
        Stage.show();
        Timer.start();
        Board.Win.addListener(winListener);
        Board.Dead.addListener(loseListener);
    }
    
    private void lose(int newVal){
        if(newVal != 1) return;
        Board.Dead.set(0);
        pauseHandler();
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("You Suck!");
        alert.setContentText("You got killed by an evil ghost");
        Platform.runLater(() -> {
            alert.showAndWait();
            newHandler();
                });
    }
    
    private void win(int newVal){
        if(newVal != 1) return;
        pauseHandler();
        if(Board.Level.get() == 1){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("You Beat Level 1");
            alert.setHeaderText("You Beat Level 1!");
            alert.setContentText("Congratulations! (Press anyhing to continue)");
            Platform.runLater(() -> alert.showAndWait());
            Board.setupLevelTwo();
        }else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("You Beat the game");
            alert.setHeaderText("You Beat the Game!");
            alert.setContentText("Congratulations! \n"
                    + "You Scored: "+ Board.Score.get() +"\n(Press anyhing to continue)");
            Platform.runLater(() -> alert.showAndWait());
            newHandler();
        }
        
    }
    
    
    private void readBoard(SavableBoard sboard){
        Board.Board = sboard.Board;
        Board.Score.set(sboard.Score);
        Board.Ghosts = sboard.Ghosts;
        Board.Level.set(sboard.Level);
        Board.StartGhosts = sboard.StartGhosts;
        Board.AddGhosts = sboard.AddGhosts;
        Platform.runLater(()->Board.Prefs.putInt("StartGhosts", sboard.StartGhosts));
        Platform.runLater(()->Board.Prefs.putInt("AddGhosts", sboard.AddGhosts));
        Board.CakesLeft.set(sboard.CakesLeft);
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
            //Platform.runLater(()->readBoard(openBoard));
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
        Board.newGame();
    }
    
    private void pauseGo(KeyEvent e)
    {
        if(e.getCode() == KeyCode.SPACE){
            if(Board.Paused) goHandler();
            else pauseHandler();
        }else Board.keyPressed(e);
    }
    
    private void goHandler(){
        Board.Paused = false;
        Go.setDisable(true);
        Save.setDisable(true);
        Open.setDisable(true);
        Pause.setDisable(false);
    }
    
    private void pauseHandler(){
        Board.Paused = true;
        Pause.setDisable(true);
        Go.setDisable(false);
        Save.setDisable(false);
        Open.setDisable(false);
    }
    
    private void settingsHandler(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        VBox vbox = new VBox();
        HBox hboxtop = new HBox();
        HBox hboxbot = new HBox();
        ComboBox startCombo = new ComboBox();
        ComboBox addCombo = new ComboBox();
        startCombo.getItems().addAll(1, 2, 3, 4);
        addCombo.getItems().addAll(1,2,3,4);
        startCombo.setValue(Board.StartGhosts);
        addCombo.setValue(Board.AddGhosts);
        Label topLabel = new Label("Ghosts at level 1");
        Label botLabel = new Label("Additional Ghosts on Level 2");
        hboxtop.getChildren().addAll(startCombo, topLabel);
        hboxbot.getChildren().addAll(addCombo, botLabel);
        vbox.getChildren().addAll(hboxtop, hboxbot);
        alert.setTitle("Settings");
        alert.getDialogPane().setContent(vbox);
        vbox.setPrefWidth(350);
        Optional<ButtonType> result = alert.showAndWait();
        //check the dropdowns for prefs
        if(result.isPresent() && result.get() == ButtonType.OK){
            if(startCombo.getValue() != null){
                Platform.runLater(()->Board.Prefs.putInt("StartGhosts", (int) startCombo.getValue()));
            }
            if(addCombo.getValue() != null){
                Platform.runLater(()->Board.Prefs.putInt("AddGhosts", (int) addCombo.getValue()));
            }
        }
        
        
    }
    
    private void onTimer(long now){
        Board.onTimer(now);
    }
    
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Steven Zuelke, CSCD 370 SlabMan, Wtr 2020");
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
        Pause.setDisable(true);
        Pause.setOnAction(e -> pauseHandler());
        Settings = new MenuItem("Settings");
        Settings.setOnAction(e -> settingsHandler());
        gameMenu.getItems().addAll(newMenuItem,(new SeparatorMenuItem()), Go, Pause,(new SeparatorMenuItem()), Settings);
        // Help menu with just an about item for now
        Menu helpMenu = new Menu("_Help");
        MenuItem aboutMenuItem = new MenuItem("_About");
        aboutMenuItem.setOnAction(actionEvent -> onAbout());
        helpMenu.getItems().add(aboutMenuItem);
        menuBar.getMenus().addAll(fileMenu, gameMenu, helpMenu);
        return menuBar;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
