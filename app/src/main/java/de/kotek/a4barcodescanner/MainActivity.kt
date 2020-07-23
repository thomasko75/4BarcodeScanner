package de.kotek.a4barcodescanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.textclassifier.TextLinks
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import kotlinx.android.synthetic.main.activity_main.*

private lateinit var detector: BarcodeDetector
private lateinit var cameraSource: CameraSource

class MainActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_Scan.setOnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    Toast.makeText(this@MainActivity, "Down", Toast.LENGTH_SHORT)

                }
                MotionEvent.ACTION_UP -> {
                    Toast.makeText(this@MainActivity, "up", Toast.LENGTH_SHORT)

                }
                MotionEvent.ACTION_CANCEL -> {
                    Toast.makeText(this@MainActivity, "Chanceled", Toast.LENGTH_SHORT)
                }

                else -> {
                    Toast.makeText(this@MainActivity, "else", Toast.LENGTH_SHORT)

                }

            }
            true
        }

        detector = BarcodeDetector.Builder(this@MainActivity).build()
        detector.setProcessor(object :Detector.Processor<Barcode>{
            override fun release() {  }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                val barcodes = detections?.detectedItems
                if(barcodes!!.size() > 0) {
                    textScanResult.post {
                        textScanResult.text = barcodes.valueAt(0).displayValue
                    }
                }
            }
        })

        cameraSource = CameraSource.Builder(this@MainActivity, detector).setRequestedPreviewSize(1024, 786).setRequestedFps(25f).setAutoFocusEnabled(true).build()
        cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {}

            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {}

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraSource.start(holder)
                } else {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA),123)

                }
            }

        })
    }

    @SuppressLint("ShowToast")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    cameraSource.start(cameraSurfaceView.holder)
                } else {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA),123)
                }


            } else {
                Toast.makeText(this, "Failed!", Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }

    private fun btn_Hold() {

    }

}


