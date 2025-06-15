package first_exercise_bonus;

import java.io.Serializable;

public class Citizen implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate;
    private String taxId;
    private String address; 

    public Citizen(String id, String firstName, String lastName, String gender, String birthDate, String taxId, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.taxId = taxId;
        this.address = address;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getBirthDate() { return birthDate; }
    public String getTaxId() { return taxId; }
    public String getAddress() { return address; }

    public void setTaxId(String taxId) { this.taxId = taxId; }
    public void setAddress(String address) { this.address = address; }

    public String toString() {
        return "ID: " + id + ", "
        	 + "Name: " + firstName + ", " 
        	 + "LastName: " + lastName + ", "
             + "Gender: " + gender + ", "
        	 + "Birth Date: " + birthDate + ", "
        	 + "Tax ID: " + (taxId != null ? taxId : "N/A") +", "
        	 + "Address: " + (address != null ? address : "N/A");
    }
}

