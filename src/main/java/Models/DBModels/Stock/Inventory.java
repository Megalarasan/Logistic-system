package Models.DBModels.Stock;
import java.util.*;

public class Inventory {
    private List<InventoryItem> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(InventoryItem item) {
        this.items.add(item);
    }
    public void removeItem(InventoryItem item) {
        this.items.remove(item);
    }
    public List<InventoryItem> getItems() {
        return items;
    }
    public void setItems(List<InventoryItem> items) {
        this.items = items;
    }
}
