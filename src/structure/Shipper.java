package structure;

public class Shipper {
    private int shipperID;
    private String shipperName;
    private double pricePerKM;

    public int getShipperID() {
        return shipperID;
    }
    public void setShipperID(int shipperID) {
        this.shipperID = shipperID;
    }
    public String getShipperName() {
        return shipperName;
    }
    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }
    public double getPricePerKM() {
        return pricePerKM;
    }
    public void setPricePerKM(double pricePerKM) {
        this.pricePerKM = pricePerKM;
    }
}
