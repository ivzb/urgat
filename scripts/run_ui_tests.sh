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
    $0 -phone/-tablet7/-tablet10 -light-mode/-dark-mode -portrait/-landscape
EOF
}

getDevice() {
  if [[ "$1" = "-phone" || "$1" = "-tablet7" || "$1" = "-tablet10" ]]; then
    device="$1"
  else
    usage
    exit 1
  fi
}

getMode() {
  if [[ "$2" = "-light-mode" || "$2" = "-dark-mode" ]]; then
    mode="$2"
  else
    usage
    exit 1
  fi
}

getOrientation() {
  if [[ "$3" = "-portrait" || "$3" = "-landscape" ]]; then
    orientation="$3"
  else
    usage
    exit 1
  fi
}

getDevice "$@"
getMode "$@"
getOrientation "$@"
testArguments="$4"

pushd ..

if [ ! -d screenshots ]; then
  mkdir creenshots
fi

popd

echo "#############################"
echo "## Urgat - Running ui tests $device $mode $orientation"
echo "#############################"

./start_emulator.sh "$device" "$mode" "$orientation"

pushd ..

./gradlew connectedDebugAndroidTest $testArguments

SCREENSHOTS_DIR="screenshots/screenshots$device$mode$orientation"

if [ -d $SCREENSHOTS_DIR ]; then
  rm -r $SCREENSHOTS_DIR
fi

mkdir $SCREENSHOTS_DIR

adb pull "/storage/emulated/0/googletest/test_outputfiles" $SCREENSHOTS_DIR/

mv $SCREENSHOTS_DIR/test_outputfiles/* $SCREENSHOTS_DIR/
rm -r $SCREENSHOTS_DIR/test_outputfiles

popd

./stop_emulator.sh