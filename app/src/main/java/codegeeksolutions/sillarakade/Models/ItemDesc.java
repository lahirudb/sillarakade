package codegeeksolutions.sillarakade.Models;

public class ItemDesc {

    private double unitPrice;
    private String unit;
    private String name;
    private String catergory;
    private boolean isCatergoryName = false;
    private boolean desc_visible = false;
    private String item_image;
    private double availableUnits;

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatergory() {
        return catergory;
    }

    public void setCatergory(String catergory) {
        this.catergory = catergory;
    }

    public boolean isCatergoryName() {
        return isCatergoryName;
    }

    public void setCatergoryName(boolean catergoryName) {
        isCatergoryName = catergoryName;
    }

    public boolean isDesc_visible() {
        return desc_visible;
    }

    public void setDesc_visible(boolean desc_visible) {
        this.desc_visible = desc_visible;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public double getAvailableUnits() {
        return availableUnits;
    }

    public void setAvailableUnits(double availableUnits) {
        this.availableUnits = availableUnits;
    }
}
