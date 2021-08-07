package resources;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PlayersHome {
    public SimpleStringProperty name,nat,pos,team;
    public SimpleIntegerProperty cnt,sgoals,sfouls;
    public SimpleDoubleProperty arating;

    public PlayersHome(SimpleStringProperty name, SimpleStringProperty nat, SimpleStringProperty pos, SimpleStringProperty team, SimpleIntegerProperty cnt, SimpleIntegerProperty sgoals, SimpleIntegerProperty sfouls, SimpleDoubleProperty arating) {
        this.name = name;
        this.nat = nat;
        this.pos = pos;
        this.team = team;
        this.cnt = cnt;
        this.sgoals = sgoals;
        this.sfouls = sfouls;
        this.arating = arating;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getNat() {
        return nat.get();
    }

    public SimpleStringProperty natProperty() {
        return nat;
    }

    public void setNat(String nat) {
        this.nat.set(nat);
    }

    public String getPos() {
        return pos.get();
    }

    public SimpleStringProperty posProperty() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos.set(pos);
    }

    public String getTeam() {
        return team.get();
    }

    public SimpleStringProperty teamProperty() {
        return team;
    }

    public void setTeam(String team) {
        this.team.set(team);
    }

    public int getCnt() {
        return cnt.get();
    }

    public SimpleIntegerProperty cntProperty() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt.set(cnt);
    }

    public int getSgoals() {
        return sgoals.get();
    }

    public SimpleIntegerProperty sgoalsProperty() {
        return sgoals;
    }

    public void setSgoals(int sgoals) {
        this.sgoals.set(sgoals);
    }

    public int getSfouls() {
        return sfouls.get();
    }

    public SimpleIntegerProperty sfoulsProperty() {
        return sfouls;
    }

    public void setSfouls(int sfouls) {
        this.sfouls.set(sfouls);
    }

    public double getArating() {
        return arating.get();
    }

    public SimpleDoubleProperty aratingProperty() {
        return arating;
    }

    public void setArating(double arating) {
        this.arating.set(arating);
    }
}
