package Domain.Employees;

import Models.DBModels.BaseModel;
import Models.DBModels.Employees.ARole;
import org.hibernate.Session;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name = "EmployeeDriver")
@DiscriminatorValue("DRIVER")
public class Driver extends ARole {
    public Driver() {
        super();
    }

    public Driver(int id) {
        super(id, Driver.class.getName());
        this.setRoleName("Driver");
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        return false; // Implement logic if needed
    }
}
