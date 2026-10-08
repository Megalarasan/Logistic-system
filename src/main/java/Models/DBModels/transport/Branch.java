package Models.DBModels.transport;

import Auxiliary.Helper;
import Models.DBModels.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.Session;

@Entity
@Table(name = "branches")
public class Branch extends APhysicalSite {

    public Branch(int id, String branchName, String address, String phone) {
        super(id, Branch.class.getName(), branchName, address, phone);
    }

    public Branch() {
        super(0, Branch.class.getName(), "", "", "");
    }

    @Override
    public String toString() {
        return "Branch{" +
                "branchName='" + getSiteName() + '\'' +
                ", address='" + getSiteAddress() + '\'' +
                ", phone='" + getSitePhone() + '\'' +
                '}' + super.toString();
    }

    @Override
    public String toPrettyString() {
        return String.format("Branch Name: %s, Address: %s, Phone: %s", getSiteName(), getSiteAddress(), getSitePhone());
    }

    /**
     * Copies the properties of another Branch object to this one.
     *
     * @param other The other Branch object to copy from.
     */
    public boolean copyFromOther(BaseModel other, Session session) {
        if (!(other instanceof Branch otherBranch)) {
            Helper.showError(String.format("%s::Other must be of type %s", this.getClass().getName(), this.getClass().getName()));
            return false;
        }
        this.setSiteName(otherBranch.getSiteName());
        this.setSiteAddress(otherBranch.getSiteAddress());
        this.setSitePhone(otherBranch.getSitePhone());
        return true;
    }
}
