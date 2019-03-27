package com.huawei.bean;

import java.util.ArrayList;

public class Car {
    private int id;
    private Cross from;//始发地和目的地似乎都是路口
    private Cross to;
    private int speed;
    private int startTime;
    private ArrayList<Road> Route;

    public Car(int id, Cross from, Cross to, int speed, int startTime) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.speed = speed;
        this.startTime = startTime;
        Route = new ArrayList<>();
    }

    public void addRoad(Road road){
        Route.add(road);
    }

    public ArrayList<Road> getRoute(){
        return Route;
    }

    public void setRoute(ArrayList<Road> Route){
        this.Route = Route;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cross getFrom() {
        return from;
    }

    public void setFrom(Cross from) {
        this.from = from;
    }

    public Cross getTo() {
        return to;
    }

    public void setTo(Cross to) {
        this.to = to;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
}