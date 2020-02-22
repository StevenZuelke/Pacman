/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szuelkeProject;

import java.util.ArrayList;
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
    Canvas Canvas;
    ArrayList<Ghost> Ghosts = new ArrayList<Ghost>();
    
    public void keyPressed(KeyEvent e){
        if(e.getCode() == KeyCode.RIGHT){
            Ghosts.get(Ghosts.indexOf(getPlayer())).Step = (getPlayer().Step + 1) % 4;
        }
        if(e.getCode() == KeyCode.UP){
            Ghosts.get(Ghosts.indexOf(getPlayer())).Direction = (getPlayer().Direction + 1) % 4;
        }
        draw();
    }
    
    public void draw(){
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
            //gc.translate(width*.125, width*.125);
            gc.fillOval(-width*.75*.5, -width*.75*.5,width*.75,width*.75);
            //gc.translate(width*.75*.5, width*.75*.5);
            //Create Mouth
            double[] xPoints = new double[3];
            double[] yPoints = new double[3];
            xPoints[0] = 0;
            yPoints[0] = 0;
            Boolean fillPoly = true;
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
    
    public void drawBoard(){
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
    
    public void initCanvas(){
        GraphicsContext g = Canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0,0,Canvas.getWidth(),Canvas.getHeight());
    }
    
    public Ghost getPlayer(){
        for(Ghost g : Ghosts){
            if(g.Player) return g;
        }
        return null;
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
        Ghosts.add(new Ghost(true, 10, 1, 1));
        Ghosts.get(0).Direction = 0;
        Ghosts.get(0).Step = 0;
        //Fill in hardcoded Board with 2 to represent there is a dot or 0 for wall
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 20; j++){
                if(i == 1 || i == 18 || j == 1 || j == 18){
                    //end rows get dots
                    Board[i][j] = 2;
                }else if((i == 9 && (j == 0 || j == 19))
                        || (j == 9 && (i == 0 || i == 19))){
                    //Middle shortcuts get dots
                    Board[i][j] = 2;
                }else if(((i == 5 || i == 13) && (j == 2 || j == 17))
                        || ((j == 5 || j == 13) && (i == 2 || i == 17))){
                    //Inward path gets dots
                    Board[i][j] = 2;
                }else if((i >= 5 && i<= 13 && (j == 3 || j == 16)) 
                        || (j >= 5 && j<= 13 && (i == 3 || i == 16))){
                    //Inward hallways get dots
                    Board[i][j] = 2;
                }else if((i == 9 && j >= 4 && j <= 15) 
                        || (j == 9 && i >= 4 && i <= 15)){
                    //Middle cross gets dots
                    Board[i][j] = 2;
                }else if(((i == 6 || i == 12) && (j >= 6 && j <= 12))
                        ||((j == 6 || j == 12) && (i >= 6 && i <= 12)) ){
                    //Middle square gets dots
                    Board[i][j] = 2;
                }else{
                    //Everywhere else gets walls
                    Board[i][j] = 0;
                }
            }
        }
    }
    
}
