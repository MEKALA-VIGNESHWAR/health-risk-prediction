"""
Diabetes Prediction Model
This module provides diabetes prediction using machine learning
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score
import pickle
import json

class DiabetesPredictionModel:
    """Machine Learning model for diabetes prediction"""
    
    def __init__(self):
        self.model = None
        self.scaler = StandardScaler()
        self.feature_names = ['Pregnancies', 'Glucose', 'BloodPressure', 'SkinThickness', 
                              'Insulin', 'BMI', 'DiabetesPedigreeFunction', 'Age']
        self.model_path = 'models/diabetes_model.pkl'
        self.scaler_path = 'models/scaler.pkl'
    
    def load_data(self, csv_path):
        """Load diabetes dataset from CSV"""
        try:
            data = pd.read_csv(csv_path)
            print(f"Data loaded successfully. Shape: {data.shape}")
            return data
        except Exception as e:
            print(f"Error loading data: {str(e)}")
            return None
    
    def prepare_data(self, data):
        """Prepare data for training"""
        try:
            X = data[self.feature_names]
            y = data['Outcome']
            
            # Handle missing values
            X = X.fillna(X.mean())
            
            print(f"Features shape: {X.shape}")
            print(f"Target shape: {y.shape}")
            
            return X, y
        except Exception as e:
            print(f"Error preparing data: {str(e)}")
            return None, None
    
    def train(self, X, y, test_size=0.2, random_state=42):
        """Train the diabetes prediction model"""
        try:
            # Split data
            X_train, X_test, y_train, y_test = train_test_split(
                X, y, test_size=test_size, random_state=random_state
            )
            
            # Scale features
            X_train_scaled = self.scaler.fit_transform(X_train)
            X_test_scaled = self.scaler.transform(X_test)
            
            # Train model
            self.model = RandomForestClassifier(
                n_estimators=100, 
                max_depth=10, 
                random_state=random_state,
                n_jobs=-1
            )
            self.model.fit(X_train_scaled, y_train)
            
            # Evaluate
            y_pred = self.model.predict(X_test_scaled)
            
            metrics = {
                'accuracy': accuracy_score(y_test, y_pred),
                'precision': precision_score(y_test, y_pred),
                'recall': recall_score(y_test, y_pred),
                'f1': f1_score(y_test, y_pred)
            }
            
            print("\n=== Model Performance ===")
            print(f"Accuracy:  {metrics['accuracy']:.4f}")
            print(f"Precision: {metrics['precision']:.4f}")
            print(f"Recall:    {metrics['recall']:.4f}")
            print(f"F1 Score:  {metrics['f1']:.4f}")
            
            return True, metrics
        except Exception as e:
            print(f"Error training model: {str(e)}")
            return False, None
    
    def predict(self, input_data):
        """Make prediction on new data"""
        try:
            if self.model is None:
                raise Exception("Model not trained or loaded")
            
            # Ensure input is a DataFrame with correct feature names
            if isinstance(input_data, dict):
                input_df = pd.DataFrame([input_data])
            else:
                input_df = input_data
            
            # Select only required features in correct order
            input_df = input_df[self.feature_names]
            
            # Scale the input
            input_scaled = self.scaler.transform(input_df)
            
            # Make prediction
            prediction = self.model.predict(input_scaled)[0]
            probability = self.model.predict_proba(input_scaled)[0]
            
            return {
                'prediction': int(prediction),
                'probability_no_diabetes': float(probability[0]),
                'probability_diabetes': float(probability[1]),
                'message': 'Diabetes Positive' if prediction == 1 else 'Diabetes Negative'
            }
        except Exception as e:
            print(f"Error making prediction: {str(e)}")
            return {'error': str(e)}
    
    def save_model(self, model_dir='models'):
        """Save trained model and scaler"""
        try:
            import os
            os.makedirs(model_dir, exist_ok=True)
            
            model_path = f"{model_dir}/diabetes_model.pkl"
            scaler_path = f"{model_dir}/scaler.pkl"
            
            with open(model_path, 'wb') as f:
                pickle.dump(self.model, f)
            
            with open(scaler_path, 'wb') as f:
                pickle.dump(self.scaler, f)
            
            print(f"Model saved to {model_path}")
            print(f"Scaler saved to {scaler_path}")
            return True
        except Exception as e:
            print(f"Error saving model: {str(e)}")
            return False
    
    def load_model(self, model_dir='models'):
        """Load pre-trained model and scaler"""
        try:
            model_path = f"{model_dir}/diabetes_model.pkl"
            scaler_path = f"{model_dir}/scaler.pkl"
            
            with open(model_path, 'rb') as f:
                self.model = pickle.load(f)
            
            with open(scaler_path, 'rb') as f:
                self.scaler = pickle.load(f)
            
            print(f"Model loaded from {model_path}")
            return True
        except Exception as e:
            print(f"Error loading model: {str(e)}")
            return False


def main():
    """Main function to demonstrate usage"""
    # Initialize model
    model = DiabetesPredictionModel()
    
    # Load and prepare data
    data = model.load_data('../../data/diabetes.csv')
    if data is None:
        return
    
    X, y = model.prepare_data(data)
    if X is None:
        return
    
    # Train model
    success, metrics = model.train(X, y)
    if not success:
        return
    
    # Save model
    model.save_model()
    
    # Test prediction
    print("\n=== Test Prediction ===")
    test_input = {
        'Pregnancies': 6,
        'Glucose': 148,
        'BloodPressure': 72,
        'SkinThickness': 35,
        'Insulin': 0,
        'BMI': 33.6,
        'DiabetesPedigreeFunction': 0.627,
        'Age': 50
    }
    
    result = model.predict(test_input)
    print(f"Input: {test_input}")
    print(f"Prediction Result: {json.dumps(result, indent=2)}")


if __name__ == '__main__':
    main()
