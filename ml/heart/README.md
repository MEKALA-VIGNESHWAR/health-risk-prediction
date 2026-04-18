# Heart Disease Risk Prediction Model

## Overview
Separate ML model for heart disease risk prediction. Does NOT modify the existing diabetes model.

## Model Details
- **Algorithm**: Random Forest Classifier (200 trees)
- **Features**: 13 medical indicators
- **Dataset**: `data/heart_clean.csv`
- **Target**: Binary classification (0=no disease, 1=disease)

## Features Used
1. **age** - Age in years
2. **sex** - Sex (0=Female, 1=Male)
3. **cp** - Chest pain type (0-3)
4. **trestbps** - Resting blood pressure (mm Hg)
5. **chol** - Serum cholesterol (mg/dl)
6. **fbs** - Fasting blood sugar (0=no, 1=yes >120mg/dl)
7. **restecg** - Resting electrocardiographic results (0-2)
8. **thalch** - Maximum heart rate achieved (bpm)
9. **exang** - Exercise induced angina (0=no, 1=yes)
10. **oldpeak** - ST depression induced by exercise (0.0-6.2)
11. **slope** - Slope of ST segment (0-2)
12. **ca** - Number of major vessels (0-4)
13. **thal** - Thalassemia (0-3)

## Training
```python
from heart_prediction import HeartDiseasePredictor

predictor = HeartDiseasePredictor()
result = predictor.train('path/to/heart_clean.csv', 'output_dir')
```

## Prediction
```python
predictor.load('heart_model.pkl', 'heart_scaler.pkl')

result = predictor.predict({
    'age': 63,
    'sex': 1,
    'cp': 3,
    'trestbps': 145,
    'chol': 233,
    'fbs': 1,
    'restecg': 0,
    'thalch': 150,
    'exang': 0,
    'oldpeak': 2.3,
    'slope': 1,
    'ca': 0,
    'thal': 1
})
```

## Output
```json
{
  "prediction": 1,
  "disease_probability": 0.85,
  "no_disease_probability": 0.15,
  "risk_level": "HIGH",
  "confidence": 0.85,
  "message": "Heart disease risk: HIGH (85.0%)",
  "top_factors": [...]
}
```

## Files Generated
- `heart_model.pkl` - Trained model
- `heart_scaler.pkl` - Feature scaler
- `heart_feature_importance.json` - Feature importance weights

## Risk Levels
- **LOW**: Probability < 33%
- **MEDIUM**: Probability 33-67%
- **HIGH**: Probability > 67%
