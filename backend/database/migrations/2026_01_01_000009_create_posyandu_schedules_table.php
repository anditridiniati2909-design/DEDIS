<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('posyandu_schedules', function (Blueprint $table) {
            $table->id();
            $table->date('event_date');
            $table->time('start_time')->default('08:00:00');
            $table->time('end_time')->default('11:00:00');
            $table->string('location')->default('Posyandu Anggrek 4, Dusun Bontocinde');
            $table->string('title')->default('Pelayanan Posyandu & Imunisasi Bulanan');
            $table->text('description')->nullable();
            $table->foreignId('created_by')->constrained('users');
            $table->timestamps();

            $table->index('event_date');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('posyandu_schedules');
    }
};

