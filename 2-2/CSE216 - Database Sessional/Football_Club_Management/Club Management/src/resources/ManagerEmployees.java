package resources;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ManagerEmployees {
    public SimpleStringProperty id, name, address, contact, type, salary, edate, remaining;

    public ManagerEmployees(SimpleStringProperty id, SimpleStringProperty name, SimpleStringProperty address, SimpleStringProperty contact, SimpleStringProperty type, SimpleStringProperty salary, SimpleStringProperty edate, SimpleStringProperty remaining) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.type = type;
        this.salary = salary;
        this.edate = edate;
        String temp=remaining.getValue();
        int num = Integer.parseInt(temp);
        if(num>=0)
            this.remaining = remaining;
        else
        {
            this.remaining = new SimpleStringProperty("Ended");
        }
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

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getContact() {
        return contact.get();
    }

    public SimpleStringProperty contactProperty() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact.set(contact);
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

    public String getSalary() {
        return salary.get();
    }

    public SimpleStringProperty salaryProperty() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary.set(salary);
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

    public String getRemaining() {
        return remaining.get();
    }

    public SimpleStringProperty remainingProperty() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining.set(remaining);
    }
}