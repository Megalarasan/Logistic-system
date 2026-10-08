package Models.DBModels.transport;

import Models.DBModels.BaseModel;
import Models.DBModels.suppliers.Supplier;
import jakarta.persistence.*;
import org.hibernate.Session;

import java.util.List;

@Entity
@Table(name = "transport_areas")
public class TransportArea extends BaseModel {
    @Column(nullable = false, unique = true, length = 100)
    private String areaName;

    @ManyToMany
    @JoinTable(
            name = "area_suppliers",
            joinColumns = @JoinColumn(name = "area_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private List<Supplier> suppliers;

    @ManyToMany
    @JoinTable(
            name = "area_branches",
            joinColumns = @JoinColumn(name = "area_id"),
            inverseJoinColumns = @JoinColumn(name = "branch_id")
    )
    private List<Branch> branches;


    public TransportArea(int id, String areaName, List<Supplier> suppliers, List<Branch> branches) {
        super(id, TransportArea.class.getName());
        this.areaName = areaName;
        this.suppliers = suppliers;
        this.branches = branches;
    }

    public TransportArea() {
        super(0, TransportArea.class.getName());
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public String toString() {
        String sb = "TransportArea{" +
                "id=" + getId() +
                ", areaName='" + areaName + '\'' +
                ", suppliers" + suppliers.stream().map(Supplier::toPrettyString).toList() +
                ", branches" + branches.stream().map(Branch::toPrettyString).toList() +
                '}' + super.toString();
        return sb;
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (other instanceof TransportArea otherTransportArea) {
            this.areaName = otherTransportArea.areaName;
            this.suppliers = otherTransportArea.suppliers;
            this.branches = otherTransportArea.branches;
            return true;
        }
        return false;
    }
}
