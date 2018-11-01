package com.company.game;

public class Player extends StateObject {

    public Player(int size) {
        super(size);
    }

/*    public Player(int size, int[] conf) throws GameException {

        if(conf.length!=1) {
            throw new GameException("Exactly one player on the grid!");
        }

        this.grid = new double[size][size];
        this.size = size;

        Position playerP = new Position(conf[0]/size, conf[0]%size);

    }*/

    public void movePlayer(int currX, int currY, int nextX, int nextY) {

        this.grid[currX][currY] = 0;
        this.grid[nextX][nextY] = 1;

    }

}
