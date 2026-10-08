package Models.DBModels.Employees;

import Auxiliary.Enums.Models.AvailabilityStatus;
import Models.DBModels.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.Session;


/**
 * Represents an employee's availability for shifts on different days of the week.
 * Tracks availability status for Sunday through Thursday and whether each day's
 * availability has been updated for the current week.
 */
@Entity
@Table (name = "Availabilitys")
public class Availability extends BaseModel {
    @Column(nullable = false)
    private AvailabilityStatus sunday;
    @Column(nullable = false)
    private AvailabilityStatus monday;
    @Column(nullable = false)
    private AvailabilityStatus tuesday;
    @Column(nullable = false)
    private AvailabilityStatus wednesday;
    @Column(nullable = false)
    private AvailabilityStatus thursday;

    // boolean values to track if the availability has been updated
    // to support incremental availability updates until Thursday
    @Column(nullable = false)
    private boolean updatedSun;
    @Column(nullable = false)
    private boolean updatedMon;
    @Column(nullable = false)
    private boolean updatedTue;
    @Column(nullable = false)
    private boolean updatedWed;
    @Column(nullable = false)
    private boolean updatedThu;


    /**
     * Creates a new Availability instance with the specified availability statuses for each day.
     *
     * @param sunday The availability status for Sunday
     * @param monday The availability status for Monday
     * @param tuesday The availability status for Tuesday
     * @param wednesday The availability status for Wednesday
     * @param thursday The availability status for Thursday
     */
    public Availability(AvailabilityStatus sunday, AvailabilityStatus monday,
                        AvailabilityStatus tuesday, AvailabilityStatus wednesday,
                        AvailabilityStatus thursday) {
        this.sunday = sunday;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;

        this.updatedSun = false;
        this.updatedMon = false;
        this.updatedTue = false;
        this.updatedWed = false;
        this.updatedThu = false;
    }

    public Availability() {
        super(0, Availability.class.getName());
    }



    // Getter and setter methods for all private fields

    public AvailabilityStatus getSunday() {
        return sunday;
    }

    public void setSunday(AvailabilityStatus sunday) {
        this.sunday = sunday;
    }

    public AvailabilityStatus getMonday() {
        return monday;
    }

    public void setMonday(AvailabilityStatus monday) {
        this.monday = monday;
    }

    public AvailabilityStatus getTuesday() {
        return tuesday;
    }

    public void setTuesday(AvailabilityStatus tuesday) {
        this.tuesday = tuesday;
    }

    public AvailabilityStatus getWednesday() {
        return wednesday;
    }

    public void setWednesday(AvailabilityStatus wednesday) {
        this.wednesday = wednesday;
    }

    public AvailabilityStatus getThursday() {
        return thursday;
    }

    public void setThursday(AvailabilityStatus thursday) {
        this.thursday = thursday;
    }

    public boolean isUpdatedSun() {
        return updatedSun;
    }

    public void setUpdatedSun(boolean updatedSun) {
        this.updatedSun = updatedSun;
    }

    public boolean isUpdatedMon() {
        return updatedMon;
    }

    public void setUpdatedMon(boolean updatedMon) {
        this.updatedMon = updatedMon;
    }

    public boolean isUpdatedTue() {
        return updatedTue;
    }

    public void setUpdatedTue(boolean updatedTue) {
        this.updatedTue = updatedTue;
    }

    public boolean isUpdatedWed() {
        return updatedWed;
    }

    public void setUpdatedWed(boolean updatedWed) {
        this.updatedWed = updatedWed;
    }

    public boolean isUpdatedThu() {
        return updatedThu;
    }

    public void setUpdatedThu(boolean updatedThu) {
        this.updatedThu = updatedThu;
    }

    @Override
    public String toString() {
        return "Availability=\n" +
                "sunday= " + getSunday() +
                ", monday= " + getMonday() +
                ", tuesday= " + getTuesday() +
                ", wednesday= " + getWednesday() +
                ", thursday= " + getThursday();
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (!(other instanceof Availability)) return false;
        Availability otherAvailability = (Availability) other;

        // Copy availability status for each day
        this.sunday = otherAvailability.sunday;
        this.monday = otherAvailability.monday;
        this.tuesday = otherAvailability.tuesday;
        this.wednesday = otherAvailability.wednesday;
        this.thursday = otherAvailability.thursday;

        // Copy update tracking flags
        this.updatedSun = otherAvailability.updatedSun;
        this.updatedMon = otherAvailability.updatedMon;
        this.updatedTue = otherAvailability.updatedTue;
        this.updatedWed = otherAvailability.updatedWed;
        this.updatedThu = otherAvailability.updatedThu;

        return true;
    }
}
