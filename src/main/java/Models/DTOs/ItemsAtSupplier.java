package Models.DTOs;

public class ItemsAtSupplier {
    private int supplierId;
    private int stockItemId;
    private double amount;
    private String barcode;

    public ItemsAtSupplier(int supplierId, int stockItemId, double amount, String barcode) {
        this.supplierId = supplierId;
        this.stockItemId = stockItemId;
        this.amount = amount;
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getStockItemId() {
        return stockItemId;
    }

    public void setStockItemId(int stockItemId) {
        this.stockItemId = stockItemId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ItemsAtSupplier{" +
                "supplierId=" + supplierId +
                ", stockItemId=" + stockItemId +
                ", amount=" + amount +
                "} ";
    }
}
