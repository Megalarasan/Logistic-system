package Models.DBModels.suppliers;

import Auxiliary.Helper;
import Models.DBModels.BaseModel;
import Models.DBModels.transport.APhysicalSite;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.Session;

//@mohamed TODO complete the class
@Entity
@Table(name = "suppliers")
public class Supplier extends APhysicalSite {

    public Supplier(int id, String supplierName, String supplierAddress, String supplierPhone) {
        super(id, Supplier.class.getName(), supplierName, supplierAddress, supplierPhone);
    }

    public Supplier() {
        super(0, Supplier.class.getName(), "", "", "");
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierName='" + getSiteName() + '\'' +
                ", supplierAddress='" + getSiteAddress() + '\'' +
                ", supplierPhone='" + getSitePhone() + '\'' +
                '}' + super.toString();
    }

    @Override
    public String toPrettyString() {
        return String.format("Supplier Name: %s, Supplier Address: %s, Supplier Phone: %s", getSiteName(), getSiteAddress(), getSitePhone());
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (!(other instanceof Supplier otherSupplier)) {
            Helper.showError(String.format("%s::Other must be of type %s", this.getClass().getName(), this.getClass().getName()));
            return false;
        }
        this.setSiteName(otherSupplier.getSiteName());
        this.setSiteAddress(otherSupplier.getSiteAddress());
        this.setSitePhone(otherSupplier.getSitePhone());
        return true;
    }

}
