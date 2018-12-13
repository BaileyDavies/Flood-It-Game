package com.example.bailey.flood_it;
/*DECELERATION: Class based upon guide obtained from Kevin Wilson */

public class Score {
    private long id;
    private int score;
    private String name;


    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    void setScore(int score){
        this.score = score;
    }
    int getScore() {
        return score;
    }

    public void setName(String name) { this.name = name; }
    public String getName() { return name;}

    public String toString(){
        return name;
    }
}


