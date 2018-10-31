package com.company.game;

public class Player extends StateObject {

    public Player(int size) {
        super(size);
    }

    public void movePlayer(int currX, int currY, int nextX, int nextY) {

        this.grid[currX][currY] = 0;
        this.grid[nextX][nextY] = 1;

    }

}
