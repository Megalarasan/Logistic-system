package Models.DBModels.Stock;

public class ProductSubCategory {
    private static int subCategoryID = 0;
    private String name;
    private ProductCategory perentCategory;

    public ProductSubCategory(int subCategoryID, String name, ProductCategory perentCategory) {
        this.subCategoryID++;
        this.name = name;
        this.perentCategory = perentCategory;
    }

    public int getSubCategoryID() {
        return subCategoryID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ProductCategory getPerentCategory() {
        return perentCategory;
    }
    public void setPerentCategory(ProductCategory perentCategory) {
        this.perentCategory = perentCategory;
    }
}
