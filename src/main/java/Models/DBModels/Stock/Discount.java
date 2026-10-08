package Models.DBModels.Stock;

public class Discount {

    private int discountID;
    private double precentage;
    private String startDate;//dd/MM/yyyy
    private String endDate;//dd/MM/yyyy
    private ProductCategory targetCategory;//can be null
    private ProductSubCategory targetSubCategory;//can be null
    private SizeCategory targetSizeCategory;//can be null

    public Discount(int discountID, double precentage, String startDate, String endDate, ProductCategory targetCategory, ProductSubCategory targetSubCategory, SizeCategory targetSizeCategory) {
        this.discountID = discountID;
        if(!Validate.iisValidePrecentage(precentage))
            throw new IllegalArgumentException("Invalid precentage(0-1)");
        this.precentage = precentage;
        if(!Validate.isValideLocation(startDate)|| !Validate.isValideLocation(endDate))
            throw new IllegalArgumentException("Invalid date format(DD/MM/YYYY)");
        this.startDate = startDate;
        this.endDate = endDate;
        this.targetCategory = targetCategory;
        this.targetSubCategory = targetSubCategory;
        this.targetSizeCategory = targetSizeCategory;
    }
    public int getDiscountID() {
        return discountID;
    }
    public void setDiscountID(int discountID) {
        this.discountID = discountID;
    }
    public double getPrecentage() {
        return precentage;
    }
    public void setPrecentage(double precentage) {
        if(!Validate.iisValidePrecentage(precentage))
            throw new IllegalArgumentException("Invalid precentage(0-1)");
        this.precentage = precentage;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        if(!Validate.isValideLocation(startDate))
            throw new IllegalArgumentException("Invalid date format(DD/MM/YYYY)");
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        if(!Validate.isValideLocation(endDate))
            throw new IllegalArgumentException("Invalid date format(DD/MM/YYYY)");
        this.endDate = endDate;
    }
    public ProductCategory getTargetCategory() {
        return targetCategory;
    }
    public void setTargetCategory(ProductCategory targetCategory) {
        this.targetCategory = targetCategory;
    }
    public ProductSubCategory getTargetSubCategory() {
        return targetSubCategory;
    }
    public void setTargetSubCategory(ProductSubCategory targetSubCategory) {
        this.targetSubCategory = targetSubCategory;
    }
    public SizeCategory getTargetSizeCategory() {
        return targetSizeCategory;
    }
    public void setTargetSizeCategory(SizeCategory targetSizeCategory) {
        this.targetSizeCategory = targetSizeCategory;
    }
}
