/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package szuelkeProject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author StevenZuelke
 */
public class SavableBoard implements Serializable{
    
    int[][] Board = new int[20][20];
    ArrayList<Ghost> Ghosts = new ArrayList<Ghost>();
    int Score;
    int Level;
    
    public SavableBoard(Board board){
        this.Board = board.Board;
        this.Ghosts = board.Ghosts;
        this.Score = board.Score.get();
        this.Level = board.Level.get();
    }
}
