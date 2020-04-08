package codegeeksolutions.sillarakade.Models;

public class StockData {

    private String catergory;
    private String date;
    private String date_time;
    private String item_name;
    private double purchased_price;
    private double purchased_units;
    private double sale_price;
    private String user;
    private double sold_units;
    private boolean selected = false;

    public String getCatergory() {
        return catergory;
    }

    public void setCatergory(String catergory) {
        this.catergory = catergory;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public double getPurchased_price() {
        return purchased_price;
    }

    public void setPurchased_price(double purchased_price) {
        this.purchased_price = purchased_price;
    }

    public double getPurchased_units() {
        return purchased_units;
    }

    public void setPurchased_units(double purchased_units) {
        this.purchased_units = purchased_units;
    }

    public double getSale_price() {
        return sale_price;
    }

    public void setSale_price(double sale_price) {
        this.sale_price = sale_price;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getSold_units() {
        return sold_units;
    }

    public void setSold_units(double sold_units) {
        this.sold_units = sold_units;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
