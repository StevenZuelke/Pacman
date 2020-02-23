/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szuelkeProject;

import java.util.ArrayList;
import java.util.Random;
import javafx.scene.canvas.Canvas;


/**
 *
 * @author StevenZuelke
 */
public class Ghost{
    
    double Speed;
    //Is this the player or a ghost
    Boolean Player;
    Boolean Moving;
    int Direction; //Clockwise 0 at top
    int Step;
    long PrevMoveTime;
    int I; //X index on board
    int J; //Y index on board
    
    public void changeDirection(int prevDir){
        ArrayList<Integer> list = new ArrayList();
        int nextInd = 0;
        int iL, iR, jL, jR;
        iL = I-1;
        iR = I+1;
        jL = J-1;
        jR = J+1;
        if(iL < 0) iL = 19;
        if(iL > 19) iL = 0;
        if(iR < 0) iR = 19;
        if(iR > 19) iR = 0;
        if(jL < 0) jL = 19;
        if(jL > 19) jL = 0;
        if(jR < 0) jR = 19;
        if(jR > 19) jR = 0;
        
        //Possible directions to go (excluding turning around)
        if(Board.Maze[I][jL] == 1) if(prevDir != 2) list.add(0);
        if(Board.Maze[iR][J] == 1) if(prevDir != 3) list.add(1);
        if(Board.Maze[I][jR] == 1) if(prevDir != 0) list.add(2);
        if(Board.Maze[iL][J] == 1) if(prevDir != 1) list.add(3);
        int index = Math.abs((new Random().nextInt()) % list.size());
        Direction = list.get(index);
        Moving = true;
    }
       
    public void checkSteps(){
        //System.out.println("CHecking steps");
        if(Step > 3){
            Boolean cont = false;
            int nextInd = 0;
            switch(Direction){
                case(0):
                    J--;
                    if(J < 0) J = 19;
                    nextInd = J-1;
                    if(nextInd < 0) nextInd = 20+nextInd;
                    if(Board.Maze[I][nextInd] == 1) {
                        cont = true;
                    }
                    break;
                case(1):
                    I++;
                    if(I > 19) I = 0;
                    nextInd = I+1;
                    if(nextInd > 19) nextInd = nextInd-20;
                    if(Board.Maze[nextInd][J] == 1) {
                        cont = true;
                    }
                    break;
                case(2):
                    J++;
                    if(J > 19) J = 0;
                    nextInd = J+1;
                    if(nextInd > 19) nextInd = nextInd-20;
                    if(Board.Maze[I][nextInd] == 1){
                        cont = true;
                    }
                    break;
                case(3):
                    I--;
                    if(I < 0) I = 19;
                    nextInd = I-1;
                    if(nextInd < 0) nextInd = 20+nextInd;
                    if(Board.Maze[nextInd][J] == 1){
                        cont = true;
                    }
                    break;
            }
            if(!cont){
                Moving = false;
                Step = 0;
            }else{
                Step = Step % 4;
            }
            if(!Player) changeDirection(Direction);
        }
    }
    
    public Ghost(Boolean player, double speed, int i, int j){
        this.Speed = speed;
        this.Player= player;
        this.I = i;
        this.J = j;
        this.Direction = 0;
        this.Step = 0;
        this.Moving = false;
        this.PrevMoveTime = System.currentTimeMillis();
    }
    
}
