package resources;

import javafx.beans.property.SimpleStringProperty;

public class PlayerStat {
    public SimpleStringProperty date,opponent,tournament,stage,result,minutes,goals,fouls,saves,rating,wdl;

    public PlayerStat(SimpleStringProperty date, SimpleStringProperty opponent, SimpleStringProperty tournament, SimpleStringProperty stage, SimpleStringProperty result, SimpleStringProperty minutes, SimpleStringProperty goals, SimpleStringProperty fouls, SimpleStringProperty saves, SimpleStringProperty rating, SimpleStringProperty wdl) {
        this.date = date;
        this.opponent = opponent;
        this.tournament = tournament;
        this.stage = stage;
        this.result = result;
        this.minutes = minutes;
        this.goals = goals;
        this.fouls = fouls;
        this.saves = saves;
        this.rating = rating;
        this.wdl = wdl;
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getOpponent() {
        return opponent.get();
    }

    public SimpleStringProperty opponentProperty() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent.set(opponent);
    }

    public String getTournament() {
        return tournament.get();
    }

    public SimpleStringProperty tournamentProperty() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament.set(tournament);
    }

    public String getStage() {
        return stage.get();
    }

    public SimpleStringProperty stageProperty() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage.set(stage);
    }

    public String getResult() {
        return result.get();
    }

    public SimpleStringProperty resultProperty() {
        return result;
    }

    public void setResult(String result) {
        this.result.set(result);
    }

    public String getMinutes() {
        return minutes.get();
    }

    public SimpleStringProperty minutesProperty() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes.set(minutes);
    }

    public String getGoals() {
        return goals.get();
    }

    public SimpleStringProperty goalsProperty() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals.set(goals);
    }

    public String getFouls() {
        return fouls.get();
    }

    public SimpleStringProperty foulsProperty() {
        return fouls;
    }

    public void setFouls(String fouls) {
        this.fouls.set(fouls);
    }

    public String getSaves() {
        return saves.get();
    }

    public SimpleStringProperty savesProperty() {
        return saves;
    }

    public void setSaves(String saves) {
        this.saves.set(saves);
    }

    public String getRating() {
        return rating.get();
    }

    public SimpleStringProperty ratingProperty() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating.set(rating);
    }

    public String getWdl() {
        return wdl.get();
    }

    public SimpleStringProperty wdlProperty() {
        return wdl;
    }

    public void setWdl(String wdl) {
        this.wdl.set(wdl);
    }
}