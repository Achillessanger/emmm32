package com.huawei.bean;

public class Road {
    private int id;
    private int length;
    private int speed;
    private int channel;
    private Cross from;
    private Cross to;
    private boolean isDuplex;
    private Car[][] carsOnRoad;
    private Road roadReverse = null;

    public Road(int id, int length, int speed, int channel, Cross from, Cross to, boolean isDuplex) {
        this.id = id;
        this.length = length;
        this.speed = speed;
        this.channel = channel;
        this.from = from;
        this.to = to;
        this.isDuplex = isDuplex;
        this.carsOnRoad = new Car[channel][length];
        for(int i = 0; i < carsOnRoad.length; i++) {
            for (int j = 0; j < carsOnRoad.length; j++) {
                carsOnRoad[i][j] = null;
            }
        }
        if(isDuplex){
            this.roadReverse = new Road(id,length,speed,channel,to,from);
            this.roadReverse.setDuplex(isDuplex);
            this.roadReverse.roadReverse = this;
        }
    }
    public Road(int id, int length, int speed, int channel, Cross from, Cross to) {
        this.id = id;
        this.length = length;
        this.speed = speed;
        this.channel = channel;
        this.from = from;
        this.to = to;
        this.carsOnRoad = new Car[channel][length];
        for(int i = 0; i < carsOnRoad.length; i++) {
            for (int j = 0; j < carsOnRoad.length; j++) {
                carsOnRoad[i][j] = null;
            }
        }
    }


    public Car[][] getCarsOnRoad() {
        return carsOnRoad;
    }

    public void setCarsOnRoad(Car[][] carsOnRoad) {
        this.carsOnRoad = carsOnRoad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
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

    public boolean isDuplex() {
        return isDuplex;
    }

    public void setDuplex(boolean duplex) {
        isDuplex = duplex;
    }
}
