package com.huawei.bean;

import java.util.HashMap;

public class Graph {
    private HashMap<Integer, Cross> vertices;

    public HashMap<Integer, Cross> getVertices() {
        return vertices;
    }

    public void setVertices(HashMap<Integer, Cross> vertices) {
        this.vertices = vertices;
    }
}
