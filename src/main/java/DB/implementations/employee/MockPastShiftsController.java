package DB.implementations.employee;

import DB.interfaces.employees.IPastShiftsController;
import Models.DBModels.Employees.Shift;

import java.time.LocalDate;
import java.util.*;

public class MockPastShiftsController implements IPastShiftsController {
    private static MockPastShiftsController instance;
    private final Map<String, Shift> pastShifts = new HashMap<>();

    private MockPastShiftsController() {
        MockPastShiftsController.getInstance();
    }

    public static MockPastShiftsController getInstance() {
        if (instance == null) {
            instance = new MockPastShiftsController();
        }
        return instance;
    }

    @Override
    public boolean addPastShift(Shift shift) {
        if (shift == null || shift.getShiftID() == null) return false;
        pastShifts.put(shift.getShiftID(), shift);
        return true;
    }

    @Override
    public boolean removePastShift(Shift shift) {
        if (shift == null || shift.getShiftID() == null) return false;
        return pastShifts.remove(shift.getShiftID()) != null;
    }

    @Override
    public Shift findShiftById(String shiftId) {
        if (shiftId == null) return null;
        return pastShifts.get(shiftId);
    }

    @Override
    public List<Shift> getPastShifts() {
        return new ArrayList<>(pastShifts.values());
    }

    @Override
    public int removeShiftsOlderThan(LocalDate date) {
        int originalSize = pastShifts.size();
        pastShifts.values().removeIf(shift -> shift.getShiftDate().isBefore(date));
        return originalSize - pastShifts.size();
    }

    @Override
    public void removeOlderThen7YearsShifts() {
        LocalDate sevenYearsAgo = LocalDate.now().minusYears(7);
        removeShiftsOlderThan(sevenYearsAgo);
    }

    @Override
    public List<String> getAllIds() {
        return new ArrayList<>(pastShifts.keySet());
    }

    // Optionally: helper for mock data loading
    public void loadMockShifts(List<Shift> mockList) {
        for (Shift shift : mockList) {
            pastShifts.put(shift.getShiftID(), shift);
        }
    }
}