package resources;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TeamHome {
    public SimpleIntegerProperty id;
    public SimpleStringProperty name,captain,manager;
    public SimpleIntegerProperty total,win,draw,loss;

    public TeamHome(SimpleIntegerProperty id, SimpleStringProperty name, SimpleStringProperty captain, SimpleStringProperty manager, SimpleIntegerProperty total, SimpleIntegerProperty win, SimpleIntegerProperty draw, SimpleIntegerProperty loss) {
        this.id = id;
        this.name = name;
        this.captain = captain;
        this.manager = manager;
        this.total = total;
        this.win = win;
        this.draw = draw;
        this.loss = loss;
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

    public String getCaptain() {
        return captain.get();
    }

    public SimpleStringProperty captainProperty() {
        return captain;
    }

    public void setCaptain(String captain) {
        this.captain.set(captain);
    }

    public String getManager() {
        return manager.get();
    }

    public SimpleStringProperty managerProperty() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager.set(manager);
    }

    public int getTotal() {
        return total.get();
    }

    public SimpleIntegerProperty totalProperty() {
        return total;
    }

    public void setTotal(int total) {
        this.total.set(total);
    }

    public int getWin() {
        return win.get();
    }

    public SimpleIntegerProperty winProperty() {
        return win;
    }

    public void setWin(int win) {
        this.win.set(win);
    }

    public int getDraw() {
        return draw.get();
    }

    public SimpleIntegerProperty drawProperty() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw.set(draw);
    }

    public int getLoss() {
        return loss.get();
    }

    public SimpleIntegerProperty lossProperty() {
        return loss;
    }

    public void setLoss(int loss) {
        this.loss.set(loss);
    }
}
