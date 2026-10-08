package Domain.Employees;

import Models.DBModels.BaseModel;
import Models.DBModels.Employees.ARole;
import org.hibernate.Session;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CASHIER")
public class Cashier extends ARole {
    public Cashier() {
        super();
    }

    public Cashier(int id) {
        super(id, Cashier.class.getName());
        this.setRoleName("Cashier");
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        return false;
    }
}
