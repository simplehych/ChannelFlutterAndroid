import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';


class MethodChannelManager {
  static const _BASIC_NAME = "com.simple.channelflutterandroid/method";
  static const _methodChannel = MethodChannel(_BASIC_NAME);
  static const _TOAST = "toast";

  static Future sendFlutter2Android(msg) async {
    return await _methodChannel.invokeMethod(_TOAST, {"msg": msg});
  }
}

class EventChannelManager {
  static const _BASIC_NAME = "com.simple.channelflutterandroid/event";
  static const _NET = "net";
  static const _eventChannel = EventChannel("$_BASIC_NAME");

  static registerAndroid2Flutter(void onData(event),
      {Function onError, void onDone(), bool cancelOnError}) {
    _eventChannel.receiveBroadcastStream(_NET).listen(onData,
        onError: onError, onDone: onDone, cancelOnError: cancelOnError);
  }
}

class BasicMassageChannelManager {
  static const BASIC_NAME = "com.simple.channelflutterandroid/basic";
  static const BASIC_BINARY_NAME =
      "com.simple.channelflutterandroid/basic/binary";
  static const _basicMessageChannel =
  BasicMessageChannel(BASIC_NAME, StringCodec());

  static Future sendFlutter2Android() async {
    return await _basicMessageChannel.send("我是flutter的数据");
  }

  static Future sendFlutter2AndroidBinary() async {
    final buffer = WriteBuffer()
      ..putFloat64(3.14)
      ..putInt32(123456789);
    var byteData = buffer.done();
    Completer completer = Completer();
    await BinaryMessages.send(BASIC_BINARY_NAME, byteData).then((data) {
      completer.complete(_decodeData(data));
    });
    return completer.future;
  }

  static registerAndroid2Flutter(Future<String> handler(String str)) {
    _basicMessageChannel.setMessageHandler(handler);
  }

  static registerAndroid2FlutterBinary(
      Future<ByteData> handler(ByteData byteData)) {
    BinaryMessages.setMessageHandler(BASIC_BINARY_NAME, handler);
  }

  static _decodeData(data) {
    var readBuffer = ReadBuffer(data);
    var float64 = readBuffer.getFloat64();
    var int32 = readBuffer.getInt32();
    return "decode $float64 $int32";
  }
}
