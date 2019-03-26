package com.huawei.bean;

public class Cross {
    private int id;
    private Road road1;
    private Road road2;
    private Road road3;
    private Road road4;

    public Cross(int id, Road road1, Road road2, Road road3, Road road4) {
        this.id = id;
        this.road1 = road1;
        this.road2 = road2;
        this.road3 = road3;
        this.road4 = road4;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Road getRoad1() {
        return road1;
    }

    public void setRoad1(Road road1) {
        this.road1 = road1;
    }

    public Road getRoad2() {
        return road2;
    }

    public void setRoad2(Road road2) {
        this.road2 = road2;
    }

    public Road getRoad3() {
        return road3;
    }

    public void setRoad3(Road road3) {
        this.road3 = road3;
    }

    public Road getRoad4() {
        return road4;
    }

    public void setRoad4(Road road4) {
        this.road4 = road4;
    }
}
