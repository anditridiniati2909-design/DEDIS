<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

/**
 * Konten edukasi awal untuk fitur Edukasi DEDIS.
 * Ini data referensi. Dapat ditambah/diedit oleh kader_admin melalui UI.
 */
class EducationContentSeeder extends Seeder
{
    public function run(): void
    {
        $articles = [
            [
                'topic'          => 'stunting',
                'title'          => 'Kenali Stunting Sejak Dini',
                'summary'        => 'Stunting adalah kondisi gagal tumbuh akibat kekurangan gizi kronis. Pelajari tanda-tandanya.',
                'read_time_min'  => 4,
                'content'        => 'Stunting adalah kondisi di mana tinggi badan anak lebih pendek dari standar usianya...',
            ],
            [
                'topic'          => 'mpasi',
                'title'          => 'MPASI Bergizi untuk Si Kecil',
                'summary'        => 'Makanan Pendamping ASI yang tepat membantu tumbuh kembang optimal anak.',
                'read_time_min'  => 5,
                'content'        => 'MPASI diberikan mulai usia 6 bulan. Pastikan mengandung karbohidrat, protein, dan lemak...',
            ],
            [
                'topic'          => 'imunisasi',
                'title'          => 'Pentingnya Imunisasi Lengkap',
                'summary'        => 'Imunisasi melindungi anak dari penyakit berbahaya. Jangan lewatkan jadwalnya.',
                'read_time_min'  => 3,
                'content'        => 'Imunisasi adalah upaya memberikan kekebalan pada anak terhadap penyakit tertentu...',
            ],
            [
                'topic'          => 'stunting',
                'title'          => 'Cara Mencegah Stunting',
                'summary'        => 'Pencegahan dimulai dari 1000 Hari Pertama Kehidupan (HPK).',
                'read_time_min'  => 6,
                'content'        => '1000 Hari Pertama Kehidupan dimulai dari masa kehamilan hingga anak usia 2 tahun...',
            ],
            [
                'topic'          => 'mpasi',
                'title'          => 'Tekstur MPASI Sesuai Usia',
                'summary'        => 'Tekstur makanan harus disesuaikan dengan kemampuan menelan bayi.',
                'read_time_min'  => 4,
                'content'        => 'Bayi 6 bulan: bubur halus. Bayi 7-9 bulan: bubur saring. Bayi 10-12 bulan: makanan cincang...',
            ],
        ];

        foreach ($articles as $article) {
            DB::table('education_contents')->updateOrInsert(
                ['title' => $article['title']],
                $article + [
                    'is_published' => true,
                    'updated_at'   => now(),
                    'created_at'   => now(),
                ]
            );
        }

        $this->command->info('EducationContentSeeder: ' . count($articles) . ' artikel diimpor.');
    }
}

