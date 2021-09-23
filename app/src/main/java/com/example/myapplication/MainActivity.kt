package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.PermissionRequest.RESOURCE_VIDEO_CAPTURE
import android.widget.Toast
import com.example.myapplication.MainActivity.Companion.TAG


class MainActivity : AppCompatActivity(), OnPermissionRequested {

    companion object {
        const val CAMERA_WEB_PERMISSION_ID = 411
        const val URL_TEST = "https://webcamtests.com/"
        const val TAG = "MainActivity"
    }

    private lateinit var webView: WebView
    private lateinit var chromeClient: MyWebViewClient
    private var permissionRequest: PermissionRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        chromeClient = MyWebViewClient(this@MainActivity)
        findViewById<WebView>(R.id.webView).apply {
            webChromeClient = chromeClient
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
        }.also {
            it.loadUrl(URL_TEST)
        }.also {
            webView = it
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_WEB_PERMISSION_ID -> {
                val isGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                sendPermissionResultToWebView(isGranted)
            }
            else -> Unit
        }
    }
    override fun onPermissionWebViewRequested(request: PermissionRequest) {
        this.permissionRequest = request
        when(request.resources[0]){
            RESOURCE_VIDEO_CAPTURE -> this.requestPermissions(
                 arrayOf(Manifest.permission.CAMERA),
                CAMERA_WEB_PERMISSION_ID
            )
            else -> Unit
        } }

    private fun sendPermissionResultToWebView(isGranted: Boolean) {
        // ToDo...
        Toast.makeText(
            applicationContext, "permission granted: $isGranted", Toast.LENGTH_SHORT
        ).show()
        permissionRequest?.let { chromeClient.setPermissions(it, isGranted) }
    }

}

interface OnPermissionRequested {
    fun onPermissionWebViewRequested(request: PermissionRequest)
}

class MyWebViewClient(private val listener: OnPermissionRequested): WebChromeClient() {

    fun setPermissions(request: PermissionRequest, isGranted: Boolean){
        if (isGranted) request.grant(request.resources)
        else request.deny()
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        Log.d(TAG, "onPermissionRequest: $request")
        request?.let { listener.onPermissionWebViewRequested(it) }
    }
}

