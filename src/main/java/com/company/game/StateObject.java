package com.company.game;

import javafx.geometry.Pos;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class StateObject {

    protected double[][] grid;
    protected int size;

    public StateObject(int size) {
        grid = new double[size][size];
        this.size = size;
    }

    public double[][] getGrid() {
        return grid;
    }

    public void placeObject(Position p) throws GameException{

        int x = p.getX();
        int y = p.getY();
        if(x < 0 || y < 0 || x >= this.size || y >= this.size) {
            throw new GameException("You are trying to place the object outside the grid!");
        }
        grid[x][y] = 1;
        return;

    }

    public void placeObjects(Set<Position> pList) throws GameException{

        for(Position p: pList) {
            placeObject(p);
        }

    }

    /*
    public Position placeObjectRandomly() {
        Random rand = new Random();
        int x = rand.nextInt(this.size);
        int y = rand.nextInt(this.size);
        grid[x][y] = 1;
        return new Position(x, y);
    }
    */

    public Position getFirstObject() throws GameException{
        for(int x = 0; x < this.size; x++) {
            for(int y = 0; y < this.size; y++) {
                if(this.grid[x][y] == 1) {
                    return new Position(x,y);
                }
            }
        }
        throw new GameException("This StateObject has no object!");
    }

    public Set<Position> getAllObjects() {

        Set<Position> oSet = new HashSet<>();

        for(int x = 0; x < this.size; x++) {
            for(int y = 0; y < this.size; y++) {
                if(this.grid[x][y] == 1) {
                    oSet.add(new Position(x,y));
                }
            }
        }

        return oSet;

    }

}
