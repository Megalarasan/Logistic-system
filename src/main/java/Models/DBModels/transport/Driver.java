package Models.DBModels.transport;

import Auxiliary.Enums.Models.LicenseTypeEnum;
import Auxiliary.Helper;
import Models.DBModels.BaseModel;
import Models.DBModels.Employees.ARole;
import jakarta.persistence.*;
import org.hibernate.Session;

@Entity
@Table(name = "drivers")
public class Driver extends BaseModel {
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String personalId;
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    @Column(nullable = false)
    private LicenseTypeEnum licenseType;

    public Driver(int id, String firstName, String lastName, String personalId, String phoneNumber, LicenseTypeEnum licenseType) {
        super(id, Driver.class.getName());
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.phoneNumber = phoneNumber;
        this.licenseType = licenseType;
    }

    public Driver() {
        super(0, Driver.class.getName());
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LicenseTypeEnum getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseTypeEnum licenseType) {
        this.licenseType = licenseType;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", personalId='" + personalId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", licenseType='" + licenseType + '\'' +
                "} " + super.toString();
    }

    @Override
    public String toPrettyString() {
        return String.format("ID: %d, First Name: %s, Last Name: %s, Personal ID: %s, Phone Number: %s, License Type: %s",
                getId(), firstName, lastName, personalId, phoneNumber, licenseType);
    }

    /**
     * Copies values from other driver into the current driver object
     *
     * @param other driver object to copy from
     */
    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (!(other instanceof Driver otherDriver)) {
            Helper.showError(String.format("%s::Other must be of type %s", this.getClass().getName(), this.getClass().getName()));
            return false;
        }
        this.firstName = otherDriver.firstName;
        this.lastName = otherDriver.lastName;
        this.personalId = otherDriver.personalId;
        this.phoneNumber = otherDriver.phoneNumber;
        this.licenseType = otherDriver.licenseType;
        return true;
    }
}
