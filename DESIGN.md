# DEDIS — Panduan Desain untuk Implementasi Android

Aplikasi Deteksi Dini Stunting (DEDIS) untuk Posyandu Anggrek 4. Dua pengguna: **orang tua** dan **kader**.
File ini adalah ringkasan desain Figma supaya agent bisa membangun UI tanpa akses ke Figma.
Screenshot tiap layar ada di folder `design/` (nama file = nama layar di bagian 5).

> **Isi dulu sebelum mulai:** Tech stack = `Kotlin + Jetpack Compose`

---

## 1. Aturan desain (wajib diikuti)

1. Satu layar punya satu tujuan dan **satu tombol utama**.
2. Hindari paragraf. Pakai angka besar, ikon, dan label pendek.
3. Status ditunjukkan dengan warna + ikon + satu/dua kata.
4. Informasi yang jarang dipakai dipindah ke layar lain, bukan ditumpuk.
5. Bahasa Indonesia sederhana, mudah dipahami orang tua dan kader.

## 2. Design tokens

### Warna
| Token | Hex | Pemakaian |
|---|---|---|
| `ink` | `#203B32` | Teks utama |
| `primary` | `#167354` | Tombol utama, ikon aktif, penekanan |
| `muted` | `#61736B` | Teks sekunder, ikon tidak aktif |
| `tint` | `#EDF7F1` | Latar kartu/ikon lembut, chip "Normal" |
| `line` | `#DDE7E1` | Border kartu, garis pemisah |
| `white` | `#FFFFFF` | Latar layar dan kartu |
| `soft` | `#D6EEE2` | Teks kecil di atas latar `primary` |
| `warning-bg` / `warning-text` | `#FFF4D6` / `#855B09` | Status "Perlu perhatian", "Perhatian", "Cek" |
| `danger-bg` / `danger-text` | `#FDE8E6` / `#B42318` | Status "Risiko tinggi", tombol Keluar |
| `neutral-bg` | `#EEF1EF` | Status "Belum diukur" |

### Tipografi
Font **Inter** (Regular 400, Medium 500, SemiBold 600, Bold 700).

| Gaya | Ukuran | Berat |
|---|---|---|
| Judul layar | 26 | Bold |
| Angka besar (hasil ukur) | 32 | Bold |
| Judul kartu | 16–20 | Bold |
| Isi | 14–15 | Regular/Medium |
| Keterangan | 12–13 | Regular/Medium |
| Label navigasi | 11 | Regular (aktif: Bold) |

### Ukuran dan bentuk
- Layar acuan: **412 dp lebar** (Android). Padding header 24 dp, padding isi 20 dp kiri-kanan, jarak antar kartu 14 dp.
- Kartu: putih, border 1 dp `line`, radius 18, padding 16–18.
- Chip/pil: radius penuh (100), padding 16×9 (kecil: 12×5).
- Tombol: tinggi ±54, radius 12. Utama = isi `primary` teks putih. Sekunder = putih, border `primary`.
- Ikon: gaya garis (Lucide), tebal 1,8. Ukuran 16–26.
- Bilah status Android di atas dan bilah navigasi sistem di bawah dihandle sistem, tidak digambar di app.

## 3. Navigasi bawah (5 tab)

Tab aktif: latar pil `tint` radius 16, ikon dan label `primary`, label Bold. Tab tidak aktif: ikon dan label `muted`.

| Alur | Tab |
|---|---|
| Orang tua | Beranda · Jadwal · Edukasi · Pesan · Profil |
| Kader | Beranda · Balita · Layanan · Laporan · Profil |

Header tiap layar: teks kecil "DEDIS" (kiri, `primary`, Bold), nama alur ("RUANG ORANG TUA" / "RUANG KADER", tengah, `muted`, 11), ikon lonceng notifikasi (kanan). Di bawahnya judul layar, lalu subjudul opsional.

## 4. Arsitektur singkat (untuk backend)

- Android orang tua dan Android kader, dengan **cache lokal + antrean sinkron** untuk mode offline.
- **Firebase Auth + Rules**: hak akses orang tua, kader, bidan.
- **Cloud Functions**: hitung Z-score, laporan, proxy layanan AI, pemicu notifikasi.
- **Firestore**: data dan riwayat. **Cloud Storage**: backup dan arsip.
- **FCM**: notifikasi push. **Layanan AI**: chatbot gizi dan ringkasan kader (hanya lewat Cloud Functions).
- Aplikasi tidak memanggil layanan AI langsung.

## 5. Daftar layar

Data pada layar adalah **contoh** (balita Aisyah Putri 24 bulan, ibu Nur Aini, Posyandu Anggrek 4).
Kolom "Node Figma" untuk memudahkan mencari layar saat export screenshot.

