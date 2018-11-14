import 'package:flutter/material.dart';

class NavigatorManager {
  static Future push(BuildContext context, Widget widget) async {
    return await Navigator.of(context)
        .push(MaterialPageRoute(builder: (context) {
      return widget;
    }));
  }
}
