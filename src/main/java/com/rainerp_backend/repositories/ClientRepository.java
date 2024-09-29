package com.rainerp_backend.repositories;

import com.rainerp_backend.models.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c WHERE LOWER(c.name) LIKE %:searchTerm% OR LOWER(c.email) LIKE %:searchTerm% OR c.phone LIKE %:searchTerm%")
    Page<Client> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.address.id = :addressId")
    long countByAddressId(@Param("addressId") Long addressId);

}
