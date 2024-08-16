package com.example.citygame.repository;

import com.example.citygame.entity.UsedCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UsedCityRepository extends JpaRepository<UsedCity, Long> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM UsedCity c WHERE c.name = :name")
    Boolean existByName(String name);
    @Modifying
    @Query("DELETE FROM UsedCity c")
    void clear();
}
