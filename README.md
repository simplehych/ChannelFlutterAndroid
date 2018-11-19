
# Flutter与Android混合开发

    官方混合开发方案：https://github.com/flutter/flutter/wiki/Add-Flutter-to-existing-apps
    闲鱼混合开发方案：https://yq.aliyun.com/articles/618599?spm=a2c4e.11153959.0.0.4f29616b9f6OWs
    头条混合开发方案：https://mp.weixin.qq.com/s/wdbVVzZJFseX2GmEbuAdfA

    https://www.jianshu.com/p/1317aed6cd8c
    
    WanAndroid客户端简单Flutter版 Apk下载地址：
    https://github.com/simplehych/simple_flutter/blob/master/apk/app-release.apk

## 集成已有项目

    本Demo采用的官方的方式，对个人简单易用，并没有实践闲鱼和头条等大项目的方式

    1. 在已有项目根目录运行
    `flutter create -t module my_flutter`

    其中不一定非一定在项目下创建，兼顾ios，位置路径对即可

    2. 在项目的`settings.gradle` 文件添加如下代码
    ```
    // MyApp/settings.gradle
    include ':app'                                     // assumed existing content
    setBinding(new Binding([gradle: this]))                                 // new
    evaluate(new File(                                                      // new
      settingsDir.parentFile,                                               // new
      'my_flutter/.android/include_flutter.groovy'                          // new
    ))                                                                      // new
    ```

    其中Binding飘红不用理会
    其中'my_flutter/.android/include_flutter.groovy'为my_flutter文件路径，报错找不到可写项目全路径'ChannelFlutterAndroid/my_flutter/.android/include_flutter.groovy'

    3. 在运行模块app的build.gradle添加依赖

    ```
    // MyApp/app/build.gradle
    :
    dependencies {
      implementation project(':flutter')
      :
    }
    ```

    4. 在原生Android端创建并添加flutterView

    ```
     val flutterView = Flutter.createView(this, lifecycle, "route1")
     val layoutParams = FrameLayout.LayoutParams(-1, -1)
     addContentView(flutterView, layoutParams)
    ```

    5. 在Flutter端入口操作

    ```
    void main() => runApp(_widgetForRoute(window.defaultRouteName));

    Widget _widgetForRoute(String route) {
      switch (route) {
        case 'route1':
          return SomeWidget(...);
        default:
          return Center(
            child: Text('Unknown route: $route', textDirection: TextDirection.ltr),
          );
      }
    }
    ```

## 两端通信传输

    本Demo主要是介绍使用方式

    原生Native端和Flutter端通信原理推荐阅读闲鱼文章：https://yq.aliyun.com/articles/630105?spm=a2c4e.11153959.0.0.59eb616bHPgOl4

    交互通信提供`MethodChannel`、`EventChannel`、`BasicMessageChannel`三种方式

    * 原则1 类似注册监听，发送的模式

    * 原则2 使用顺序：先注册监听，后发送，否则接收不到。尤其使用 `MethodChannel` `EventChannel` 不符合该原则会抛出异常，`BasicMessageChannel`方式只是收不到消息

### MethodChannel

    应用场景：Flutter端 调用 Native端

    Flutter端代码：

    ```
    var result = await MethodChannel("com.simple.channelflutterandroid/method", StandardMethodCodec()).invokeMethod("toast", {"msg": msg})

    ```

    Android端代码：

    ```
    MethodChannel(flutterView, "com.simple.channelflutterandroid/method",StandardMethodCodec.INSTANCE).setMethodCallHandler { methodCall, result ->
                when (methodCall.method) {
                    "toast" -> {
                        //调用传来的参数"msg"对应的值
                        val msg = methodCall.argument<String>("msg")
                        //调用本地Toast的方法
                        Toast.makeText(cxt, msg, Toast.LENGTH_SHORT).show()
                        //回调给客户端
                        result.success("native android toast success")
                    }
                    "other" -> {
                        // ...
                    }
                    else -> {
                        // ...
                    }
                }
            }
    ```

    注意点1：俩端对应一致的点
    方法名称：com.simple.channelflutterandroid/method，可以不采取包名，对应一致即可
    传参key："msg"

    注意点2：以下为Flutter为例，Android同理
    StandardMethodCodec()非必传，默认是StandardMethodCodec()，是一种消息编解码器
    对于MethodChannel和EventChannel，有俩种编解码器，均实现MethodCodec类，分别为StandardMethodCodec和JsonMethodCodec。
    对于BasicMessageChannel，有四种编解码器，均实现MessageCodec类，分别为StandardMessageCodec、JSONMessageCodec、StringCodec和BinaryCodec。

    注意点3：setMethodCallHandler(),针对三种方式一一对应，属于监听
    MethodChannel - setMethodCallHandler()
    EventChannel - setStreamHandler()
    BasicMessageChannel - setMessageHandler()
    其中setStreamHandler()方式稍微特殊，具体查看闲鱼文章

    注意点4：FlutterView实现BinaryMessenger接口

    下俩种方法不再赘述


