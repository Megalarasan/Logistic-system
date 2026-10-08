package Models.DBModels.Stock;
import java.util.*;
public class DefectiveProductReport {
    private String startDate;//dd/MM/yyyy
    private String endDate;//dd/MM/yyyy
    private String generatedOn;//dd/MM/yyyy
    private List<DefectiveProduct> defectiveProducts;

    public DefectiveProductReport(String startDate, String endDate, String generatedOn) {
        if(!Validate.isValideLocation(startDate)|| !Validate.isValideLocation(endDate) || !Validate.isValideLocation(generatedOn))
            throw new IllegalArgumentException("Invalid date format(DD/MM/YYYY)");
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedOn = generatedOn;
        this.defectiveProducts = new ArrayList<>();
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
    public void setEndDate(String endDate)
    {
        if(!Validate.isValideLocation(endDate))
            throw new IllegalArgumentException("Invalid date format(DD/MM/YYYY)");
        this.endDate = endDate;
    }

    public void addDefactiveProduct(DefectiveProduct defectiveProduct) {
        this.defectiveProducts.add(defectiveProduct);
    }
    public List<DefectiveProduct> getDefectiveProducts() {
        return defectiveProducts;
    }

    public String getGeneratedOn() {
        return generatedOn;
    }
    public void setGeneratedOn(String generatedOn)
    {
        if(!Validate.isValideLocation(generatedOn))
            throw new IllegalArgumentException("Invalid date format(DD/MM/YYYY)");
        this.generatedOn = generatedOn;
    }

    public void removeDefactiveProduct(DefectiveProduct defectiveProduct) {
        if(defectiveProduct == null) {
            throw new IllegalArgumentException("Defective product cannot be null");
        }
        if(!this.defectiveProducts.contains(defectiveProduct)) {
            throw new IllegalArgumentException("Defective product not found in the report");
        }
        this.defectiveProducts.remove(defectiveProduct);
    }
}
