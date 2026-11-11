<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('child_guardian_links', function (Blueprint $table) {
            $table->id();
            $table->foreignId('child_id')->constrained('children')->cascadeOnDelete();
            $table->foreignId('user_id')->nullable()->constrained('users')->cascadeOnDelete();
            // Kode tautan 8 karakter sekali pakai, berlaku 7 hari
            $table->string('link_code', 8)->unique();
            $table->timestamp('link_code_expires_at');
            $table->unsignedInteger('failed_claim_attempts')->default(0);
            $table->boolean('is_linked')->default(false);
            $table->timestamp('linked_at')->nullable();
            $table->foreignId('created_by')->constrained('users');
            $table->timestamps();

            $table->index(['link_code', 'is_linked', 'link_code_expires_at']);
            $table->index(['child_id', 'user_id']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('child_guardian_links');
    }
};

