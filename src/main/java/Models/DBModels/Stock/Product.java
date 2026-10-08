package Models.DBModels.Stock;

public class Product {
    private static int productID=0;
    private String name;
    private double purchasePrice;
    private double salePrice;
    private String manufacturer;
    private ProductCategory category1;
    private ProductSubCategory category2;
    private SizeCategory sizeCategory;


    public Product(String name, double purchasePrice, double salePrice, String manufacturer, ProductCategory category1, ProductSubCategory category2, SizeCategory sizeCategory) {
        this.productID++;
        this.name = name;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.manufacturer = manufacturer;
        this.category1 = category1;
        this.category2 = category2;
        this.sizeCategory = sizeCategory;
    }

    public int getProductID() {
        return productID;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }
    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getSalePrice()
    {
        return this.salePrice;
    }
    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public ProductCategory getCategory1() {
        return category1;
    }
    public void setCategory1(ProductCategory category1) {
        this.category1 = category1;
    }

    public ProductSubCategory getCategory2() {
        return category2;
    }
    public void setCategory2(ProductSubCategory category2) {
        this.category2 = category2;
    }


    public SizeCategory getSizeCategory() {
        return sizeCategory;
    }
    public void setSizeCategory(SizeCategory sizeCategory) {
        this.sizeCategory = sizeCategory;
    }
}
