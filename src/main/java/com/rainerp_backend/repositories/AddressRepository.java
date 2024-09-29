package com.rainerp_backend.repositories;

import com.rainerp_backend.models.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a WHERE LOWER(a.street) LIKE %:searchTerm% OR LOWER(a.city) LIKE %:searchTerm% OR LOWER(a.state) LIKE %:searchTerm% OR LOWER(a.country) LIKE %:searchTerm%")
    Page<Address> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

}
