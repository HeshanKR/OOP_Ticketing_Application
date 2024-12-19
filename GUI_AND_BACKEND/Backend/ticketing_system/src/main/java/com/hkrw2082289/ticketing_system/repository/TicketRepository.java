//Real-Time Ticketing System Backend by Heshan Ratnaweera, Student ID UOW: W2082289 IIT: 20222094.
package com.hkrw2082289.ticketing_system.repository;
import com.hkrw2082289.ticketing_system.model.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This Repository interface for managing {@link TicketEntity} entities.
 *
 * This interface extends {@link JpaRepository}, providing standard CRUD operations
 * for working with {@link TicketEntity} data in the database.
 *
 * This is Annotated with {@link Repository} to indicate that it is a Spring-managed component
 * and to enable exception translation for database access errors.
 */
@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, String> {
}