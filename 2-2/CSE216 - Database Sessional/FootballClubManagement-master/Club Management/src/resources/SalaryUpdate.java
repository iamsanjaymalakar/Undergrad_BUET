package resources;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SalaryUpdate {

    public SimpleStringProperty name, type, pSal, rSal, date;
    //public SimpleIntegerProperty pSal, rSal;

    public SalaryUpdate(SimpleStringProperty name, SimpleStringProperty type, SimpleStringProperty pSal, SimpleStringProperty rSal, SimpleStringProperty date) {
        this.name = name;
        this.type = type;
        this.pSal = pSal;
        this.rSal = rSal;
        this.date = date;

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

    public String getType() {
        return type.get();
    }

    public SimpleStringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getpSal() {
        return pSal.get();
    }

    public SimpleStringProperty pSalProperty() {
        return pSal;
    }

    public void setpSal(String pSal) {
        this.pSal.set(pSal);
    }

    public String getrSal() {
        return rSal.get();
    }

    public SimpleStringProperty rSalProperty() {
        return rSal;
    }

    public void setrSal(String rSal) {
        this.rSal.set(rSal);
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
}

