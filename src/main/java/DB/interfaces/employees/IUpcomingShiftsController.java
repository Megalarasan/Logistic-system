package DB.interfaces.employees;

import Models.DBModels.Employees.Shift;
import Models.DBModels.transport.Branch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IUpcomingShiftsController extends IEmpController<Shift> {
    /**
     * Adds an upcoming shift to the database.
     *
     * @param shift The shift to be added.
     * @return true if the shift was added successfully, false otherwise.
     */
    boolean addUpcomingShift(Shift shift);

    /**
     * Removes an upcoming shift from the database.
     *
     * @param shift The shift to be removed.
     * @return true if the shift was removed successfully, false otherwise.
     */
    boolean removeUpcomingShift(Shift shift);

    /**
     * Finds an upcoming shift by its ID.
     *
     * @param shiftId The ID of the shift to be found.
     * @return The shift with the specified ID, or null if not found.
     */
    Shift findShiftById(String shiftId);

    /**
     * Gets all upcoming shifts.
     *
     * @return A list of all upcoming shifts.
     */
    List<Shift> getUpcomingShifts();

    public boolean updateShiftInDB(Shift updatedShift);



    /**
     * Finds a shift by date, time, and branch.
     *
     * @param date The date of the shift.
     * @param time The time of the shift.
     * @param branch The branch of the shift.
     * @return The shift with the specified date, time, and branch, or null if not found.
     */
    Shift findShiftByDateTimeAndBranch(LocalDate date, LocalTime time, Branch branch);

    /**
     * Moves shifts that have passed to the past shifts database.
     *
     * @return The number of shifts moved.
     */
    int moveShiftsToPast();
}
