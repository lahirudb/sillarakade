package codegeeksolutions.sillarakade.Models;

import java.util.List;

public class ItemCatergory {

    private String catergoryName;
    private List<ItemDesc> items;
    private boolean itemlist_visible;
    private boolean catergory_selected = false;

    public String getCatergoryName() {
        return catergoryName;
    }

    public void setCatergoryName(String catergoryName) {
        this.catergoryName = catergoryName;
    }

    public List<ItemDesc> getItems() {
        return items;
    }

    public void setItems(List<ItemDesc> items) {
        this.items = items;
    }

    public boolean isItemlist_visible() {
        return itemlist_visible;
    }

    public void setItemlist_visible(boolean itemlist_visible) {
        this.itemlist_visible = itemlist_visible;
    }

    public boolean isCatergory_selected() {
        return catergory_selected;
    }

    public void setCatergory_selected(boolean catergory_selected) {
        this.catergory_selected = catergory_selected;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ItemCatergory catergory = new ItemCatergory();
        catergory.setItems(this.getItems());
        catergory.setCatergoryName(this.getCatergoryName());
        catergory.setItemlist_visible(this.isItemlist_visible());
        catergory.setCatergory_selected(this.isCatergory_selected());

        return catergory;

    }
}
