<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AiConsultationController;
use App\Http\Controllers\Api\ChildController;
use App\Http\Controllers\Api\AuthController;

// ==========================================
// 1. AUTHENTICATION (LOGIN, REGISTER, LOGOUT)
// ==========================================
$authPublicRoutes = function () {
    Route::post('/register', [AuthController::class, 'register']);
    Route::post('/login', [AuthController::class, 'login']);
    Route::post('/reset-password', [AuthController::class, 'resetPassword']);
    Route::post('/change-password', [AuthController::class, 'changePassword']);
};

$authProtectedRoutes = function () {
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/me', [AuthController::class, 'me']);
};

Route::prefix('v1')->group($authPublicRoutes);
Route::group([], $authPublicRoutes);

Route::middleware('auth:sanctum')->group(function () use ($authProtectedRoutes) {
    Route::prefix('v1')->group($authProtectedRoutes);
    Route::group([], $authProtectedRoutes);
});

// ==========================================
// 2. FITUR AI KONSULTASI GIZI & STUNTING
// ==========================================
Route::post('/v1/chat', [AiConsultationController::class, 'chat']);
Route::post('/chat', [AiConsultationController::class, 'chat']);

// ==========================================
// 3. FITUR CRUD DATA BALITA & PENGUKURAN
// ==========================================
$childRoutes = function () {
    Route::get('/children', [ChildController::class, 'index']);
    Route::get('/children/{id}', [ChildController::class, 'show']);
    Route::post('/children', [ChildController::class, 'store']);
    Route::put('/children/{id}', [ChildController::class, 'update']);
    Route::post('/children/{id}/measurements', [ChildController::class, 'storeMeasurement']);
    Route::delete('/children/{id}', [ChildController::class, 'destroy']);
};

Route::prefix('v1')->group($childRoutes);
Route::group([], $childRoutes);
