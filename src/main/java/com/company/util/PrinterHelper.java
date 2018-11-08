package com.company.util;

public class PrinterHelper {

    public void printGrid(char[] chars, int[][] grid) {
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid[i].length; j++) {
                System.out.print(chars[grid[i][j]] + "\t");
            }
            System.out.println("");
        }
    }

}
