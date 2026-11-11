<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('children', function (Blueprint $table) {
            $table->id();
            // Kode kartu unik untuk QR (acak, bukan data sensitif/NIK)
            $table->string('card_code', 16)->unique();
            $table->string('name');
            $table->date('birth_date');
            $table->enum('gender', ['L', 'P']);
            $table->string('mother_name');
            $table->string('hamlet')->default('Bontocinde'); // Dusun
            $table->boolean('is_mbg_recipient')->default(false); // Penanda Makan Bergizi Gratis
            $table->boolean('is_archived')->default(false); // Otomatis arsip saat > 59 bulan
            $table->foreignId('created_by')->constrained('users');
            $table->timestamps();
            $table->softDeletes();

            $table->index('card_code');
            $table->index(['birth_date', 'gender']);
            $table->index('is_archived');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('children');
    }
};

