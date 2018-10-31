package com.company.game;

import java.util.Set;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class State {

    private int size;
    private Player player;
    private Obstacles obstacles;
    private Goal goal;

    public State(int size) {
        this.size = size;
        this.player = new Player(size);
        this.obstacles = new Obstacles(size);
        this.goal = new Goal(size);
    }

    public void stateInit(Position playerP, Position goalP) {
        try {
            this.player.placeObject(playerP);
            this.goal.placeObject(goalP);
        } catch (GameException e) {
            System.out.println("Initialization failed: " + e.getMessage());
        }
    }

    public void obstaclesInit(Set<Position> oList) {
        try {
            this.obstacles.placeObjects(oList);
        } catch (GameException e) {
            System.out.println("Initialization failed: " + e.getMessage());
        }
    }

    public Position getPlayerPosition() {
        try {
            return this.player.getFirstObject();
        } catch(GameException e) {
            System.out.println(e.getMessage());
        }
        return new Position(-1,-1);
    }

    public Position getGoalPosition() {
        try {
            return this.goal.getFirstObject();
        } catch(GameException e) {
            System.out.println(e.getMessage());
        }
        return new Position(-1,-1);
    }

    public Set<Position> getObstaclePosition() {
        return this.obstacles.getAllObjects();
    }

    public void makeMove(int nodeID) {

        
        Position playerP = this.getPlayerPosition();
        this.player.movePlayer(playerP.getX(), playerP.getY(), nodeID/size, nodeID%size);

    }

    public double getReward() {

        Position playerP = this.getPlayerPosition();
        Position goalP = this.getGoalPosition();
        Set<Position> obstaclesP = this.getObstaclePosition();

        if(playerP.equals(goalP))
            return 1;
        if(obstaclesP.contains(playerP))
            return -1;
        return -1.0/(size*size);

    }

    public void printState() {

        Position playerP = this.getPlayerPosition();
        Position goalP = this.getGoalPosition();
        Set<Position> obstaclesP = this.getObstaclePosition();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (obstaclesP.contains(new Position(i, j))) {
                    System.out.print("O\t");
                } else if (playerP.equals(new Position(i, j))) {
                    System.out.print("P\t");
                } else if (goalP.equals(new Position(i, j))) {
                    System.out.print("G\t");
                } else {
                    System.out.print("-\t");
                }
            }
            System.out.println();
        }

    }

    public INDArray getINDArrayFromState() {

        double[][][] completeState = new double[3][size][size];
        completeState[0] = this.player.getGrid();
        completeState[1] = this.goal.getGrid();
        completeState[2] = this.obstacles.getGrid();

        INDArray INDState = Nd4j.create(completeState);

        return INDState;
    }



    //public void stateRandInit()
}
