import 'package:flutter/material.dart';
import 'package:my_flutter/flutter_channel_manager.dart';
import 'package:my_flutter/navigator_manager.dart';
import 'package:my_flutter/page_two.dart';

class PageOne extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return PageOneState();
  }
}

class PageOneState extends State<PageOne> {
  var toastText = "invoke start";

  @override
  void initState() {
    super.initState();

    EventChannelManager.receiveAndroid2FlutterForNet((event) {
      print(
          "It is Flutter -  EventChannelManager receiveAndroid2FlutterForNet $event");
    });

    BasicMessageChannelManager.registerAndroid2Flutter((str) {
      print(
          "It is Flutter -  BasicMassageChannelManager registerAndroid2Flutter $str");
    });

    BasicMessageChannelManager.registerAndroid2FlutterBinary((byteData) {
      print(
          "It is Flutter -  BasicMassageChannelManagerBinary registerAndroid2Flutter $byteData");
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
            icon: Icon(Icons.arrow_back),
            onPressed: () {
              Navigator.pop(context);
            }),
      ),
      body: Center(
        child: Column(
          children: <Widget>[
            RaisedButton(
              child: Text("jump next page"),
              onPressed: () {
                NavigatorManager.push(context, PageTwo());
              },
            ),
            RaisedButton(
              child: Text(toastText),
              onPressed: () async {
                toastText = await MethodChannelManager.sendFlutter2Android(
                    "send to native by method channel");
                print(toastText);
                setState(() {});
              },
            ),
            RaisedButton(
              child: Text("event default"),
              onPressed: () async {
                EventChannelManager.receiveAndroid2FlutterForDefault((data) {
                  print(
                      "It is Flutter -  EventChannelManager registerAndroid2FlutterForDefault $data");
                });
              },
            ),
            RaisedButton(
              child: Text("basic message sendFlutter2Android"),
              onPressed: () async {
                var res = await BasicMessageChannelManager.sendFlutter2Android(
                    "send to native from flutter by basic message");
                print(
                    "It is Flutter - BasicMessageChannelManager sendFlutter2Android res: $res");
              },
            ),
            RaisedButton(
              child: Text("basic message  sendFlutter2AndroidBinary"),
              onPressed: () async {
                var res = await BasicMessageChannelManager
                    .sendFlutter2AndroidBinary();
                print(
                    "It is Flutter - BasicMessageChannelManager sendFlutter2AndroidBinary res: $res");
              },
            ),
          ],
        ),
      ),
    );
  }
}
