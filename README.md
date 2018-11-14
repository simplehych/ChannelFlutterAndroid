

## Q & A

Q: flutter create module 和project的区别

A:

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