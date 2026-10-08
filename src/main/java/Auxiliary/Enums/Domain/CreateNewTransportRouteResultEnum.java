package Auxiliary.Enums.Domain;

/**
 * Enum representing the different fields that can be updated for a branch.
 */
public enum CreateNewTransportRouteResultEnum {
    Success(1),
    SomeItemsUnprovided(2),
    UNDEFINED(0);

    private final int value;

    CreateNewTransportRouteResultEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CreateNewTransportRouteResultEnum intToEnum(int value) {
        for (CreateNewTransportRouteResultEnum e : CreateNewTransportRouteResultEnum.values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return UNDEFINED;
    }
}