# CARA MENJALANKAN — DEDIS (FASE 1)

Panduan menjalankan backend Laravel dan menghubungkannya ke aplikasi Android DEDIS.

---

## Prasyarat

| Alat | Versi minimal | Keterangan |
|---|---|---|
| PHP CLI | 8.2 | Pastikan `php` tersedia di PATH atau gunakan XAMPP: `C:\xampp\php\php.exe` |
| Composer | 2.x | https://getcomposer.org |
| Android Studio | Hedgehog+ | Termasuk JDK di `C:\Program Files\Android\Android Studio1\jbr` |
| Gradle | (dari wrapper) | Gunakan `.\gradlew` di folder proyek Android |

---

## A. Jalankan Backend Laravel

### 1. Masuk ke folder backend

```bash
cd "C:\Users\ACER ID\Documents\DEDIS\backend"
```

### 2. Salin dan isi konfigurasi `.env`

```bash
copy .env.example .env
```

Buka `.env` dan isi nilai berikut (sesuaikan dengan database lokal Anda):

```env
APP_KEY=          # diisi otomatis oleh langkah berikutnya
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=dedis
DB_USERNAME=root
DB_PASSWORD=
```

> **Catatan**: Jika menggunakan SQLite, ubah `DB_CONNECTION=sqlite` dan isi `DB_DATABASE` dengan path absolut ke file `.sqlite`.

### 3. Install dependensi dan generate key

```bash
composer install
php artisan key:generate
```

### 4. Jalankan migrasi database

```bash
php artisan migrate
```

### 5. Isi data contoh (Demo Seeder)

```bash
php artisan db:seed --class=DemoDataSeeder
```

Data yang dibuat:
| Peran | Email | PIN |
|---|---|---|
| Kader | `sari@posyandu.id` | `123456` |
| Orang Tua | `nuraini@posyandu.id` | `123456` |

Orang Tua memiliki 2 anak (Aisyah Putri & Nabil Pratama), masing-masing dengan 3 pengukuran.

### 6. Jalankan server

**Untuk emulator Android (10.0.2.2 → localhost):**

```bash
php artisan serve --port=8000
```

**Untuk HP fisik di Wi-Fi yang sama:**

```bash
php artisan serve --host=0.0.0.0 --port=8000
```

Server berjalan di `http://127.0.0.1:8000` (atau `http://0.0.0.0:8000` jika host diatur).

---

## B. Konfigurasi BASE_URL di Android

Buka file `app/build.gradle.kts` dan ubah bagian `buildConfigField` di dalam `buildTypes { debug { ... } }`:

### Emulator Android (default)

```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/api/v1/\"")
```

`10.0.2.2` adalah alias emulator Android ke `localhost` laptop.

### HP fisik di Wi-Fi yang sama

Cari IP laptop Anda di Wi-Fi:
- Windows: jalankan `ipconfig` di Command Prompt, cari "IPv4 Address" di adapter Wi-Fi (contoh: `192.168.1.5`)

