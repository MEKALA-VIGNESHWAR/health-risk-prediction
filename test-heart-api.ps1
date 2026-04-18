# Test Heart Disease Prediction API

$baseUrl = "http://localhost:8080"

# Step 1: Register test user
Write-Host "=== Step 1: Registering test user ===" -ForegroundColor Cyan

$registerPayload = @{
    username = "testuser_$(Get-Random)"
    email = "test$(Get-Random)@example.com"
    password = "TestPass123!"
    confirmPassword = "TestPass123!"
    firstName = "Test"
    lastName = "User"
} | ConvertTo-Json

$registerResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/register" `
    -Method Post `
    -ContentType "application/json" `
    -Body $registerPayload `
    -ErrorAction SilentlyContinue

$username = ($registerPayload | ConvertFrom-Json).username
Write-Host "✅ User registered: $username" -ForegroundColor Green

# Step 2: Login to get JWT token
Write-Host "`n=== Step 2: Logging in ===" -ForegroundColor Cyan

$loginPayload = @{
    username = $username
    password = "TestPass123!"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" `
    -Method Post `
    -ContentType "application/json" `
    -Body $loginPayload `
    -ErrorAction SilentlyContinue

if ($loginResponse.token) {
    $jwtToken = $loginResponse.token
    Write-Host "✅ Login successful! JWT Token received" -ForegroundColor Green
} else {
    Write-Host "❌ Login failed" -ForegroundColor Red
    exit 1
}

# Step 3: Test Heart Disease Prediction
Write-Host "`n=== Step 3: Testing Heart Disease Prediction ===" -ForegroundColor Cyan

$heartPayload = @{
    age = 45
    sex = 1
    cp = 3
    trestbps = 130.0
    chol = 250.0
    fbs = 0
    restecg = 1
    thalch = 150.0
    exang = 1
    oldpeak = 2.5
} | ConvertTo-Json

Write-Host "Request Payload:`n$heartPayload" -ForegroundColor Yellow

$headers = @{
    "Authorization" = "Bearer $jwtToken"
    "Content-Type" = "application/json"
}

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/predict/heart" `
        -Method Post `
        -Headers $headers `
        -Body $heartPayload `
        -ErrorAction Stop

    Write-Host "`n✅ Prediction Response:" -ForegroundColor Green
    Write-Host "Prediction Result: $($response.result)" -ForegroundColor Green
    Write-Host "Risk Level: $($response.riskLevel)" -ForegroundColor Green
    Write-Host "Disease Probability: $($response.probabilities.disease | % {"{0:P1}" -f $_})" -ForegroundColor Green
    Write-Host "Confidence: $($response.confidence | % {"{0:P1}" -f $_})" -ForegroundColor Green
    Write-Host "Message: $($response.message)" -ForegroundColor Green
} catch {
    Write-Host "`n❌ Prediction failed:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
}

Write-Host "`n✅ === Test Complete ===" -ForegroundColor Cyan
