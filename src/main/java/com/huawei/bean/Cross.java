package com.huawei.bean;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Cross {
    private int id;
    private int[] road = new int[4];
    private int[] roadSorted = new int[4];

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

}
