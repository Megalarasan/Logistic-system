package DB.implementations.employee;

//import DB.implementations.employee.MockPastShiftsController;
import DB.interfaces.employees.IUpcomingShiftsController;
import DB.interfaces.employees.IPastShiftsController;
import Models.DBModels.Employees.Shift;
import Models.DBModels.transport.Branch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class MockUpcomingShiftsController implements IUpcomingShiftsController {
    private static MockUpcomingShiftsController instance;
    private final Map<String, Shift> upcomingShifts = new HashMap<>();
    private final IPastShiftsController pastShiftsController;

    private MockUpcomingShiftsController() {
        this.pastShiftsController = MockPastShiftsController.getInstance();
    }

    public static MockUpcomingShiftsController getInstance() {
        if (instance == null) {
            instance = new MockUpcomingShiftsController();
        }
        return instance;
    }

    @Override
    public boolean addUpcomingShift(Shift shift) {
        if (shift == null || shift.getShiftID() == null) return false;
        upcomingShifts.put(shift.getShiftID(), shift);
        return true;
    }

    @Override
    public boolean removeUpcomingShift(Shift shift) {
        if (shift == null || shift.getShiftID() == null) return false;
        return upcomingShifts.remove(shift.getShiftID()) != null;
    }

    @Override
    public boolean updateShiftInDB(Shift updatedShift) {
        if (updatedShift == null || updatedShift.getShiftID() == null) return false;
        if (!upcomingShifts.containsKey(updatedShift.getShiftID())) return false;
        upcomingShifts.put(updatedShift.getShiftID(), updatedShift);
        return true;
    }

    @Override
    public Shift findShiftById(String shiftId) {
        if (shiftId == null) return null;
        return upcomingShifts.get(shiftId);
    }

    @Override
    public List<Shift> getUpcomingShifts() {
        return new ArrayList<>(upcomingShifts.values());
    }

    @Override
    public Shift findShiftByDateTimeAndBranch(LocalDate date, LocalTime time, Branch branch) {
        for (Shift shift : upcomingShifts.values()) {
            if (!shift.getShiftDate().equals(date)) continue;
            if (!shift.getBranch().equals(branch)) continue;
            LocalTime start = shift.getStartShiftTime();
            LocalTime end = shift.getEndShiftTime();
            if (!start.isAfter(time) && !end.isBefore(time)) {
                return shift;
            }
        }
        return null;
    }

    @Override
    public int moveShiftsToPast() {
        LocalDate today = LocalDate.now();
        List<String> shiftsToMove = new ArrayList<>();
        for (Shift shift : upcomingShifts.values()) {
            if (shift.getShiftDate().isBefore(today)) {
                shiftsToMove.add(shift.getShiftID());
            }
        }
        int count = 0;
        for (String shiftId : shiftsToMove) {
            Shift shift = upcomingShifts.get(shiftId);
            shift.setPast(true);
            if (pastShiftsController.addPastShift(shift)) {
                upcomingShifts.remove(shiftId);
                count++;
            }
        }
        return count;
    }

    @Override
    public List<String> getAllIds() {
        return new ArrayList<>(upcomingShifts.keySet());
    }

    // Optionally: helper to load mock data
    public void loadMockShifts(List<Shift> mockList) {
        for (Shift shift : mockList) {
            upcomingShifts.put(shift.getShiftID(), shift);
        }
    }
}