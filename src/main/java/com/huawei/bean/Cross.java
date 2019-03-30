package com.huawei.bean;

import java.util.Arrays;

public class Cross implements Comparable<Cross> {
    private int id;
    private int[] road = new int[4];
    private int[] roadSorted = new int[4];
    //用于Dijkstra的变量
    private int min=2147483647;
    private boolean isDealt;
    private Cross pre;

    public Cross getPre() {
        return pre;
    }

    public void setPre(Cross pre) {
        this.pre = pre;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public boolean isDealt() {
        return isDealt;
    }

    public void setDealt(boolean dealt) {
        isDealt = dealt;
    }

    public Cross(int id, int road1, int road2, int road3, int road4) {
        this.id = id;
        this.road[0] = this.roadSorted[0] = road1;
        this.road[1] = this.roadSorted[1] = road2;
        this.road[2] = this.roadSorted[2] = road3;
        this.road[3] = this.roadSorted[3] = road4;
        Arrays.sort(roadSorted);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[] getRoad() {
        return road;
    }

    public void setRoad(int[] road) {
        this.road = road;
    }

    public int[] getRoadSorted() {
        return roadSorted;
    }

    @Override
    public int compareTo(Cross o) {
        if (this.min > o.min)
            return 1;
        else if (this.min == o.min)
            return 0;
        else
            return -1;
    }
}
