package com.findhiddencamera.spycameralocator.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object AppPermissionHelper {

    private const val PREFS_NAME = "permission_request_state"

    fun homeRequiredPermissions(): Array<String> {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
        return permissions.toTypedArray()
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun missingPermissions(context: Context, permissions: Array<String>): List<String> {
        return permissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestHomePermissionsIfNeeded(
        activity: Activity,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        val requiredPermissions = homeRequiredPermissions()
        if (!hasPermissions(activity, requiredPermissions)) {
            requestPermissionsOrOpenSettings(activity, requiredPermissions, launcher)
        }
    }

    fun requestPermissionsOrOpenSettings(
        activity: Activity,
        permissions: Array<String>,
        launcher: ActivityResultLauncher<Array<String>>
    ): Boolean {
        val missing = missingPermissions(activity, permissions)
        if (missing.isEmpty()) return true

        val canRequest = missing.any { permission -> canRequestAgain(activity, permission) }
        if (canRequest) {
            markPermissionsRequested(activity, missing)
            launcher.launch(missing.toTypedArray())
        } else {
            openAppPermissionSettings(activity)
        }
        return false
    }

    fun shouldOpenSettings(activity: Activity, permissions: Array<String>): Boolean {
        val missing = missingPermissions(activity, permissions)
        if (missing.isEmpty()) return false
        return missing.none { permission -> canRequestAgain(activity, permission) }
    }

    fun openAppPermissionSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(intent)
    }

    private fun canRequestAgain(activity: Activity, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return !hasRequestedBefore(activity, permission) ||
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    private fun markPermissionsRequested(context: Context, permissions: List<String>) {
        if (permissions.isEmpty()) return
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        permissions.forEach { permission ->
            editor.putBoolean(permission, true)
        }
        editor.apply()
    }

    private fun hasRequestedBefore(context: Context, permission: String): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(permission, false)
    }
}