### EventChannel

    应用场景：Native端 调用 Flutter端，方式稍微特殊

    Flutter端代码：属于接收方

    ```
    EventChannel("com.simple.channelflutterandroid/event").receiveBroadcastStream().listen((event){
        print("It is Flutter -  receiveBroadcastStream $event");
    })
    ```

    注意 `receiveBroadcastStream`

    Android端代码：属于发送方

    ```
    EventChannel(flutterView, "com.simple.channelflutterandroid/event").setStreamHandler(object : EventChannel.StreamHandler {
        override fun onListen(o: Any?, eventSink: EventChannel.EventSink) {
            eventSink.success("我是发送Native的消息")
        }

        override fun onCancel(o: Any?) {
            // ...
        }
    })
    ```



### BasicMessageChannel

    应用场景：互相调用

    俩种使用方式，创建方式和格式不一样

    ### 第一种

    Flutter端
    ```
    _basicMessageChannel = BasicMessageChannel("com.simple.channelflutterandroid/basic", StringCodec())

    // 发送消息
    _basicMessageChannel.send("我是Flutter发送的消息");

    // 接收消息
    _basicMessageChannel.setMessageHandler((str){
        print("It is Flutter -  receive str");
    });

    ```

    Android端

    ```
    val basicMessageChannel = BasicMessageChannel<String>(flutterView, "com.simple.channelflutterandroid/basic", StringCodec.INSTANCE)

    // 发送消息
    basicMessageChannel.send("我是Native发送的消息")

    // 接收消息
    basicMessageChannel.setMessageHandler { o, reply ->
        println("It is Native - setMessageHandler $o")
        reply.reply("It is reply from native")
    }
    ```

    注意：StringCodec()，区别于第二种

    ### 第二种

    Flutter端

    ```
    static const BASIC_BINARY_NAME = "com.simple.channelflutterandroid/basic/binary";

    // 发送消息
    val res = await BinaryMessages.send(BASIC_BINARY_NAME, ByteData(256))

    // 接收消息
    BinaryMessages.setMessageHandler(BASIC_BINARY_NAME, (byteData){
        println("It is Flutter - setMessageHandler $byteData")
    });

    ```

    Android端
    ```
    const val BASIC_BINARY_NAME = "com.simple.channelflutterandroid/basic/binary"

    // 发送消息
    flutterView.send(BASIC_BINARY_NAME,ByteBuffer.allocate(256));

    // 接收消息
    flutterView.setMessageHandler(BASIC_BINARY_NAME) { byteBuffer, binaryReply ->
        println("It is Native - setMessageHandler $byteBuffer")
        binaryReply.reply(ByteBuffer.allocate(256))
    }
    ```

    注意：此组合可以进行图片的传递

    但是在操作中使用此方式传输数据总报错，所以传了默认ByteBuffer，还是姿势不对？？？


### 其他

    1. Native端接收方收到消息后都能进行回传信息
    eg：MethodChannel：result.success("我是回传信息")；
    eg：EventChannel：eventSink.success("我是回传信息")；
    eg：BasicMessageChannel：binaryReply.reply(-)；- Flutter端：ByteData；Android端：ByteBuffer

### Q&A

Q：使用命令行flutter create创建的project或者module，文件android/ios为隐藏打点开头的.android/.ios文件

A：所以有的时候会出现问题，寻找不到文件的情况

Q: flutter create module 和project的区别

A:

Q: 使用BasicMessageChannel Binary方式抛出异常

A: 参数转换异常，Android原生的数据在Flutter没法解析？？？？？

Q: couldn't locate lint-gradle-api-26.1.2.jar for flutter project
A: https://stackoverflow.com/questions/52945041/couldnt-locate-lint-gradle-api-26-1-2-jar-for-flutter-project


Q: Could not resolve all files for configuration ':app:androidApis'.
Failed to transform file 'android.jar' to match attributes {artifactType=android-mockable-jar, returnDefaultValues=false} using transform MockableJarTransform

https://github.com/anggrayudi/android-hidden-api/issues/46

Q: 完全卸载
A：https://www.jetbrains.com/help/webstorm/2016.2/directories-used-by-webstorm-to-store-settings-caches-plugins-and-logs.html



Q: I/flutter: ══╡ EXCEPTION CAUGHT BY SERVICES LIBRARY ╞══════════════════════════════════════════════════════════
I/flutter: The following MissingPluginException was thrown while activating platform stream on channel
    com.simple.compileflutterapp:
    MissingPluginException(No implementation found for method listen on channel
    com.simple.compileflutterapp)
I/flutter: When the exception was thrown, this was the stack:
I/flutter: #0      MethodChannel.invokeMethod (package:flutter/src/services/platform_channel.dart:278:7)
I/flutter: <asynchronous suspension>
    #1      EventChannel.receiveBroadcastStream.<anonymous closure> (package:flutter/src/services/platform_channel.dart:424:29)
    <asynchronous suspension>

A: channel的名称不能一样

Q: Flutter Error: Navigator operation requested with a context that does not include a Navigator 

A: https://github.com/flutter/flutter/issues/15919
