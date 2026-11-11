<?php

namespace App\Services;

use App\Models\AuditLog;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Log;

class AuditLogService
{
    /**
     * Catat tindakan audit.
     *
     * @param string     $action     'create'|'update'|'delete'|'login'|'logout'|'export'
     * @param Model|null $model      Model yang terpengaruh (opsional)
     * @param array      $oldValues  Nilai sebelum perubahan
     * @param array      $newValues  Nilai sesudah perubahan
     * @param string|null $notes     Catatan tambahan
     */
    public static function log(
        string $action,
        ?Model $model = null,
        array $oldValues = [],
        array $newValues = [],
        ?string $notes = null
    ): void {
        try {
            $request = request();
            AuditLog::create([
                'user_id'        => Auth::id(),
                'action'         => $action,
                'auditable_type' => $model ? get_class($model) : null,
                'auditable_id'   => $model?->getKey(),
                'old_values'     => empty($oldValues) ? null : $oldValues,
                'new_values'     => empty($newValues) ? null : $newValues,
                'ip_address'     => $request?->ip(),
                'user_agent'     => $request?->userAgent(),
                'notes'          => $notes,
            ]);
        } catch (\Throwable $e) {
            // Gagal audit log tidak boleh menghentikan operasi utama
            Log::error('AuditLog gagal: ' . $e->getMessage());
        }
    }

    /**
     * Hitung diff antara dua array — hanya field yang berubah.
     */
    public static function diff(array $old, array $new): array
    {
        $changed = [];
        foreach ($new as $key => $value) {
            if (! array_key_exists($key, $old) || $old[$key] !== $value) {
                $changed[$key] = $value;
            }
        }
        return $changed;
    }
}

