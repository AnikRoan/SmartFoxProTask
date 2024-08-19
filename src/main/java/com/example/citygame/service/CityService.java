package com.example.citygame.service;

import com.example.citygame.entity.City;
import com.example.citygame.entity.UsedCity;
import com.example.citygame.exception.WrongLetterException;
import com.example.citygame.repository.CityRepository;
import com.example.citygame.repository.UsedCityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor

public class CityService {
    private final CityRepository cityRepository;
    private final UsedCityRepository usedCityRepository;
    private static char lastLetterInGame;
    private static char lastLetterInWord;
    private static char firstLetterInWord;

    public City getRundomCity() {
        long allUsedCitiesIds = usedCityRepository.count();
        if (allUsedCitiesIds > 0) {
            usedCityRepository.clear();
            lastLetterInGame = 0;
        }

        long allCitiesIds = cityRepository.count();
        //or I can get all ids to list and then get list size and choose random id
        //because if id in DB will be changed and will be start not from 1 this approach will not work
        Random random = new Random();
        long randomCityId = random.nextLong(1, allCitiesIds);
        City city = cityRepository.findById(randomCityId).orElseThrow(()
                -> new IllegalArgumentException("Місто не знайдено спробуйте ще"));
        lastLetterInGame = cityRepository.getLastLetter(city.getId());
        return city;
    }

    public Optional<City> getCityByLetter(String cityName) {
        cityName = cityName.trim();
        checkWordIsWord(cityName);
        isUserCitiRequestIsRealCity(cityName);

        Optional<City> city = findCityProgramResponse(cityName);
        if (city.isEmpty()) {
            return Optional.ofNullable(City.builder()
                    .name("Міста на букву: " + lastLetterInWord + "скінчились. натисніть кнопку Почати гру").build());
        }

        addUsedCityToUsedCityDBList(city);

        lastLetterInGame = cityRepository.getLastLetter(city.get().getId());
        return city;
    }

    private boolean isCitiUsed(City city) {
        return usedCityRepository.existByName(city.getName());
    }

    private Optional<City> findCityProgramResponse(String cityName) {
        firstLetterInWord = Character.toLowerCase(cityName.charAt(0));

        if (lastLetterInGame == 0) {
            throw new IllegalArgumentException("Натисніть кнопку Почати гру");
        }
        if (firstLetterInWord == lastLetterInGame) {
            lastLetterInWord = checkLastLetter(cityName);

            Optional<List<City>> cityList = cityRepository.findByFirstLetter(lastLetterInWord);
            if (cityList.isPresent()) {
                Iterator<City> iterator = cityList.get().iterator();
                while (iterator.hasNext()) {
                    City city = iterator.next();
                    if (isCitiUsed(city)) {
                        iterator.remove();
                    } else {
                        return Optional.of(city);
                    }

                }

            }
        } else {
            throw new WrongLetterException("Перша літера не співпадає з останньою ");
        }

        return Optional.empty();
    }

    private char checkLastLetter(String cityName) {
        lastLetterInWord = Character.toUpperCase(cityName.charAt(cityName.length() - 1));
        if (lastLetterInWord == 'Ь') {
            lastLetterInWord = Character.toUpperCase(cityName.charAt(cityName.length() - 2));
        }
        return lastLetterInWord;

    }

    private void checkWordIsWord(String cityName) {
        if (isNumeric(cityName) || cityName.length() < 2) {
            throw new IllegalArgumentException("Введіть назву міста");
        }
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void isUserCitiRequestIsRealCity(String cityName) {
        Optional<City> city = cityRepository.findByName(cityName);
        if (!addUsedCityToUsedCityDBList(city)) {
            throw new IllegalArgumentException(
                    "немає такого міста в Україні. спробуйте ще або натисніть кнопку Почати гру");

        }

    }

    private boolean addUsedCityToUsedCityDBList(Optional<City> city) {
        if (city.isPresent()) {
            UsedCity usedCity = UsedCity.builder()
                    .name(city.get().getName())
                    .build();
            usedCityRepository.save(usedCity);
            return true;
        }
        return false;
    }

    @Transactional
    public String endGame() {
        long allUsedCitiesIds = usedCityRepository.count();
        if(allUsedCitiesIds == 0) {
            return "Натисніть кнопку Почати гру";
        }

        usedCityRepository.clear();
        lastLetterInGame = 0;

        return "Спасибі за гру";
    }

}
