"""
Diabetes Prediction Model - Advanced ML Pipeline
Includes data preprocessing, multiple algorithms, comprehensive metrics, and model interpretability.
Features: data cleaning, outlier detection, multiple models, ensemble methods, and confidence calibration.
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split, cross_val_score, GridSearchCV
from sklearn.preprocessing import StandardScaler, RobustScaler
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier, VotingClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (accuracy_score, precision_score, recall_score, f1_score, 
                             roc_auc_score, confusion_matrix, roc_curve, auc,
                             classification_report, matthews_corrcoef)
from sklearn.calibration import CalibratedClassifierCV
import pickle
import json
import warnings
warnings.filterwarnings('ignore')

class DiabetesPredictionModel:
    """Advanced Machine Learning model for diabetes prediction with preprocessing and ensemble methods"""
    
    def __init__(self):
        self.model = None
        self.best_model = None
        self.models_comparison = {}
        self.scaler = RobustScaler()  # Better for outliers than StandardScaler
        self.feature_names = ['Pregnancies', 'Glucose', 'BloodPressure', 'SkinThickness', 
                              'Insulin', 'BMI', 'DiabetesPedigreeFunction', 'Age']
        self.feature_importance = {}
        self.model_path = 'models/diabetes_model.pkl'
        self.scaler_path = 'models/scaler.pkl'
        self.metrics_history = []
        self.confusion_matrix_data = None
        self.roc_curve_data = None
    
    def load_data(self, csv_path):
        """Load diabetes dataset from CSV"""
        try:
            data = pd.read_csv(csv_path)
            print(f"✓ Data loaded successfully. Shape: {data.shape}")
            print(f"  Columns: {list(data.columns)}")
            return data
        except Exception as e:
            print(f"✗ Error loading data: {str(e)}")
            return None
    
    def preprocess_data(self, data):
        """Advanced data preprocessing pipeline"""
        try:
            print("\n=== DATA PREPROCESSING ===")
            data_clean = data.copy()
            
            # 1. Detect and handle invalid zero values
            print("\n[1] Handling Invalid Zero Values...")
            zero_columns = ['Glucose', 'BloodPressure', 'SkinThickness', 'Insulin', 'BMI']
            for col in zero_columns:
                zero_count = (data_clean[col] == 0).sum()
                if zero_count > 0:
                    # Replace with median (more robust than mean for medical data)
                    median_val = data_clean[col][data_clean[col] > 0].median()
                    data_clean.loc[data_clean[col] == 0, col] = median_val
                    print(f"  - {col}: Replaced {zero_count} zeros with median ({median_val:.2f})")
            
            # 2. Handle missing values
            print("\n[2] Handling Missing Values...")
            missing_before = data_clean.isnull().sum().sum()
            data_clean = data_clean.fillna(data_clean.mean())
            missing_after = data_clean.isnull().sum().sum()
            print(f"  - Missing values: {missing_before} → {missing_after}")
            
            # 3. Remove duplicate records
            print("\n[3] Removing Duplicates...")
            duplicates = data_clean.duplicated().sum()
            data_clean = data_clean.drop_duplicates()
            print(f"  - Removed {duplicates} duplicate records")
            print(f"  - Dataset shape: {data_clean.shape}")
            
            # 4. Detect and handle outliers using IQR method
            print("\n[4] Detecting & Handling Outliers (IQR Method)...")
            outliers_removed = 0
            for col in self.feature_names:
                Q1 = data_clean[col].quantile(0.25)
                Q3 = data_clean[col].quantile(0.75)
                IQR = Q3 - Q1
                lower_bound = Q1 - 3 * IQR  # 3 * IQR for extreme outliers
                upper_bound = Q3 + 3 * IQR
                
                outliers = ((data_clean[col] < lower_bound) | (data_clean[col] > upper_bound)).sum()
                if outliers > 0:
                    data_clean = data_clean[(data_clean[col] >= lower_bound) & (data_clean[col] <= upper_bound)]
                    outliers_removed += outliers
                    print(f"  - {col}: {outliers} outliers removed (bounds: {lower_bound:.2f} - {upper_bound:.2f})")
            
            print(f"  - Total outliers removed: {outliers_removed}")
            print(f"  - Final dataset shape: {data_clean.shape}")
            
            # 5. Data statistics
            print("\n[5] Data Statistics:")
            print(data_clean.describe())
            
            return data_clean
        
        except Exception as e:
            print(f"✗ Error in preprocessing: {str(e)}")
            return None
    
    def prepare_data(self, data):
        """Prepare data for training"""
        try:
            print("\n=== DATA PREPARATION ===")
            X = data[self.feature_names]
            y = data['Outcome']
            
            print(f"✓ Features shape: {X.shape}")
            print(f"✓ Target shape: {y.shape}")
            print(f"  Class distribution: {y.value_counts().to_dict()}")
            print(f"  Positive class ratio: {y.mean():.2%}")
            
            return X, y
        except Exception as e:
            print(f"✗ Error preparing data: {str(e)}")
            return None, None
    
    def train_multiple_models(self, X, y, test_size=0.2, random_state=42):
        """Train and compare multiple models"""
        try:
            print("\n=== DATA SPLITTING ===")
            X_train, X_test, y_train, y_test = train_test_split(
                X, y, test_size=test_size, random_state=random_state, stratify=y
            )
            
            print(f"✓ Training set: {X_train.shape}")
            print(f"✓ Test set: {X_test.shape}")
            
            # Scale features
            print("\n=== FEATURE SCALING ===")
            X_train_scaled = self.scaler.fit_transform(X_train)
            X_test_scaled = self.scaler.transform(X_test)
            print("✓ Features scaled using RobustScaler")
            
            # Train individual models
            print("\n=== TRAINING INDIVIDUAL MODELS ===\n")
            
            # 1. Decision Tree
            print("[1] Decision Tree Classifier...")
            dt_model = DecisionTreeClassifier(
                criterion='gini',
                max_depth=8,
                min_samples_split=10,
                min_samples_leaf=4,
                random_state=random_state
            )
            dt_model.fit(X_train_scaled, y_train)
            dt_metrics = self._evaluate_model(dt_model, X_test_scaled, y_test, "Decision Tree")
            self.models_comparison['DecisionTree'] = dt_metrics
            
            # 2. Logistic Regression
            print("\n[2] Logistic Regression...")
            lr_model = LogisticRegression(
                max_iter=1000,
                random_state=random_state,
                solver='lbfgs',
                n_jobs=-1
            )
            lr_model.fit(X_train_scaled, y_train)
            lr_metrics = self._evaluate_model(lr_model, X_test_scaled, y_test, "Logistic Regression")
            self.models_comparison['LogisticRegression'] = lr_metrics
            
            # 3. Random Forest (with tuned hyperparameters)
            print("\n[3] Random Forest Classifier (Tuned)...")
            rf_model = RandomForestClassifier(
                n_estimators=200,  # Increased from 100
                max_depth=12,      # Increased from 10
                min_samples_split=8,
                min_samples_leaf=3,
                random_state=random_state,
                n_jobs=-1,
                class_weight='balanced'
            )
            rf_model.fit(X_train_scaled, y_train)
            rf_metrics = self._evaluate_model(rf_model, X_test_scaled, y_test, "Random Forest")
            self.models_comparison['RandomForest'] = rf_metrics
            
            # 4. Gradient Boosting
            print("\n[4] Gradient Boosting Classifier...")
            gb_model = GradientBoostingClassifier(
                n_estimators=100,
                learning_rate=0.1,
                max_depth=5,
                min_samples_split=8,
                min_samples_leaf=4,
                random_state=random_state,
                subsample=0.8
            )
            gb_model.fit(X_train_scaled, y_train)
            gb_metrics = self._evaluate_model(gb_model, X_test_scaled, y_test, "Gradient Boosting")
            self.models_comparison['GradientBoosting'] = gb_metrics
            
            # 5. Advanced Ensemble (Stacking Classifier)
            print("\n[5] Stacking Ensemble (Final Model)...")
            from sklearn.ensemble import StackingClassifier
            from sklearn.svm import SVC
            
            estimators = [
                ('rf', rf_model),
                ('gb', gb_model),
                ('dt', dt_model)
            ]
            
            stacking_model = StackingClassifier(
                estimators=estimators,
                final_estimator=LogisticRegression(),
                cv=5,
                n_jobs=-1
            )
            stacking_model.fit(X_train_scaled, y_train)
            stacking_metrics = self._evaluate_model(stacking_model, X_test_scaled, y_test, "Stacking Ensemble")
            self.models_comparison['StackingEnsemble'] = stacking_metrics
            
            # Select best model
            print("\n=== MODEL COMPARISON ===")
            print("\nF1-Score Rankings:")
            for i, (model_name, metrics) in enumerate(sorted(self.models_comparison.items(), 
                                                            key=lambda x: x.get('f1', 0), reverse=True), 1):
                print(f"  {i}. {model_name:20s} - F1: {metrics.get('f1', 0):.4f}, AUC-ROC: {metrics.get('roc_auc', 0):.4f}")
            
            # Use stacking as best model with calibration
            self.best_model = stacking_model
            self.model = CalibratedClassifierCV(stacking_model, method='sigmoid', cv=5)
            self.model.fit(X_train_scaled, y_train)
            
            # Get calibrated metrics
            print("\n=== STACKING ENSEMBLE WITH PROBABILITY CALIBRATION ===")
            ensemble_metrics = self._evaluate_model(self.model, X_test_scaled, y_test, "Calibrated Stacking")
            
            # Store confusion matrix and ROC curve data
            y_pred = self.model.predict(X_test_scaled)
            y_proba = self.model.predict_proba(X_test_scaled)[:, 1]
            self.confusion_matrix_data = confusion_matrix(y_test, y_pred).tolist()
            fpr, tpr, _ = roc_curve(y_test, y_proba)
            self.roc_curve_data = {'fpr': fpr.tolist(), 'tpr': tpr.tolist()}
            
            # Store feature importance (from Random Forest)
            self.feature_importance = dict(zip(self.feature_names, rf_model.feature_importances_.tolist()))
            
            self.metrics_history.append({
                'model': 'Calibrated Ensemble',
                'metrics': ensemble_metrics
            })
            
            return True, ensemble_metrics
        
        except Exception as e:
            print(f"✗ Error training models: {str(e)}")
            import traceback
            traceback.print_exc()
            return False, None
    
    def _evaluate_model(self, model, X_test, y_test, model_name):
        """Evaluate model comprehensively"""
        try:
            y_pred = model.predict(X_test)
            y_proba = model.predict_proba(X_test)[:, 1] if hasattr(model, 'predict_proba') else y_pred
            
            metrics = {
                'accuracy': accuracy_score(y_test, y_pred),
                'precision': precision_score(y_test, y_pred, zero_division=0),
                'recall': recall_score(y_test, y_pred, zero_division=0),
                'f1': f1_score(y_test, y_pred, zero_division=0),
                'roc_auc': roc_auc_score(y_test, y_proba) if hasattr(model, 'predict_proba') else 0,
                'mcc': matthews_corrcoef(y_test, y_pred),
                'confusion_matrix': confusion_matrix(y_test, y_pred).tolist()
            }
            
            print(f"  Accuracy:  {metrics['accuracy']:.4f}")
            print(f"  Precision: {metrics['precision']:.4f}")
            print(f"  Recall:    {metrics['recall']:.4f}")
            print(f"  F1 Score:  {metrics['f1']:.4f}")
            print(f"  ROC-AUC:   {metrics['roc_auc']:.4f}")
            print(f"  MCC:       {metrics['mcc']:.4f}")
            print(f"  CM:        {metrics['confusion_matrix']}")
            
            return metrics
        
        except Exception as e:
            print(f"  ✗ Error evaluating {model_name}: {str(e)}")
            return {}
    
    def predict(self, input_data):
        """Make prediction with enhanced confidence calibration and risk levels"""
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
            
            # Calculate confidence level (higher of the two probabilities)
            confidence = max(probability[0], probability[1])
            
            # Determine risk level based on probability of diabetes
            prob_diabetes = probability[1]
            if prob_diabetes < 0.3:
                risk_level = "LOW"
            elif prob_diabetes < 0.6:
                risk_level = "MEDIUM"
            elif prob_diabetes < 0.8:
                risk_level = "HIGH"
            else:
                risk_level = "CRITICAL"
            
            # Apply confidence thresholding
            confidence_threshold = 0.65
            if confidence < confidence_threshold:
                confidence_text = "BORDERLINE - Recommend further clinical evaluation"
            else:
                confidence_text = "CONFIDENT" if confidence > 0.75 else "MODERATE"
            
            return {
                'prediction': int(prediction),
                'probability_no_diabetes': float(probability[0]),
                'probability_diabetes': float(probability[1]),
                'message': 'Diabetes Positive' if prediction == 1 else 'Diabetes Negative',
                'confidence': float(confidence),
                'confidence_text': confidence_text,
                'risk_level': risk_level,
                'feature_importance': self.feature_importance,
                'model_version': 'Calibrated Ensemble v2.0'
            }
        except Exception as e:
            print(f"Error making prediction: {str(e)}")
            return {'error': str(e)}
    
    def predict_batch(self, input_data_list):
        """Make predictions on multiple records"""
        try:
            predictions = []
            for input_data in input_data_list:
                pred = self.predict(input_data)
                predictions.append(pred)
            return predictions
        except Exception as e:
            print(f"Error in batch prediction: {str(e)}")
            return {'error': str(e)}
    
    def save_model(self, model_dir='models'):
        """Save trained model, scaler, and metrics"""
        try:
            import os
            os.makedirs(model_dir, exist_ok=True)
            
            model_path = f"{model_dir}/diabetes_model.pkl"
            scaler_path = f"{model_dir}/scaler.pkl"
            metrics_path = f"{model_dir}/metrics.json"
            importance_path = f"{model_dir}/feature_importance.json"
            
            with open(model_path, 'wb') as f:
                pickle.dump(self.model, f)
            
            with open(scaler_path, 'wb') as f:
                pickle.dump(self.scaler, f)
            
            # Save metrics
            metrics_data = {
                'models_comparison': self.models_comparison,
                'confusion_matrix': self.confusion_matrix_data,
                'roc_curve': self.roc_curve_data,
                'metrics_history': self.metrics_history
            }
            with open(metrics_path, 'w') as f:
                json.dump(metrics_data, f, indent=2)
            
            # Save feature importance
            with open(importance_path, 'w') as f:
                json.dump(self.feature_importance, f, indent=2)
            
            print(f"✓ Model saved to {model_path}")
            print(f"✓ Scaler saved to {scaler_path}")
            print(f"✓ Metrics saved to {metrics_path}")
            print(f"✓ Feature importance saved to {importance_path}")
            return True
        except Exception as e:
            print(f"✗ Error saving model: {str(e)}")
            return False
    
    def load_model(self, model_dir='models'):
        """Load pre-trained model, scaler, and metrics"""
        try:
            model_path = f"{model_dir}/diabetes_model.pkl"
            scaler_path = f"{model_dir}/scaler.pkl"
            metrics_path = f"{model_dir}/metrics.json"
            importance_path = f"{model_dir}/feature_importance.json"
            
            with open(model_path, 'rb') as f:
                self.model = pickle.load(f)
            
            with open(scaler_path, 'rb') as f:
                self.scaler = pickle.load(f)
            
            # Load metrics if available
            try:
                with open(metrics_path, 'r') as f:
                    metrics_data = json.load(f)
                    self.models_comparison = metrics_data.get('models_comparison', {})
                    self.confusion_matrix_data = metrics_data.get('confusion_matrix')
                    self.roc_curve_data = metrics_data.get('roc_curve')
                    self.metrics_history = metrics_data.get('metrics_history', [])
            except:
                pass
            
            # Load feature importance if available
            try:
                with open(importance_path, 'r') as f:
                    self.feature_importance = json.load(f)
            except:
                pass
            
            print(f"✓ Model loaded from {model_path}")
            return True
        except Exception as e:
            print(f"✗ Error loading model: {str(e)}")
            return False
    
    def get_model_info(self):
        """Get comprehensive model information"""
        return {
            'model_version': 'Calibrated Ensemble v2.0',
            'algorithms': ['Decision Tree', 'Logistic Regression', 'Random Forest', 'Gradient Boosting', 'Voting Ensemble'],
            'features': self.feature_names,
            'feature_importance': self.feature_importance,
            'models_comparison': self.models_comparison,
            'confusion_matrix': self.confusion_matrix_data,
            'roc_curve': self.roc_curve_data,
            'preprocessing': {
                'scaler': 'RobustScaler',
                'zero_handling': 'Median replacement',
                'outlier_detection': 'IQR method (3x)',
                'normalization': 'Per-feature based on training data'
            }
        }
    
    def export_metrics_report(self, output_path='models/model_report.json'):
        """Export comprehensive metrics report"""
        try:
            report = {
                'model_info': self.get_model_info(),
                'metrics_history': self.metrics_history,
                'comprehensive_metrics': {
                    'models_comparison': self.models_comparison,
                    'feature_importance': self.feature_importance,
                    'confusion_matrix': self.confusion_matrix_data,
                    'roc_curve_data': self.roc_curve_data
                }
            }
            
            with open(output_path, 'w') as f:
                json.dump(report, f, indent=2)
            
            print(f"✓ Metrics report exported to {output_path}")
            return True
        except Exception as e:
            print(f"✗ Error exporting report: {str(e)}")
            return False


def main():
    """Main function demonstrating the advanced ML pipeline"""
    print("=" * 80)
    print("DIABETES PREDICTION ML PIPELINE v2.0 - ADVANCED")
    print("=" * 80)
    
    # Initialize model
    model = DiabetesPredictionModel()
    
    # Load data
    data = model.load_data('../../data/diabetes.csv')
    if data is None:
        return
    
    print(f"\nOriginal data shape: {data.shape}")
    
    # Preprocess data
    data_clean = model.preprocess_data(data)
    if data_clean is None:
        return
    
    print(f"Cleaned data shape: {data_clean.shape}")
    
    # Prepare data
    X, y = model.prepare_data(data_clean)
    if X is None:
        return
    
    # Train multiple models with enhanced evaluation
    success, metrics = model.train_multiple_models(X, y)
    if not success:
        return
    
    # Save model with all artifacts
    model.save_model()
    
    # Export comprehensive metrics report
    model.export_metrics_report()
    
    # Display model info
    print("\n=== MODEL INFORMATION ===")
    model_info = model.get_model_info()
    print(json.dumps(model_info, indent=2))
    
    # Test predictions with various scenarios
    print("\n=== TEST PREDICTIONS ===\n")
    
    test_cases = [
        {
            'name': 'High Risk Case',
            'data': {
                'Pregnancies': 6,
                'Glucose': 148,
                'BloodPressure': 72,
                'SkinThickness': 35,
                'Insulin': 125,
                'BMI': 33.6,
                'DiabetesPedigreeFunction': 0.627,
                'Age': 50
            }
        },
        {
            'name': 'Low Risk Case',
            'data': {
                'Pregnancies': 1,
                'Glucose': 90,
                'BloodPressure': 70,
                'SkinThickness': 27,
                'Insulin': 50,
                'BMI': 24.0,
                'DiabetesPedigreeFunction': 0.25,
                'Age': 25
            }
        },
        {
            'name': 'Borderline Case',
            'data': {
                'Pregnancies': 3,
                'Glucose': 110,
                'BloodPressure': 82,
                'SkinThickness': 30,
                'Insulin': 100,
                'BMI': 28.5,
                'DiabetesPedigreeFunction': 0.4,
                'Age': 38
            }
        }
    ]
    
    for test_case in test_cases:
        print(f"\n{test_case['name']}:")
        print(f"  Input: {test_case['data']}")
        result = model.predict(test_case['data'])
        print(f"  Prediction: {result['prediction']}")
        print(f"  Message: {result['message']}")
        print(f"  Risk Level: {result['risk_level']}")
        print(f"  Confidence: {result['confidence']:.2%}")
        print(f"  Confidence Text: {result['confidence_text']}")
        print(f"  Probability (No Diabetes): {result['probability_no_diabetes']:.4f}")
        print(f"  Probability (Diabetes): {result['probability_diabetes']:.4f}")
    
    print("\n" + "=" * 80)
    print("Pipeline execution completed successfully!")
    print("=" * 80)


if __name__ == '__main__':
    main()
