package com.smarttourism.smart_tourism_backend.controller;

import com.smarttourism.smart_tourism_backend.entity.Hotel;
import com.smarttourism.smart_tourism_backend.service.HotelService;
import com.smarttourism.smart_tourism_backend.service.HotelbedsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin(origins = "*")
public class HotelController {

    private final HotelService hotelService;
    private final HotelbedsService hotelbedsService;

    public HotelController(HotelService hotelService, HotelbedsService hotelbedsService) {
        this.hotelService = hotelService;
        this.hotelbedsService = hotelbedsService;
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/place/{placeId}")
    public List<Hotel> getHotelsByPlaceId(@PathVariable Integer placeId) {
        return hotelService.getHotelsByPlaceId(placeId);
    }

    @GetMapping("/{hotelId}")
    public Hotel getHotelById(@PathVariable Integer hotelId) {
        return hotelService.getHotelById(hotelId);
    }

    @GetMapping("/live")
    public String getLiveHotelRates(
            @RequestParam String destinationCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(defaultValue = "1") int rooms,
            @RequestParam(defaultValue = "2") int adults) {
        return hotelbedsService.getLiveHotelRates(destinationCode, checkIn, checkOut, rooms, adults);
    }
}