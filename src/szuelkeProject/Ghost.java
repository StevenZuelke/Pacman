/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szuelkeProject;

import javafx.scene.canvas.Canvas;


/**
 *
 * @author StevenZuelke
 */
public class Ghost{
    
    double Speed;
    //Is this the player or a ghost
    Boolean Player;
    int Direction; //Clockwise 0 at top
    int Step;
    int I; //X index on board
    int J; //Y index on board
    
    public Ghost(Boolean player, double speed, int i, int j){
        this.Speed = speed;
        this.Player= player;
        this.I = i;
        this.J = j;
    }
    
}
