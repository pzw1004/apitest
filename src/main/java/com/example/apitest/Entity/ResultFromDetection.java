package com.example.apitest.Entity;

public class ResultFromDetection {
    public String[][] position;
    public String[][] belief;
    public String houdu;
    public String[] edge; 
    public String[][] getPosition() {
        return position;
    }

    public void setPosition(String[][] position) {
        this.position = position;
    }

    public String[][] getBelief() {
        return belief;
    }

    public void setBelief(String[][] belief) {
        this.belief = belief;
    }
    public void setHoudu(String houdu)
    {
        this.houdu = houdu;
    }
    public String getHoudu()
    {
        return this.houdu;
    }
    public void setEdge(String[] all_edges)
    {
        this.edge = all_edges;
    }
}
