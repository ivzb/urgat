#!/bin/bash

set -e

handle_error() {
    echo "#############################"
    echo "## Urgat - $0 - An error occurred on line $1\n"
    echo "#############################"
    usage
    exit 1
}

trap 'handle_error $LINENO' ERR

usage()
{
cat << EOF
usage:
    $0 <output dir> -phone/-tablet7/-tablet10 -light-mode/-dark-mode -portrait/-landscape
EOF
}

getScreenshotsDir() {
  if [ -n "$1" ]; then
    screenshotsDir="$1"
  else
      usage
      exit 1
  fi
}

getDevice() {
  if [[ "$2" = "-phone" || "$2" = "-tablet7" || "$2" = "-tablet10" ]]; then
    device="$2"
  else
    usage
    exit 1
  fi
}

getMode() {
  if [[ "$3" = "-light-mode" || "$3" = "-dark-mode" ]]; then
    mode="$3"
  else
    usage
    exit 1
  fi
}

getOrientation() {
  if [[ "$4" = "-portrait" || "$4" = "-landscape" ]]; then
    orientation="$4"
  else
    usage
    exit 1
  fi
}

getScreenshotsDir "$@"
getDevice "$@"
getMode "$@"
getOrientation "$@"
testArguments="$5"

pushd ..

if [ ! -d screenshotsDir ]; then
  mkdir screenshotsDir
fi

popd

echo "#############################"
echo "## Urgat - Running ui tests $device $mode $orientation"
echo "#############################"

./start_emulator.sh "$device" "$mode" "$orientation"

pushd ..

./gradlew connectedDebugAndroidTest $testArguments

SCREENSHOTS_DIR="$screenshotsDir/screenshots$device$mode$orientation"

if [ -d $SCREENSHOTS_DIR ]; then
  rm -r $SCREENSHOTS_DIR
fi

mkdir $SCREENSHOTS_DIR

adb pull "/storage/emulated/0/googletest/test_outputfiles" $SCREENSHOTS_DIR/

mv $SCREENSHOTS_DIR/test_outputfiles/* $SCREENSHOTS_DIR/
rm -r $SCREENSHOTS_DIR/test_outputfiles

popd

./stop_emulator.sh