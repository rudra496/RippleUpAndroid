package com.yft.rippleup.ui.screens.scan

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

/**
 * Live camera preview wired to an ML Kit barcode analyser. Calls [onScanned]
 * with the first detected QR/barcode value and then the caller can disable
 * scanning via [enabled] to avoid duplicate logs.
 *
 * Uses CameraX directly (no third-party wrapper) for a small dependency surface.
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onScanned: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_CODE_128)
                .build()
        )
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val providerFuture = ProcessCameraProvider.getInstance(ctx)
            providerFuture.addListener({
                val provider = providerFuture.get()
                bindCamera(
                    context = ctx,
                    provider = provider,
                    previewView = previewView,
                    lifecycleOwner = lifecycleOwner,
                    scanner = scanner,
                    executor = executor,
                    enabledProvider = { enabled },
                    onScanned = onScanned,
                )
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
    )
}

private fun bindCamera(
    context: Context,
    provider: ProcessCameraProvider,
    previewView: PreviewView,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    executor: java.util.concurrent.Executor,
    enabledProvider: () -> Boolean,
    onScanned: (String) -> Unit,
) {
    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider(previewView.surfaceProvider)
    }
    val analysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(executor) { imageProxy ->
                if (!enabledProvider()) {
                    imageProxy.close()
                    return@setAnalyzer
                }
                processImage(scanner, imageProxy, onScanned)
            }
        }

    try {
        provider.unbindAll()
        provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            analysis,
        )
    } catch (e: Exception) {
        // Camera init can fail on emulators / no-camera devices — presets still work.
    }
}

private fun processImage(
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onScanned: (String) -> Unit,
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }
    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            barcodes.firstOrNull()?.rawValue?.let { value ->
                onScanned(value)
            }
        }
        .addOnCompleteListener { imageProxy.close() }
}
