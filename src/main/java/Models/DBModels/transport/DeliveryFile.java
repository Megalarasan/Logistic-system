package Models.DBModels.transport;

import Models.DBModels.BaseModel;
import jakarta.persistence.*;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_files")
public class DeliveryFile extends BaseModel {
    @OneToMany(mappedBy = "deliveryFile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)/*We use Eager because we need the file items ASAP*/
    private List<DeliveryFileItem> deliveryFileItems;

    public DeliveryFile(int id, List<DeliveryFileItem> deliveryFileItemIds) {
        super(id, DeliveryFile.class.getName());
        this.deliveryFileItems = deliveryFileItemIds;
        for (var item : deliveryFileItemIds) {
            item.setDeliveryFile(this);
        }
    }

    public DeliveryFile() {
        super(0, DeliveryFile.class.getName());
    }

    public List<DeliveryFileItem> getDeliveryFileItems() {
        return deliveryFileItems;
    }

    public void setDeliveryFileItems(List<DeliveryFileItem> deliveryFileItems) {
        this.deliveryFileItems = deliveryFileItems;
        for (var item : deliveryFileItems) {
            item.setDeliveryFile(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeliveryFile{");
        if (deliveryFileItems != null) {
            sb.append("deliveryFileItems=[");
            for (int i = 0; i < deliveryFileItems.size(); i++) {
                sb.append(deliveryFileItems.get(i).toString());
                if (i < deliveryFileItems.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        } else {
            sb.append("deliveryFileItems=null");
        }
        sb.append("} ");
        sb.append(super.toString());
        return sb.toString();
    }

    @Override
    public boolean copyFromOther(BaseModel other, Session session) {
        if (other instanceof DeliveryFile otherDeliveryFile) {
            this.setDeliveryFileItems(otherDeliveryFile.getDeliveryFileItems());
            return true;
        }
        return false;
    }
}
