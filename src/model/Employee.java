package model;

// 직원 정보를 나타내는 클래스
public class Employee extends Person {
    static int count = 0; // 직원 ID 생성용 카운터
    private String id; // ID (고유 식별자)
    private String wageInfo; // 급여 정보 (시급, 연봉 등)

    public Employee(String name, String contactNumber, String wageInfo) {
        super(name, contactNumber); // 부모 클래스 Person의 생성자 호출
        this.id = "E" + (++count); // ID 생성 (E1, E2, ...)
        this.wageInfo = wageInfo;
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

    public String getWageInfo() {
        return this.wageInfo;
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

    public void setWageInfo(String wageInfo) {
        this.wageInfo = wageInfo;
    }

}