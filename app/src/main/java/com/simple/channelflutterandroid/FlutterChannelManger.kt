package com.simple.channelflutterandroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import io.flutter.plugin.common.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author hych
 * @date 2018/11/14 18:40
 */

class MethodChannelManager(private val cxt: Context, private val messenger: BinaryMessenger) {
    companion object {
        const val METHOD_NAME = "com.simple.channelflutterandroid/method"
        const val TOAST = "toast"
        const val SING = "sing"
    }

    fun registerAndBackFlutter2Android() {
        MethodChannel(messenger, METHOD_NAME).setMethodCallHandler { methodCall, result ->
            when (methodCall.method) {
                TOAST -> {
                    val msg = methodCall.argument<String>("msg")
                    println("onMethodCall Toast $msg")
                    Toast.makeText(cxt, msg, Toast.LENGTH_SHORT).show()
                    result.success("native android toast success")
                }
                SING -> {
                }
                else -> {
                }
            }
        }
    }
}

class EventChannelManager(private val context: Context, private val messenger: BinaryMessenger) {
    companion object {
        private const val EVENT_NAME = "com.simple.channelflutterandroid/event"
        private const val NET = "net"
    }

    fun sendAndroid2Flutter() {

        var netReceiver: BroadcastReceiver

        fun isNetworkConnected(): Boolean {
            val connectivityManager: ConnectivityManager? =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager?.activeNetworkInfo?.state == NetworkInfo.State.CONNECTED
        }

        EventChannel(messenger, EVENT_NAME).setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(o: Any?, eventSink: EventChannel.EventSink) {
                println("EventChannelManager net ${o.toString()}")

                when (o) {
                    NET -> {
                        netReceiver = object : BroadcastReceiver() {
                            override fun onReceive(context: Context?, intent: Intent?) {
                                val str: String = if (isNetworkConnected()) "网络可用" else "网络不可用"
                                eventSink.success(str)
                            }
                        }
                        context.registerReceiver(netReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
                    }
                    else -> {
                    }
                }
            }

            override fun onCancel(o: Any?) {
            }
        })
    }
}


class BasicMessageChannelManager(private val messenger: BinaryMessenger) {

    companion object {
        private const val BASIC_NAME = "com.simple.channelflutterandroid/basic"
        private const val BASIC_BINARY_NAME = "com.simple.channelflutterandroid/basic/binary"
    }

    fun sendAndroid2Flutter() {
        val basicMessageChannel = BasicMessageChannel<String>(messenger, BASIC_NAME, StringCodec.INSTANCE)
        println("BasicMessageChannelManager 方式一 send")
        basicMessageChannel.send("发消息给你")
    }

    fun registerFlutter2Android() {
        val basicMessageChannel = BasicMessageChannel<String>(messenger, BASIC_NAME, StringCodec.INSTANCE)
        basicMessageChannel.setMessageHandler { o, reply ->
            println("BasicMessageChannelManager 方式一 setMessageHandler $o")
            reply.reply("回消息给你")
        }
    }

    fun sendAndroid2FlutterBinary() {
        /**
         * 方式二：使用BinaryMessenger发送接收
         */
        val buffer = ByteBuffer.allocate(256)
        buffer.putDouble(3.14)
        buffer.putInt(123456789)

        println("BasicMessageChannelManager 方式二 send")
        messenger.send(BASIC_BINARY_NAME, buffer)
    }

    fun registerFlutter2AndroidBinary() {
        val buffer = ByteBuffer.allocate(256)
        buffer.putDouble(3.14)
        buffer.putInt(123456789)

        messenger.setMessageHandler(BASIC_BINARY_NAME) { byteBuffer, binaryReply ->
            byteBuffer.order(ByteOrder.nativeOrder())
            val double = byteBuffer.double
            val int = byteBuffer.int
            println("BasicMessageChannelManager 方式二 setMessageHandler $double  $int")
            binaryReply.reply(buffer)
        }
    }
}