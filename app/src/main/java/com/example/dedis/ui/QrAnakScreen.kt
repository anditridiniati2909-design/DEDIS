package com.example.dedis.ui

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.dedis.R
import com.example.dedis.ui.components.DedisCard
import com.example.dedis.ui.components.DedisChoiceChip
import com.example.dedis.ui.components.DedisSecondaryButton
import com.example.dedis.ui.theme.DedisInk
import com.example.dedis.ui.theme.DedisMuted
import com.example.dedis.ui.theme.DedisPrimary
import com.example.dedis.ui.theme.DedisWhite
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Layar QR Anak.
 * Pilih anak lewat chip, tampilkan kode QR (format sama dengan KaderPindaiQrScreen).
 * Tombol "Simpan gambar" dan "Bagikan".
 *
 * Format kode: "DEDIS-{childId}-{registrationNumber}" (sama dengan backend showQr)
 */
@Composable
fun QrAnakScreen(
    modifier: Modifier = Modifier,
    children: List<Pair<Long, String>> = listOf(1L to "Aisyah Putri", 2L to "Nabil"),
    getQrCode: (childId: Long) -> String = { id -> "DEDIS-$id-${id.toString().padStart(4, '0')}" },
    onNavigateBack: () -> Unit = {}
) {
    var selectedChildId by remember { mutableStateOf(children.firstOrNull()?.first) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Generate QR whenever selected child changes
    LaunchedEffect(selectedChildId) {
        selectedChildId?.let { id ->
            val code = getQrCode(id)
            qrBitmap = withContext(Dispatchers.Default) {
                generateQrBitmap(code, 512)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().navigationBarsPadding(),
        containerColor = DedisWhite,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = "Kembali",
                        tint = DedisPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column {
                Text("QR Anak", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = DedisInk)
                Spacer(modifier = Modifier.height(2.dp))
                Text("Tunjukkan kode ini ke kader saat posyandu", fontSize = 13.sp, color = DedisMuted)
            }

            // Chip pilih anak
            if (children.size > 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    children.forEach { (id, name) ->
                        DedisChoiceChip(
                            text = name,
                            isSelected = selectedChildId == id,
                            onClick = { selectedChildId = id }
                        )
                    }
                }
            }

            // Kartu QR
            DedisCard(contentPadding = 24.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val bitmap = qrBitmap
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .size(240.dp)
                                .background(DedisWhite, RoundedCornerShape(12.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .background(DedisWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Memuat...", color = DedisMuted)
                        }
                    }

                    val selectedName = children.firstOrNull { it.first == selectedChildId }?.second
                    if (selectedName != null) {
                        Text(
                            text = selectedName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DedisInk
                        )
                    }
                    Text(
                        text = "ID: DEDIS-${selectedChildId ?: ""}-${selectedChildId?.toString()?.padStart(4, '0') ?: ""}",
                        fontSize = 12.sp,
                        color = DedisMuted
                    )
                }
            }

            // Tombol Simpan & Bagikan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DedisSecondaryButton(
                    text = "Simpan Gambar",
                    onClick = {
                        qrBitmap?.let { bmp ->
                            scope.launch {
                                val saved = saveQrImage(context, bmp, "QR_${selectedChildId}")
                                snackbarHostState.showSnackbar(
                                    if (saved) "Gambar QR disimpan ke Galeri" else "Gagal menyimpan gambar"
                                )
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                DedisSecondaryButton(
                    text = "Bagikan",
                    onClick = {
                        qrBitmap?.let { bmp ->
                            scope.launch {
                                shareQrImage(context, bmp)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/** Generate QR bitmap menggunakan ZXing (offline). */
private fun generateQrBitmap(content: String, size: Int): Bitmap {
    val hints = mapOf(EncodeHintType.MARGIN to 1)
    val bits = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bitmap
}

/** Simpan QR ke MediaStore / Galeri. */
private suspend fun saveQrImage(context: Context, bitmap: Bitmap, fileName: String): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "${fileName}.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DEDIS")
                }
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                } != null
            } else {
                val dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "DEDIS"
                ).also { it.mkdirs() }
                val file = File(dir, "${fileName}.png")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                true
            }
        } catch (e: Exception) { false }
    }
}

/** Bagikan QR ke aplikasi lain via share intent. */
private suspend fun shareQrImage(context: Context, bitmap: Bitmap) {
    withContext(Dispatchers.IO) {
        val cacheFile = File(context.cacheDir, "dedis_qr_share.png")
        FileOutputStream(cacheFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            cacheFile
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan QR Anak"))
    }
}

