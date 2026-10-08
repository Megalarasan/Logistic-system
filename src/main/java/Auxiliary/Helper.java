package Auxiliary;

import Auxiliary.Enums.Models.LicenseTypeEnum;
import Models.DBModels.BaseModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Helper {
    public final static int UndefinedUserInput = -1;
    public final static int UndefinedId = 0;

    /**
     * This method is used to get user input from the console.
     *
     * @return - 1 if the input is invalid, otherwise returns the user input as an integer.
     */
    public static int getUserInputInteger() {
        try {
            return new java.util.Scanner(System.in).nextInt();
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            return UndefinedUserInput;
        }
    }

    /**
     * This method is used to get user input from the console.
     *
     * @return - the user input as a string or null if the input is invalid.
     */
    public static String getUserInputString() {
        try {
            return new java.util.Scanner(System.in).nextLine();
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a string.");
            return null;
        }
    }

    /**
     * This method is used to get user double from the console.
     *
     * @return -1 if the input is invalid, otherwise returns the user input as a double.
     */
    public static double getUserInputDouble() {
        try {
            return new java.util.Scanner(System.in).nextDouble();
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            return UndefinedUserInput;
        }
    }

    /**
     * This method is used to show an error message to the user.
     *
     * @param message   The error message to be displayed.
     * @param trace     The stack trace of the error.
     * @param logToFile Whether to log the error message to a file or not.
     */
    public static void showError(String message, String trace, boolean logToFile) {
        String err = String.format("--------- System Error message: {%s} ---------\n%s\n--------- END TRACE ---------",
                message, trace != null ? trace + "\n" : "");
        System.err.println(err);
        if (logToFile) {
            //todo implement logging to file
        }
    }

    /**
     * This method is used to show an error message to the user.
     *
     * @param message The error message to be displayed.
     * @param trace   The stack trace of the error.
     */
    public static void showError(String message, String trace) {
        showError(message, trace, true);
    }

    /**
     * This method is used to show an error message to the user.
     *
     * @param message The error message to be displayed.
     * @param e       The exception that caused the error.
     */
    public static void showError(String message, Exception e) {
        showError(message, getStringTraceOfException(e), true);
    }

    /**
     * This method is used to show an error message to the user.
     *
     * @param message The error message to be displayed.
     */
    public static void showError(String message) {
        showError(message, null, true);
    }

    /**
     * This method is used to show an error message to the user.
     *
     * @param message   - The error message to be displayed.
     * @param logToFile - Whether to log the error message to a file or not.
     */
    public static void print(String message, boolean logToFile) {
        System.out.print(message);
        if (logToFile) {
            //todo implement logging to file
        }
    }

    /**
     * This method is used to show an error message to the user.
     *
     * @param message - The error message to be displayed.
     */
    public static void print(String message) {
        print(message, true);
    }

    /**
     * This method is used to show an error message to the user.
     *
     * @param message   - The error message to be displayed.
     * @param logToFile - Whether to log the error message to a file or not.
     */
    public static void println(String message, boolean logToFile) {
        System.out.println(message);
        if (logToFile) {
            //todo implement logging to file
        }
    }

    /**
     * This method is used to show an error message to the user.
     *
     * @param message - The error message to be displayed.
     */
    public static void println(String message) {
        println(message, true);
    }

    /**
     * This method is used to get the stack trace of an exception as a string.
     *
     * @param e - The exception to get the stack trace from.
     * @return - The stack trace of the exception as a string.
     */
    public static String getStringTraceOfException(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * This method is used to check if a given ID is valid.
     *
     * @param areas   - The list of areas to check against.
     * @param checkId - The ID to check.
     * @return - true if the ID is valid, false otherwise.
     */
    public static boolean isValidId(List<? extends BaseModel> areas, int checkId) {
        return checkId > 0 && areas.stream().anyMatch(a -> a.getId() == checkId);
    }

    /**
     * Retrieves a list of unique, valid IDs from user input based on a provided list of models.
     *
     * @param srcLst - the list of models extending BaseModel
     * @return an ArrayList of unique, valid IDs entered by the user
     */
    public static ArrayList<Integer> retrieveIdsListFromInput(List<? extends BaseModel> srcLst) {
        Set<Integer> uniqueIds = new HashSet<>();//TODO do unit testing for this shit

        while (uniqueIds.isEmpty()) {
            Helper.println("Enter IDs like this: '1,2,3,4,5' or just '1' to add a single object.");
            String input = Helper.getUserInputString();

            if (input == null || input.trim().isEmpty()) {
                Helper.println("Invalid input. Please try again.");
                continue;
            }

            for (String rawId : input.split(",")) {
                try {
                    int id = Integer.parseInt(rawId.trim());
                    if (Helper.isValidId(srcLst, id)) {
                        uniqueIds.add(id);
                    } else {
                        Helper.println("Invalid ID: " + id);
                    }
                } catch (NumberFormatException e) {
                    Helper.println("Invalid number format: '" + rawId.trim() + "'. Please enter only integers.");
                }
            }

            if (uniqueIds.isEmpty()) {
                Helper.println("No valid IDs were entered. Please try again.");
            }
        }

        return new ArrayList<>(uniqueIds);
    }

    public static final String DefaultDateTimeFormat = "yyyy-MM-dd HH:mm";
    public static final DateTimeFormatter DefaultDateTimeFormatter = java.time.format.DateTimeFormatter
            .ofPattern(DefaultDateTimeFormat);

    /**
     * This method is used to convert a string to a LocalDateTime object.
     *
     * @param dateTime - The string to be converted.
     * @return - The LocalDateTime object.
     */
    public static LocalDateTime stringToDateTime(String dateTime) {
        return stringToDateTime(dateTime, DefaultDateTimeFormat);
    }

    /**
     * This method is used to convert a string to a LocalDateTime object.
     *
     * @param dateTime - The string to be converted.
     * @param format   - The format of the string such as "yyyy-MM-dd HH:mm".
     * @return - The LocalDateTime object.
     */
    public static LocalDateTime stringToDateTime(String dateTime, String format) {
        try {
            if (dateTime == null || dateTime.trim().isEmpty() || format == null || format.trim().isEmpty()) {
                Helper.showError("Invalid date and time format. Please try again.");
                return null;
            }
            return LocalDateTime.parse(dateTime, java.time.format.DateTimeFormatter.ofPattern(format));
        } catch (java.time.format.DateTimeParseException e) {
            Helper.showError("Invalid date and time format. Please try again.");
            return null;
        }
    }

    /**
     * This method is used to check if a given license type is valid.
     * Only supported license types are: B, C1, C, C+E
     *
     * @param licenseType - The license type to be checked.
     * @return - true if the license type is valid, false otherwise.
     */
    public static boolean isLicenseTypeValid(LicenseTypeEnum licenseType) {
        return licenseType != null
                && (
                licenseType.equals(LicenseTypeEnum.B)
                        || licenseType.equals(LicenseTypeEnum.C)
                        || licenseType.equals(LicenseTypeEnum.C1)
                        || licenseType.equals(LicenseTypeEnum.CPlusE));
    }

    /**
     * This method is used to check if a given date and time is in between two other date and time.
     *
     * @param dateTime - The date and time to be checked.
     * @param start    - The start date and time.
     * @param end      - The end date and time.
     * @return - true if the date and time is in between, false otherwise.
     */
    public static boolean isDateTimeInBetween(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        return (dateTime.isAfter(start) || dateTime.isEqual(start)) && (dateTime.isBefore(end) || dateTime.isEqual(end));
    }


    /**
     * This method is used to check if a given license type is valid for a truck.
     * Only supported license types are: B, C1, C, C+E
     *
     * @param driverLicenseType   - The license type to be checked.
     * @param requiredLicenseType - The required license type for the truck.
     * @return - true if the license type is valid for the truck, false otherwise.
     */
    public static boolean isLicenseTypeValidPerTruck(LicenseTypeEnum driverLicenseType, LicenseTypeEnum requiredLicenseType) {
        if (!isLicenseTypeValid(driverLicenseType) || !isLicenseTypeValid(requiredLicenseType)) {
            return false;
        }
        switch (driverLicenseType) {
            case CPlusE -> {
                return requiredLicenseType.equals(LicenseTypeEnum.CPlusE) || requiredLicenseType.equals(LicenseTypeEnum.C)
                        || requiredLicenseType.equals(LicenseTypeEnum.C1) || requiredLicenseType.equals(LicenseTypeEnum.B);
            }
            case C -> {
                return requiredLicenseType.equals(LicenseTypeEnum.C) || requiredLicenseType.equals(LicenseTypeEnum.C1)
                        || requiredLicenseType.equals(LicenseTypeEnum.B);
            }
            case LicenseTypeEnum.C1 -> {
                return requiredLicenseType.equals(LicenseTypeEnum.C1) || requiredLicenseType.equals(LicenseTypeEnum.B);
            }
            default -> {
                return false;
            }
        }
    }

    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public static LocalDate getValidDateForMock(LocalDate date) {
        if (date.getDayOfWeek() == java.time.DayOfWeek.FRIDAY || date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY) {
            return date.minusDays(2);
        }
        return date;
    }
    public static LocalDateTime getValidDateTimeForMock(LocalDateTime dateTime) {
        if (dateTime.getDayOfWeek() == java.time.DayOfWeek.FRIDAY || dateTime.getDayOfWeek() == java.time.DayOfWeek.SATURDAY) {
            return dateTime.minusDays(2);
        }
        return dateTime;
    }
}
