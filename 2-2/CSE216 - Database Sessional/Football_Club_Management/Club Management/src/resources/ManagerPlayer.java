package resources;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ManagerPlayer {
    public SimpleStringProperty name,nationality,position,agent;
    public SimpleIntegerProperty height,weight,contact,wage,contract;

    public ManagerPlayer(SimpleStringProperty name, SimpleStringProperty nationality, SimpleStringProperty position, SimpleStringProperty agent, SimpleIntegerProperty height, SimpleIntegerProperty weight, SimpleIntegerProperty contact, SimpleIntegerProperty wage, SimpleIntegerProperty contract) {
        this.name = name;
        this.nationality = nationality;
        this.position = position;
        this.agent = agent;
        this.height = height;
        this.weight = weight;
        this.contact = contact;
        this.wage = wage;
        this.contract = contract;
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

    public String getNationality() {
        return nationality.get();
    }

    public SimpleStringProperty nationalityProperty() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality.set(nationality);
    }

    public String getPosition() {
        return position.get();
    }

    public SimpleStringProperty positionProperty() {
        return position;
    }

    public void setPosition(String position) {
        this.position.set(position);
    }

    public String getAgent() {
        return agent.get();
    }

    public SimpleStringProperty agentProperty() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent.set(agent);
    }

    public int getHeight() {
        return height.get();
    }

    public SimpleIntegerProperty heightProperty() {
        return height;
    }

    public void setHeight(int height) {
        this.height.set(height);
    }

    public int getWeight() {
        return weight.get();
    }

    public SimpleIntegerProperty weightProperty() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight.set(weight);
    }

    public int getContact() {
        return contact.get();
    }

    public SimpleIntegerProperty contactProperty() {
        return contact;
    }

    public void setContact(int contact) {
        this.contact.set(contact);
    }

    public int getWage() {
        return wage.get();
    }

    public SimpleIntegerProperty wageProperty() {
        return wage;
    }

    public void setWage(int wage) {
        this.wage.set(wage);
    }

    public int getContract() {
        return contract.get();
    }

    public SimpleIntegerProperty contractProperty() {
        return contract;
    }

    public void setContract(int contract) {
        this.contract.set(contract);
    }
}
