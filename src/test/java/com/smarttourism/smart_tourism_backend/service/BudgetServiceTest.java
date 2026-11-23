package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.entity.BudgetEstimate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BudgetServiceTest {

    @Autowired
    private BudgetService budgetService;

    @Test
    void testBudgetCalculationForPlace1() {
        BudgetEstimate estimate = budgetService.calculateBudget(1, 3, 2);

        assertNotNull(estimate);
        assertEquals(1, estimate.getPlaceId());
        assertEquals("Siddharameshwar Temple", estimate.getDestination());
        assertEquals(3, estimate.getDays());
        assertEquals(2, estimate.getTravelers());

        assertTrue(estimate.getTransportationCost() > 0);
        assertEquals(5000.0, estimate.getAccommodationCost());
        assertEquals(2520.0, estimate.getFoodCost());

        double expectedTotal = estimate.getTransportationCost() + estimate.getAccommodationCost() + estimate.getFoodCost();
        assertEquals(Math.round(expectedTotal * 100.0) / 100.0, estimate.getTotalBudget());
        assertEquals(Math.round((estimate.getTotalBudget() / 2.0) * 100.0) / 100.0, estimate.getPerPerson());

        assertNotNull(estimate.getTransportation());
        assertNotNull(estimate.getAccommodation());
        assertNotNull(estimate.getFood());
        assertEquals("Hotel City Center", estimate.getAccommodation().getHotelName());
        assertEquals(2, estimate.getAccommodation().getNights());
    }

    @Test
    void testBudgetCalculationWithDifferentParameters() {
        BudgetEstimate estimate = budgetService.calculateBudget(2, 4, 3);

        assertNotNull(estimate);
        assertEquals(2, estimate.getPlaceId());
        assertEquals(4, estimate.getDays());
        assertEquals(3, estimate.getTravelers());
        assertEquals(3, estimate.getAccommodation().getNights()); // 4 days - 1 = 3 nights

        double expectedTotal = estimate.getTransportationCost() + estimate.getAccommodationCost() + estimate.getFoodCost();
        assertEquals(Math.round(expectedTotal * 100.0) / 100.0, estimate.getTotalBudget());
        assertEquals(Math.round((estimate.getTotalBudget() / 3.0) * 100.0) / 100.0, estimate.getPerPerson());
    }

    @Test
    void testBudgetCalculationWithSpecificTransport() {
        BudgetEstimate autoEstimate = budgetService.calculateBudget(1, 2, 2, "Auto");
        BudgetEstimate taxiEstimate = budgetService.calculateBudget(1, 2, 2, "Taxi");
        BudgetEstimate busEstimate = budgetService.calculateBudget(1, 2, 2, "Bus");

        assertEquals("Auto", autoEstimate.getTransportation().getType());
        assertEquals("Taxi", taxiEstimate.getTransportation().getType());
        assertEquals("Bus", busEstimate.getTransportation().getType());

        assertTrue(taxiEstimate.getTransportationCost() > autoEstimate.getTransportationCost());
        assertTrue(autoEstimate.getTransportationCost() > busEstimate.getTransportationCost());
    }

    @Test
    void testInvalidPlaceId() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            budgetService.calculateBudget(99999, 3, 2);
        });
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testInvalidDays() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            budgetService.calculateBudget(1, 0, 2);
        });
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testInvalidTravelers() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            budgetService.calculateBudget(1, 3, 0);
        });
        assertEquals(400, ex.getStatusCode().value());
    }
}

