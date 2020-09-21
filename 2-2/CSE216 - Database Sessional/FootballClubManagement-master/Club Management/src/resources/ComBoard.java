package resources;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ComBoard {

    public SimpleStringProperty role, sdate, edate;
    public SimpleIntegerProperty budget, income;

    public ComBoard(SimpleStringProperty role, SimpleStringProperty sdate, SimpleStringProperty edate, SimpleIntegerProperty budget, SimpleIntegerProperty income) {
        this.role = role;
        this.sdate = sdate;
        this.edate = edate;
        this.budget = budget;
        this.income = income;

    }

    public String getRole() {
        return role.get();
    }

    public SimpleStringProperty roleProperty() {
        return role;
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public String getSdate() {
        return sdate.get();
    }

    public SimpleStringProperty sdateProperty() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate.set(sdate);
    }

    public String getEdate() {
        return edate.get();
    }

    public SimpleStringProperty edateProperty() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate.set(edate);
    }

    public int getBudget() {
        return budget.get();
    }

    public SimpleIntegerProperty budgetProperty() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget.set(budget);
    }

    public int getIncome() {
        return income.get();
    }

    public SimpleIntegerProperty incomeProperty() {
        return income;
    }

    public void setIncome(int income) {
        this.income.set(income);
    }

}