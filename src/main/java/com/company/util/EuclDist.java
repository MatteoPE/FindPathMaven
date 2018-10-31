package com.company.util;

import com.company.model.Node;

public class EuclDist
{
    /**
     * Finds 2D Euclidean distance between two points
     * @param x1 - x coordinate of the source
     * @param y1 - y coordinate of the source
     * @param x2 - x coordinate of the destination
     * @param y2 - y coordinate of the destination
     * @return 2D Euclidean distance
     */
    public static double d(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * Finds 2D Euclidean distance between two nodes
     * @param src - source node
     * @param dst - destination node
     * @return 2D Euclidean distance
     */
    public static double d(Node src, Node dst)
    {
        return d(src.getX(), src.getY(), dst.getX(), dst.getY());
    }
}
