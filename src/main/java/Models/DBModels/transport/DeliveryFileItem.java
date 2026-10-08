package Models.DBModels.transport;

import Auxiliary.Helper;
import Models.DBModels.BaseModel;
import jakarta.persistence.*;
import org.hibernate.Session;

@Entity
@Table(name = "delivery_file_items")
public class DeliveryFileItem extends BaseModel {
    @ManyToOne(optional = false)
    @JoinColumn(name = "delivery_file_id", nullable = false)
    private DeliveryFile deliveryFile;

    //@OneToOne
    //TODO: replace the int stockItemId with a StockItem object once it is in the DB!
    @Column(nullable = false)
    private int stockItemId;
    @Column(nullable = false)
    private double amount;
    @Column()
    private double amountPickedUp;

    public DeliveryFileItem(int id, int stockItemId, double amount, double amountPickedUp) {
        super(id, DeliveryFileItem.class.getName());
        this.stockItemId = stockItemId;
        this.amount = amount;
        this.amountPickedUp = amountPickedUp;
    }

    public DeliveryFileItem(DeliveryFileItem fileItem) {
        super(0, DeliveryFileItem.class.getName());
        this.copyFromOther(fileItem, null);
    }


    public void setDeliveryFile(DeliveryFile deliveryFile) {
        this.deliveryFile = deliveryFile;
    }

    public DeliveryFile getDeliveryFile() {
        return deliveryFile;
    }

    public DeliveryFileItem() {
        super(0, DeliveryFileItem.class.getName());
    }

    public int getStockItemId() {
        return stockItemId;
    }

    public void setStockItemId(int stockItemId) {
        this.stockItemId = stockItemId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmountPickedUp() {
        return amountPickedUp;
    }

    public void setAmountPickedUp(double amountPickedUp) {
        this.amountPickedUp = amountPickedUp;
    }

    @Override
    public String toString() {
        return "DeliveryFileItem{" +
                "stockItemId=" + stockItemId +
                ", amount=" + amount +
                ", amountPickedUp=" + amountPickedUp +
                "} " + super.toString();
    }

    /**
     * Copies the values from another DeliveryFileItem object to this one.
     *
     * @param other The other DeliveryFileItem object to copy from.
     */
    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (!(other instanceof DeliveryFileItem otherDeliveryFileItem)) {
            Helper.showError(String.format("%s::Other must be of type %s", this.getClass().getName(), this.getClass().getName()));
            return false;
        }
        this.stockItemId = otherDeliveryFileItem.stockItemId;
        this.amount = otherDeliveryFileItem.amount;
        this.amountPickedUp = otherDeliveryFileItem.amountPickedUp;
        return true;
    }
}
