package com.financeapp.service;

import com.financeapp.entity.Insurance;
import com.financeapp.repository.InsuranceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InsuranceService {
    private final InsuranceRepository insuranceRepository;

    public InsuranceService(InsuranceRepository insuranceRepository) {
        this.insuranceRepository = insuranceRepository;
    }

    public List<Insurance> getAllInsurances() {
        return insuranceRepository.findAll();
    }

    public Optional<Insurance> getInsuranceById(Long id) {
        return insuranceRepository.findById(id);
    }

    public Insurance createInsurance(Insurance insurance) {
        return insuranceRepository.save(insurance);
    }

    public Insurance updateInsurance(Long id, Insurance insuranceDetails) {
        Insurance insurance = insuranceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insurance not found with id: " + id));
        
        insurance.setPolicyNumber(insuranceDetails.getPolicyNumber());
        insurance.setInsuranceType(insuranceDetails.getInsuranceType());
        insurance.setProvider(insuranceDetails.getProvider());
        insurance.setPremium(insuranceDetails.getPremium());
        insurance.setCoverageAmount(insuranceDetails.getCoverageAmount());
        insurance.setStartDate(insuranceDetails.getStartDate());
        insurance.setEndDate(insuranceDetails.getEndDate());
        insurance.setStatus(insuranceDetails.getStatus());
        insurance.setDescription(insuranceDetails.getDescription());
        
        return insuranceRepository.save(insurance);
    }

    public void deleteInsurance(Long id) {
        Insurance insurance = insuranceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insurance not found with id: " + id));
        insuranceRepository.delete(insurance);
    }
}
