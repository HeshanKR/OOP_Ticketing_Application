//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.repository;
import com.hkrw2082289.ticketing_system.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This Repository interface is for managing {@link Customer} entities.
 * This interface extends {@link JpaRepository}, providing standard CRUD operations
 * and custom query methods for working with {@link Customer} data in the database.
 * This is Annotated with {@link Repository} to indicate that it is a Spring-managed
 * component and to enable exception translation for database access errors.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /**
     * This checks if a {@link Customer} exists with the given customer ID.
     *
     * @param customerId the unique identifier of the customer to check.
     * @return {@code true} if a customer exists with the specified customer ID, otherwise {@code false}.
     */
    boolean existsByCustomerId(String customerId);

    /**
     * This finds a {@link Customer} by their customer ID and password.
     *
     * @param customerId the unique identifier of the customer.
     * @param password the password associated with the customer.
     * @return the {@link Customer} with the specified customer ID and password,
     *         or {@code null} if no such customer exists.
     */
    Customer findByCustomerIdAndPassword(String customerId, String password);
}

