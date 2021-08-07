package resources;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ManagerPlayerStat {
    public SimpleIntegerProperty id;
    public SimpleStringProperty name;
    public SimpleIntegerProperty count,sgoals;
    public SimpleDoubleProperty agoals;
    public SimpleIntegerProperty sfouls;
    public SimpleDoubleProperty afouls,arating;

    public ManagerPlayerStat(SimpleIntegerProperty id, SimpleStringProperty name, SimpleIntegerProperty count, SimpleIntegerProperty sgoals, SimpleDoubleProperty agoals, SimpleIntegerProperty sfouls, SimpleDoubleProperty afouls, SimpleDoubleProperty arating) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.sgoals = sgoals;
        this.agoals = agoals;
        this.sfouls = sfouls;
        this.afouls = afouls;
        this.arating = arating;
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
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

    public int getCount() {
        return count.get();
    }

    public SimpleIntegerProperty countProperty() {
        return count;
    }

    public void setCount(int count) {
        this.count.set(count);
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

    public double getAgoals() {
        return agoals.get();
    }

    public SimpleDoubleProperty agoalsProperty() {
        return agoals;
    }

    public void setAgoals(double agoals) {
        this.agoals.set(agoals);
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

    public double getAfouls() {
        return afouls.get();
    }

    public SimpleDoubleProperty afoulsProperty() {
        return afouls;
    }

    public void setAfouls(double afouls) {
        this.afouls.set(afouls);
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
