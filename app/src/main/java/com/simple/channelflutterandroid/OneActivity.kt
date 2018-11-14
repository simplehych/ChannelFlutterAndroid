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

        findViewById<TextView>(R.id.add_flutter_view)
                .setOnClickListener {
                    val layoutParams = FrameLayout.LayoutParams(-1, -1)
                    addContentView(flutterView, layoutParams)
                }

        findViewById<TextView>(R.id.send_event_channel)
                .setOnClickListener {
                    // EventChannelManager 发送消息
                    EventChannelManager(this, flutterView).sendAndroid2Flutter()
                }

        findViewById<TextView>(R.id.send_basic_message)
                .setOnClickListener {
                    // BasicMessageChannelManager 发送消息
                    BasicMessageChannelManager(flutterView).sendAndroid2Flutter()
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





