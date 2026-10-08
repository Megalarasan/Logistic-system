package Models.DBModels.Stock;

public class Validate {


    public static boolean isDateValide(String date) {
        // Check if the date is in the format DD/MM/YYYY
        String regex = "\\d{2}/\\d{2}/\\d{4}";
        return date.matches(regex);
    }
    public static boolean iisValidePrecentage(double percentage) {
        // Check if the percentage is between 0 and 100
        return percentage >= 0 && percentage <= 1;
    }

    public static boolean isValideLocation(String location) {
        // Check if the location is not empty and does not contain special characters
        if(location == null || location.isEmpty()) {
            return false;
        }

        if(location=="warehouse" || location=="shelves") {
            return true;
        }
        else
            return false;
    }
}
