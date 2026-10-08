package Auxiliary.Enums.Models;

/**
 * TransportStatusEnum is an enumeration that represents the status of a transport operation.
 */
public enum TransportStatusEnum {
    PENDING(1),
    STARTED(2),
    DONE(3),
    DONE_WITH_ISSUES(4),
    CONTINUED_WITH_ISSUES(5),
    SOURCE_COMPLETED(6),
    DESTINATION_COMPLETED(7),
    SOURCE_COMPLETED_WITH_ISSUES(8),
    DESTINATION_COMPLETED_WITH_ISSUES(9), //Optional for now
    CANCELED(10);// optional for now


    private final int value;

    TransportStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}