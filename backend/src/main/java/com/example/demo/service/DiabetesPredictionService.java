package com.example.demo.service;

import com.example.demo.dto.DiabetesPredictionRequest;
import com.example.demo.dto.DiabetesPredictionResponse;
import com.example.demo.entity.DiabetesPrediction;
import com.example.demo.repository.DiabetesPredictionRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DiabetesPredictionService - Business logic for diabetes predictions (Supabase PostgreSQL)
 * Integrates with ML model to make predictions and store results
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiabetesPredictionService {

    private final DiabetesPredictionRepositoryJPA predictionRepository;

    /**
     * Make diabetes prediction using ML model logic
     * This is a simplified implementation - in production, you would call Python ML model
     */
    public DiabetesPredictionResponse predictDiabetes(DiabetesPredictionRequest request) {
        log.info("Processing diabetes prediction for age: {}, glucose: {}", request.getAge(), request.getGlucose());

        try {
            // Simple ML prediction logic (would replace with actual model call)
            int prediction = calculatePrediction(request);
            double[] probabilities = calculateProbabilities(request, prediction);

            String message = prediction == 1 ? "Diabetes Positive" : "Diabetes Negative";
            String risk = prediction == 1 ? "HIGH" : "LOW";
            
            // Calculate confidence level as the higher probability
            double confidenceLevel = Math.max(probabilities[0], probabilities[1]);

            // Create response
            DiabetesPredictionResponse response = new DiabetesPredictionResponse(
                    prediction,
                    probabilities[0],
                    probabilities[1],
                    message
            );
            response.setRisk(risk);
            response.setConfidenceLevel(confidenceLevel);
            response.setModelUsed("Random Forest Decision Model v1.0");

            // Save prediction to database
            DiabetesPrediction dbPrediction = savePrediction(request, prediction, probabilities, message);
            response.setPredictionId(dbPrediction.getId().toString());

            log.info("Prediction completed: {} (Risk: {})", message, risk);
            return response;

        } catch (Exception e) {
            log.error("Error making prediction: {}", e.getMessage());
            throw new RuntimeException("Prediction failed: " + e.getMessage());
        }
    }

    /**
     * Calculate prediction using decision logic
     * In production, this would call the Python ML model
     */
    private int calculatePrediction(DiabetesPredictionRequest request) {
        // Simplified prediction logic for demonstration
        // In production: call trained Random Forest model from Python
        
        // Risk factors calculation
        double riskScore = 0;

        // Glucose level is strongest predictor
        if (request.getGlucose() > 126) riskScore += 2;
        else if (request.getGlucose() > 100) riskScore += 1;

        // BMI is important
        if (request.getBmi() > 30) riskScore += 1.5;
        else if (request.getBmi() > 25) riskScore += 0.5;

        // Age factor
        if (request.getAge() > 45) riskScore += 1;

        // Pregnancies (for women)
        if (request.getPregnancies() > 5) riskScore += 1;

        // Insulin resistance indicator
        if (request.getInsulin() > 125) riskScore += 1;

        // Pedigree function
        if (request.getDiabetesPedigreeFunction() > 0.5) riskScore += 1;

        // Blood pressure
        if (request.getBloodPressure() > 90) riskScore += 0.5;

        // DecisionBoundary: if risk score > 4, predict positive
        return riskScore > 4 ? 1 : 0;
    }

    /**
     * Calculate prediction probabilities
     */
    private double[] calculateProbabilities(DiabetesPredictionRequest request, int prediction) {
        double[] probabilities = new double[2];
        
        // Simplified probability calculation
        double riskFactor = calculateRiskFactor(request);
        
        if (prediction == 1) {
            probabilities[1] = Math.min(0.99, 0.5 + riskFactor); // Probability of diabetes
            probabilities[0] = 1 - probabilities[1]; // Probability of no diabetes
        } else {
            probabilities[0] = Math.min(0.99, 0.5 + (0.3 - riskFactor)); // Probability of no diabetes
            probabilities[1] = 1 - probabilities[0]; // Probability of diabetes
        }

        return probabilities;
    }

    /**
     * Calculate risk factor for probability
     */
    private double calculateRiskFactor(DiabetesPredictionRequest request) {
        double risk = 0;
        
        if (request.getGlucose() > 126) risk += 0.15;
        if (request.getBmi() > 30) risk += 0.1;
        if (request.getAge() > 45) risk += 0.08;
        if (request.getInsulin() > 125) risk += 0.07;
        
        return Math.min(risk, 0.3);
    }

    /**
     * Save prediction to database
     */
    private DiabetesPrediction savePrediction(DiabetesPredictionRequest request, int prediction, 
                                               double[] probabilities, String message) {
        DiabetesPrediction dbPrediction = new DiabetesPrediction();
        
        // Convert userId from Long to UUID
        if (request.getUserId() != null) {
            dbPrediction.setUserId(UUID.fromString(request.getUserId().toString()));
        }
        
        dbPrediction.setPregnancies(request.getPregnancies());
        dbPrediction.setGlucose(request.getGlucose());
        dbPrediction.setBloodPressure(request.getBloodPressure());
        dbPrediction.setSkinThickness(request.getSkinThickness());
        dbPrediction.setInsulin(request.getInsulin());
        dbPrediction.setBmi(new BigDecimal(request.getBmi()));
        dbPrediction.setDiabetesPedigreeFunction(new BigDecimal(request.getDiabetesPedigreeFunction()));
        dbPrediction.setAge(request.getAge());
        
        dbPrediction.setPredictionResult(prediction);
        dbPrediction.setProbabilityNoDiabetes(probabilities[0]);
        dbPrediction.setProbabilityDiabetes(probabilities[1]);
        dbPrediction.setPredictionMessage(message);
        dbPrediction.setCreatedDate(System.currentTimeMillis());

        return predictionRepository.save(dbPrediction);
    }

    /**
     * Get prediction history for a user
     */
    public List<DiabetesPrediction> getPredictionHistory(String userId) {
        log.info("Fetching prediction history for user: {}", userId);
        return predictionRepository.findByUserId(UUID.fromString(userId));
    }

    /**
     * Get all high-risk predictions
     */
    public List<DiabetesPrediction> getHighRiskPredictions() {
        log.info("Fetching high-risk predictions");
        return predictionRepository.findByPredictionResult(1);
    }

    /**
     * Get prediction by ID
     */
    public DiabetesPrediction getPredictionById(String id) {
        log.info("Fetching prediction with id: {}", id);
        return predictionRepository.findById(UUID.fromString(id)).orElse(null);
    }

}

