package com.inkapplications.android.extensions

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import kotlin.reflect.KClass

/**
 * Start an activity by class reference.
 */
fun <T: Activity> Activity.startActivity(target: KClass<T>) {
    Intent(this, target.java).run(this::startActivity)
}

/**
 * Sends lifecycle event logs to a logger.
 */
class LifecycleLogger(
    private val logger: (String) -> Unit
): Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {
        logger("${activity.javaClass.simpleName} -> Paused")
    }

    override fun onActivityStarted(activity: Activity) {
        logger("${activity.javaClass.simpleName} -> Started")
    }

    override fun onActivityDestroyed(activity: Activity) {
        logger("${activity.javaClass.simpleName} -> Destroyed")
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
        logger("${activity.javaClass.simpleName} -> Saved Instance")
    }

    override fun onActivityStopped(activity: Activity) {
        logger("${activity.javaClass.simpleName} -> Stopped")
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        logger("${activity.javaClass.simpleName} -> Created")
    }

    override fun onActivityResumed(activity: Activity) {
        logger("${activity.javaClass.simpleName} -> Resumed")
    }
}
