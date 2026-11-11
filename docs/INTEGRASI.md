# Audit Integrasi dan Navigasi Aplikasi DEDIS

Dokumen ini berisi audit menyeluruh terhadap arsitektur, layar antarmuka (UI), keterhubungan navigasi, status 10 alur kunci, dan kesiapan backend untuk aplikasi **DEDIS (Deteksi Dini Stunting)** Posyandu Anggrek 4.

> **Catatan Audit**: Dokumen ini disusun murni berdasarkan hasil audit kode per 10 November 2026 tanpa mengubah kode aplikasi.

---

## 1. Inventaris dan Sumber Data Seluruh Layar

Backend yang tersedia di proyek adalah **Laravel 11 REST API** (di folder `backend/`) dengan autentikasi Laravel Sanctum, tabel MySQL, dan perhitungan Z-score WHO. Pada aplikasi Android (`app/`), saat ini **belum terpasang pustaka HTTP client (Retrofit/Ktor)** maupun database lokal (Room), sehingga sebagian besar layar masih mengandalkan data contoh (mock/hardcode).

### A. Alur Orang Tua

| Layar | File UI | Sumber Data Saat Ini | Tombol / Menu Belum Berfungsi | Data yang Tidak Terbawa dari Layar Sebelumnya |
|---|---|---|---|---|
| **Pembuka (Splash)** | `PembukaDedisScreen.kt` | Lokal (`SessionManager`) | - | - |
| **Kenali DEDIS (Onboarding)** | `KenaliDedisScreen.kt` | Tetap di kode (3 halaman onboarding) | - | - |
| **Pilih Peran & Masuk** | `RoleSelectionScreen.kt` | Lokal (`SessionManager`) | - | - |
| **Masuk Orang Tua** | `MasukOrangTuaScreen.kt` | Lokal / Form statis | Tombol "Daftar" hanya kembali ke pemilihan peran | - |
| **Beranda Orang Tua** | `OrangTuaBerandaScreen.kt` | Data contoh (Aisyah 24 bln & Nabil 8 bln, tinggi 79,2 cm, berat 10,2 kg) | • Ikon lonceng notifikasi di header (`onNotificationClick` kosong)<br>• Menu cepat "QR Anak" memunculkan dialog sementara, bukan membuka layar `QrAnakScreen`<br>• Tombol "Lihat jadwal" pada kartu posyandu hanya navigasi rute tanpa ID jadwal | `selectedChild` tersimpan di memori lokal Beranda, tidak dibagikan ke layar lain |
| **Pertumbuhan (Detail Balita)** | `OrangTuaDetailBalitaScreen.kt` | Data contoh tetap di kode (Aisyah Putri, riwayat 3 bulan, Z-score -2.1 SD) | - | **Tidak menerima `childId`**; selalu menampilkan data Aisyah Putri meskipun orang tua memilih Nabil |
| **Jadwal Posyandu** | `OrangTuaJadwalScreen.kt` | Parameter default statis (Rabu 7 Oktober, Balai Warga) | • Tombol "Konfirmasi Kehadiran" hanya mengubah state UI lokal (belum panggil API `POST /schedules/{id}/confirm`) | Tidak ada informasi balita mana yang akan dibawa |
| **Imunisasi** | `OrangTuaImunisasiScreen.kt` | Data contoh tetap (5 riwayat vaksin, 1 jadwal peninjauan Buku KIA) | - | **Tidak menerima `childId`**; pilihan chip anak berdiri sendiri |
| **Edukasi** | `OrangTuaEdukasiScreen.kt` | Data contoh tetap (4 artikel, 1 kartu sorotan) | • Tombol "Baca Sekarang" pada kartu sorotan (`onClick` kosong)<br>• 4 kartu artikel tidak memiliki navigasi detail artikel<br>• Kolom pencarian & chip kategori belum memfilter isi daftar | - |
| **Pesan & Konsultasi** | `OrangTuaPesanScreen.kt` | State memori Compose (`mutableStateListOf`) | • Pesan tidak tersimpan ke database (hilang saat layar ditutup)<br>• Belum polling/mengambil pesan balasan kader dari backend | Tidak membawa konteks anak yang sedang dikonsultasikan |
| **Profil Orang Tua** | `OrangTuaProfilScreen.kt` | Campuran: Nama dari `SessionManager`, jumlah anak (2) dan posyandu hardcoded | - | - |
| **Ubah Profil** | `UbahProfilScreen.kt` | State lokal form | Tombol "Simpan" hanya melakukan `popBackStack()`, belum memanggil API `PUT /profile` | - |
| **Ubah Kata Sandi** | `UbahKataSandiScreen.kt` | State lokal form | Tombol "Simpan" hanya `popBackStack()`, belum memanggil API `POST /password` | - |
| **Notifikasi (Pengaturan)** | `NotifikasiScreen.kt` | State lokal (4 sakelar) | Tombol "Simpan Pengaturan" hanya `popBackStack()`, belum memanggil API `PUT /notifications` | - |
| **QR Anak** | `QrAnakScreen.kt` | Generate ZXing offline dari ID statis (1L & 2L) | Tombol "Simpan gambar" dan "Bagikan" sudah berfungsi secara lokal | Tidak menerima pilihan anak aktif dari Beranda |
| **Tentang DEDIS** | `TentangScreen.kt` | `BuildConfig.VERSION_NAME` & teks statis | - | - |

