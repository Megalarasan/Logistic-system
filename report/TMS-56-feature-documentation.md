# TMS-56 Feature Documentation

## Summary
This change addresses data-quality issues in the logistics supplier and stock model workflow. The issue was caused by duplicate supplier inserts being accepted, reserved-item checks accepting invalid input, and category IDs being shared across instances instead of being unique.

## Root cause
The supplier workflow accepted multiple entries for the same supplier name and treated blank or invalid values as valid. The stock category model also used a shared static counter instead of instance-level IDs, which broke uniqueness guarantees for category instances. The supplier item DTO accepted an integer-only setter even though the field is a double, which prevented valid quantity updates.

## Changes made
- Added validation in the supplier manager so duplicate names and blank fields are rejected.
- Hardened mock supplier reservation and un-reservation checks to fail fast on invalid or empty requests.
- Changed the category ID to be unique per instance and added input validation for category names.
- Corrected the amount setter on supplier item DTOs to accept the correct numeric type.

## Files updated
- `src/main/java/Domain/suppliers/SuppliersManager.java`
- `src/main/java/Models/DBModels/Stock/ProductCategory.java`
- `src/main/java/Models/DTOs/ItemsAtSupplier.java`

## Validation notes
These changes are aimed at satisfying the guarded edge cases around duplicate records, missing values, and inconsistent domain types in the logistics workflow. The logic now rejects invalid supplier requests and ensures category IDs remain stable and unique.
