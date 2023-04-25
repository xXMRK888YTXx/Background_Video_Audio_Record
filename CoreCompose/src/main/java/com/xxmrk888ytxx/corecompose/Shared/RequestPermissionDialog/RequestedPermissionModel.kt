package com.xxmrk888ytxx.corecompose.Shared.RequestPermissionDialog

import androidx.annotation.IdRes
import java.io.FileDescriptor

data class RequestedPermissionModel(
    @IdRes val description:Int,
    val isGranted:Boolean,
    val onRequest:() -> Unit
)