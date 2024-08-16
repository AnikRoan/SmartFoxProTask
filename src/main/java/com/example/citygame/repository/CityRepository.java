package com.example.citygame.repository;

import com.example.citygame.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    @Query("SELECT c FROM City c WHERE c.firstLetter = :letter")
    Optional<List<City>> findByFirstLetter(char letter);
    @Query("SELECT c.lastLetter FROM City c WHERE c.id = :id")
    char getLastLetter(Long id);
    @Query("SELECT c FROM City c WHERE c.name = :name")
    Optional<City> findByName(String name);
}

