package Domain.Employees;

import Models.DBModels.BaseModel;
import Models.DBModels.Employees.ARole;
import org.hibernate.Session;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SECURITY")
public class Security extends ARole {
    public Security() {
        super();
    }

    public Security(int id) {
        super(id, Security.class.getName());
        this.setRoleName("Security");
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        return false;
    }
}