Lalu ubah:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://192.168.1.5:8000/api/v1/\"")
```

Pastikan Laravel dijalankan dengan `--host=0.0.0.0`.

> **Jangan** menyimpan alamat IP atau kredensial langsung di kode sumber yang di-commit. Gunakan hanya `BuildConfig` atau `.env` yang sudah ada di `.gitignore`.

---

## C. Build dan Jalankan Aplikasi Android

```bash
cd "C:\Users\ACER ID\Documents\DEDIS"
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio1\jbr"
.\gradlew assembleDebug
```

Install APK ke emulator atau HP yang terhubung:

```bash
.\gradlew installDebug
```

---

## D. Cara Menguji Alur Daftar Balita → Detail Balita (Fase 1)

1. Buka aplikasi DEDIS.
2. Di layar Role Selection, pilih **Kader** dan login sebagai `sari@posyandu.id` / PIN `123456`.
3. Di Beranda Kader, ketuk **Daftar Balita**.
4. Layar **Daftar Balita** memuat data dari backend (`GET /api/v1/children`).
   - Jika berhasil: tampil 2 balita (Aisyah Putri & Nabil Pratama).
   - Jika gagal (koneksi): tampil pesan error dan tombol "Coba Lagi".
5. Ketuk salah satu balita.
6. Layar **Detail Balita** memuat data dari backend (`GET /api/v1/children/{id}`).
   - Tampil nama, usia, berat/tinggi terakhir, riwayat pengukuran, dan info orang tua.

---

## E. Cara Menguji Alur Catat Pengukuran → Hasil Pertumbuhan (Fase 2)

1. Dari layar **Detail Balita** (contoh: Aisyah Putri):
   - Ketuk tombol hijau utama **"Catat Pengukuran"**.
2. Di layar **Catat Pengukuran**:
   - Bagian atas otomatis menampilkan info anak terpilih (Aisyah Putri, 24 bulan, Ibu Nur Aini).
   - Tanggal default terisi hari ini (`YYYY-MM-DD`).
   - Cara pengukuran default otomatis terpilih **"Berdiri"** (karena usia ≥ 24 bulan).
   - Coba uji validasi:
     - Kosongkan berat atau isi `0` / `45` -> muncul pesan error merah di bawah kolom.
     - Isi tinggi `< 40` atau `> 130` -> muncul pesan error merah di bawah kolom.
   - Masukkan nilai valid:
     - Berat Badan: `10.4`
     - Tinggi Badan: `80.0`
     - Lingkar Kepala: `46.5` (opsional)
   - Ketuk **"Simpan & Hitung Status"**.
3. Status Pengiriman:
   - Tombol berubah menjadi "Menyimpan & Menghitung..." (nonaktif) dengan spinner.
   - Permintaan dikirim ke `POST /api/v1/children/1/measurements`.
   - Backend menghitung Z-score WHO (TB/U, BB/U, BB/TB) dan status gizi.
4. Di layar **Hasil Pertumbuhan**:
   - Menampilkan hasil respon backend secara nyata:
     - Dua angka besar: **10,4 kg** dan **80,0 cm**.
     - Indikator Z-Score: TB/U, BB/U, BB/TB dengan badge status dan warna sesuai DESIGN.md.
     - Kartu rekomendasi tindak lanjut.
5. Kembali ke **Detail Balita** atau **Daftar Balita**:
   - Layar otomatis memuat ulang (refresh) data terbaru.
   - Riwayat pengukuran Aisyah Putri bertambah, dan status terbarui.

---

## F. Endpoint API yang Digunakan

| Method | URL | Keterangan |
|---|---|---|
| `POST` | `/api/v1/login` | Login, balas dengan `token` dan `role` |
| `GET` | `/api/v1/children` | Daftar semua balita (perlu token) |
| `GET` | `/api/v1/children/{id}` | Detail balita beserta riwayat pengukuran |
| `POST` | `/api/v1/children/{id}/measurements` | Catat pengukuran baru & hitung Z-score WHO |

Header yang dikirim oleh aplikasi secara otomatis:

```
Authorization: Bearer <token>
```

---

## G. Seeder Pengujian Pengukuran (CLI Backend)

Untuk menguji penyimpanan pengukuran langsung dari CLI Laravel:

```bash
php artisan db:seed --class=TestMeasurementSeeder
```

Seeder ini mencatat pengukuran untuk Aisyah Putri (BB: 10.4 kg, TB: 80.0 cm) dan mencetak nilai Z-score yang dihasilkan.

---

## F. Troubleshooting

| Masalah | Solusi |
|---|---|
| `CLEARTEXT communication not permitted` | Pastikan menggunakan build **Debug**, bukan Release. |
| `Connection refused` di emulator | Pastikan server berjalan dan BASE_URL adalah `http://10.0.2.2:8000/api/v1/`. |
| `401 Unauthorized` | Sesi kedaluwarsa. Logout dan login ulang. |
| `No such table: children` | Jalankan `php artisan migrate` dan `php artisan db:seed --class=DemoDataSeeder`. |
| Gradle gagal: `JAVA_HOME not set` | Jalankan: `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio1\jbr"` sebelum `.\gradlew`. |

