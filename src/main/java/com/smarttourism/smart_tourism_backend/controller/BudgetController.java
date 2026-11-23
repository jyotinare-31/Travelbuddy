package com.smarttourism.smart_tourism_backend.controller;

import com.smarttourism.smart_tourism_backend.entity.BudgetEstimate;
import com.smarttourism.smart_tourism_backend.service.BudgetService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS})
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<?> getBudget(
            @PathVariable Integer placeId,
            @RequestParam(defaultValue = "3") int days,
            @RequestParam(defaultValue = "2") int travelers,
            @RequestParam(required = false) String transportType) {

        try {
            BudgetEstimate estimate = budgetService.calculateBudget(
                    placeId,
                    days,
                    travelers,
                    transportType
            );
            return ResponseEntity.ok(estimate);
        } catch (ResponseStatusException ex) {
            String errorMsg = ex.getReason() != null ? ex.getReason() : (ex.getMessage() != null ? ex.getMessage() : "Request error");
            return ResponseEntity.status(ex.getStatusCode()).body(
                    Map.of("error", errorMsg, "status", ex.getStatusCode().value())
            );
        } catch (Exception ex) {
            String errorMsg = ex.getMessage() != null ? ex.getMessage() : "Unable to calculate budget";
            return ResponseEntity.badRequest().body(
                    Map.of("error", errorMsg, "status", 400)
            );
        }
    }

    @GetMapping
    public ResponseEntity<?> getBudgetByParam(
            @RequestParam(required = false) Integer placeId,
            @RequestParam(defaultValue = "3") int days,
            @RequestParam(defaultValue = "2") int travelers,
            @RequestParam(required = false) String transportType) {

        if (placeId == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Place ID is required (e.g. /api/budget/{placeId} or ?placeId={placeId})", "status", 400)
            );
        }
        return getBudget(placeId, days, travelers, transportType);
    }
}