<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('measurements', function (Blueprint $table) {
            $table->id();
            $table->foreignId('child_id')->constrained('children')->cascadeOnDelete();
            $table->foreignId('recorded_by')->constrained('users');
            $table->date('measurement_date');
            $table->unsignedInteger('age_in_months');
            
            $table->decimal('weight_kg', 5, 2)->nullable();
            $table->decimal('height_cm', 5, 2)->nullable();
            $table->decimal('head_circumference_cm', 5, 2)->nullable();
            
            $table->enum('measurement_method', ['standing', 'recumbent'])->default('standing');
            $table->boolean('correction_applied')->default(false); // Koreksi 0.7cm WHO jika cara ukur tidak sesuai umur

            // Z-score dan klasifikasi status gizi (Permenkes 2/2020)
            $table->decimal('z_score_hfa', 5, 2)->nullable(); // TB/U (Indikator Stunting)
            $table->decimal('z_score_wfa', 5, 2)->nullable(); // BB/U
            $table->decimal('z_score_wfh', 5, 2)->nullable(); // BB/TB
            $table->decimal('z_score_bfa', 5, 2)->nullable(); // IMT/U

            $table->string('status_hfa', 32)->default('belum_bisa_dinilai');
            $table->string('status_wfa', 32)->nullable();
            $table->string('status_wfh', 32)->nullable();
            $table->string('status_bfa', 32)->nullable();

            $table->boolean('needs_attention')->default(false);
            $table->string('attention_reason')->nullable();
            $table->boolean('is_verified_by_midwife')->default(false);
            
            // Pengenal sinkronisasi offline (WorkManager)
            $table->uuid('offline_sync_id')->nullable()->unique();

            $table->timestamps();
            $table->softDeletes();

            $table->index(['child_id', 'measurement_date']);
            $table->index('status_hfa');
            $table->index('needs_attention');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('measurements');
    }
};

