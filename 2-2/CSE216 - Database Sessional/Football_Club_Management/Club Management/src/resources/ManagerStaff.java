package resources;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ManagerStaff {
    public SimpleIntegerProperty staff_id;
    public SimpleStringProperty staff_name;
    public SimpleStringProperty staff_address;
    public SimpleIntegerProperty contact_no;
    public SimpleStringProperty type;
    public SimpleIntegerProperty salary;

    public ManagerStaff(SimpleIntegerProperty staff_id, SimpleStringProperty staff_name, SimpleStringProperty staff_address, SimpleIntegerProperty contact_no, SimpleStringProperty type, SimpleIntegerProperty salary) {
        this.staff_id = staff_id;
        this.staff_name = staff_name;
        this.staff_address = staff_address;
        this.contact_no = contact_no;
        this.type = type;
        this.salary = salary;
    }

    public int getStaff_id() {
        return staff_id.get();
    }

    public SimpleIntegerProperty staff_idProperty() {
        return staff_id;
    }

    public void setStaff_id(int staff_id) {
        this.staff_id.set(staff_id);
    }

    public String getStaff_name() {
        return staff_name.get();
    }

    public SimpleStringProperty staff_nameProperty() {
        return staff_name;
    }

    public void setStaff_name(String staff_name) {
        this.staff_name.set(staff_name);
    }

    public String getStaff_address() {
        return staff_address.get();
    }

    public SimpleStringProperty staff_addressProperty() {
        return staff_address;
    }

    public void setStaff_address(String staff_address) {
        this.staff_address.set(staff_address);
    }

    public int getContact_no() {
        return contact_no.get();
    }

    public SimpleIntegerProperty contact_noProperty() {
        return contact_no;
    }

    public void setContact_no(int contact_no) {
        this.contact_no.set(contact_no);
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

    public int getSalary() {
        return salary.get();
    }

    public SimpleIntegerProperty salaryProperty() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary.set(salary);
    }
}
