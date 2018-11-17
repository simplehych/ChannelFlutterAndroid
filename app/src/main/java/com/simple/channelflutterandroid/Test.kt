package com.simple.channelflutterandroid

import  kotlinx.coroutines.*

/**
 * @author hych
 * @date 2018/11/17 15:10
 */

fun  main() = runBlocking {
    launch {
        delay(200L)
        println("Task from runBlocking")
    }

    coroutineScope {
        launch {
            delay(500L)
            println("Task from nested launch")
        }

        delay(100L)
        println("Task from coroutine scope")
    }

    println("Coroutine scope is over")

}
