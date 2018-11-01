package com.company.newgame;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class NewState {

    private int playerID;
    private int goalID;
    private int[] obstacleIDs;
    private int size;
    private int numID;

    public NewState(int playerID, int goalID, int[] obstacleIDs, int size) {
        this.playerID = playerID;
        this.goalID = goalID;
        this.obstacleIDs = obstacleIDs;
        this.size = size;
        this.numID = size*size;
    }

    public int getNumID() {
        return numID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public INDArray getINDArray() {

        // three objects made up the state: player, goal, obstacles
        double[] completeState = new double[this.numID*3];
        completeState[this.playerID] = 1;
        completeState[this.goalID+this.numID] = 1;
        for (int obstacle: obstacleIDs) {
            completeState[obstacle+this.numID+this.numID] = 1;
        }

        return Nd4j.create(completeState);


    }

    public double getReward() {

        if(this.playerID == this.goalID)
            return 1;
        for(int i = 0; i < this.obstacleIDs.length; i++) {
            if (this.playerID == this.obstacleIDs[i]) {
                return -1;
            }
        }
        return -1.0/this.numID;



    }
}
