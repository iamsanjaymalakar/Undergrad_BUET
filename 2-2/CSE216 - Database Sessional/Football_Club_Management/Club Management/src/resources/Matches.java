package resources;

import javafx.beans.property.SimpleStringProperty;

public class Matches {
    SimpleStringProperty date,venue,opp,tournament,stage,score,result;

    public Matches(SimpleStringProperty date, SimpleStringProperty venue, SimpleStringProperty opp, SimpleStringProperty tournament, SimpleStringProperty stage, SimpleStringProperty score, SimpleStringProperty result) {
        this.date = date;
        this.venue = venue;
        this.opp = opp;
        this.tournament = tournament;
        this.stage = stage;
        this.score = score;
        this.result = result;
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

    public String getVenue() {
        return venue.get();
    }

    public SimpleStringProperty venueProperty() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue.set(venue);
    }

    public String getOpp() {
        return opp.get();
    }

    public SimpleStringProperty oppProperty() {
        return opp;
    }

    public void setOpp(String opp) {
        this.opp.set(opp);
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

    public String getScore() {
        return score.get();
    }

    public SimpleStringProperty scoreProperty() {
        return score;
    }

    public void setScore(String score) {
        this.score.set(score);
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
}
