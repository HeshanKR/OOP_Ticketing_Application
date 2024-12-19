//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.repository;
import com.hkrw2082289.ticketing_system.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This Repository interface for managing {@link Vendor} entities.
 *
 * This interface extends {@link JpaRepository}, providing standard CRUD operations
 * and custom query methods for working with {@link Vendor} data in the database.
 *
 * This Annotated with {@link Repository} to indicate that it is a Spring-managed component
 * and to enable exception translation for database access errors.
 */
@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {

    /**
     * This checks if a {@link Vendor} exists with the given vendor ID.
     *
     * @param vendorId the unique identifier of the vendor to check.
     * @return {@code true} if a vendor exists with the specified vendor ID, otherwise {@code false}.
     */
    boolean existsByVendorId(String vendorId);

    /**
     * This finds a {@link Vendor} by their vendor ID and password.
     *
     * @param vendorId the unique identifier of the vendor.
     * @param password the password associated with the vendor.
     * @return the {@link Vendor} with the specified vendor ID and password,
     *         or {@code null} if no such vendor exists.
     */
    Vendor findByVendorIdAndPassword(String vendorId, String password);
}