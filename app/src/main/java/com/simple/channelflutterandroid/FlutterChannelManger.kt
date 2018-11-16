package com.simple.channelflutterandroid

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import io.flutter.plugin.common.*
import java.nio.ByteBuffer

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
        MethodChannel(messenger, METHOD_NAME, StandardMethodCodec.INSTANCE).setMethodCallHandler { methodCall, result ->
            when (methodCall.method) {
                TOAST -> {
                    val msg = methodCall.argument<String>("msg")
                    println("It is Native - onMethodCall Toast $msg")
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
        const val NET = "$EVENT_NAME/net"
        const val BACK = "$EVENT_NAME/back"
        const val DEFAULT = "$EVENT_NAME/default"
    }

    fun sendAndroid2FlutterNet() {

        var netReceiver: BroadcastReceiver? = null

        fun isNetworkConnected(): Boolean {
            val connectivityManager: ConnectivityManager? =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager?.activeNetworkInfo?.state == NetworkInfo.State.CONNECTED
        }

        EventChannel(messenger, NET).setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(o: Any?, eventSink: EventChannel.EventSink) {
                println("It is Native - EventChannelManager ${o.toString()}")

                /**
                 * 这种实现不太理想，一直创建BroadcastReceiver
                 */
                netReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        println("It is Native - netReceiver")
                        val str: String = if (isNetworkConnected()) "网络可用" else "网络不可用"
                        eventSink.success(str)
                    }
                }
                context.registerReceiver(netReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            }

            override fun onCancel(o: Any?) {
            }
        })
    }

    /**
     * 不能采取一直点击的方式向Flutter端发送消息，但Flutter端可以一直点击接收
     * 实现方式特殊，属于注册监听，然后再回传，
     */
    fun sendAndroid2FlutterBack() {
        println("It is Native - EventChannelManager")
        EventChannel(messenger, BACK).setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(p0: Any, eventSink: EventChannel.EventSink) {
                println("It is Native - EventChannelManager sendAndroid2FlutterBack $p0")
                eventSink.success("back")
            }

            override fun onCancel(p0: Any?) {
            }

        })
    }

    fun sendAndroid2FlutterDefault() {
        EventChannel(messenger, DEFAULT).setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(p0: Any, eventSink: EventChannel.EventSink) {
                eventSink.success("default")
            }

            override fun onCancel(p0: Any?) {
            }

        })
    }
}


class BasicMessageChannelManager(private val context: Context, private val messenger: BinaryMessenger) {

    companion object {
        private const val BASIC_NAME = "com.simple.channelflutterandroid/basic"
        private const val BASIC_BINARY_NAME = "com.simple.channelflutterandroid/basic/binary"
    }

    fun sendAndroid2Flutter(str: String) {
        val basicMessageChannel = BasicMessageChannel<String>(messenger, BASIC_NAME, StringCodec.INSTANCE)
        println("It is Native - BasicMessageChannelManager 方式一 send $str")
        basicMessageChannel.send(str)
    }

    fun registerFlutter2Android() {
        val basicMessageChannel = BasicMessageChannel<String>(messenger, BASIC_NAME, StringCodec.INSTANCE)
        basicMessageChannel.setMessageHandler { o, reply ->
            println("It is Native - BasicMessageChannelManager 方式一 setMessageHandler $o")
            if (o == "native_back") {
                if (context is OneActivity) {
                    context.finish()
                }
            }
            reply.reply("It is reply from native")
        }
    }

    fun sendAndroid2FlutterBinary() {
        println("It is Native - BasicMessageChannelManager 方式二 send")

        /**
         * 方式二：使用BinaryMessenger发送接收
         */
//        val buffer = ByteBuffer.allocate(256)
//        buffer.putInt(123)
//        messenger.send(BASIC_BINARY_NAME, buffer)

        messenger.send(BASIC_BINARY_NAME, ByteBuffer.allocate(256))
    }

    fun registerFlutter2AndroidBinary() {
        messenger.setMessageHandler(BASIC_BINARY_NAME) { byteBuffer, binaryReply ->
            val double = byteBuffer.double
            val int = byteBuffer.int
            println("It is Native - BasicMessageChannelManager 方式二 setMessageHandler $double  $int")

            /**
             * 参数转换异常，Android原生的数据在Flutter没法解析？？？？？
             */
//            val buffer = ByteBuffer.allocate(256)
//            buffer.putInt(456)
//            byteBuffer.order(ByteOrder.nativeOrder())
//            binaryReply.reply(buffer)

            binaryReply.reply(ByteBuffer.allocate(256))
        }
    }
}