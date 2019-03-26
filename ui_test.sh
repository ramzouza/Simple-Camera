#/bin/sh
./gradlew compileDebugAndroidTestSources
adb push $(pwd)/app/build/outputs/apk/debug/camera-debug.apk /data/local/tmp/com.simplemobiletools.camera.debug;
adb shell pm install -t -r "/data/local/tmp/com.simplemobiletools.camera.debug";
adb shell pm grant com.simplemobiletools.camera.debug android.permission.WRITE_EXTERNAL_STORAGE;
adb shell pm grant com.simplemobiletools.camera.debug android.permission.READ_EXTERNAL_STORAGE;
adb shell pm grant com.simplemobiletools.camera.debug android.permission.CAMERA;
# adb shell pm grant com.simplemobiletools.camera.debug android.permission.RECORD_AUDIO;
adb shell am instrument -w -r   -e debug false -e class com.simplemobiletools.camera.activities.MainActivityTest com.simplemobiletools.camera.debug.test/androidx.test.runner.AndroidJUnitRunner;