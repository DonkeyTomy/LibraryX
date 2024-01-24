package com.tomy.compose.components.monitor

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.core.content.getSystemService
import com.tomy.compose.components.monitor.base.ItemState
import kotlinx.coroutines.channels.ProducerScope

class SoundMonitor(context: Context): StateOnlyBroadcastMonitor(context) {

    override val mAction: List<String>
        get() = arrayListOf(AudioManager.RINGER_MODE_CHANGED_ACTION)

    override suspend fun performAction(
        action: String,
        producerScope: ProducerScope<ItemState>,
        intent: Intent
    ) {
        assert(isIconListInit() && mIconList.size == 2)
        val mode = intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL)
        producerScope.send(ItemState.StatusOnly(convertState(mode)))
    }

    override fun convertState(originState: Int): Int {
        val icon = if (originState == AudioManager.RINGER_MODE_NORMAL) {
            if (mIconList[1] > 0) {
                mIconList[1]
            } else {
                1
            }
        } else {
            if (mIconList[0] > 0) {
                mIconList[0]
            } else {
                0
            }
        }
        return icon
    }

    override suspend fun sendInitState(producerScope: ProducerScope<ItemState>) {
        mContext.getSystemService<AudioManager>()?.apply {
            producerScope.send(ItemState.StatusOnly(convertState(ringerMode)))
        }
    }
}