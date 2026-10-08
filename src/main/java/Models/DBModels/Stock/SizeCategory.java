package Models.DBModels.Stock;

public class SizeCategory {
    private int sizeCategoryID=0;
    private String name;
    private ProductSubCategory parentCategory;


    public SizeCategory(int sizeCategoryID, String name, ProductSubCategory parentCategory) {
        this.sizeCategoryID ++;
        this.name = name;
        this.parentCategory = parentCategory;
    }


    public int getSizeCategoryID() {
        return sizeCategoryID;
    }



    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public ProductSubCategory getPerentCategory() {
        return parentCategory;
    }
    public void setPerentCategory(ProductSubCategory perentCategory) {
        this.parentCategory = perentCategory;
    }
}
