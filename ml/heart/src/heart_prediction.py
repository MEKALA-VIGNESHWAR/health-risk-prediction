"""
Heart Disease Risk Prediction Model
Separate from diabetes model - uses heart_clean.csv dataset
Features: age, sex, cp, trestbps, chol, fbs, restecg, thalch, exang, oldpeak
Model: Random Forest Classifier
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, roc_auc_score
import pickle
import json
import sys
from pathlib import Path

# Configuration
MODEL_NAME = "heart_model.pkl"
SCALER_NAME = "heart_scaler.pkl"
FEATURE_IMPORTANCE_FILE = "heart_feature_importance.json"

# Heart disease feature names (in order of expected input) - 10 features
FEATURE_NAMES = [
    'age', 'sex', 'cp', 'trestbps', 'chol', 'fbs', 
    'restecg', 'thalch', 'exang', 'oldpeak'
]

class HeartDiseasePredictor:
    """Heart disease prediction model"""
    
    def __init__(self):
        self.model = None
        self.scaler = None
        self.feature_importance = {}
        self.model_accuracy = 0
        
    def train(self, csv_path: str, save_path: str = "."):
        """Train the heart disease model"""
        print(f"[HEART MODEL] Loading data from {csv_path}")
        
        # Load and prepare data
        df = pd.read_csv(csv_path)
        print(f"[HEART MODEL] Dataset shape: {df.shape}")
        print(f"[HEART MODEL] Columns: {list(df.columns)}")
        
        # Handle missing values
        df = df.dropna()
        print(f"[HEART MODEL] Shape after removing NaN: {df.shape}")
        
        # Separate features and target
        X = df[FEATURE_NAMES]
        y = df['target']
        
        print(f"[HEART MODEL] Features shape: {X.shape}")
        print(f"[HEART MODEL] Target distribution: {y.value_counts().to_dict()}")
        
        # Scale features
        self.scaler = StandardScaler()
        X_scaled = self.scaler.fit_transform(X)
        
        # Split data
        X_train, X_test, y_train, y_test = train_test_split(
            X_scaled, y, test_size=0.2, random_state=42, stratify=y
        )
        
        # Train Random Forest
        print("[HEART MODEL] Training Random Forest Classifier...")
        self.model = RandomForestClassifier(
            n_estimators=200,
            max_depth=20,
            min_samples_split=5,
            min_samples_leaf=2,
            random_state=42,
            n_jobs=-1
        )
        self.model.fit(X_train, y_train)
        
        # Evaluate
        y_pred = self.model.predict(X_test)
        y_pred_proba = self.model.predict_proba(X_test)[:, 1]
        
        accuracy = accuracy_score(y_test, y_pred)
        precision = precision_score(y_test, y_pred)
        recall = recall_score(y_test, y_pred)
        f1 = f1_score(y_test, y_pred)
        auc = roc_auc_score(y_test, y_pred_proba)
        
        self.model_accuracy = accuracy
        
        print(f"[HEART MODEL] Model Performance:")
        print(f"  - Accuracy:  {accuracy:.4f}")
        print(f"  - Precision: {precision:.4f}")
        print(f"  - Recall:    {recall:.4f}")
        print(f"  - F1 Score:  {f1:.4f}")
        print(f"  - ROC AUC:   {auc:.4f}")
        
        # Store feature importance
        self.feature_importance = {
            name: float(importance)
            for name, importance in zip(FEATURE_NAMES, self.model.feature_importances_)
        }
        
        # Save model
        model_path = Path(save_path) / MODEL_NAME
        scaler_path = Path(save_path) / SCALER_NAME
        importance_path = Path(save_path) / FEATURE_IMPORTANCE_FILE
        
        with open(model_path, 'wb') as f:
            pickle.dump(self.model, f)
        print(f"[HEART MODEL] Model saved to {model_path}")
        
        with open(scaler_path, 'wb') as f:
            pickle.dump(self.scaler, f)
        print(f"[HEART MODEL] Scaler saved to {scaler_path}")
        
        with open(importance_path, 'w') as f:
            json.dump(self.feature_importance, f, indent=2)
        print(f"[HEART MODEL] Feature importance saved to {importance_path}")
        
        return {
            'status': 'success',
            'accuracy': accuracy,
            'precision': precision,
            'recall': recall,
            'f1': f1,
            'auc': auc,
            'model_path': str(model_path),
            'scaler_path': str(scaler_path),
            'feature_importance': self.feature_importance
        }
    
    def load(self, model_path: str, scaler_path: str):
        """Load pre-trained model"""
        with open(model_path, 'rb') as f:
            self.model = pickle.load(f)
        with open(scaler_path, 'rb') as f:
            self.scaler = pickle.load(f)
        print(f"[HEART MODEL] Model loaded from {model_path}")
        
    def predict(self, data: dict) -> dict:
        """
        Make prediction for a single patient
        
        Input:
        {
            'age': 63,
            'sex': 1,
            'cp': 3,
            'trestbps': 145,
            'chol': 233,
            'fbs': 1,
            'restecg': 0,
            'thalch': 150,
            'exang': 0,
            'oldpeak': 2.3
        }
        """
        try:
            # Extract features in order (10 features)
            features = np.array([[
                data.get('age', 0),
                data.get('sex', 0),
                data.get('cp', 0),
                data.get('trestbps', 0),
                data.get('chol', 0),
                data.get('fbs', 0),
                data.get('restecg', 0),
                data.get('thalch', 0),
                data.get('exang', 0),
                data.get('oldpeak', 0)
            ]])
            
            # Scale features
            features_scaled = self.scaler.transform(features)
            
            # Make prediction
            prediction = self.model.predict(features_scaled)[0]
            probability = self.model.predict_proba(features_scaled)[0]
            
            # Determine risk level
            heart_disease_prob = probability[1]
            if heart_disease_prob < 0.33:
                risk_level = "LOW"
            elif heart_disease_prob < 0.67:
                risk_level = "MEDIUM"
            else:
                risk_level = "HIGH"
            
            # Get feature importance for this prediction
            feature_importance_scores = self._get_feature_importance_for_prediction(
                features[0], self.model.feature_importances_
            )
            
            return {
                'prediction': int(prediction),
                'disease_probability': float(probability[1]),
                'no_disease_probability': float(probability[0]),
                'risk_level': risk_level,
                'confidence': float(max(probability)),
                'message': f"Heart disease risk: {risk_level} ({heart_disease_prob*100:.1f}%)",
                'top_factors': feature_importance_scores
            }
            
        except Exception as e:
            return {
                'error': str(e),
                'message': 'Prediction failed'
            }
    
    def _get_feature_importance_for_prediction(self, features: np.ndarray, importances: np.ndarray) -> list:
        """Get top 5 factors affecting the prediction"""
        factor_scores = []
        for i, name in enumerate(FEATURE_NAMES):
            score = float(importances[i]) * float(features[i])
            factor_scores.append({
                'factor': name,
                'importance': float(importances[i]),
                'value': float(features[i]),
                'score': score
            })
        
        # Sort by importance and return top 5
        return sorted(factor_scores, key=lambda x: x['importance'], reverse=True)[:5]


def main():
    """Main entry point - train the model"""
    import os
    
    # Determine paths
    script_dir = Path(__file__).parent
    data_dir = script_dir.parent.parent.parent / 'data'
    csv_path = data_dir / 'heart_clean.csv'
    
    print(f"[HEART MODEL] Script directory: {script_dir}")
    print(f"[HEART MODEL] Data directory: {data_dir}")
    print(f"[HEART MODEL] CSV path: {csv_path}")
    
    if not csv_path.exists():
        print(f"[ERROR] CSV file not found: {csv_path}")
        return
    
    # Train model
    predictor = HeartDiseasePredictor()
    result = predictor.train(str(csv_path), str(script_dir))
    
    print(f"\n[HEART MODEL] Training complete!")
    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()
