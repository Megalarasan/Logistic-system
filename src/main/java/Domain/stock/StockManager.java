package Domain.stock;

import Models.DTOs.StockItem;

import java.util.ArrayList;
import java.util.List;

public class StockManager {
    private static final ArrayList<StockItem> stockItems = new ArrayList<>();

    /**
     * This method is used to search for stock items based on a search string and branch ID.
     *
     * @param searchString The search string to filter stock items.
     * @param branchId     The ID of the branch to filter stock items.
     * @return A list of relevant stock items.
     */
    public static ArrayList<StockItem> mockSearchAndReturnRelevantStockItems(String searchString, int branchId) {
        stockItems.clear();
        // Mock implementation to simulate searching and returning relevant stock items
        stockItems.add(new StockItem(1, "Item1", branchId, 10, "1213165" , 1));
        stockItems.add(new StockItem(2, "Item2", branchId, 20, "barcode132132" , 50));
        stockItems.add(new StockItem(3, "Item3", branchId, 30, "23616565", 20));
        stockItems.add(new StockItem(4, "Item4", branchId, 40, "163596898", 10));
        stockItems.add(new StockItem(5, "Item5", branchId, 50, "65165165165", 6));
        return stockItems;
    }


    public static StockItem getStockItemById(int stockItemId) {
        for (StockItem stockItem : stockItems) {
            if (stockItem.getStockItemId() == stockItemId) {
                return stockItem;
            }
        }
        return null; // Return null if no matching stock item is found
    }

    /**
     * This method is used to get stock items by their IDs.
     *
     * @param ids The list of IDs of the stock items to be retrieved.
     * @return A list of stock items with the specified IDs.
     */
    public static List<StockItem> mockGetStockItemsByIds(List<Integer> ids) {
        // Mock implementation to simulate getting stock items by IDs
        List<StockItem> stockItems = new ArrayList<>();
        for (int id : ids) {
            stockItems.add(new StockItem(id, "Item" + id, 1, 10, "barcode" + id , 1));
        }
        return stockItems;
    }

}