---

### B. Alur Kader

| Layar | File UI | Sumber Data Saat Ini | Tombol / Menu Belum Berfungsi | Data yang Tidak Terbawa dari Layar Sebelumnya |
|---|---|---|---|---|
| **Beranda Kader** | `KaderBerandaScreen.kt` | Data contoh tetap (92/115 diukur, 80 normal, 9 pendek, 3 sangat pendek) | • Ikon lonceng notifikasi header kosong<br>• Klik balita di daftar "Perlu Ditinjau" membuka Detail Balita tanpa ID | - |
| **Daftar Balita** | `KaderDaftarBalitaScreen.kt` | Data contoh tetap (6 balita di dalam `listOf`) | • Filter chip (Semua, Belum, Perhatian) dan kolom cari belum memfilter data dinamis<br>• Klik balita membuka Detail Balita tanpa membawa ID balita | - |
| **Detail Balita (Kader)** | `KaderDetailBalitaScreen.kt` | Data contoh tetap (Aisyah Putri, 24 bln, Ibu Nur Aini) | • **Tidak ada tombol untuk melakukan pengukuran baru untuk balita ini** (hanya ada tombol "Lihat hasil" dan "Buat tindak lanjut") | **Tidak menerima `balitaId`**; selalu memuat Aisyah Putri dari balita mana pun yang diklik |
| **Catat Pengukuran** | `KaderPengukuranBulananScreen.kt` | Data contoh tetap (Aisyah Putri, nilai awal 10.2 kg / 79.2 cm / 46.2 cm) | • Tombol "Simpan & Hitung Status" hanya membuka layar Hasil tanpa memanggil API `POST /children/{id}/measurements`<br>• Input pengukuran tidak diteruskan ke layar Hasil | **Tidak menerima `balitaId`**; sasaran pengukuran tertulis tetap |
| **Hasil Pertumbuhan** | `KaderHasilPertumbuhanScreen.kt` | Data contoh tetap (TB/U -2.1 SD, BB/U -0.4 SD, BB/TB -0.6 SD) | • Data tidak berasal dari perhitungan Z-score backend | **Tidak menerima hasil ukur** dari layar Catat Pengukuran |
| **Tindak Lanjut** | `KaderTindakLanjutScreen.kt` | Data contoh tetap (Aisyah Putri, 10 September 2026) | • Tombol "Simpan Tindak Lanjut" hanya `popBackStack()`, belum menyimpan ke tabel `growth_follow_ups` | **Tidak menerima `balitaId`** maupun hasil pengukuran |
| **Pindai QR Balita** | `KaderPindaiQrScreen.kt` | Kamera CameraX + ML Kit nyata | • Tombol "Detail" & "Catat Ukur" membuka layar tujuan **tanpa membawa ID balita yang berhasil dipindai**<br>• Data balita yang tampil setelah scan di-hardcode ke "Aisyah Putri" | - |
| **Laporan Bulanan** | `KaderLaporanScreen.kt` | Data contoh tetap (Cakupan 80%, distribusi status gizi) | • Tombol "Unduh Rekapitulasi (PDF)" (`onClick` kosong)<br>• Tombol "Kirim Ringkasan ke Bidan" (`onClick` kosong) | - |
| **Pesan Masuk (Inbox)** | `KaderInboxScreen.kt` | Data contoh tetap (3 percakapan) | Belum sinkron ke tabel `messages` di backend | - |
| **Chat Kader** | `KaderChatScreen.kt` | State memori lokal | Input balas pesan tidak tersimpan ke database | Hanya menerima `partnerName`, tidak menerima `partnerId` |
| **Tambah Balita (Form)** | `KaderFormulirBalitaScreen.kt` | State lokal form | Tombol "Simpan Data Balita" hanya `popBackStack()`, belum memanggil API `POST /children` | - |
| **Profil Kader** | `KaderProfilScreen.kt` | Teks tetap ("Kader Sari", "Posyandu Anggrek 4") | Submenu "Ubah Profil Kader", "Pengaturan Posyandu", "Notifikasi & Jadwal", "Tentang DEDIS" belum memiliki tujuan rute (`onClick = {}`) | - |

