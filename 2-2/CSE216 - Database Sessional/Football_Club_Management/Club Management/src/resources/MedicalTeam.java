package resources;

import javafx.beans.property.SimpleStringProperty;

public class MedicalTeam {
    public SimpleStringProperty id,name,address,contact,edate;

    public MedicalTeam(SimpleStringProperty id, SimpleStringProperty name, SimpleStringProperty address, SimpleStringProperty contact, SimpleStringProperty edate) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.edate = edate;
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

    public String getEdate() {
        return edate.get();
    }

    public SimpleStringProperty edateProperty() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate.set(edate);
    }
}

