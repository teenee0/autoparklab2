package com.example.autoparklab2.repository;

import com.example.autoparklab2.Models.Cars;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarsRepository extends JpaRepository<Cars, Long> {

    @Query("SELECT c FROM Cars c WHERE " +
            "(:brand IS NULL OR c.brand LIKE %:brand%) AND " +
            "(:manufacture_year IS NULL OR c.manufacture_year = :manufacture_year) AND " +
            "(:registration_date IS NULL OR c.registration_date = :registration_date) AND " +
            "(:full_name IS NULL OR c.full_name LIKE %:full_name%)")
    List<Cars> findByParams(@Param("brand") String brand,
                            @Param("manufacture_year") Integer manufacture_year,
                            @Param("registration_date") LocalDate registration_date,
                            @Param("full_name") String full_name,
                            Sort sort);
    @Query("SELECT c.registration_date, COUNT(c) FROM Cars c GROUP BY c.registration_date")
    List<Object[]> findCarIssueStats();
}

