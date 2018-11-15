package com.simple.channelflutterandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import android.widget.TextView
import io.flutter.facade.Flutter

/**
 * @author hych
 * @date 2018/11/14 12:38
 */
class OneActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)

        val flutterView = Flutter.createView(this, lifecycle, "route1")
        EventChannelManager(this, flutterView).sendAndroid2Flutter(type = EventChannelManager.NET)

        findViewById<TextView>(R.id.add_flutter_view)
            .setOnClickListener {
                val layoutParams = FrameLayout.LayoutParams(-1, -1)
                addContentView(flutterView, layoutParams)
            }

        findViewById<TextView>(R.id.send_event_channel)
            .setOnClickListener {
                // EventChannelManager 发送消息
                // 点击再触发不好使，需要放在之前
                // 细想实现方式，不可能出现点击发送的场景，当进入Flutter页面之前，必须注册完成以后可能遇到的情况，不可能来回交互，所以大部分用在注册监听事件
                // 场景1：进入时携带数据 例如：在initState携带时获取必须在初始化flutterView注册，否则会抛出异常
                // ！！！！ 但是BasicMessageChannel不受此限制，只是接收不到数据，不会抛出异常，同理MethodChannel有有此限制，注册优于使用
                // 场景2：native变化时flutter改变
                // 场景3：不用在initState使用，点击使用必须优于之前
                // 所以最终时在进入时注册，

//                    EventChannelManager(this, flutterView).sendAndroid2Flutter()
                EventChannelManager(this, flutterView).sendAndroid2Flutter(type = EventChannelManager.DEFAULT)
            }

        findViewById<TextView>(R.id.send_basic_message)
            .setOnClickListener {
                // BasicMessageChannelManager 发送消息
                BasicMessageChannelManager(flutterView).sendAndroid2Flutter("send to flutter from native by BasicMessageChannelManager")
            }

        findViewById<TextView>(R.id.send_basic_message_binary)
            .setOnClickListener {
                // BasicMessageChannelManagerBinary 发送消息
                BasicMessageChannelManager(flutterView).sendAndroid2FlutterBinary()
            }

        MethodChannelManager(this, flutterView).registerAndBackFlutter2Android()
        BasicMessageChannelManager(flutterView).registerFlutter2Android()
        BasicMessageChannelManager(flutterView).registerFlutter2AndroidBinary()
    }
}





