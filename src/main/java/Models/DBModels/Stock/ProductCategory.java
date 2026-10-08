package Models.DBModels.Stock;

public class ProductCategory {
    private static int categoryID=0;
    private String name;

    public ProductCategory( String name) {
        this.categoryID++;
        this.name = name;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String getName() {
        return name;
    }
    //@sean TODO add exeption handling
    public void setName(String newName)
    {
        this.name = newName;
    }
}
