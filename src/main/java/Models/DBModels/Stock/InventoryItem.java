package Models.DBModels.Stock;

public class InventoryItem {
    private Product product;
    private int quantity;
    private String location;//warehouse or shelves
    private int low_stock_threshold;

    public InventoryItem(Product product, int quantity, String location, int low_stock_threshold) {
        this.product = product;
        this.quantity = quantity;
        if(!Validate.isValideLocation(location))
            throw new IllegalArgumentException("Invalid location(warehouse or shelves)");
        this.location = location;
        this.low_stock_threshold = low_stock_threshold;
    }

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLocation()
    {
        return this.location;
    }
    public void setLocation(String location)
    {
        if(!Validate.isValideLocation(location))
            throw new IllegalArgumentException("Invalid location(warehouse or shelves)");
        this.location = location;
    }
    public int getLowStockThreshold() {
        return low_stock_threshold;
    }
    public void setLowStockThreshold(int low_stock_threshold) {
        this.low_stock_threshold = low_stock_threshold;
    }
}
