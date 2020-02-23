/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szuelkeProject;

import java.util.ArrayList;
import java.util.Random;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 *
 * @author StevenZuelke
 */
public class Board extends Region{
    int[][] Board = new int[20][20];
    static int[][] Maze = new int[20][20];
    Canvas Canvas;
    ArrayList<Ghost> Ghosts = new ArrayList<Ghost>();
    long PreviousTime = System.currentTimeMillis();
    
    public void keyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.DOWN){
         playerDirectionChange(e);  
        }
        draw();
    }
    
    public void playerDirectionChange(KeyEvent e){
        Ghost player = getPlayer();
        int prevDirection = player.Direction;
        if(player.Step == 3){ //If player decides to turn and almost in next square give it to them
            player.Step = 4;
            player.checkSteps();
        }
        if(e.getCode() == KeyCode.UP){
            player.Direction = 0;
        }
        if(e.getCode() == KeyCode.RIGHT){
            player.Direction = 1;
        }
        if(e.getCode() == KeyCode.DOWN){
            player.Direction = 2;
        }
        if(e.getCode() == KeyCode.LEFT){
            player.Direction = 3;
        }
        if(checkNextSpace(player)){
            player.Moving = true;
        }else{
           
            player.Moving = false;
            player.Step = 0;
        }
    }
    
    public Boolean checkNextSpace(Ghost g){
        Boolean clear = false;
        int nextInd = 0;
        switch(g.Direction){
            case(0):
                nextInd = g.J-1;
                if(nextInd < 0) nextInd = 20+nextInd;
                if(Maze[g.I][nextInd] == 1) {
                    clear = true;
                }
                break;
            case(1):
                nextInd = g.I+1;
                if(nextInd > 19) nextInd = nextInd-20;
                if(Maze[nextInd][g.J] == 1) {
                    clear = true;
                }
                break;
            case(2):
                nextInd = g.J+1;
                if(nextInd > 19) nextInd = nextInd-20;
                if(Maze[g.I][nextInd] == 1){
                    clear = true;
                }
                break;
            case(3):
                nextInd = g.I-1;
                if(nextInd < 0) nextInd = 20+nextInd;
                if(Maze[nextInd][g.J] == 1){
                    clear = true;
                }
                break;   
        }
        return clear;
    }
    
    public void onTimer(long now, Boolean paused){
        now = System.currentTimeMillis();
        if(paused) PreviousTime = now;
        else{
            double elapsed = (now-PreviousTime);
            double elapsecond = elapsed/1000.0;
            if(elapsed>50.0){
                for(Ghost g : Ghosts){
                    long deltaT = now - g.PrevMoveTime;
                    double tNeed = 250.0 / g.Speed;
                    int steps = (int) (deltaT / tNeed);
                    if(g.Moving) g.Step += steps;
                    if(steps > 0) g.PrevMoveTime = now;
                    g.checkSteps();
                }
                draw();
                PreviousTime = System.currentTimeMillis();
            }
        } 
    }
    public void draw(){ //draw everything  
        initCanvas();
        drawBoard();
        drawGhosts();
    }
    
    public void drawGhosts(){
        //Puts ghost at specified index in the board
        double width = Canvas.getWidth()/20.0;
        GraphicsContext gc = Canvas.getGraphicsContext2D();
        gc.save();
        for(Ghost g : Ghosts){ //Draw each ghost
            gc.save();
            gc.translate(width*g.I, width*g.J); //Translate to index location
            gc.translate(width*.5, width*.5); //Center of that spot
            switch(g.Direction){  //Rotate to direction ghost is moving
                case(1): gc.rotate(90); break;
                case(2): gc.rotate(180); break;
                case(3): gc.rotate(270); break;
            }
            gc.translate(0, -width*g.Step/4); //Step toward the target index location
            if(g.Player) gc.setFill(Color.DARKGREEN);
            else gc.setFill(Color.CRIMSON);
            gc.fillOval(-width*.75*.5, -width*.75*.5,width*.75,width*.75);
            //Create Mouth
            double[] xPoints = new double[3];
            double[] yPoints = new double[3];
            xPoints[0] = 0;
            yPoints[0] = 0;
            Boolean fillPoly = true;
            //Determine how open the mouth is
            switch(g.Step){
                case(0): fillPoly = false;
                case(1): 
                    xPoints[1] = -.15*.75*width;
                    xPoints[2] = -xPoints[1];
                    yPoints[1] = -.5*.75*width;
                    yPoints[2] = yPoints[1];
                    break;
                case(2):
                    xPoints[1] = -.3*.75*width;
                    xPoints[2] = -xPoints[1];
                    yPoints[1] = -.5*.75*width;
                    yPoints[2] = yPoints[1];
                    break;
                case(3): 
                    xPoints[1] = -.15*.75*width;
                    xPoints[2] = -xPoints[1];
                    yPoints[1] = -.5*.75*width;
                    yPoints[2] = yPoints[1];
                    break;
        }
            gc.setFill(Color.CHOCOLATE);
            if(fillPoly) gc.fillPolygon(xPoints, yPoints, 3);
            gc.restore();
        }
        gc.restore();
    }
    
    public void drawBoard(){ //Draw the board according to the array
        GraphicsContext g = Canvas.getGraphicsContext2D();
        g.save();       
        double width = Canvas.getWidth()/20.0;
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 20; j++){
                if(Board[i][j] == 0) g.setFill(Color.BLACK);
                else g.setFill(Color.CHOCOLATE);
                g.fillRect(width*(double)i, width*(double)j, width, width);
                if(Board[i][j] == 2){
                    //Fill dots
                    g.setFill(Color.WHEAT);
                    g.fillOval(width*i+width/4, width*j+width/4, width/2, width/2);
                }
            }
        }
        g.restore();
    }
    
    public void initCanvas(){ //Clear canvas
        GraphicsContext g = Canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0,0,Canvas.getWidth(),Canvas.getHeight());
    }
    
    public Ghost getPlayer(){ //Find the ghost that is the player
        for(Ghost g : Ghosts){
            if(g.Player) return g;
        }
        return null;
    }
    
    public void addGhosts(int numGhosts, double speed){ //Fill the nonplayer Ghosts in
        ArrayList<Integer[]> takenSpots = new ArrayList<Integer[]>();
        Integer[] ints = new Integer[2];
        for(int i = 0; i < numGhosts; i++){
            ints[0] = (new Random()).nextInt(20);
            ints[1] = (new Random()).nextInt(20);
            while(takenSpots.contains(ints) || Board[ints[0]][ints[1]] == 0 //So Ghost is not in same spot as another, Ghost is not in a wall
                    && !((ints[0] == 9 && ints[1] < 16 && ints[1] > 3) || (ints[1] == 9 && ints[0] < 16 && ints[0] > 3))){ //Dont spawn ghosts in center area by the player spawn 
                ints[0] = (new Random()).nextInt(20);
                ints[1] = (new Random()).nextInt(20);
            }
            Ghost g = new Ghost(false, speed, ints[0], ints[1]);
            g.changeDirection(4);//any direction to start moving
            Ghosts.add(g);
        }
    }
    
    public void addPlayer(){
        Ghosts.add(new Ghost(true, 1, 9, 9));
    }
    
    @Override
    protected void layoutChildren(){
        super.layoutChildren();
        if(this.getWidth() < this.getHeight()){
            Canvas.setWidth(this.getWidth());
            Canvas.setHeight(this.getWidth());
        }else{
            Canvas.setWidth(this.getHeight());
            Canvas.setHeight(this.getHeight());
        }
        layoutInArea(Canvas, 0, 0, this.getWidth(), this.getHeight(), 0, HPos.CENTER, VPos.CENTER);
        draw();
    }
    
    public Board(){
        Canvas = new Canvas(this.getWidth(),this.getHeight());
        this.getChildren().add(Canvas);
        this.addEventHandler(KeyEvent.KEY_PRESSED, e -> keyPressed(e));
        //Fill in hardcoded Board with 2 to represent there is a dot or 0 for wall
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 20; j++){
                if(i == 1 || i == 18 || j == 1 || j == 18){
                    //end rows get dots
                    Board[i][j] = 2;
                    Maze[i][j] = 1;
                }else if((i == 9 && (j == 0 || j == 19))
                        || (j == 9 && (i == 0 || i == 19))){
                    //Middle shortcuts get dots
                    Board[i][j] = 2;
                    Maze[i][j] = 1;
                }else if(((i == 5 || i == 13) && (j == 2 || j == 17))
                        || ((j == 5 || j == 13) && (i == 2 || i == 17))){
                    //Inward path gets dots
                    Board[i][j] = 2;
                    Maze[i][j] = 1;
                }else if((i >= 5 && i<= 13 && (j == 3 || j == 16)) 
                        || (j >= 5 && j<= 13 && (i == 3 || i == 16))){
                    //Inward hallways get dots
                    Board[i][j] = 2;
                    Maze[i][j] = 1;
                }else if((i == 9 && j >= 4 && j <= 15) 
                        || (j == 9 && i >= 4 && i <= 15)){
                    //Middle cross gets dots
                    Board[i][j] = 2;
                    Maze[i][j] = 1;
                }else if(((i == 6 || i == 12) && (j >= 6 && j <= 12))
                        ||((j == 6 || j == 12) && (i >= 6 && i <= 12)) ){
                    //Middle square gets dots
                    Board[i][j] = 2;
                    Maze[i][j] = 1;
                }else{
                    //Everywhere else gets walls
                    Board[i][j] = 0;
                    Maze[i][j] = 0;
                }
            }
        }
    }
    
}
