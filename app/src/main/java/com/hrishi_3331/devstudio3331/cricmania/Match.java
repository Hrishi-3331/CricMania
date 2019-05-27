package com.hrishi_3331.devstudio3331.cricmania;

public class Match {

    private String title;
    private String team1;
    private String team2;
    private String id;

    public Match() {

    }

    public Match(String title, String team1, String team2, String id) {
        this.title = title;
        this.team1 = team1;
        this.team2 = team2;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
