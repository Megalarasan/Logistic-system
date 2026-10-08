package Models.DTOs.transport;

import Auxiliary.Enums.Domain.CreateNewTransportRouteResultEnum;
import Models.DBModels.transport.TransportRoute;
import Models.DTOs.StockItem;

import java.util.List;

public class CreateNewTransportRoutesResult {
    private List<TransportRoute> createdTransportRoutes;
    private CreateNewTransportRouteResultEnum resultEnum;
    private List<StockItem> unprovidedItems;

    public CreateNewTransportRoutesResult(List<TransportRoute> createdTransportRoutesIds,
                                          CreateNewTransportRouteResultEnum resultEnum,
                                          List<StockItem> unprovidedItems) {
        this.createdTransportRoutes = createdTransportRoutesIds;
        this.resultEnum = resultEnum;
        this.unprovidedItems = unprovidedItems;
    }

    public List<StockItem> getUnprovidedItems() {
        return unprovidedItems;
    }

    public void setUnprovidedItems(List<StockItem> unprovidedItems) {
        this.unprovidedItems = unprovidedItems;
    }

    public List<TransportRoute> getCreatedTransportRoutes() {
        return createdTransportRoutes;
    }

    public void setCreatedTransportRoutes(List<TransportRoute> createdTransportRoutes) {
        this.createdTransportRoutes = createdTransportRoutes;
    }

    public CreateNewTransportRouteResultEnum getResultEnum() {
        return resultEnum;
    }

    public void setResultEnum(CreateNewTransportRouteResultEnum resultEnum) {
        this.resultEnum = resultEnum;
    }

}
