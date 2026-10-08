package Models.DBModels.Employees;
import Models.DBModels.BaseModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.Session;

@Entity
@Table(name = "Employees")
public class Employee extends BaseModel {
    @Column(nullable = false)
    private double salary;
    @Column(nullable = false , unique = true)
    private String employeeID;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate recruitment_Date;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "employee_roles",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<ARole> allRoles = new ArrayList<>();


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_account_id", referencedColumnName = "id", nullable = false)
    private BankAccount bank_info;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_id", referencedColumnName = "id", nullable = false)
    private Contract employee_condition;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "availability_id", referencedColumnName = "id", nullable = false)
    private Availability availability;

    public Employee(int id, String name, double salary, String employeeId, LocalDate recruitment_Date,
                    List<ARole> roles, BankAccount bank_info, Contract employee_condition, Availability availability) {
        super(id, Employee.class.getName());
        this.salary = salary;
        this.employeeID = employeeId;
        this.name = name;
        this.recruitment_Date = recruitment_Date;
        if (roles != null) {
            this.allRoles.addAll(roles);
        }
        this.bank_info = bank_info;
        this.employee_condition = employee_condition;
        this.availability = availability;
    }

    public Employee() {
        super(0,Employee.class.getName());
    }

    //salary setters and getter will be used in the future !
    public double getSalary() {
        return salary;
    }
    public void setSalary(double salary) {
        this.salary = salary;
    }
    public String getEmployeeID() {
        return employeeID;
    }
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ARole> getRolesList() {
        return allRoles;
    }
    public Availability getAvailability() {
        return availability;
    }
    public void setAvailability(Availability availability) {
        this.availability = availability;
    }
    public void setAllRoles(List<ARole> allRoles) {
        this.allRoles = allRoles;
    }

    public BankAccount getBankInfo() {
        return bank_info;
    }

    public Contract getEmployeeCondition() {
        return employee_condition;
    }



    @Override
    public String toString() {
        String strToReturn = "\nEmployee: \n" +
                "Name= " + name + '\n' +
                "EmployeeID= " + employeeID + '\n' +
                "Salary= " + salary + " Shekels per Hour \n" +
                "Recruitment_Date= " + recruitment_Date + '\n' +
                bank_info.toString() + '\n' +
                employee_condition.toString() + '\n'
                + availability.toString()+ '\n' ;

        for (ARole role : allRoles) {
            strToReturn += role.getRole()+ ", ";
        }

        return strToReturn.substring(0, strToReturn.length() - 2);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee employee = (Employee) obj;
        // Use employeeID as the primary key for equality since it's unique
        return employeeID != null && employeeID.equals(employee.employeeID);
    }

    @Override
    public int hashCode() {
        // Use employeeID for hashCode since it's unique and immutable
        return employeeID != null ? employeeID.hashCode() : 0;
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (!(other instanceof Employee)) return false;
        Employee otherEmployee = (Employee) other;

        // Copy basic fields
        this.salary = otherEmployee.salary;
        this.name = otherEmployee.name;
        this.employeeID = otherEmployee.employeeID;  // This was missing!
        this.recruitment_Date = otherEmployee.recruitment_Date;

        // Handle bank_info - properly manage entity relationships
        if (otherEmployee.bank_info != null) {
            if (this.bank_info == null) {
                // If this employee doesn't have bank info, we need to merge the other's bank info
                this.bank_info = session.merge(otherEmployee.bank_info);
            } else {
                // If this employee already has bank info, copy the data
                this.bank_info.copyFromOther(otherEmployee.bank_info, session);
            }
        } else {
            this.bank_info = null;
        }

        // Handle employee_condition - properly manage entity relationships
        if (otherEmployee.employee_condition != null) {
            if (this.employee_condition == null) {
                // If this employee doesn't have a contract, merge the other's contract
                this.employee_condition = session.merge(otherEmployee.employee_condition);
            } else {
                // If this employee already has a contract, copy the data
                this.employee_condition.copyFromOther(otherEmployee.employee_condition, session);
            }
        } else {
            this.employee_condition = null;
        }

        // Handle availability - properly manage entity relationships
        if (otherEmployee.availability != null) {
            if (this.availability == null) {
                // If this employee doesn't have availability, merge the other's availability
                this.availability = session.merge(otherEmployee.availability);
            } else {
                // If this employee already has availability, copy the data
                this.availability.copyFromOther(otherEmployee.availability, session);
            }
        } else {
            this.availability = null;
        }

        // Handle roles - direct assignment since roles are already managed by updateEmployee
        if (otherEmployee.allRoles != null) {
            this.allRoles.clear();
            this.allRoles.addAll(otherEmployee.allRoles);
        } else {
            this.allRoles.clear();
        }

        return true;
    }

    public BankAccount getBank_info() {
        return bank_info;
    }

    public Contract getEmployee_condition() {
        return employee_condition;
    }
}
