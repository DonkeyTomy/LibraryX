package com.tomy.component.worker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit
import kotlin.contracts.ContractBuilder

/**@author Tomy
 * Created by Tomy on 2023/8/23.
 */

/**
 * @receiver Context
 * @param constraints Constraints [ContractBuilder]
 * @param tag String
 * @return WorkRequest
 */
inline fun <reified T: CoroutineWorker> Context.enqueueOneTimeCoroutineWork(
    constraints: Constraints,
    tag: String = T::class.java.simpleName,
    existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.KEEP
): WorkRequest {
    val request = OneTimeWorkRequestBuilder<T>()
        .setConstraints(constraints)
        .addTag(tag)
        .build()
    WorkManager.getInstance(this)
        .beginUniqueWork(tag, existingWorkPolicy, request)
        .enqueue()
    return request
}

/**
 * @receiver Context
 * @param constraints Constraints [ContractBuilder]
 * @param tag String
 * @return WorkRequest
 */
inline fun <reified T: CoroutineWorker> Context.enqueueRepeatCoroutineWork(
    repeatInterval: Long,
    timeUnit: TimeUnit,
    constraints: Constraints,
    tag: String = T::class.java.simpleName,
    existingWorkPolicy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP
): WorkRequest {
    val request = PeriodicWorkRequestBuilder<T>(repeatInterval, timeUnit)
        .setConstraints(constraints)
        .addTag(tag)
        .build()
    WorkManager.getInstance(this)
        .enqueueUniquePeriodicWork(tag, existingWorkPolicy, request)
    return request
}

inline fun <reified T: CoroutineWorker> Context.cancelCoroutineWork() {
    cancelWork(T::class.java.simpleName)
}

inline fun <reified T: CoroutineWorker> Context.getCoroutineWorkInfoLiveDataByTag(): LiveData<MutableList<WorkInfo>> {
    return WorkManager.getInstance(this)
        .getWorkInfosByTagLiveData(T::class.java.simpleName)
}
