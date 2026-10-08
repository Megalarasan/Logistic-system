package DB.interfaces.employees;

import Models.DBModels.Employees.Shift;

import java.time.LocalDate;
import java.util.List;

public interface IPastShiftsController extends IEmpController<Shift> {
    /**
     * Adds a past shift to the database.
     *
     * @param shift The shift to be added.
     * @return true if the shift was added successfully, false otherwise.
     */
    boolean addPastShift(Shift shift);

    /**
     * Removes a past shift from the database.
     *
     * @param shift The shift to be removed.
     * @return true if the shift was removed successfully, false otherwise.
     */
    boolean removePastShift(Shift shift);

    /**
     * Finds a past shift by its ID.
     *
     * @param shiftId The ID of the shift to be found.
     * @return The shift with the specified ID, or null if not found.
     */
    Shift findShiftById(String shiftId);

    /**
     * Gets all past shifts.
     *
     * @return A list of all past shifts.
     */
    List<Shift> getPastShifts();

    /**
     * Removes shifts older than the specified date.
     *
     * @param date The date before which shifts should be removed.
     * @return The number of shifts removed.
     */
    int removeShiftsOlderThan(LocalDate date);

    /**
     * Removes shifts older than 7 years.
     * This helps maintain database efficiency by removing very old records.
     */
    public void removeOlderThen7YearsShifts();
}
