package com.huawei.bean;

import java.util.ArrayList;

public class Car {
    private int id;
    private Cross from;//始发地和目的地似乎都是路口
    private Cross to;
    private int speed;
    private int startTime;
    private ArrayList<Road> Route;
    private int state = 0;//0终止 1等待
    private int startTimeInAnswerSheet;
    private boolean isFinished = false;

    public Car(int id, Cross from, Cross to, int speed, int startTime) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.speed = speed;
        this.startTime = startTime;
        Route = new ArrayList<>();
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getStartTimeInAnswerSheet() {
        return startTimeInAnswerSheet;
    }

    public void setStartTimeInAnswerSheet(int startTimeInAnswerSheet) {
        this.startTimeInAnswerSheet = startTimeInAnswerSheet;
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
