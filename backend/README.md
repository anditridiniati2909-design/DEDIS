# DEDIS Backend — Laravel REST API

**Deteksi Dini Stunting** · Posyandu Anggrek 4, Dusun Bontocinde, Kab. Gowa

---

## Prasyarat

| Software | Versi minimum |
|----------|--------------|
| PHP | 8.2+ |
| MySQL | 8.0+ |
| Composer | 2.x |
| Laravel | 10.x atau 11.x |

> **Lingkungan lokal:** PHP tersedia di `C:\xampp\php\php.exe`

---

## Instalasi

```bash
# 1. Clone repo dan masuk ke folder backend
cd backend

# 2. Install dependensi
composer install

# 3. Salin konfigurasi environment
cp .env.example .env

# 4. Edit .env — isi DB_*, APP_KEY, dll.
php artisan key:generate

# 5. Jalankan migrasi
php artisan migrate

# 6. Jalankan seeder (data referensi saja — BUKAN data pengguna)
# CATATAN: WhoGrowthStandardSeeder TIDAK dijalankan otomatis.
# Lihat bagian "Data WHO" di bawah.
php artisan db:seed

# 7. Jalankan server lokal
php artisan serve
```

---

## Data WHO (WAJIB sebelum produksi)

**⚠️ Aplikasi TIDAK bisa menghitung status gizi tanpa data ini.**

1. Unduh file CSV dari: https://www.who.int/tools/child-growth-standards/standards
2. Letakkan di: `database/seeders/who_data/`
3. File yang dibutuhkan (lihat `WhoGrowthStandardSeeder.php` untuk daftar lengkap):
   - `wfa_boys_0_5_zscores.csv`
   - `wfa_girls_0_5_zscores.csv`
   - `lhfa_boys_0_2_zscores.csv` / `hfa_boys_2_5_zscores.csv`
   - dll.
4. Implementasikan logic baca CSV di `WhoGrowthStandardSeeder.php`
5. Jalankan: `php artisan db:seed --class=WhoGrowthStandardSeeder`

---

## Struktur Peran

| Peran | Hak Akses |
|-------|-----------|
| `orang_tua` | Lihat anak sendiri, riwayat pengukuran, jadwal, edukasi |
| `kader` | Tambah/edit anak, catat pengukuran, lihat laporan |
| `kader_admin` | Semua hak kader + manajemen akun kader, undangan |

> **Penting:** Peran ditentukan 100% oleh server. Klien TIDAK boleh menentukan peran.
> Registrasi publik selalu menghasilkan peran `orang_tua`.
> Akun kader dibuat via kode undangan dari `kader_admin`.

---

## API Endpoints (ringkasan)

```
POST   /api/v1/auth/login            Semua peran
POST   /api/v1/auth/register         Publik → orang_tua
POST   /api/v1/auth/activate-kader   Via kode undangan → kader
GET    /api/v1/auth/me               Token valid
POST   /api/v1/auth/logout

GET    /api/v1/children              Daftar anak (orang_tua: milik sendiri; kader: semua)
POST   /api/v1/children              [kader] Tambah anak
GET    /api/v1/children/{id}         Detail anak
PUT    /api/v1/children/{id}         [kader] Edit anak
DELETE /api/v1/children/{id}         [kader_admin] Hapus anak
POST   /api/v1/children/link         [orang_tua] Tautkan via link_code

GET    /api/v1/children/{id}/measurements      Riwayat pengukuran
POST   /api/v1/children/{id}/measurements      [kader] Catat pengukuran
GET    /api/v1/children/{id}/growth-trend      Tren pertumbuhan

GET    /api/v1/schedules             Jadwal mendatang
POST   /api/v1/schedules             [kader] Buat jadwal
PATCH  /api/v1/schedules/{id}/cancel [kader] Batalkan jadwal

GET    /api/v1/reports/monthly       [kader] Laporan bulanan

GET    /api/v1/kader                        [kader_admin] Daftar kader
POST   /api/v1/kader/invitations            [kader_admin] Buat kode undangan
GET    /api/v1/kader/invitations            [kader_admin] Lihat undangan aktif
PATCH  /api/v1/kader/{id}/deactivate        [kader_admin] Nonaktifkan kader
PATCH  /api/v1/kader/{id}/reactivate        [kader_admin] Aktifkan kembali kader
```

---

## Keamanan

- Autentikasi: **Laravel Sanctum** (token bearer)
- Peran divalidasi dari database pada setiap request (bukan hanya dari token)
- Rate limit login: 5 percobaan per 15 menit per IP+email
- Field `role`, `is_active`, `locked_until` ditolak dari semua request klien
- Soft delete untuk semua data sensitif
- Audit log untuk semua operasi CRUD

---

## Environment Variables Penting

```ini
APP_KEY=              # Generate: php artisan key:generate
APP_ENV=production    # Ganti ke 'local' untuk dev
APP_DEBUG=false       # false di production

DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=dedis
DB_USERNAME=
DB_PASSWORD=

SANCTUM_STATEFUL_DOMAINS=  # Domain frontend

# Kunci API AI hanya di sini, TIDAK pernah dikirim ke klien Android
AI_API_KEY=
```

---

## Dibuat untuk

Tugas Project-Based Learning, Aplikasi Mobile  
Politeknik Negeri Ujung Pandang — 2025/2026

