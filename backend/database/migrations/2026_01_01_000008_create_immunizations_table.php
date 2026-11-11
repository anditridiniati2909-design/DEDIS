<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('immunizations', function (Blueprint $table) {
            $table->id();
            $table->foreignId('child_id')->constrained('children')->cascadeOnDelete();
            $table->string('vaccine_name');
            $table->date('scheduled_date');
            $table->date('given_date')->nullable();
            $table->string('given_by')->nullable();
            $table->boolean('has_vitamin_a')->default(false); // Penanda kapsul vitamin A (Februari / Agustus)
            $table->string('notes')->nullable();
            $table->timestamps();

            $table->index(['child_id', 'given_date']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('immunizations');
    }
};

