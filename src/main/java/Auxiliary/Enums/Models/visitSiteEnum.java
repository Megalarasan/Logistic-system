package Auxiliary.Enums.Models;

public enum visitSiteEnum {
    OVERLOAD(1),
    FAILED(2),
    SUCCESS(3);

    private final int value;

    visitSiteEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
