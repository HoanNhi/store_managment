package structure;

public class Supplier {
    private int SupplierID;
    private String name;
    private String Contact_person;
    private String phone;

    public void setSupplierID(int supplierID) {
        SupplierID = supplierID;
    }

    public int getSupplierID() {
        return SupplierID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContactPerson(String contact_person) {
        Contact_person = contact_person;
    }

    public String getContactPerson() {
        return Contact_person;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