### Alur orang tua

| Layar | Node Figma | Isi utama |
|---|---|---|
| Beranda | `96:2` | Sapaan "Halo, Ibu Nur Aini". Pilihan anak (chip Aisyah/Nabil). Kartu anak: inisial, nama, umur, status, dua angka besar (berat kg, tinggi cm), tombol "Lihat grafik". Kartu jadwal posyandu berikutnya. Menu cepat 4 ikon: Imunisasi, Edukasi, Chat kader, QR anak. |
| Pertumbuhan | `96:187` | Ikuti screenshot. |
| Jadwal | `122:2` | Kartu hijau `primary`: "Posyandu berikutnya", tanggal besar (Rabu, 7 Oktober), jam, tempat. Kartu "Ingatkan saya" dengan sakelar dan dua chip waktu (H-3, H-1). Kartu "Yang perlu dibawa": Buku KIA, Kartu posyandu (ikon centang). |
| Imunisasi | `122:149` | Chip pilihan anak. Kartu `tint` "Akan datang" (Peninjauan Buku KIA, 7 Okt 08.00). Daftar riwayat: ikon status, nama, tanggal, chip "Sudah" atau "Cek". Catatan kecil: "Data contoh. Cocokkan dengan Buku KIA." |
| Edukasi | `123:2` | Kolom cari. Chip topik (Semua, Stunting, MPASI, Imunisasi). Kartu bacaan utama "Kenali stunting" (4 menit baca). Daftar 4 bacaan: ikon topik, judul, durasi, panah. |
| Profil | `123:173` | Kartu nama (avatar inisial, nama, "Orang tua · anak"). Daftar menu 5 baris: Ubah profil, Kata sandi, Notifikasi, QR anak, Tentang DEDIS. Tombol "Keluar" (outline `danger`). |

### Alur kader

| Layar | Node Figma | Isi utama |
|---|---|---|
| Beranda | `96:363` | Sapaan "Selamat bertugas, Sari", Posyandu Anggrek 4. Ringkasan: sudah diukur 92 dari 115 balita; Normal 80, Pendek 9, Sangat pendek 3. Aksi cepat: Mulai layanan, Pindai QR, Tambah balita. Daftar "Perlu ditinjau". |
| Catat pengukuran | `96:563` | Ikuti screenshot. |
| Hasil pertumbuhan | `96:701` | Ikuti screenshot. |
| Daftar balita | `128:2` | Judul "Balita", subjudul "115 sasaran · September 2026". Kolom cari. Chip penyaring (Semua 115, Belum 23, Perhatian 8). Daftar: avatar inisial, nama, umur · nama ibu, chip status. Tombol utama "Tambah balita". |
| Detail balita | `128:168` | Judul = nama anak, subjudul umur · jenis kelamin. Chip status. Kartu pengukuran terakhir: dua angka besar, tombol "Lihat hasil pertumbuhan". Kartu orang tua. Riwayat 3 pengukuran (tanggal, berat · tinggi). |
| Tambah balita | `128:314` | Isian: nama anak, tanggal lahir, jenis kelamin (chip Perempuan/Laki-laki), nama ibu/pengasuh, dusun. Kotak persetujuan orang tua. Tombol "Simpan data balita". Undangan akun orang tua dibuat sebagai langkah terpisah setelah data tersimpan. |
| Tindak lanjut | `128:452` | Kartu peringatan "Langkah pertama: Ukur ulang BB dan TB bersama bidan". Pilihan pendampingan (radio): Konseling gizi, Konsultasi bidan, Rujukan ke fasilitas kesehatan. Isian tanggal kontrol dan penanggung jawab. Tombol "Simpan tindak lanjut". |
| Pindai QR | `128:591` | Area kamera gelap dengan bingkai QR. Kolom kode manual + tombol "Cari". Kartu hasil (nama, umur, ibu) dengan dua tombol: "Catat ukur" (utama), "Detail" (sekunder). |

> Layar kader lain (Alur layanan, Ringkasan AI, Laporan bulanan, MBG dan kehadiran, Pusat notifikasi, Sinkronisasi, Backup, Profil kader) dan layar orang tua lain (Artikel, Resep, Menu personal, Chatbot, Chat kader) **belum disederhanakan**. Kerjakan terakhir dan ikuti aturan di bagian 1.

## 6. Aturan label status balita

| Label | Warna |
|---|---|
| Normal | `tint` / `primary` |
| Perhatian / Perlu perhatian / Cek | `warning-bg` / `warning-text` |
| Risiko tinggi | `danger-bg` / `danger-text` |
| Belum diukur | `neutral-bg` / `muted` |

Label program MBG bukan status gizi. Jangan digabung dengan label di atas.

