package com.example.dedis.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dedis.data.network.ApiClient
import com.example.dedis.data.repository.BalitaRepositoryImpl
import com.example.dedis.data.repository.PengukuranRepositoryImpl
import com.example.dedis.util.SessionManager
import com.example.dedis.util.SplashDestination

object DedisDestinations {
    // Alur Pembuka & Masuk
    const val PEMBUKA = "pembuka"
    const val KENALI = "kenali"
    const val ROLE_SELECTION = "role_selection"
    const val MASUK_ORANG_TUA = "masuk_orang_tua"

    // Alur Orang Tua (DESIGN.md Bagian 5)
    const val BERANDA = "beranda"
    const val JADWAL = "jadwal"
    const val EDUKASI = "edukasi"
    const val PESAN = "pesan"
    const val PROFIL = "profil"
    const val DETAIL_BALITA = "detail_balita"
    const val IMUNISASI = "imunisasi"

    // Sub-layar Profil Orang Tua
    const val UBAH_PROFIL = "ubah_profil"
    const val UBAH_KATA_SANDI = "ubah_kata_sandi"
    const val NOTIFIKASI = "notifikasi"
    const val QR_ANAK = "qr_anak"
    const val TENTANG = "tentang"

    // Alur Kader (DESIGN.md Bagian 5)
    const val KADER_BERANDA = "kader_beranda"
    const val KADER_DAFTAR_BALITA = "kader_daftar_balita"
    const val KADER_DETAIL_BALITA = "kader_detail_balita/{balitaId}"
    fun kaderDetailBalita(balitaId: Long) = "kader_detail_balita/$balitaId"
    const val KADER_FORMULIR = "kader_formulir"
    const val KADER_PENGUKURAN = "kader_pengukuran?balitaId={balitaId}"
    fun kaderPengukuran(balitaId: Long = 1L) = "kader_pengukuran?balitaId=$balitaId"
    const val KADER_HASIL_PERTUMBUHAN = "kader_hasil_pertumbuhan"
    const val KADER_TINDAK_LANJUT = "kader_tindak_lanjut"
    const val KADER_PINDAI_QR = "kader_pindai_qr"
    const val KADER_LAPORAN = "kader_laporan"
    const val KADER_PROFIL = "kader_profil"
    const val KADER_INBOX = "kader_inbox"
    const val KADER_CHAT = "kader_chat/{partnerName}"
    fun kaderChat(partnerName: String) = "kader_chat/$partnerName"
}

