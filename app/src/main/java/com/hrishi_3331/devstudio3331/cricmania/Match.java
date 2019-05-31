package com.hrishi_3331.devstudio3331.cricmania;

public class Match {

    private String title;
    private String team1;
    private String team1_icon;
    private String team2;
    private String team2_icon;
    private String match_id;

    public Match() {

    }

    public Match(String title, String team1, String team1_icon, String team2, String team2_icon, String match_id) {
        this.title = title;
        this.team1 = team1;
        this.team1_icon = team1_icon;
        this.team2 = team2;
        this.team2_icon = team2_icon;
        this.match_id = match_id;
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

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    public String getTeam1_icon() {
        return team1_icon;
    }

    public void setTeam1_icon(String team1_icon) {
        this.team1_icon = team1_icon;
    }

    public String getTeam2_icon() {
        return team2_icon;
    }

    public void setTeam2_icon(String team2_icon) {
        this.team2_icon = team2_icon;
    }
}
