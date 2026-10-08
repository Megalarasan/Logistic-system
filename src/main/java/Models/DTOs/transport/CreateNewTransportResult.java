package Models.DTOs.transport;

import Auxiliary.Enums.Domain.CreateNewTransportResultEnum;

public class CreateNewTransportResult {
    int createdTransportId;
    CreateNewTransportResultEnum resultEnum;

    public CreateNewTransportResult(int createdTransportId, CreateNewTransportResultEnum resultEnum) {
        this.createdTransportId = createdTransportId;
        this.resultEnum = resultEnum;
    }

    public int getCreatedTransportId() {
        return createdTransportId;
    }

    public void setCreatedTransportId(int createdTransportId) {
        this.createdTransportId = createdTransportId;
    }

    public CreateNewTransportResultEnum getResultEnum() {
        return resultEnum;
    }

    public void setResultEnum(CreateNewTransportResultEnum resultEnum) {
        this.resultEnum = resultEnum;
    }

}
