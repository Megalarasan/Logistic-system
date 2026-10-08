package Models.DBModels.Employees;
import Models.DBModels.BaseModel;
import Models.DBModels.transport.Branch;
import jakarta.persistence.*;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Entity
@Table(name = "Shifts")
public class Shift extends BaseModel {
    //TODO should be here global verb?
    public static LocalTime START_HOUR_DAY = LocalTime.of(8, 0);
    public static LocalTime END_HOUR_DAY = LocalTime.of(15, 0);
    public static LocalTime START_HOUR_NIGHT = LocalTime.of(16, 0);
    public static LocalTime END_HOUR_NIGHT = LocalTime.of(23, 0);
    public static Map<String, Integer> REQUIRED_ROLES_FOR_SHIFT = new HashMap<>(){{
        put("Shift Manager", 1);
        put("Cashier", 1);
        put("Cleaner", 1);
        put("Security", 1);
    }};

    @Column(nullable = false, unique = true)
    private String shiftID;
    @Column(nullable = false)
    private LocalDate shiftDate;
    @Column(nullable = false)
    private String shiftType;
    @Column(nullable = false)
    private LocalTime startShiftTime;
    @Column(nullable = false)
    private LocalTime endShiftTime;

    @ManyToOne
    @JoinColumn(name = "shift_manager_id")
    private Employee shiftManager;

    @ElementCollection
    @CollectionTable(name = "shift_assigned_employees",
                    joinColumns = @JoinColumn(name = "shift_id"))
    @MapKeyJoinColumn(name = "employee_id")
    @Column(name = "role")
    private Map<Employee,String> assignedEmployees = new HashMap<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "shift_available_employees",
            joinColumns = @JoinColumn(name = "shift_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> allAvailableEmployees = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "shift_required_roles",
                    joinColumns = @JoinColumn(name = "shift_id"))
    @MapKeyColumn(name = "role_name")
    @Column(name = "count")
    private Map<String, Integer> requiredRoles = new HashMap<>();

    private static int shiftCounter = 1;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(nullable = false)
    private boolean isPast = false;

    public Shift(int id, LocalDate shiftDate, String shiftType,
                 Employee shiftManager, Map<Employee,String> assignedEmployees,
                 Set<Employee> allAvailableEmployees , Branch branch) {
        super(id, Shift.class.getName());
        this.shiftID = generateSequentialShiftId();
        this.shiftDate = shiftDate;
        this.shiftType = shiftType;
        //method to check the sart shft time via the shift type
        LocalTime startShiftTime = getStartShiftTime();
        LocalTime endShiftTime = getEndShiftTime();
        this.startShiftTime = startShiftTime;
        this.endShiftTime = endShiftTime;
        this.shiftManager = shiftManager;
        this.branch = branch;

        // Create a new set for available employees to avoid modifying the input list
        this.allAvailableEmployees = new HashSet<>(allAvailableEmployees);

        // Create a new map for assigned employees to avoid modifying the input map
        this.assignedEmployees = new HashMap<>(assignedEmployees);

        // Remove assigned employees from the available list
        // An employee cannot be both assigned and available at the same time
        for (Employee employee : this.assignedEmployees.keySet()) {
            this.allAvailableEmployees.remove(employee);
        }

        this.requiredRoles = new HashMap<>(getRequiredRoles());
    }

    public Shift() {
        super(0, Shift.class.getName());
    }

    public String getShiftID() {
        return shiftID;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public Employee getShiftManager() {
        return shiftManager;
    }
    public String getShiftType() {
        return shiftType;
    }

    public LocalTime getStartShiftTime() {
        // During initialization, get from global variables
        if (this.startShiftTime == null) {
            if (shiftType.equals("Day")) {
                return START_HOUR_DAY;
            } else if (shiftType.equals("Night")) {
                return START_HOUR_NIGHT;
            } else {
                throw new IllegalArgumentException("Invalid shift type: " + shiftType);
            }
        }
        // After initialization, return the local field
        return this.startShiftTime;
    }

    public static void setStartHourDay(LocalTime startShiftTime) {
        START_HOUR_DAY = startShiftTime;
    }



    public static void setStartHourNight(LocalTime startShiftTime) {
        START_HOUR_NIGHT = startShiftTime;
    }


    public LocalTime getEndShiftTime() {
        // During initialization, get from global variables
        if (this.endShiftTime == null) {
            if (shiftType.equals("Day")) {
                return END_HOUR_DAY;
            } else if (shiftType.equals("Night")) {
                return END_HOUR_NIGHT;
            } else {
                throw new IllegalArgumentException("Invalid shift type: " + shiftType);
            }
        }
        // After initialization, return the local field
        return this.endShiftTime;
    }

    public static void setEndHourDay(LocalTime endShiftTime) {
        END_HOUR_DAY = endShiftTime;
    }


    public static void setEndHourNight(LocalTime endShiftTime) {
        END_HOUR_NIGHT = endShiftTime;
    }


    public Map<Employee,String> getAssignedEmployees() {
        return assignedEmployees;
    }

    public Set<Employee> getAllAvailableEmployees() {return allAvailableEmployees;}



    public static Map<String, Integer> getRequiredRoles() {
        return REQUIRED_ROLES_FOR_SHIFT;
    }


    // method to generate a unique shift ID
    private static synchronized String generateSequentialShiftId() {
        return "SHIFT-" + (shiftCounter++);
    }

    @Override
    public String toString() {
        return "ShiftID= '" + shiftID + '\'' +
                ", ShiftDate= " + shiftDate +
                ", ShiftType= '" + shiftType + '\'';
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (!(other instanceof Shift)) return false;
        Shift otherShift = (Shift) other;

        this.shiftDate = otherShift.shiftDate;
        this.shiftType = otherShift.shiftType;
        this.startShiftTime = otherShift.startShiftTime;
        this.endShiftTime = otherShift.endShiftTime;
        this.isPast = otherShift.isPast;

        // Handle shiftManager
        if (otherShift.shiftManager != null) {
            this.shiftManager = otherShift.shiftManager;
        }

        // Handle branch
        if (otherShift.branch != null) {
            this.branch = otherShift.branch;
        }

        // Handle assignedEmployees
        if (otherShift.assignedEmployees != null) {
            this.assignedEmployees.clear();
            this.assignedEmployees.putAll(otherShift.assignedEmployees);
        }

        // Handle allAvailableEmployees
        if (otherShift.allAvailableEmployees != null) {
            this.allAvailableEmployees.clear();
            this.allAvailableEmployees.addAll(otherShift.allAvailableEmployees);
        }

        // Handle requiredRoles
        if (otherShift.requiredRoles != null) {
            this.requiredRoles.clear();
            this.requiredRoles.putAll(otherShift.requiredRoles);
        }

        return true;
    }
    public Branch getBranch() {
        return branch;
    }

    public boolean isPast() {
        return isPast;
    }

    public void setPast(boolean past) {
        isPast = past;
    }
}
