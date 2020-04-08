package codegeeksolutions.sillarakade.Models;

public class SellingData {
    private String itemName;
    private String stockDocId;
    private double units;
    private double sold_price;
    private double original_price;
    private double totalprice;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getStockDocId() {
        return stockDocId;
    }

    public void setStockDocId(String stockDocId) {
        this.stockDocId = stockDocId;
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }

    public double getSold_price() {
        return sold_price;
    }

    public void setSold_price(double sold_price) {
        this.sold_price = sold_price;
    }

    public double getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(double original_price) {
        this.original_price = original_price;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }
}