---

## 2. Peta Navigasi dan Titik-Titik Putus

```
[PEMBUKA] ──(Sesi aman)──► [BERANDA ORANG TUA] 
   │                              │
   │ (Belum login)                ├──► [DETAIL_BALITA (Pertumbuhan)] ◄── [PUTUS: Argumen childId tidak dikirim]
   ▼                              ├──► [IMUNISASI] ◄──────────────────── [PUTUS: Argumen childId tidak dikirim]
[KENALI]                          ├──► [JADWAL]
   │                              ├──► [EDUKASI] ──► [PUTUS: Detail Artikel tidak ada]
   ▼                              ├──► [PESAN] ──► [PUTUS: Pesan tidak sampai ke Kader]
[ROLE_SELECTION]                  └──► [PROFIL]
   │                                     ├──► [UBAH_PROFIL] ──► [PUTUS: Simpan tidak ke API]
   │                                     ├──► [UBAH_KATA_SANDI] ──► [PUTUS: Simpan tidak ke API]
   │                                     ├──► [NOTIFIKASI] ──► [PUTUS: Simpan tidak ke API]
   │                                     ├──► [QR_ANAK]
   │                                     └──► [TENTANG]
   │
   └──────(Masuk Kader)────────► [KADER_BERANDA]
                                        ├──► [KADER_INBOX] ──► [KADER_CHAT] ◄── [PUTUS: Pesan terisolasi]
                                        ├──► [KADER_PINDAI_QR] ──► [PUTUS: Hasil scan tidak membawa ID ke Detail/Catat]
                                        ├──► [KADER_FORMULIR] ──► [PUTUS: Data baru tidak masuk ke Daftar Balita]
                                        ├──► [KADER_DAFTAR_BALITA]
                                        │         └──► [KADER_DETAIL_BALITA] ◄── [PUTUS: balitaId tidak dikirim]
                                        │                   ├──► [KADER_HASIL_PERTUMBUHAN]
                                        │                   │         └──► [KADER_TINDAK_LANJUT] ──► [PUTUS: Simpan tidak ke DB]
                                        │                   └──► [PUTUS: Tidak bisa langsung Catat Ukur dari Detail]
                                        ├──► [KADER_PENGUKURAN] ──► [KADER_HASIL_PERTUMBUHAN] ◄── [PUTUS: Nilai ukur tidak dibawa]
                                        ├──► [KADER_LAPORAN] ──► [PUTUS: Unduh PDF & Kirim Bidan kosong]
                                        └──► [KADER_PROFIL] ──► [PUTUS: 4 submenu profil kader onClick kosong]
```

### Rincian Titik Putus Kritis:
1. **Titik Putus Argumen Balita**: `DedisNavGraph.kt` mendefinisikan rute `detail_balita`, `kader_detail_balita`, `kader_pengukuran`, dan `kader_hasil_pertumbuhan` tanpa argumen (misal `{childId}`). Akibatnya layar selalu me-render balita default ("Aisyah Putri").
2. **Titik Putus Alur Ukur Kader**: Dari `KaderDetailBalitaScreen` tidak ada tombol "Catat Pengukuran". Sebaliknya, `KaderPengukuranBulananScreen` hanya bisa dibuka dari Beranda Kader tanpa mengetahui balita mana yang sedang diukur.
3. **Titik Putus Integrasi Jaringan**: Belum ada modul Client API (Retrofit/Ktor) pada modul Android untuk menghubungkan UI Compose dengan endpoint Laravel di `backend/`.

---

## 3. Evaluasi 10 Alur Kunci