@Composable
fun DedisNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = DedisDestinations.PEMBUKA
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val balitaRepository = remember { BalitaRepositoryImpl(ApiClient.getBalitaApiService(context)) }
    val pengukuranRepository = remember { PengukuranRepositoryImpl(ApiClient.getBalitaApiService(context)) }

    // Fungsi logout terpusat untuk seluruh aplikasi
    val performLogout: () -> Unit = {
        sessionManager.logout()
        navController.navigate(DedisDestinations.ROLE_SELECTION) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Pembuka & Autentikasi
        composable(DedisDestinations.PEMBUKA) {
            PembukaDedisScreen(
                onNavigateNext = { destination ->
                    when (destination) {
                        SplashDestination.BERANDA_ORANG_TUA -> {
                            navController.navigate(DedisDestinations.BERANDA) {
                                popUpTo(DedisDestinations.PEMBUKA) { inclusive = true }
                            }
                        }
                        SplashDestination.BERANDA_KADER -> {
                            navController.navigate(DedisDestinations.KADER_BERANDA) {
                                popUpTo(DedisDestinations.PEMBUKA) { inclusive = true }
                            }
                        }
                        SplashDestination.ONBOARDING -> {
                            navController.navigate(DedisDestinations.KENALI) {
                                popUpTo(DedisDestinations.PEMBUKA) { inclusive = true }
                            }
                        }
                        SplashDestination.ROLE_SELECTION -> {
                            navController.navigate(DedisDestinations.ROLE_SELECTION) {
                                popUpTo(DedisDestinations.PEMBUKA) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(DedisDestinations.KENALI) {
            KenaliDedisScreen(
                onNavigateToMasukDaftar = {
                    sessionManager.setOnboardingCompleted()
                    navController.navigate(DedisDestinations.ROLE_SELECTION) {
                        popUpTo(DedisDestinations.KENALI) { inclusive = true }
                    }
                }
            )
        }

        composable(DedisDestinations.ROLE_SELECTION) {
            RoleSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSelectOrangTua = { phone, name ->
                    // Simpan sesi orang tua (token sesi aman) & langsung masuk ke Beranda (1 langkah)
                    val token = "dedis-orang_tua-" + System.currentTimeMillis()
                    sessionManager.saveSession(
                        token = token,
                        role = SessionManager.ROLE_ORANG_TUA,
                        userName = name,
                        phone = phone
                    )
                    navController.navigate(DedisDestinations.BERANDA) {
                        popUpTo(DedisDestinations.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onSelectKader = { idOrPhone, name ->
                    // Simpan sesi kader & langsung masuk ke Beranda Kader
                    val token = "dedis-kader-" + System.currentTimeMillis()
                    sessionManager.saveSession(
                        token = token,
                        role = SessionManager.ROLE_KADER,
                        userName = name,
                        phone = idOrPhone
                    )
                    navController.navigate(DedisDestinations.KADER_BERANDA) {
                        popUpTo(DedisDestinations.ROLE_SELECTION) { inclusive = true }
                    }
                }
            )
        }

        composable(DedisDestinations.MASUK_ORANG_TUA) {
            MasukOrangTuaScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBeranda = {
                    val token = "dedis-orang_tua-" + System.currentTimeMillis()
                    sessionManager.saveSession(
                        token = token,
                        role = SessionManager.ROLE_ORANG_TUA,
                        userName = "Nur Aini"
                    )
                    navController.navigate(DedisDestinations.BERANDA) {
                        popUpTo(DedisDestinations.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onNavigateToDaftar = {
                    navController.navigate(DedisDestinations.ROLE_SELECTION)
                }
            )
        }

        // ==========================================
        // ALUR ORANG TUA
        // ==========================================
        composable(DedisDestinations.BERANDA) {
            val parentName = sessionManager.getUserName().ifBlank { "Ibu Nur Aini" }
            OrangTuaBerandaScreen(
                parentName = parentName,
                onNavigateToRoute = { route -> navController.navigate(route) },
                onNavigateToDetailBalita = { navController.navigate(DedisDestinations.DETAIL_BALITA) },
                onNavigateToImunisasi = { navController.navigate(DedisDestinations.IMUNISASI) }
            )
        }

        composable(DedisDestinations.DETAIL_BALITA) {
            OrangTuaDetailBalitaScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(DedisDestinations.IMUNISASI) {
            OrangTuaImunisasiScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToJadwal = { navController.navigate(DedisDestinations.JADWAL) },
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(DedisDestinations.JADWAL) {
            OrangTuaJadwalScreen(
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(DedisDestinations.EDUKASI) {
            OrangTuaEdukasiScreen(
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(DedisDestinations.PESAN) {
            OrangTuaPesanScreen(
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(DedisDestinations.PROFIL) {
            val currentUserName = sessionManager.getUserName().ifBlank { "Nur Aini" }
            OrangTuaProfilScreen(
                userName = currentUserName,
                onNavigateToRoute = { route -> navController.navigate(route) },
                onNavigateToUbahProfil = { navController.navigate(DedisDestinations.UBAH_PROFIL) },
                onNavigateToKataSandi = { navController.navigate(DedisDestinations.UBAH_KATA_SANDI) },
                onNavigateToNotifikasi = { navController.navigate(DedisDestinations.NOTIFIKASI) },
                onNavigateToQrAnak = { navController.navigate(DedisDestinations.QR_ANAK) },
                onNavigateToTentang = { navController.navigate(DedisDestinations.TENTANG) },
                onLogout = performLogout
            )
        }

        // Sub-layar Profil Orang Tua
        composable(DedisDestinations.UBAH_PROFIL) {
            UbahProfilScreen(
                onNavigateBack = { navController.popBackStack() },
                onSimpan = { _, _, _ -> navController.popBackStack() }
            )
        }

        composable(DedisDestinations.UBAH_KATA_SANDI) {
            UbahKataSandiScreen(
                onNavigateBack = { navController.popBackStack() },
                onSimpan = { _, _ -> navController.popBackStack() }
            )
        }

        composable(DedisDestinations.NOTIFIKASI) {
            NotifikasiScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(DedisDestinations.QR_ANAK) {
            QrAnakScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(DedisDestinations.TENTANG) {
            TentangScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==========================================
        // ALUR KADER
        // ==========================================
        composable(DedisDestinations.KADER_BERANDA) {
            val kaderName = sessionManager.getUserName().ifBlank { "Sari" }
            KaderBerandaScreen(
                kaderName = kaderName,
                onNavigateToRoute = { route -> navController.navigate(route) },
                onNavigateToDaftarBalita = { navController.navigate(DedisDestinations.KADER_DAFTAR_BALITA) },
                onNavigateToFormulir = { navController.navigate(DedisDestinations.KADER_FORMULIR) },
                onNavigateToPengukuran = { navController.navigate(DedisDestinations.KADER_PENGUKURAN) },
                onNavigateToPindaiQr = { navController.navigate(DedisDestinations.KADER_PINDAI_QR) },
                onNavigateToDetailBalita = { navController.navigate(DedisDestinations.kaderDetailBalita(0L)) },
                onNavigateToInbox = { navController.navigate(DedisDestinations.KADER_INBOX) }
            )
        }

        composable(DedisDestinations.KADER_DAFTAR_BALITA) {
            KaderDaftarBalitaScreen(
                balitaRepository = balitaRepository,
                onNavigateToRoute = { route -> navController.navigate(route) },
                onNavigateToDetail = { balitaId ->
                    navController.navigate(DedisDestinations.kaderDetailBalita(balitaId))
                },
                onNavigateToFormulir = { navController.navigate(DedisDestinations.KADER_FORMULIR) }
            )
        }

        composable(
            route = DedisDestinations.KADER_DETAIL_BALITA,
            arguments = listOf(navArgument("balitaId") {
                type = NavType.LongType
                defaultValue = 1L
            })
        ) { backStackEntry ->
            val balitaId = backStackEntry.arguments?.getLong("balitaId") ?: 1L
            KaderDetailBalitaScreen(
                balitaId = balitaId,
                balitaRepository = balitaRepository,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) },
                onNavigateToCatatPengukuran = { id -> navController.navigate(DedisDestinations.kaderPengukuran(id)) },
                onNavigateToHasilPertumbuhan = { navController.navigate(DedisDestinations.KADER_HASIL_PERTUMBUHAN) },
                onNavigateToTindakLanjut = { navController.navigate(DedisDestinations.KADER_TINDAK_LANJUT) }
            )
        }

        composable(DedisDestinations.KADER_FORMULIR) {
            KaderFormulirBalitaScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(
            route = DedisDestinations.KADER_PENGUKURAN,
            arguments = listOf(navArgument("balitaId") {
                type = NavType.LongType
                defaultValue = 1L
            })
        ) { backStackEntry ->
            val balitaId = backStackEntry.arguments?.getLong("balitaId") ?: 1L
            KaderPengukuranBulananScreen(
                balitaId = balitaId,
                balitaRepository = balitaRepository,
                pengukuranRepository = pengukuranRepository,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHasil = { navController.navigate(DedisDestinations.KADER_HASIL_PERTUMBUHAN) },
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(DedisDestinations.KADER_HASIL_PERTUMBUHAN) {
            KaderHasilPertumbuhanScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTindakLanjut = { navController.navigate(DedisDestinations.KADER_TINDAK_LANJUT) },
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(DedisDestinations.KADER_TINDAK_LANJUT) {
            KaderTindakLanjutScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(DedisDestinations.KADER_PINDAI_QR) {
            KaderPindaiQrScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCatatUkur = { navController.navigate(DedisDestinations.kaderPengukuran(1L)) },
                onNavigateToDetail = { navController.navigate(DedisDestinations.kaderDetailBalita(0L)) }
            )
        }

        composable(DedisDestinations.KADER_LAPORAN) {
            KaderLaporanScreen(
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        composable(DedisDestinations.KADER_PROFIL) {
            KaderProfilScreen(
                onNavigateToRoute = { route -> navController.navigate(route) },
                onLogout = performLogout
            )
        }

        // Pesan Masuk Kader (dibuka dari ikon pesan di header Beranda)
        composable(DedisDestinations.KADER_INBOX) {
            KaderInboxScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenConversation = { _, partnerName ->
                    navController.navigate(DedisDestinations.kaderChat(partnerName))
                }
            )
        }

        composable(DedisDestinations.KADER_CHAT) { backStack ->
            val partnerName = backStack.arguments?.getString("partnerName") ?: "Orang Tua"
            KaderChatScreen(
                partnerName = partnerName,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
