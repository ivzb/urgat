#!/bin/bash

set -e

handle_error() {
    echo "## Urgat - $0 - An error occurred on line $1\n"
    usage
    exit 1
}

trap 'handle_error $LINENO' ERR

DEVICE_TYPES=(
  -phone
#  -tablet
)

MODES=(
  -light-mode
  -dark-mode
)

for device in "${DEVICE_TYPES[@]}"; do
  for mode in "${MODES[@]}"; do
    echo "## Urgat - Running ui tests $device $mode"

    ./start_emulator.sh "$device" "$mode"

    pushd ..

    ./gradlew connectedDebugAndroidTest

    SCREENSHOTS_DIR="screenshots$device$mode"

    if [ -d $SCREENSHOTS_DIR ]; then
      rm -r $SCREENSHOTS_DIR
    fi

    mkdir $SCREENSHOTS_DIR

    adb pull "/storage/emulated/0/googletest/test_outputfiles" $SCREENSHOTS_DIR/

    mv $SCREENSHOTS_DIR/test_outputfiles/* $SCREENSHOTS_DIR/
    rm -r $SCREENSHOTS_DIR/test_outputfiles

    popd

    ./stop_emulator.sh
  done
done