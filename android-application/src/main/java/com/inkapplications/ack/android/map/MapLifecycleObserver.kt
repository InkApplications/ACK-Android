package com.inkapplications.ack.android.map

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.mapbox.mapboxsdk.maps.MapView

/**
 * Implementing the lifecycle observer MapBox was too lazy to.
 */
class MapLifecycleObserver(
    private val view: MapView
): LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        view.onStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        view.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        view.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        view.onStop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        view.onDestroy()
    }
}

/**
 * Get the lifecycle observer for a map.
 */
val MapView.lifecycleObserver get() = MapLifecycleObserver(this)
