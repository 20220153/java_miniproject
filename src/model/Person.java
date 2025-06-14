package model;

// 직원, 고객 등의 공통 상위 클래스 (추상 클래스)
public abstract class Person {
    protected String name; // 이름
    protected String contactNumber; // 연락처 (전화번호 등)

    Person(String name, String contactNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
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
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}

