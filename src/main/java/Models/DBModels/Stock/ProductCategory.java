package Models.DBModels.Stock;

public class ProductCategory {
    private static int nextCategoryId = 1;
    private final int categoryID;
    private String name;

    public ProductCategory(String name) {
        this.categoryID = nextCategoryId++;
        setName(name);
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Product category name cannot be blank.");
        }
        this.name = newName.trim();
    }
}
