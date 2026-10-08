package Models.DTOs;


public class StockItem {
    private int stockItemId;
    private String itemName;
    private String barcode;
    private int branchId;
    private double amount;
    private final double weight;

    public StockItem(int stockItemId, String itemName, int branchId, double amount, String barcode , int weight) {
        this.stockItemId = stockItemId;
        this.itemName = itemName;
        this.branchId = branchId;
        this.amount = amount;
        this.barcode = barcode;
        this.weight = weight;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getStockItemId() {
        return stockItemId;
    }

    public void setStockItemId(int stockItemId) {
        this.stockItemId = stockItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public double getAmount() {
        return amount;
    }

    public double getWeight() {
        return weight;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "StockItem{" +
                "stockItemId=" + stockItemId +
                ", itemName='" + itemName + '\'' +
                ", branchId=" + branchId +
                ", amount=" + amount +
                "} ";
    }
}
