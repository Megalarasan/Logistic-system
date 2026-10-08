package Models.DBModels.Stock;

public class DefectiveProduct {
    private Product product;
    private String description;
    private String location;//warehouse or shelves
    private int quantity;

    public DefectiveProduct(Product product, String description, String location, int quantity) {
        this.product = product;
        this.description = description;
        if(Validate.isValideLocation(location))
            this.location = location;
        else
            new IllegalArgumentException("Invalid location(warehouse or shelves)");
        this.quantity = quantity;
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
    public void setQuantity(int quantity)
    {
        this.quantity=quantity;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location)
    {
        if(Validate.isValideLocation(location))
            this.location = location;
        else
            new Exception("Invalid location(warehouse or shelves)");
    }

}
