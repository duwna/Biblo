package com.duwna.biblo.ui.base

import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

abstract class BaseViewModel<T : IViewModelState>(
    initState: T
) : ViewModel() {

    private val notifications = MutableLiveData<Event<Notify>>()

    val state: MutableLiveData<T> = MutableLiveData<T>().apply {
        value = initState
    }

    val currentState
        get() = state.value!!

    protected inline fun updateState(update: T.() -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState
    }

    protected inline fun postUpdateState(update: T.() -> T) {
        val updatedState: T = update(currentState)
        state.postValue(updatedState)
    }

    protected fun notify(content: Notify) {
        notifications.postValue(Event(content))
    }

    protected fun doAsync(block: suspend () -> Unit) {
        viewModelScope.launch(IO) {
            try {
                block()
            } catch (t: Throwable) {
                t.printStackTrace()
                notify(Notify.DataError)
            }
        }
    }

    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner,
            EventObserver { onNotify(it) })
    }

}

class Event<out E>(private val content: E) {
    var hasBeenHandled = false
    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }
}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) : Observer<Event<E>> {
    override fun onChanged(event: Event<E>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

sealed class Notify {
    data class TextMessage(val message: String) : Notify()
    data class MessageFromRes(@StringRes val resId: Int) : Notify()
    object DataError : Notify()
    object InternetError : Notify()
}