| No | Alur Kunci | Status | Catatan Evaluasi (1 Kalimat) |
|---|---|---|---|
| **1** | Login, lalu masuk ke Beranda sesuai peran. Nama dan data anak dari akun dipakai di semua layar. | **Sebagian** | Sesi token terenkripsi dan routing peran berhasil, namun data anak dan profil di layar-layar masih berupa nilai contoh tetap di kode. |
| **2** | Orang tua: anak yang dipilih di Beranda sama di Pertumbuhan, Jadwal, Imunisasi, dan Pesan. Menu cepat Beranda membuka layar yang benar. "Lihat grafik" membuka Pertumbuhan anak itu. | **Sebagian** | Menu cepat membuka layar yang benar, namun status pilihan anak (Aisyah/Nabil) terisolasi di Beranda dan tidak diteruskan ke layar Pertumbuhan, Imunisasi, Jadwal, maupun Pesan. |
| **3** | Kader: Beranda lalu Pindai QR atau Daftar balita, lalu Detail balita, lalu Catat pengukuran, lalu Hasil pertumbuhan, lalu Tindak lanjut. Balita yang dipilih terbawa lewat ID. | **Putus** | Navigasi antar layar kader dapat berpindah, tetapi ID balita tidak pernah dikirimkan melalui rute NavGraph sehingga data balita selalu terkunci pada data contoh. |
| **4** | Kader menyimpan pengukuran, lalu backend menghitung Z-score, lalu hasilnya muncul di Detail balita, Daftar balita (status), Beranda kader (ringkasan), dan sisi orang tua. | **Putus** | Tombol "Simpan & Hitung Status" hanya berpindah layar secara lokal tanpa memanggil endpoint pengukuran backend, sehingga Z-score tidak dihitung dan ringkasan tidak terbarui. |
| **5** | Tindak lanjut dari kader (tanggal kontrol) muncul di sisi orang tua (Beranda, peringatan di Pertumbuhan, Jadwal). | **Putus** | Tindak lanjut kader tidak disimpan ke database backend dan kartu peringatan kontrol di sisi orang tua masih memuat tanggal tetap yang ditulis langsung di kode. |
| **6** | QR anak di aplikasi orang tua bisa dipindai di Pindai QR kader dan membuka Detail balita yang sama. | **Sebagian** | Kode QR berhasil digenerate dan dipindai secara kamera fisik, tetapi hasil decode string QR di kader belum memicu pencarian ID balita ke backend dan hanya menampilkan balita contoh. |
| **7** | Tambah balita (kader) muncul di Daftar balita dan terhubung ke akun orang tua (`child_guardian_links`). | **Putus** | Formulir tambah balita hanya melakukan `popBackStack()` tanpa request `POST /children`, sehingga data tidak masuk ke database maupun daftar balita. |
| **8** | Pesan: orang tua mengirim, kader menerima dan membalas. | **Sebagian** | UI percakapan dan input pesan sudah rapi di kedua sisi, namun data percakapan masih tersimpan di memori lokal masing-masing perangkat dan belum tersambung ke API pesan. |
| **9** | Notifikasi (ikon lonceng) berisi peristiwa nyata: jadwal, hasil ukur, pesan. | **Putus** | Ikon lonceng pada header kedua peran tidak memiliki aksi klik dan belum ada layar/dialog riwayat peristiwa notifikasi. |
| **10** | Logout dan sesi: setelah login tidak perlu login ulang, setelah logout harus login ulang. | **Tersambung** | Sesi tersimpan aman di Android Keystore, auto-login splash screen berjalan, dan fungsi logout membersihkan seluruh sesi serta mengunci back stack secara sempurna. |

---

## 4. Evaluasi Kesiapan Backend (Laravel REST API)

Di folder `backend/`, skema tabel dan controller Laravel sudah siap, namun terdapat beberapa endpoint yang belum tersedia:

### A. Endpoint & Tabel yang Sudah Ada
* **Autentikasi & Akun**: `users` (`POST /api/v1/auth/login`, `POST /api/v1/auth/register`, `POST /api/v1/auth/logout`, `GET /api/v1/auth/me`).
* **Profil & Preferensi**: `user_notifications` (`GET & PUT /api/v1/profile`, `POST /api/v1/password`, `GET & PUT /api/v1/notifications`).
* **Balita & Penautan**: `children`, `child_guardian_links` (`GET & POST /api/v1/children`, `GET /api/v1/children/{id}`, `GET /api/v1/children/{id}/qr`, `POST /api/v1/children/link`).
* **Pengukuran & Antropometri**: `measurements`, `who_growth_standards` (`POST /api/v1/children/{id}/measurements`, `GET /api/v1/children/{id}/measurements`). Controller sudah memiliki fungsi otomatis hitung Z-score WHO (TB/U, BB/U, BB/TB).
* **Jadwal & Konfirmasi**: `posyandu_schedules`, `schedule_confirmations` (`GET & POST /api/v1/schedules`, `POST & DELETE /api/v1/schedules/{id}/confirm`, `GET /api/v1/schedules/{id}/share-text`).
* **Pesan & Konsultasi**: `messages` (`GET /api/v1/messages`, `GET /api/v1/messages/{partnerId}`, `POST /api/v1/messages`).
* **Laporan**: `audit_logs` (`GET /api/v1/reports/monthly`, `GET /api/v1/children/{id}/growth-trend`).

