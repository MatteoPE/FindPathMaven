package com.company.model;

import java.util.ArrayList;
import java.util.List;

public class Obstacle
{
    private int id;
    private double x;
    private double y;
    private double qx;
    private double qy;
    private double r;
    private double q;
    private List<Double> xx;
    private List<Double> yy;
    private List<Double> qq;

    public Obstacle(int id, double x, double y, double r)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
        this.q = 0;
        this.qx = this.x;
        this.qy = this.y;
        this.xx = new ArrayList<>();
        this.yy = new ArrayList<>();
        this.qq = new ArrayList<>();
    }


    public int getId()
    {
        return this.id;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public int numSecCharges()
    {
        return this.qq.size();
    }

    public double getXX(int i)
    {
        if (i < xx.size())
            return xx.get(i);
        else
            throw new RuntimeException("Unknown hole with index i=" + i);
    }

    public double getYY(int i)
    {
        if (i < yy.size())
            return yy.get(i);
        else
            throw new RuntimeException("Unknown hole with index i=" + i);
    }

    public double getQx()
    {
        return this.qx;
    }

    public double getQy()
    {
        return this.qy;
    }

    public double getR()
    {
        return this.r;
    }

    public double getQ()
    {
        return this.q;
    }

    public void setQ(double q)
    {
        this.q = q;
    }

    public void setQx(double qx)
    {
        this.qx = qx;
    }

    public void setQy(double qy)
    {
        this.qy = qy;
    }

    public String toString()
    {
        return "hole" + id + " (" + x + "," + y + "), r=" + r;
    }

    public int hashCode()
    {
        return toString().hashCode();
    }

    public boolean equals(Object o)
    {
        if (o != null)
            return this.hashCode() == o.hashCode();
        else
            return false;
    }


}
