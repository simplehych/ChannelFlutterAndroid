import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class MethodChannelManager {
  static const _BASIC_NAME = "com.simple.channelflutterandroid/method";
  static const _methodChannel =
      MethodChannel(_BASIC_NAME, StandardMethodCodec());
  static const _TOAST = "toast";

  static Future sendFlutter2Android(msg) async {
    return await _methodChannel.invokeMethod(_TOAST, {"msg": msg});
  }
}

class EventChannelManager {
  static const _BASIC_NAME = "com.simple.channelflutterandroid/event";
  static const _NET = "net";
  static const _DEFAULT = "default";
  static const _eventChannel = EventChannel("$_BASIC_NAME");

  static receiveAndroid2FlutterForNet(void onData(event)) {
    _eventChannel.receiveBroadcastStream(_NET).listen(onData);
  }

  static receiveAndroid2FlutterForDefault(void onData(event)) {
    _eventChannel.receiveBroadcastStream(_DEFAULT).listen(onData);
  }
}

class BasicMessageChannelManager {
  static const BASIC_NAME = "com.simple.channelflutterandroid/basic";
  static const BASIC_BINARY_NAME =
      "com.simple.channelflutterandroid/basic/binary";
  static const _basicMessageChannel =
      BasicMessageChannel(BASIC_NAME, StringCodec());

  static Future sendFlutter2Android(msg) async {
    return await _basicMessageChannel.send(msg);
  }

  static registerAndroid2Flutter(Future<String> handler(String str)) {
    _basicMessageChannel.setMessageHandler(handler);
  }

  static Future sendFlutter2AndroidBinary() async {
    final buffer = WriteBuffer()
      ..putInt32(987);
    var byteData = buffer.done();
    Completer completer = Completer();
    await BinaryMessages.send(BASIC_BINARY_NAME, byteData).then((data) {
      print("${data.toString()}");
      completer.complete(data);
//      completer.complete(_decodeData(data));
    });
    return completer.future;
  }

  static registerAndroid2FlutterBinary(
      Future<ByteData> handler(ByteData byteData)) {
    BinaryMessages.setMessageHandler(BASIC_BINARY_NAME, handler);
  }

  static _decodeData(data) {
    var readBuffer = ReadBuffer(data);
    var int32 = readBuffer.getInt32();
    return "decode $int32";
  }
}
