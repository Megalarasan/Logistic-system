package Auxiliary.Enums.Domain;

/**
 * Enum representing the different fields that can be updated in a transport area.
 */
public enum UpdateTransportAreaEnum {
    Update_Name(1),
    Add_Suppliers(2),
    Remove_Suppliers(3),
    Add_Branches(4),
    Remove_Branches(5),
    UNDEFINED(0);

    private final int value;

    UpdateTransportAreaEnum(int value) {
        this.value = value;
    }

    /**
     * Returns the integer value associated with the enum.
     *
     * @return The integer value associated with the enum.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the string representation of the enum value.
     *
     * @return The string representation of the enum value.
     */
    public String getStringLabel() {
        return this.name().replace("_", " ");
    }

    public static UpdateTransportAreaEnum intToEnum(int value) {
        for (UpdateTransportAreaEnum e : UpdateTransportAreaEnum.values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        return UNDEFINED;
    }
}