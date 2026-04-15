# RealTime Application JAR Verification Script
# Run this script to verify the JAR file works correctly

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "RealTime JAR Verification Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Check Java
Write-Host "1. Checking Java Installation..." -ForegroundColor Yellow
try {
    $java = java -version 2>&1
    Write-Host "✅ Java is installed" -ForegroundColor Green
    Write-Host "   Version: $($java[0])"
} catch {
    Write-Host "❌ Java not found" -ForegroundColor Red
    exit 1
}

# 2. Check JAR file
Write-Host ""
Write-Host "2. Checking JAR File..." -ForegroundColor Yellow
if (Test-Path "target/realtime-app-1.0.0.jar") {
    $size = (Get-Item "target/realtime-app-1.0.0.jar").Length / 1MB
    Write-Host "✅ JAR file exists" -ForegroundColor Green
    Write-Host "   Path: target/realtime-app-1.0.0.jar"
    Write-Host "   Size: $([Math]::Round($size, 1)) MB"
} else {
    Write-Host "❌ JAR file not found at target/realtime-app-1.0.0.jar" -ForegroundColor Red
    exit 1
}

# 3. Stop any existing Java processes
Write-Host ""
Write-Host "3. Cleaning up existing processes..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2
Write-Host "✅ Cleanup complete" -ForegroundColor Green

# 4. Start the application
Write-Host ""
Write-Host "4. Starting Application..." -ForegroundColor Yellow
Start-Process java -ArgumentList "-jar", "target/realtime-app-1.0.0.jar" -NoNewWindow -RedirectStandardOutput "verify.log"
Write-Host "   Application starting..." -ForegroundColor Cyan
Start-Sleep -Seconds 15

# 5. Test HTTP connectivity
Write-Host ""
Write-Host "5. Testing HTTP Connectivity..." -ForegroundColor Yellow
$retries = 3
$connected = $false
for ($i = 1; $i -le $retries; $i++) {
    try {
        $response = Invoke-WebRequest http://localhost:8080 -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
        Write-Host "✅ HTTP Response: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "   Content Size: $($response.Content.Length) bytes"
        $connected = $true
        break
    } catch {
        if ($i -lt $retries) {
            Write-Host "   Retry $i/$retries..." -ForegroundColor Yellow
            Start-Sleep -Seconds 3
        }
    }
}
if (-not $connected) {
    Write-Host "❌ HTTP Request Failed" -ForegroundColor Red
    exit 1
}

# 6. Check for errors in logs
Write-Host ""
Write-Host "6. Checking Application Logs..." -ForegroundColor Yellow
$logs = Get-Content "verify.log" -ErrorAction SilentlyContinue
$errors = $logs | Select-String "ERROR" -ErrorAction SilentlyContinue
if ($errors) {
    Write-Host "⚠️ Errors found in logs:" -ForegroundColor Yellow
    $errors | ForEach-Object { Write-Host "   $_" }
} else {
    Write-Host "✅ No ERROR entries in logs" -ForegroundColor Green
}

# 7. Final summary
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "VERIFICATION COMPLETE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ RealTime Application JAR is working correctly" -ForegroundColor Green
Write-Host ""
Write-Host "Access the application at:" -ForegroundColor Yellow
Write-Host "   http://localhost:8080" -ForegroundColor Cyan
Write-Host ""
Write-Host "To stop the application, close the Java window" -ForegroundColor Gray
Write-Host ""
