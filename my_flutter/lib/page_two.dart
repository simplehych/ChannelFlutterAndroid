import 'package:flutter/material.dart';
import 'package:my_flutter/flutter_channel_manager.dart';
import 'package:my_flutter/page_one.dart';

class PageTwo extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return PageTwoState();
  }
}

class PageTwoState extends State<PageTwo> {
  var toastText = "点我变";

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
        child: RaisedButton(
          child: Text(toastText),
          onPressed: () async {
            toastText = await MethodChannelManager.sendFlutter2Android("send to native");
            print(toastText);
            setState(() {});
          },
        ),
      ),
    );
  }
}
