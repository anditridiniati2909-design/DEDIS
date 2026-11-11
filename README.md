# DEDIS (Deteksi Dini Stunting)

Aplikasi Android untuk membantu posyandu memantau pertumbuhan balita dan mendeteksi stunting lebih awal.
Dipakai oleh dua peran: **orang tua** (melihat perkembangan anak) dan **kader posyandu** (mencatat pengukuran dan tindak lanjut).

Proyek PjBL Aplikasi Mobile, Kelompok 2, Kelas 3A TKJ.
Mitra: Posyandu Anggrek 4.

## Anggota

| Nama | Peran |
|---|---|
| Muhammad Farid Idris | Programmer, Back end, Project Lead |
| Nurul Annisa | Front end, Writer, Tester |
| A. Tridiniati | UI/UX, Front end, Writer |

## Teknologi

- Android: Kotlin, Jetpack Compose
- Jaringan: Retrofit, OkHttp, Gson
- Backend: Laravel 11 (REST API)
- Desain: Figma

## Struktur folder

```
app/        Aplikasi Android
backend/    REST API Laravel
docs/       Dokumentasi (cara menjalankan, hasil audit integrasi, screenshot)
DESIGN.md   Panduan desain (warna, tipografi, daftar layar)
```

## Cara menjalankan

Langkah lengkap ada di [docs/CARA_MENJALANKAN.md](docs/CARA_MENJALANKAN.md). Ringkasnya:

1. Jalankan backend dari folder `backend/`:
   ```
   composer install
   cp .env.example .env
   php artisan key:generate
   php artisan migrate --seed
   php artisan serve --host=0.0.0.0 --port=8000
   ```
2. Atur `BASE_URL` di aplikasi Android (emulator: `http://10.0.2.2:8000/api/`; HP asli: alamat laptop di Wi-Fi yang sama).
3. Buka folder proyek di Android Studio, lalu jalankan aplikasi.

## Status fitur

Status per minggu 12.

| Fitur | Status |
|---|---|
| Splash Screen, On-boarding, pemilihan peran | Tampilan selesai |
| Login dan Register | Tampilan selesai |
| Sesi login (masuk otomatis) dan Logout | Selesai |
| Dashboard orang tua dan kader | Tampilan selesai, memakai data contoh |
| Daftar balita dan Detail balita (kader) | Terhubung ke backend, dalam pengujian |
| Catat pengukuran dan perhitungan Z-score | Dalam pengerjaan |
| Hasil pertumbuhan orang tua | Dalam pengerjaan |
| Tindak lanjut dan Tambah balita | Dalam pengerjaan |
| Pindai QR | Dalam pengerjaan |
| Pesan orang tua dan kader | Direncanakan |
| Jadwal dan pengingat | Direncanakan |

## Desain

Desain UI/UX dibuat di Figma: [Buka prototype](https://www.figma.com/proto/gtSMdOFQcn8PzrWkeBbyKd/Untitled?node-id=9-3&p=f&t=c3BPRXIDgb1PUyYi-1&scaling=min-zoom&content-scaling=fixed&page-id=0%3A1&starting-point-node-id=9%3A3&show-proto-sidebar=1)

Panduan warna, tipografi, dan daftar layar ada di [DESIGN.md](DESIGN.md).

<!--
Hapus tanda pembuka dan penutup komentar ini setelah gambar sudah diunggah ke docs/screenshots.

## Tampilan Aplikasi

Screenshot dari aplikasi yang berjalan, memakai data contoh.

**Umum**

| Splash | On-boarding | Pilih Peran | Login | Register |
|:---:|:---:|:---:|:---:|:---:|
| <img src="docs/screenshots/splash.jpeg" width="150"> | <img src="docs/screenshots/onboarding.jpeg" width="150"> | <img src="docs/screenshots/pilih-peran.jpeg" width="150"> | <img src="docs/screenshots/login.jpeg" width="150"> | <img src="docs/screenshots/register.jpeg" width="150"> |

**Orang Tua**

| Beranda | Pertumbuhan | Jadwal | Edukasi | Profil |
|:---:|:---:|:---:|:---:|:---:|
| <img src="docs/screenshots/ortu-beranda.jpeg" width="150"> | <img src="docs/screenshots/ortu-pertumbuhan.jpeg" width="150"> | <img src="docs/screenshots/ortu-jadwal.jpeg" width="150"> | <img src="docs/screenshots/ortu-edukasi.jpeg" width="150"> | <img src="docs/screenshots/ortu-profil.jpeg" width="150"> |

**Kader**

| Beranda | Daftar Balita | Detail Balita | Catat Ukur | Pindai QR |
|:---:|:---:|:---:|:---:|:---:|
| <img src="docs/screenshots/kader-beranda.jpeg" width="150"> | <img src="docs/screenshots/kader-daftar.jpeg" width="150"> | <img src="docs/screenshots/kader-detail.jpeg" width="150"> | <img src="docs/screenshots/kader-ukur.jpeg" width="150"> | <img src="docs/screenshots/kader-qr.jpeg" width="150"> |
-->

## Catatan data

Semua data balita dan akun di repositori ini adalah **data contoh** dari seeder, bukan data nyata.
Jangan mengunggah data asli balita atau orang tua ke repositori ini.
