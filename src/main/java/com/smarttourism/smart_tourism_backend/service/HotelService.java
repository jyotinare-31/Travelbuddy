package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.entity.Hotel;
import com.smarttourism.smart_tourism_backend.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> getHotelsByPlaceId(Integer placeId) {
        return hotelRepository.findByPlaceId(placeId);
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel getHotelById(Integer hotelId) {
        return hotelRepository.findById(hotelId)
                .orElse(null);
    }
}