package com.financeapp.controller;

import com.financeapp.entity.Insurance;
import com.financeapp.service.InsuranceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/insurances")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class InsuranceController {
    private final InsuranceService insuranceService;

    public InsuranceController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    @GetMapping
    public ResponseEntity<List<Insurance>> getAllInsurances() {
        List<Insurance> insurances = insuranceService.getAllInsurances();
        return ResponseEntity.ok(insurances);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Insurance> getInsuranceById(@PathVariable Long id) {
        return insuranceService.getInsuranceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Insurance> createInsurance(@Valid @RequestBody Insurance insurance) {
        Insurance createdInsurance = insuranceService.createInsurance(insurance);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInsurance);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Insurance> updateInsurance(@PathVariable Long id, @Valid @RequestBody Insurance insurance) {
        try {
            Insurance updatedInsurance = insuranceService.updateInsurance(id, insurance);
            return ResponseEntity.ok(updatedInsurance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsurance(@PathVariable Long id) {
        try {
            insuranceService.deleteInsurance(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportInsurances() {
        List<Insurance> insurances = insuranceService.getAllInsurances();
        StringBuilder csv = new StringBuilder();
        
        // CSV Header
        csv.append("ID,Policy Number,Insurance Type,Provider,Premium,Coverage Amount,Start Date,End Date,Status,Description,Created At,Updated At\n");
        
        // CSV Data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Insurance insurance : insurances) {
            csv.append(insurance.getId()).append(",");
            csv.append(escapeCsv(insurance.getPolicyNumber())).append(",");
            csv.append(escapeCsv(insurance.getInsuranceType())).append(",");
            csv.append(escapeCsv(insurance.getProvider())).append(",");
            csv.append(insurance.getPremium()).append(",");
            csv.append(insurance.getCoverageAmount()).append(",");
            csv.append(insurance.getStartDate() != null ? insurance.getStartDate().format(dateFormatter) : "").append(",");
            csv.append(insurance.getEndDate() != null ? insurance.getEndDate().format(dateFormatter) : "").append(",");
            csv.append(escapeCsv(insurance.getStatus())).append(",");
            csv.append(escapeCsv(insurance.getDescription() != null ? insurance.getDescription() : "")).append(",");
            csv.append(insurance.getCreatedAt() != null ? insurance.getCreatedAt().format(formatter) : "").append(",");
            csv.append(insurance.getUpdatedAt() != null ? insurance.getUpdatedAt().format(formatter) : "").append("\n");
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "insurances_export.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString());
    }
    
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
