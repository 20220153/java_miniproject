package model;

public class Customer extends Person {
    static int count = 0; // 고객 ID 생성용 카운터
    private String id; // ID (고유 식별자)
    
    public Customer(String name, String contactNumber) {
        super(name, contactNumber);
        id = "C" + (++count); // ID 생성 (C1, C2, ...)
    }

    public String getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void SetID(String id) {
        this.id = id;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}