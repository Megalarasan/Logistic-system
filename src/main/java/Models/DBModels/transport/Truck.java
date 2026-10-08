package Models.DBModels.transport;

import Auxiliary.Enums.Models.LicenseTypeEnum;
import Models.DBModels.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.Session;

@Entity
@Table(name = "trucks")
public class Truck extends BaseModel {
    @Column(nullable = false, unique = true)
    private String plate;
    @Column(nullable = false)
    private double netWeight;
    @Column(nullable = false)
    private double maxWeight;
    @Column(nullable = false)
    private LicenseTypeEnum requiredLicenseType;
    @Column(nullable = false)
    private String model;

    public Truck(int id, String plate, double netWeight, double maxWeight, LicenseTypeEnum requiredLicenseType, String model) {
        super(id, Truck.class.getName());
        this.plate = plate;
        this.netWeight = netWeight;
        this.maxWeight = maxWeight;
        this.requiredLicenseType = requiredLicenseType;
        this.model = model;
    }

    public Truck() {
        super(0, Truck.class.getName());
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(double netWeight) {
        this.netWeight = netWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public LicenseTypeEnum getRequiredLicenseType() {
        return requiredLicenseType;
    }

    public void setRequiredLicenseType(LicenseTypeEnum requiredLicenseType) {
        this.requiredLicenseType = requiredLicenseType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Truck{" +
                "plate='" + plate + '\'' +
                ", netWeight=" + netWeight +
                ", maxWeight=" + maxWeight +
                ", requiredLicenseType='" + requiredLicenseType + '\'' +
                ", model='" + model + '\'' +
                "} " + super.toString();
    }


    @Override
    public String toPrettyString() {
        return "Truck{" +
                "id='" + getId() + '\'' +
                ", plate='" + plate + '\'' +
                ", netWeight=" + netWeight +
                ", maxWeight=" + maxWeight +
                ", requiredLicenseType='" + requiredLicenseType + '\'' +
                ", model='" + model + '\'' +
                "} ";
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (other instanceof Truck otherTruck) {
            this.plate = otherTruck.plate;
            this.netWeight = otherTruck.netWeight;
            this.maxWeight = otherTruck.maxWeight;
            this.requiredLicenseType = otherTruck.requiredLicenseType;
            this.model = otherTruck.model;
            return true;
        }
        return false;
    }
}
