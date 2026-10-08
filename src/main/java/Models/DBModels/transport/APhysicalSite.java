package Models.DBModels.transport;

import Models.DBModels.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // or SINGLE_TABLE or TABLE_PER_CLASS
public abstract class APhysicalSite extends BaseModel {
    @Column(nullable = false, unique = true)
    private String siteName;
    @Column(nullable = false, unique = true)
    private String siteAddress;
    @Column(nullable = false, unique = true)
    private String sitePhone;

    public APhysicalSite(int id, String modelName, String siteName, String siteAddress, String sitePhone) {
        super(id, modelName);
        if (siteName == null) {
            throw new IllegalArgumentException(String.format("%s::Site name cannot be null", this.getClass().getName()));
        }
        if (siteAddress == null) {
            throw new IllegalArgumentException(String.format("%s::Site address cannot be null", this.getClass().getName()));
        }
        if (sitePhone == null) {
            throw new IllegalArgumentException(String.format("%s::Site phone cannot be null", this.getClass().getName()));
        }
        this.siteName = siteName;
        this.siteAddress = siteAddress;
        this.sitePhone = sitePhone;
    }

    public APhysicalSite() {
        super(0, APhysicalSite.class.getName());
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getSitePhone() {
        return sitePhone;
    }

    public void setSitePhone(String sitePhone) {
        this.sitePhone = sitePhone;
    }
}
