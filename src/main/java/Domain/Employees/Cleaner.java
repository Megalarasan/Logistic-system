package Domain.Employees;

import Models.DBModels.BaseModel;
import Models.DBModels.Employees.ARole;
import org.hibernate.Session;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLEANER")
public class Cleaner extends ARole {
    public Cleaner() {
        super();
    }

    public Cleaner(int id) {
        super(id, Cleaner.class.getName());
        this.setRoleName("Cleaner");
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        return false;
    }
}
