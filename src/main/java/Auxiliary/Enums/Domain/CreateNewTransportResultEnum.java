package Auxiliary.Enums.Domain;

public enum CreateNewTransportResultEnum {
    Success(1),
    StockerUnavailable(2),
    InvalidInput(3),
    TransportCreationFailed(4),
    UNDEFINED(0);

    private final int value;

    CreateNewTransportResultEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CreateNewTransportResultEnum intToEnum(int value) {
        for (CreateNewTransportResultEnum e : CreateNewTransportResultEnum.values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return UNDEFINED;
    }
}