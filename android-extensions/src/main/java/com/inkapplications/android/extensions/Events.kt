package com.inkapplications.android.extensions

inline fun stopPropagation(action: () -> Unit = {}): Boolean {
    action()
    return true
}

inline fun continuePropagation(action: () -> Unit = {}): Boolean {
    action()
    return false
}