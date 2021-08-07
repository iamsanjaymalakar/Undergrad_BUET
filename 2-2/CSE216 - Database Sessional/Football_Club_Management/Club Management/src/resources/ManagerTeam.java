package resources;

import javafx.beans.property.SimpleStringProperty;

public class ManagerTeam {
    public SimpleStringProperty date,venue,opponent,tournament,stage,result, wdl;

    public ManagerTeam(SimpleStringProperty date, SimpleStringProperty venue, SimpleStringProperty opponent, SimpleStringProperty tournament, SimpleStringProperty stage, SimpleStringProperty result, SimpleStringProperty wdl) {
        this.date = date;
        this.venue = venue;
        this.opponent = opponent;
        this.tournament = tournament;
        this.stage = stage;
        this.result = result;
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

    public String getVenue() {
        return venue.get();
    }

    public SimpleStringProperty venueProperty() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue.set(venue);
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
