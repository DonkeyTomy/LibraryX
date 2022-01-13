package com.tomy.lib.lifecycle.data

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**@author Tomy
 * Created by Tomy on 2022/1/13.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContent(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            return content
        }
    }

    fun peekContent() = content
}

typealias MutableEvent<T> = MutableLiveData<Event<T>>

typealias EventLiveData<T> = LiveData<Event<T>>

@MainThread
inline fun <T> EventLiveData<T>.observeEvent(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit): Observer<Event<T>> {
    val wrappedObserved = Observer<Event<T>> {
        it.getContent()?.let { data ->
            onChanged(data)
        }
    }
    observe(owner, wrappedObserved)
    return wrappedObserved
}