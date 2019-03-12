package project.finalyearapp.Model;

import java.util.Currency;

public class User {
    private String FirstName;
    private String LastName;
    private String Password;
    private String UserType;
    private String CompanyName;
    private String Address;
    private String BankDetails;

    public User() {
    }

    public User(String firstName, String lastName, String password, String userType, String companyName, String address, String bankDetails) {
        FirstName = firstName;
        LastName = lastName;
        Password = password;
        UserType = userType;
        CompanyName = companyName;
        Address = address;
        BankDetails = bankDetails;
    }

    public String getFirstName() {

        return FirstName;
    }

    public void setFirstName(String firstName) {

        FirstName = firstName;
    }

    public String getLastName() {

        return LastName;
    }

    public void setLastName(String lastName) {

        LastName = lastName;
    }

    public String getPassword() {

        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUserType() {
        return UserType;
    }

    public void setUserType(String userType) {
        UserType = userType;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getBankDetails() {
        return BankDetails;
    }

    public void setBankDetails(String bankDetails) {
        BankDetails = bankDetails;
    }
}