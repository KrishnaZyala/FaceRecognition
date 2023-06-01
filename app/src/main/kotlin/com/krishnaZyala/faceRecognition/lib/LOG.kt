package com.krishnaZyala.faceRecognition.lib

import android.util.Log

object LOG {
    val debug get():Boolean = true
    fun stackTrace(index: Int): StackTraceElement = Thread.currentThread().stackTrace[index]
    fun msg(args: Array<out Any?>): String = buildString { append("\t");args.forEach { append("\n$it") } }
    val TAG
        get() = try {
            """${stackTrace(6).lineNumber} ${stackTrace(6).fileName} | ${
                stackTrace(6).className.substringAfter("$").substringBefore("$").substringBeforeLast("Kt")
            }.${stackTrace(6).methodName}()"""
        } catch (t: Throwable) {
            t.message
        }

    fun d(vararg args: Any?) = if (!debug) null else Log.d(TAG, msg(args))
    fun d(throwable: Throwable?, vararg args: Any?) = if (!debug) null
    else if (throwable != null) Log.d(TAG, msg(args), throwable) else Log.d(TAG, msg(args))

    fun e(vararg args: Any?) = if (!debug) null else Log.e(TAG, msg(args))
    fun e(throwable: Throwable?, vararg args: Any?) = if (!debug) null
    else if (throwable != null) Log.e(TAG, msg(args), throwable) else Log.e(TAG, msg(args))

    fun w(vararg args: Any?) = if (!debug) null else Log.w(TAG, msg(args))
    fun w(throwable: Throwable?, vararg args: Any?) = if (!debug) null
    else if (throwable != null) Log.w(TAG, msg(args), throwable) else Log.w(TAG, msg(args))

    fun i(vararg args: Any?) = if (!debug) null else Log.i(TAG, msg(args))
    fun i(throwable: Throwable?, vararg args: Any?) = if (!debug) null
    else if (throwable != null) Log.i(TAG, msg(args), throwable) else Log.i(TAG, msg(args))

    fun v(vararg args: Any?) = if (!debug) null else Log.v(TAG, msg(args))
    fun v(throwable: Throwable?, vararg args: Any?) = if (!debug) null else
        if (throwable != null) Log.v(TAG, msg(args), throwable) else Log.v(TAG, msg(args))
}
