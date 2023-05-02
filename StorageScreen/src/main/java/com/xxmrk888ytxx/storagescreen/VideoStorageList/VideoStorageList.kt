package com.xxmrk888ytxx.storagescreen.VideoStorageList

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.storagescreen.MediaFileItem.MediaFileItem
import com.xxmrk888ytxx.storagescreen.MediaFileItem.models.MediaFileButton
import com.xxmrk888ytxx.storagescreen.R
import com.xxmrk888ytxx.storagescreen.Stub

@SuppressLint("ResourceType")
@Composable
fun VideoStorageList(videoStorageListViewModel: VideoStorageListViewModel) {
    val videoFiles by videoStorageListViewModel.videoFiles.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = videoFiles, block = {
        Log.d("MyLog",videoFiles.toString())
    })


    if(videoFiles.isEmpty()) {
        Stub(text = stringResource(R.string.Videos_missing))
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(videoFiles, key = { it.id }) {
                val buttons = remember {
                    listOf(
                        MediaFileButton(
                            icon = R.drawable.baseline_delete_24,
                            onClick = { videoStorageListViewModel.removeFile(it.id) }
                        )
                    )
                }

                MediaFileItem(
                    duration = it.duration,
                    created = it.created,
                    buttons = buttons
                )
            }
        }
    }
}