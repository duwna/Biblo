package com.duwna.biblo.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

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

    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver { onNotify(it) })
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
    abstract val message: String

    data class TextMessage(override val message: String) : Notify()

    data class InternetError(
        override val message: String = "Отсутствует подключение к интернету"
    ) : Notify()

    data class Error(
        override val message: String = "Возникла ошибка загрузки данных"
    ) : Notify()

    data class ActionMessage(
        override val message: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)
    ) : Notify()
}