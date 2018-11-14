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

    EventChannelManager.registerAndroid2Flutter((event) {
      print("PageOneState EventChannelManager registerAndroid2Flutter $event");
    });

    BasicMassageChannelManager.registerAndroid2Flutter((str) {
      print(
          "PageOneState BasicMassageChannelManager registerAndroid2Flutter $str");
    });
    BasicMassageChannelManager.registerAndroid2FlutterBinary((byteData) {
      print(
          "PageOneState BasicMassageChannelManagerBinary registerAndroid2Flutter $byteData");
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
                    "send to native");
                print(toastText);
                setState(() {});
              },
            ),
            RaisedButton(
              child: Text("sendFlutter2Android"),
              onPressed: () async {
                await BasicMassageChannelManager.sendFlutter2Android();
              },
            ),
            RaisedButton(
              child: Text("sendFlutter2AndroidBinary"),
              onPressed: () async {
                await BasicMassageChannelManager.sendFlutter2AndroidBinary();
              },
            ),
          ],
        ),
      ),
    );
  }
}
