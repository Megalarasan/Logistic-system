package DB.WorkersDB;

import Models.DBModels.Employees.Shift;
import Models.DBModels.transport.Branch;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class ShiftsDB {
    private static final List<Shift> upcomingShiftsDB = new ArrayList<>();
    private static final List<Shift> pastShiftsDB = new ArrayList<>();

    // upcomingShiftsDB methods
    public static void addUpcomingShift(Shift shift) {
        upcomingShiftsDB.add(shift);
    }

    public static List<Shift> getUpcomingShifts() {
        return new ArrayList<>(upcomingShiftsDB); // return copy for safety
    }

    public static void removeUpcomingShift(Shift shift) {
        upcomingShiftsDB.remove(shift);
    }


    public static Shift findShiftById(String shiftId) {
        for (Shift shift : upcomingShiftsDB) {
            if (shift.getShiftID().equals(shiftId)) {
                return shift;
            }
        }
        return null;
    }
    public static Shift findShiftByDateTimeAndBranch(LocalDate date, LocalTime eta, Branch branch) {
        for (Shift shift : upcomingShiftsDB) {
            //TODO make sure the if condition is correct
            if (shift.getBranch() == null || !shift.getBranch().equals(branch)) {
                continue;
            }
            if (shift.getShiftDate().equals(date)) {
                LocalTime start = shift.getStartShiftTime();
                LocalTime end = shift.getEndShiftTime();
                if (!start.isAfter(eta) && !end.isBefore(eta)) {
                    return shift;
                }
            }
        }
        return null;
    }


    public static void moveShiftToPast() {
        LocalDate today = LocalDate.now();
        List<Shift> shiftsToMove = new ArrayList<>();

        // First, identify all shifts that need to be moved
        for (Shift shift : upcomingShiftsDB) {
            if (shift.getShiftDate().isBefore(today)) {
                shiftsToMove.add(shift);
            }
        }

        // Then move them to past shifts and remove from upcoming shifts
        for (Shift shift : shiftsToMove) {
            addPastShift(shift);
            removeUpcomingShift(shift);
        }
        // Clean up old past shifts
        removeOldPastShifts();
    }

    // pastShiftsDB methods
    public static void addPastShift(Shift shift) {
        pastShiftsDB.add(shift);
        // Check and remove old shifts whenever a new past shift is added
        removeOldPastShifts();
    }

    public static void removePastShift(Shift shift) {

        pastShiftsDB.remove(shift);
    }

    /**
     * Removes shifts from pastShiftsDB that are older than 7 years
     * This helps maintain database efficiency by removing very old records
     */
    public static void removeOldPastShifts() {
        LocalDate today = LocalDate.now();
        LocalDate sevenYearsAgo = today.minusYears(7);
        List<Shift> shiftsToRemove = new ArrayList<>();

        // Identify shifts older than 7 years
        for (Shift shift : pastShiftsDB) {
            if (shift.getShiftDate().isBefore(sevenYearsAgo)) {
                shiftsToRemove.add(shift);
            }
        }

        // Remove the identified old shifts
        for (Shift shift : shiftsToRemove) {
            removePastShift(shift);
        }
    }

}
