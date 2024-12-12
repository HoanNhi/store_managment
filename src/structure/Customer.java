package structure;

public class Customer {
    private int customerID;
    private int userID;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;

    private class Card{
        String cardNumber;
        String csv;
        String expiryYear;
        String expiryMonth;
        String cardType;

        public Card(){
            this.cardNumber = "";
            this.csv = "";
            this.expiryYear = "";
            this.expiryMonth = "";
            this.cardType = "";
        }
    }

    Card card = new Card();

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
        this.customerID = userID;
    }

    public int getUserID() {
        return userID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCard(String cardNumber, String csv, String expiryYear, String expiryMonth, String cardType) {
        this.card.cardNumber = cardNumber;
        this.card.csv = csv;
        this.card.expiryYear = expiryYear;
        this.card.expiryMonth = expiryMonth;
        this.card.cardType = cardType;
    }

    public String getCardNumber() {
        return card.cardNumber;
    }

    public String getCsv() {
        return card.csv;
    }

    public String getExpiryYear() {
        return card.expiryYear;
    }

    public String getExpiryMonth() {
        return card.expiryMonth;
    }

    public String getCardType() {
        return card.cardType;
    }

}
