package Models.DBModels.transport;

import Models.DBModels.BaseModel;
import jakarta.persistence.*;
import org.hibernate.Session;

@Entity
@Table(name = "transport_issue_logs")
public class TransportIssueLog extends BaseModel {
    @ManyToOne
    @JoinColumn(name = "current_truck_id", referencedColumnName = "id", nullable = false)
    private Truck currentTruck;

    @ManyToOne
    @JoinColumn(name = "new_truck_id", referencedColumnName = "id")
    private Truck newTruck;


    @ManyToOne
    @JoinColumn(name = "related_transport_route_id", referencedColumnName = "id", nullable = false)
    private TransportRoute relatedTransportRoute;

    @OneToOne
    @JoinColumn(name = "delivery_file_of_unloaded_items_id", referencedColumnName = "id")
    private DeliveryFile deliveryFileOfUnloadedItems;

    @ManyToOne
    @JoinColumn(name = "transport_id")
    private Transport transport;

    public TransportIssueLog(int id, Truck currentTruck, Truck newTruck, TransportRoute relatedTransportRoute,
                             DeliveryFile deliveryFileOfUnloadedItems) {
        super(id, TransportIssueLog.class.getName());
        this.currentTruck = currentTruck;
        this.newTruck = newTruck;
        this.relatedTransportRoute = relatedTransportRoute;
        this.deliveryFileOfUnloadedItems = deliveryFileOfUnloadedItems;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Transport getTransport() {
        return transport;
    }

    public TransportIssueLog() {
        super(0, TransportIssueLog.class.getName());
    }

    public Truck getCurrentTruck() {
        return currentTruck;
    }

    public void setCurrentTruck(Truck currentTruck) {
        this.currentTruck = currentTruck;
    }

    public Truck getNewTruck() {
        return newTruck;
    }

    public void setNewTruck(Truck newTruck) {
        this.newTruck = newTruck;
    }

    public TransportRoute getRelatedTransportRoute() {
        return relatedTransportRoute;
    }
    public void setRelatedTransportRoute(TransportRoute relatedTransportRoute) {
        this.relatedTransportRoute = relatedTransportRoute;
    }

    public DeliveryFile getDeliveryFileOfUnloadedItems() {
        return deliveryFileOfUnloadedItems;
    }

    public void setDeliveryFileOfUnloadedItems(DeliveryFile deliveryFileOfUnloadedItems) {
        this.deliveryFileOfUnloadedItems = deliveryFileOfUnloadedItems;
    }

    @Override
    public String toString() {
        return "TransportIssueLog{" +
                "currentTruck=" + currentTruck +
                ", newTruck=" + newTruck +
                ", relatedTransportRoute=" + relatedTransportRoute +
                ", deliveryFileOfUnloadedItems=" + deliveryFileOfUnloadedItems +
                '}';
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (other instanceof TransportIssueLog otherTransportIssueLog) {
            this.setCurrentTruck(otherTransportIssueLog.getCurrentTruck());
            this.setNewTruck(otherTransportIssueLog.getNewTruck());
            this.setRelatedTransportRoute(otherTransportIssueLog.getRelatedTransportRoute());
            this.setDeliveryFileOfUnloadedItems(otherTransportIssueLog.getDeliveryFileOfUnloadedItems());
            return true;
        }
        return false;
    }
}
