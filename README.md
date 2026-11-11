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
docs/       Dokumentasi (cara menjalankan, hasil audit integrasi)
design/     Screenshot desain dan DESIGN.md
```
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

Centang sesuai kondisi sebenarnya.

- [ ] Splash Screen, On-boarding, pemilihan peran
- [ ] Login dan Register
- [ ] Sesi login (masuk otomatis) dan Logout
- [ ] Dashboard orang tua dan kader
- [ ] Daftar balita dan Detail balita (data dari backend)
- [ ] Catat pengukuran dan perhitungan Z-score
- [ ] Hasil pertumbuhan orang tua
- [ ] Tindak lanjut
- [ ] Pindai QR
- [ ] Pesan orang tua dan kader
- [ ] Jadwal dan pengingat

## Desain

Desain UI/UX: [tempel link Figma di sini]

## Catatan data

Semua data balita dan akun di repositori ini adalah **data contoh** dari seeder, bukan data nyata.
Jangan mengunggah data asli balita atau orang tua ke repositori ini.
