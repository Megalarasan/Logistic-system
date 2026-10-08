package Domain.Employees;

import DB.DBemployeeManager;
import DB.interfaces.employees.IPastShiftsController;
import Models.DBModels.Employees.Shift;

public class PastShiftsManagerCLS {
    private static final IPastShiftsController pastShiftsController = DBemployeeManager.getInstance().getPastShiftsController();

    public static void addPastShift(Shift shift) {
        pastShiftsController.addPastShift(shift);
        pastShiftsController.removeOlderThen7YearsShifts();
    }

    public static void removePastShift(Shift shift) {
        pastShiftsController.removePastShift(shift);
        pastShiftsController.removeOlderThen7YearsShifts();
    }

    /**
     * Removes shifts from pastShiftsDB that are older than 7 years
     * This helps maintain database efficiency by removing very old records
     */
    public static void removeOldPastShifts() {
        pastShiftsController.removeOlderThen7YearsShifts();
    }

 }
