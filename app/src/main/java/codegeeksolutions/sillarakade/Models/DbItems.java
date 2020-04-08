package codegeeksolutions.sillarakade.Models;

import java.util.List;

public class DbItems {
    private List<DbItemData> stock;
    private String unit;
    private String unitPrice;

    public List<DbItemData> getStock() {
        return stock;
    }

    public void setStock(List<DbItemData> stock) {
        this.stock = stock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }
}
