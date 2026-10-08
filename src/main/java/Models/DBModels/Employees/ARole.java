package Models.DBModels.Employees;

import Models.DBModels.BaseModel;
import jakarta.persistence.*;
import org.hibernate.Session;

/**
 * Abstract class representing a role in the system.
 * This class is used to define different roles that employees can have.
 */
@Entity
@Table(name = "Roles")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ARole extends BaseModel {
    @Column(nullable = false, name = "role_name")
    private String roleName;

    public ARole() {
        super(0, ARole.class.getName());
    }

    public ARole(int id, String roleName) {
        super(id, ARole.class.getName());
        this.roleName = roleName;
    }

    public String getRole() {
        return roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (!(other instanceof ARole)) return false;
        ARole otherRole = (ARole) other;
        this.roleName = otherRole.roleName;
        return true;
    }
}
