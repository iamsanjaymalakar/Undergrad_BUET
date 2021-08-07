package resources;

import javafx.beans.property.SimpleStringProperty;

public class Player_Basic {
    public SimpleStringProperty id, name, dob, nat, pos, height, weight, contactNo, wage, contacTill, value, buyClause, agname;

    public Player_Basic(SimpleStringProperty id, SimpleStringProperty name, SimpleStringProperty dob, SimpleStringProperty nat, SimpleStringProperty pos, SimpleStringProperty height, SimpleStringProperty weight, SimpleStringProperty contactNo, SimpleStringProperty wage, SimpleStringProperty contacTill, SimpleStringProperty value, SimpleStringProperty buyClause, SimpleStringProperty agname) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.nat = nat;
        this.pos = pos;
        this.height = height;
        this.weight = weight;
        this.contactNo = contactNo;
        this.wage = wage;
        this.contacTill = contacTill;
        this.value = value;
        this.buyClause = buyClause;
        this.agname = agname;
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
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

    public String getDob() {
        return dob.get();
    }

    public SimpleStringProperty dobProperty() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob.set(dob);
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

    public String getHeight() {
        return height.get();
    }

    public SimpleStringProperty heightProperty() {
        return height;
    }

    public void setHeight(String height) {
        this.height.set(height);
    }

    public String getWeight() {
        return weight.get();
    }

    public SimpleStringProperty weightProperty() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight.set(weight);
    }

    public String getContactNo() {
        return contactNo.get();
    }

    public SimpleStringProperty contactNoProperty() {
        return contactNo;
    }

    public void setContactNo(String contactno) {
        this.contactNo.set(contactno);
    }

    public String getWage() {
        return wage.get();
    }

    public SimpleStringProperty wageProperty() {
        return wage;
    }

    public void setWage(String wage) {
        this.wage.set(wage);
    }

    public String getContacTill() {
        return contacTill.get();
    }

    public SimpleStringProperty contacTillProperty() {
        return contacTill;
    }

    public void setContacTill(String contacTill) {
        this.contacTill.set(contacTill);
    }

    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getBuyClause() {
        return buyClause.get();
    }

    public SimpleStringProperty buyClauseProperty() {
        return buyClause;
    }

    public void setBuyClause(String buyClause) {
        this.buyClause.set(buyClause);
    }

    public String getAgname() {
        return agname.get();
    }

    public SimpleStringProperty agnameProperty() {
        return agname;
    }

    public void setAgname(String agname) {
        this.agname.set(agname);
    }
}