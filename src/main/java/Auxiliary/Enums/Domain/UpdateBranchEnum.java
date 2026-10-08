package Auxiliary.Enums.Domain;

/**
 * Enum representing the different fields that can be updated for a branch.
 */
public enum UpdateBranchEnum {
    Name(1),
    Address(2),
    Phone(3),
    UNDEFINED(0);

    private final int value;

    UpdateBranchEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UpdateBranchEnum intToEnum(int value) {
        for (UpdateBranchEnum e : UpdateBranchEnum.values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return UNDEFINED;
    }
}