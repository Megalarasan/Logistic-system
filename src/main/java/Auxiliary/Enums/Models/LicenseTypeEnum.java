package Auxiliary.Enums.Models;

public enum LicenseTypeEnum {
    B("B"),
    C("C"),
    C1("C1"),
    CPlusE("C+E"),
    UNDEFINED("UNDEFINED");

    private final String value;

    LicenseTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LicenseTypeEnum stringToEnum(String licenseType) {
        if (licenseType != null
                && !licenseType.trim().isEmpty()) {
            return licenseType.equalsIgnoreCase("B") ? B
                    : licenseType.equalsIgnoreCase("C") ? C :
                    licenseType.equalsIgnoreCase("C1") ? C1 :
                            licenseType.equalsIgnoreCase("C+E") ? CPlusE :
                                    UNDEFINED;
        }
        return UNDEFINED;
    }
}
