package Models.DBModels.transport;

import Auxiliary.Helper;
import Models.DBModels.BaseModel;
import Auxiliary.Enums.Models.TransportStatusEnum;
import jakarta.persistence.*;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transports")
public class Transport extends BaseModel {
    @Column(nullable = false)
    private LocalDateTime plannedStartDateTime;
    @Column(nullable = false)
    private LocalDateTime plannedEndDateTime;
    @Column(nullable = false)
    private TransportStatusEnum status;
    @Column()
    private LocalDateTime actualStartDatetime;
    @Column()
    private LocalDateTime actualEndDatetime;

    @ManyToOne
    @JoinColumn(name = "truck_id", referencedColumnName = "id", nullable = false)
    private Truck truck;
    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id", nullable = false)
    private Driver driver;

    @OneToMany(mappedBy = "transport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransportRoute> transportRoutes;

    @OneToMany(mappedBy = "transport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransportIssueLog> transportIssueLogs;

    public Transport(int id, LocalDateTime plannedStartDateTime, LocalDateTime plannedEndDateTime, TransportStatusEnum status, Truck truck, Driver driver,
                     List<TransportRoute> transportRoutes, List<TransportIssueLog> transportIssueLogs,
                     LocalDateTime actualStartDatetime, LocalDateTime actualEndDatetime) {
        super(id, Transport.class.getName());
        this.plannedStartDateTime = plannedStartDateTime;
        this.plannedEndDateTime = plannedEndDateTime;
        this.status = status;
        this.actualStartDatetime = actualStartDatetime;
        this.actualEndDatetime = actualEndDatetime;
        this.truck = truck;
        this.driver = driver;
        this.transportRoutes = transportRoutes;
        this.transportIssueLogs = transportIssueLogs;

    }

    public Transport() {
        super(0, Transport.class.getName());
    }

    public LocalDateTime getPlannedStartDateTime() {
        return plannedStartDateTime;
    }

    public LocalDateTime getPlannedEndDateTime() {
        return plannedEndDateTime;
    }

    public void setPlannedStartDateTime(LocalDateTime plannedDateTime) {
        this.plannedStartDateTime = plannedDateTime;
    }

    public void setPlannedEndDateTime(LocalDateTime plannedEndDateTime) {
        this.plannedEndDateTime = plannedEndDateTime;
    }

    public TransportStatusEnum getStatus() {
        return status;
    }

    public void setStatus(TransportStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getActualStartDatetime() {
        return actualStartDatetime;
    }

    public void setActualStartDatetime(LocalDateTime actualStartDatetime) {
        this.actualStartDatetime = actualStartDatetime;
    }

    public LocalDateTime getActualEndDatetime() {
        return actualEndDatetime;
    }

    public void setActualEndDatetime(LocalDateTime actualEndDatetime) {
        this.actualEndDatetime = actualEndDatetime;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public List<TransportRoute> getTransportRoutes() {
        return transportRoutes;
    }

    public void setTransportRoutes(List<TransportRoute> transportRoutes) {
        this.transportRoutes = transportRoutes;
    }

    public List<TransportIssueLog> getTransportIssueLogs() {
        return transportIssueLogs;
    }

    public void setTransportIssueLogs(List<TransportIssueLog> transportIssueLogs) {
        this.transportIssueLogs = transportIssueLogs;
    }

    public int getTruckId() {
        return truck != null ? truck.getId() : 0;
    }

    public int getDriverId() {
        return driver != null ? driver.getId() : 0;
    }

    private List<Integer> getTransportRouteIds() {
        return transportRoutes != null ? transportRoutes.stream().map(TransportRoute::getId).toList() : List.of();
    }

    private List<Integer> getTransportIssueLogIds() {
        return transportIssueLogs != null ? transportIssueLogs.stream().map(TransportIssueLog::getId).toList() : List.of();
    }


    @Override
    public String toString() {
        return "Transport{" + "id=" + getId() + ", plannedStartDateTime=" + plannedStartDateTime + ", plannedEndDateTime=" + plannedEndDateTime +
                ", status=" + status + ", truckId=" + getTruckId() + ", driverId=" + getDriverId() + ", transportRouteIds=" + getTransportRouteIds() + ", transportIssueLogIds=" + getTransportIssueLogIds() + ", actualStartDatetime=" + actualStartDatetime + ", actualEndDatetime=" + actualEndDatetime + '}';
    }

    @Override
    public String toPrettyString() {
        return String.format("""
                ID: %d
                Planned Start DateTime: %s
                Planned End DateTime: %s
                Status: %s
                Truck ID: %d
                Driver ID: %d
                Transport Route IDs: %s
                Transport Issue Log IDs: %s
                Actual Start DateTime: %s
                Actual End DateTime: %s
                """, getId(), plannedStartDateTime, plannedEndDateTime, status, getTruckId(), getDriverId(), getTransportRouteIds(), getTransportIssueLogIds(), actualStartDatetime, actualEndDatetime);
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (!(other instanceof Transport otherTransport)) {
            Helper.showError(String.format("%s::Other must be of type %s", this.getClass().getName(), this.getClass().getName()));
            return false;
        }

        this.plannedStartDateTime = otherTransport.plannedStartDateTime;
        this.plannedEndDateTime = otherTransport.plannedEndDateTime;
        this.status = otherTransport.status;
        this.actualStartDatetime = otherTransport.actualStartDatetime;
        this.actualEndDatetime = otherTransport.actualEndDatetime;
        this.truck = session.merge(otherTransport.truck);
        this.driver = session.merge(otherTransport.driver);

        // Clear and merge transportRoutes
        if (this.transportRoutes != null) {
            this.transportRoutes.clear();
        }
        if (otherTransport.transportRoutes != null) {
            for (TransportRoute route : otherTransport.transportRoutes) {
                TransportRoute mergedRoute = session.merge(route);
                mergedRoute.setTransport(this);
                this.transportRoutes.add(mergedRoute);
            }
        }

        // Clear and merge transportIssueLogs
        if (this.transportIssueLogs != null) {
            this.transportIssueLogs.clear();
        }
        if (otherTransport.transportIssueLogs != null) {
            for (TransportIssueLog log : otherTransport.transportIssueLogs) {
                TransportIssueLog mergedLog = session.merge(log);
                mergedLog.setTransport(this);
                this.transportIssueLogs.add(mergedLog);
            }
        }

        return true;
    }
}
