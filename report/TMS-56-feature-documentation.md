# TMS-56 Feature Documentation

## 1. Feature & Jira Story Metadata
* **Jira Ticket ID & Title:** TMS-56 - Supplier and stock validation hardening
* **Target Repository:** Logistic-system
* **Working Branch:** feature/TMS-56
* **Author / Agent:** Jira Multi-Repo Engineer / S.Megalarasan
* **Implementation Date:** 2026-10-09

## 2. Context & Acceptance Criteria
### Problem Statement
The logistics workflow accepted invalid supplier input, allowed duplicate supplier records, and treated some stock metadata as weakly validated. This created inconsistent supplier data and unreliable stock-category identity in the supplier-to-stock integration layer.

### Acceptance Criteria Verification
1. Reject blank supplier name, address, and phone values when creating a supplier.
   - Implemented in [src/main/java/Domain/suppliers/SuppliersManager.java](src/main/java/Domain/suppliers/SuppliersManager.java): the addSupplier method now validates the required fields before writing any supplier record.
2. Prevent duplicate supplier entries for the same name.
   - Implemented in the same supplier manager: the method checks the existing supplier by name and returns early with an error instead of creating a duplicate record.
3. Harden supplier reservation/unreservation mock validation for invalid or empty requests.
   - Implemented in [src/main/java/Domain/suppliers/SuppliersManager.java](src/main/java/Domain/suppliers/SuppliersManager.java): both mockReserveItemsOfSupplier and mockUnReserveItemsOfSupplier now reject invalid supplier IDs, null lists, empty lists, and non-positive stock quantities.
4. Ensure product categories get unique IDs and invalid category names are rejected.
   - Implemented in [src/main/java/Models/DBModels/Stock/ProductCategory.java](src/main/java/Models/DBModels/Stock/ProductCategory.java): the class now assigns a unique instance ID using a monotonic counter and throws for blank names.
5. Correct the supplier item amount setter to use the real quantity type.
   - Implemented in [src/main/java/Models/DTOs/ItemsAtSupplier.java](src/main/java/Models/DTOs/ItemsAtSupplier.java): the setter now accepts double values so the DTO matches the underlying amount field.

## 3. Technical Architecture & Design Changes
### Component Modifications
- [src/main/java/Domain/suppliers/SuppliersManager.java](src/main/java/Domain/suppliers/SuppliersManager.java): added validation and guard logic for supplier creation and mock inventory reservation flows.
- [src/main/java/Models/DBModels/Stock/ProductCategory.java](src/main/java/Models/DBModels/Stock/ProductCategory.java): corrected unique ID generation and enforced non-blank category names.
- [src/main/java/Models/DTOs/ItemsAtSupplier.java](src/main/java/Models/DTOs/ItemsAtSupplier.java): fixed numeric typing for quantity updates.

### Key Algorithmic Rules
- Supplier creation fails immediately when any required field is blank.
- Duplicate supplier names are blocked before persisting new supplier data.
- Reservation requests are rejected unless the supplier exists and includes valid, positive item IDs and quantities.
- Product category names are trimmed and validated before assignment, preventing empty or whitespace-only values.
- Category IDs are assigned from a static sequence to preserve uniqueness across created instances.

### State & Persistence Behavior
- No database migration was required for this fix. The update is limited to validation logic and model consistency.
- Validation failure is handled before persistence or reservation processing, preventing corrupted supplier and stock state.
- The change is non-breaking to successful valid flows; only invalid requests are now rejected earlier.

## 4. File Inventory

| File Path | Action Type (`Created` / `Modified` / `Deleted`) | Purpose / Responsibility |
| :--- | :--- | :--- |
| [src/main/java/Domain/suppliers/SuppliersManager.java](src/main/java/Domain/suppliers/SuppliersManager.java) | Modified | Validates supplier creation and reservation inputs, blocks duplicates, and guards invalid requests. |
| [src/main/java/Models/DBModels/Stock/ProductCategory.java](src/main/java/Models/DBModels/Stock/ProductCategory.java) | Modified | Ensures unique instance IDs and rejects blank category names. |
| [src/main/java/Models/DTOs/ItemsAtSupplier.java](src/main/java/Models/DTOs/ItemsAtSupplier.java) | Modified | Fixes the amount setter type so supplier item quantities are stored correctly. |
| [report/TMS-56-feature-documentation.md](report/TMS-56-feature-documentation.md) | Created | Feature documentation for the implemented story. |

## 5. Verification & Testing Guide
### Automated Test Coverage
- No new automated test class was added for this specific patch in the current branch.
- Existing project verification should include the Java project test suite and any supplier/stock tests already present in the repository.

### Manual Verification Steps
1. Build the project with Maven:
   - `mvn test`
2. Verify supplier creation rejects invalid data:
   - attempt `addSupplier("", "Main St", "123")`
   - attempt `addSupplier("Supplier A", "", "123")`
   - confirm each call returns false or fails validation.
3. Verify duplicate supplier creation is rejected:
   - create a supplier named `Supplier A`
   - create another with the same name
   - confirm the second call is blocked.
4. Verify reservation validation fails for invalid data:
   - call `mockReserveItemsOfSupplier(1, null)`
   - call `mockReserveItemsOfSupplier(1, emptyList)`
   - call `mockReserveItemsOfSupplier(1, List.of(invalidItem))`
   - confirm all reject.
5. Verify product category behavior:
   - instantiate `new ProductCategory("Bakery")`
   - instantiate `new ProductCategory("Dairy")`
   - confirm IDs are unique.
   - confirm `new ProductCategory("   ")` throws `IllegalArgumentException`.

### Edge Cases Validated
- Empty and whitespace-only field values
- Duplicate supplier names
- Null or empty item lists during reservation
- Non-positive item quantities
- Blank category names
- Unique category ID generation across repeated instantiation

## 6. Backward Compatibility & Rollback
### Breaking Changes
- This is a non-breaking validation fix. Valid flows continue to work as before; only invalid data paths are now rejected earlier.

### Rollback Procedure
- Revert the feature branch to the prior commit or reset the three modified Java files to the previous version.
- Remove the report file if the story is deemed not ready for merge.
- Re-run `mvn test` after rollback to confirm the repository returns to its previous behavior.
