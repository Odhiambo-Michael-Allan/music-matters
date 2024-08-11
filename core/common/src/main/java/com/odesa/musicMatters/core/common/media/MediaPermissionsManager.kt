package com.odesa.musicMatters.core.common.media

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

object MediaPermissionsManager {

    private val _readExternalStoragePermissionGranted = MutableStateFlow( false )
    val readExternalStoragePermissionGranted = _readExternalStoragePermissionGranted.asStateFlow()

    private val _readMediaAudioPermissionGranted = MutableStateFlow( false )
    val readMediaAudioPermissionGranted = _readMediaAudioPermissionGranted.asStateFlow()

    private val _postNotificationPermissionGranted = MutableStateFlow( false )
    val postNotificationPermissionGranted = _postNotificationPermissionGranted

    private val _hasAllRequiredPermissions = MutableStateFlow( false )
    val hasAllRequiredPermissions: StateFlow<Boolean> = _hasAllRequiredPermissions.asStateFlow()

    private val hasAllRequiredPermissionsChangeListeners: MutableList<(Boolean)->Unit> =
        mutableListOf()


    fun readExternalStoragePermissionGranted( isGranted: Boolean, context: Context ) {
        Timber.tag( TAG ).d( "READ EXTERNAL STORAGE PERMISSION GRANTED: $isGranted" )
        _readExternalStoragePermissionGranted.value = isGranted
        _hasAllRequiredPermissions.value = hasAllRequiredPermissions( context )
        Timber.tag( TAG ).d( "HAS ALL REQUIRED PERMISSIONS: ${hasAllRequiredPermissions( context ) }" )
        notifyListeners( isGranted )
    }

    fun postNotificationPermissionGranted( isGranted: Boolean, context: Context ) {
        _postNotificationPermissionGranted.value = isGranted
        _hasAllRequiredPermissions.value = hasAllRequiredPermissions( context )
        notifyListeners( isGranted )
    }

    fun readMediaAudioPermissionGranted( isGranted: Boolean, context: Context ) {
        _readMediaAudioPermissionGranted.value = isGranted
        _hasAllRequiredPermissions.value = hasAllRequiredPermissions( context )
        notifyListeners( isGranted )
    }

    fun checkForPermissions( context: Context ) {
        _readExternalStoragePermissionGranted.value = hasReadExternalStoragePermission( context )
        _readMediaAudioPermissionGranted.value = hasReadMediaAudioPermission( context )
        _postNotificationPermissionGranted.value = hasPostNotificationPermissionGranted( context )
        _hasAllRequiredPermissions.value = hasAllRequiredPermissions( context )
        notifyListeners( hasAllRequiredPermissions( context ) )
    }

    private fun notifyListeners( hasAllRequiredPermissions: Boolean ) {
        hasAllRequiredPermissionsChangeListeners.forEach { it( hasAllRequiredPermissions ) }
    }

    private fun hasReadExternalStoragePermission(
        context: Context
    ) = hasPermission( context, Manifest.permission.READ_EXTERNAL_STORAGE )

    private fun hasReadMediaAudioPermission( context: Context ): Boolean {
        return if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU )
            hasPermission( context, Manifest.permission.READ_MEDIA_AUDIO )
        else false
    }

    private fun hasPostNotificationPermissionGranted( context: Context ): Boolean {
        return if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU )
            hasPermission( context, Manifest.permission.POST_NOTIFICATIONS )
        else false
    }

    private fun hasAllRequiredPermissions( context: Context ): Boolean {
        getRequiredPermissions().forEach {
            if ( !hasPermission( context, it ) ) {
                return false
            }
        }
        return true
    }

    private fun hasPermission( context: Context, permission: String ) =
        context.checkSelfPermission(
            permission
        ) == PackageManager.PERMISSION_GRANTED

    private fun getRequiredPermissions(): List<String> {
        val requiredPermissions = mutableListOf<String>()
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {
            requiredPermissions.add( Manifest.permission.READ_MEDIA_AUDIO )
            requiredPermissions.add( Manifest.permission.POST_NOTIFICATIONS )
        } else {
            requiredPermissions.add( Manifest.permission.READ_EXTERNAL_STORAGE )
        }
        return requiredPermissions
    }

}

private const val TAG = "MEDIA PERMISSIONS MANAGER TAG"
