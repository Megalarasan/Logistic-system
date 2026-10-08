package DB.implementations.transport;

import DB.interfaces.transport.ITransportIssueLogController;
import Models.DBModels.transport.TransportIssueLog;

import java.util.ArrayList;
import java.util.List;

public class MockTransportIssueLogController implements ITransportIssueLogController {
    private static MockTransportIssueLogController instance = null;
    private final ArrayList<TransportIssueLog> transportIssueLogs;
    private int idCounter = 1;

    private MockTransportIssueLogController() {
        this.transportIssueLogs = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of MockTransportIssueLogController.
     *
     * @return The singleton instance of MockTransportIssueLogController.
     */
    public static MockTransportIssueLogController getInstance() {
        if (instance == null) {
            instance = new MockTransportIssueLogController();
        }
        return instance;
    }

    @Override
    public int addTransportIssueLog(TransportIssueLog transportIssueLog) {
        if (transportIssueLog == null) {
            return -1;
        }
        transportIssueLog.setId(idCounter++);
        transportIssueLogs.add(transportIssueLog);
        return transportIssueLog.getId();
    }

    @Override
    public boolean updateTransportIssueLog(TransportIssueLog transportIssueLog, int id) {
        if (transportIssueLog == null || id <= 0) {
            return false;
        }
        for (int i = 0; i < transportIssueLogs.size(); i++) {
            if (transportIssueLogs.get(i).getId() == id) {
                transportIssueLogs.set(i, transportIssueLog);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteTransportIssueLog(int id) {
        if (id <= 0) {
            return false;
        }
        for (int i = 0; i < transportIssueLogs.size(); i++) {
            if (transportIssueLogs.get(i).getId() == id) {
                transportIssueLogs.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public TransportIssueLog getTransportIssueLogById(int id) {
        if (id <= 0) {
            return null;
        }
        for (TransportIssueLog transportIssueLog : transportIssueLogs) {
            if (transportIssueLog.getId() == id) {
                return transportIssueLog;
            }
        }
        return null;
    }

    @Override
    public List<Integer> getAllIds() {
        List<Integer> ids = new ArrayList<>();
        for (TransportIssueLog transportIssueLog : transportIssueLogs) {
            ids.add(transportIssueLog.getId());
        }
        return ids;
    }
}