### B. Endpoint & Tabel yang Belum Ada / Kurang
1. **Tindak Lanjut Kader**: Tabel `growth_follow_ups` sudah dibuat di migrasi, tetapi **controller dan route-nya belum ada** di `routes/api.php` (`POST /api/v1/children/{id}/follow-ups` dan `GET /api/v1/children/{id}/follow-ups`).
2. **Daftar Notifikasi Lonceng (In-App Notifications)**: Belum ada tabel `notifications_log` dan endpoint `GET /api/v1/notifications/feed` untuk menampilkan daftar peristiwa riwayat (misal: "Hasil ukur baru telah diverifikasi", "Pesan dari Kader Sari").
3. **Edukasi & Artikel**: Belum ada tabel/endpoint untuk modul edukasi; saat ini artikel murni statis di aplikasi Android.
4. **Catatan Imunisasi Balita**: Tabel `immunizations` sudah ada di migrasi, namun route pencatatan/daftar imunisasi per balita (`GET & POST /api/v1/children/{id}/immunizations`) belum didaftarkan di `api.php`.

---

## 5. Rekomendasi Urutan Perbaikan (Berdasarkan Dampak)

1. **Fase 1: State Management & Passing Argumen NavGraph (Dampak Tertinggi - Tanpa Jaringan)**
   * Mengubah rute navigasi di `DedisNavGraph.kt` agar menerima parameter ID:
     - `kader_detail_balita/{balitaId}`
     - `kader_pengukuran/{balitaId}`
     - `kader_hasil_pertumbuhan/{balitaId}`
     - `kader_tindak_lanjut/{balitaId}`
     - `detail_balita/{childId}`
   * Menyimpan `selectedChildId` orang tua di level state aplikasi (atau ViewModel bersama) agar sinkron di seluruh tab (Beranda, Pertumbuhan, Imunisasi, Pesan).
   * Menambahkan tombol "Catat Pengukuran" langsung di `KaderDetailBalitaScreen`.
   * Mengarahkan menu cepat "QR Anak" di Beranda Orang Tua ke `QrAnakScreen`.

2. **Fase 2: Integrasi Jaringan Android ke Laravel API (Penyambungan Data Nyata)**
   * Menambahkan library networking (`Retrofit` + `Moshi`/`Kotlinx Serialization` + `OkHttp`) di `build.gradle.kts`.
   * Memasang `AuthInterceptor` untuk menyertakan token dari `SessionManager` secara otomatis di header `Authorization: Bearer <token>`.
   * Menghubungkan alur pencatatan pengukuran kader (`POST /children/{id}/measurements`) ke backend agar Z-score WHO dihitung secara riil oleh server dan hasilnya langsung diteruskan ke layar Hasil Pertumbuhan.

3. **Fase 3: Penyambungan Formulir Tambah Balita, QR Scanner, & Tindak Lanjut**
   * Menghubungkan formulir kader ke `POST /children`.
   * Menghubungkan kamera QR Scanner kader agar memanggil `GET /children/{card_code}` sehingga balita yang dipindai otomatis teridentifikasi.
   * Membuat controller & route tindak lanjut di Laravel (`growth_follow_ups`), lalu sambungkan ke `KaderTindakLanjutScreen` dan tampilkan tanggal kontrolnya di kartu orang tua.

4. **Fase 4: Sinkronisasi Pesan & Notifikasi Lonceng**
   * Mengganti list memori di `OrangTuaPesanScreen` dan `KaderChatScreen` dengan panggilan endpoint `GET & POST /api/v1/messages`.
   * Membuat dialog/layar "Notifikasi Masuk" saat ikon lonceng di header diklik.
   * Menghubungkan sakelar konfirmasi kehadiran jadwal orang tua ke API konfirmasi.

