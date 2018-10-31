package com.company.game;

public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /*
    public Position fromKtoXY(int k, int size) {
        int x = k/size;
        int y = k%size;
        return new Position(x,y);
    }
    */
    public int fromXYtoK(int size) {
        return this.getX()*size + this.getY();
    }

    @Override
    public String toString() {
        return "[" + this.x + "][" + this.y + "]";
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Position)) {
            return false;
        }

        Position p = (Position) o;

        return this.getX() == p.getX() && this.getY() == p.getY();
    }

    @Override
    public int hashCode() {

        int result = 17;

        result = 31 * result + x;
        result = 31 * result + y;

        return result;
    }

}
