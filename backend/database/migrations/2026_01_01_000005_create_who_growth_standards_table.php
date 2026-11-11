<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('who_growth_standards', function (Blueprint $table) {
            $table->id();
            $table->enum('indicator', ['HFA', 'WFA', 'WFH', 'BFA']);
            $table->enum('gender', ['L', 'P']);
            // Umur (bulan 0-60) atau Panjang/Tinggi badan (cm 45-120)
            $table->decimal('age_or_length', 5, 2);
            $table->decimal('l_value', 8, 4);
            $table->decimal('m_value', 8, 4);
            $table->decimal('s_value', 8, 4);
            $table->decimal('sd3neg', 8, 4);
            $table->decimal('sd2neg', 8, 4);
            $table->decimal('sd1neg', 8, 4);
            $table->decimal('sd0', 8, 4);
            $table->decimal('sd1pos', 8, 4);
            $table->decimal('sd2pos', 8, 4);
            $table->decimal('sd3pos', 8, 4);
            $table->timestamps();

            $table->unique(['indicator', 'gender', 'age_or_length'], 'who_std_unique');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('who_growth_standards');
    }
};

