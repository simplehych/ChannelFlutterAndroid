package com.simple.channelflutterandroid

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

/**
 * @author hych
 * @date 2018/11/14 12:38
 */
class OneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)
        val tv = findViewById<TextView>(R.id.act_one_tv)
        tv.setOnClickListener { view ->
            Snackbar.make(view, "activity one", Snackbar.LENGTH_SHORT).show()
        }
    }
}