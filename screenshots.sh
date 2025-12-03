#!/bin/bash

./gradlew connectedDebugAndroidTest

rm -r screenshots
mkdir screenshots

adb pull "/storage/emulated/0/googletest/test_outputfiles" screenshots/

mv screenshots/test_outputfiles/* screenshots/
rm -r screenshots/test_outputfiles