# Diabetes Prediction Model

This module provides a machine learning model for diabetes prediction using Random Forest classifier.

## Features

- Load and preprocess diabetes dataset
- Train Random Forest model
- Make predictions on new patient data
- Save and load trained models
- Generate performance metrics (Accuracy, Precision, Recall, F1-Score)

## Dataset

The model uses the Pima Indians Diabetes Database with 8 medical features:
- Pregnancies
- Glucose
- BloodPressure
- SkinThickness
- Insulin
- BMI
- DiabetesPedigreeFunction
- Age

## Installation

```bash
pip install -r requirements.txt
```

## Usage

### Basic Training

```python
from diabetes_prediction import DiabetesPredictionModel

# Initialize model
model = DiabetesPredictionModel()

# Load data
data = model.load_data('../../data/diabetes.csv')

# Prepare data
X, y = model.prepare_data(data)

# Train model
success, metrics = model.train(X, y)

# Save model
model.save_model()
```

### Making Predictions

```python
# Load pre-trained model
model = DiabetesPredictionModel()
model.load_model()

# Make prediction
test_data = {
    'Pregnancies': 6,
    'Glucose': 148,
    'BloodPressure': 72,
    'SkinThickness': 35,
    'Insulin': 0,
    'BMI': 33.6,
    'DiabetesPedigreeFunction': 0.627,
    'Age': 50
}

result = model.predict(test_data)
print(result)
```

## Output Format

```json
{
    "prediction": 1,
    "probability_no_diabetes": 0.25,
    "probability_diabetes": 0.75,
    "message": "Diabetes Positive"
}
```

## Performance Metrics

The model provides:
- **Accuracy**: Overall correctness of predictions
- **Precision**: False positive prevention
- **Recall**: False negative prevention
- **F1-Score**: Harmonic mean of precision and recall

## Author

Machine Learning Team

## License

MIT
