#!/bin/bash

set -e

# Validate environment variables
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/env.sh"

handle_error() {
    echo "#############################"
    echo "## Urgat - $0 - An error occurred on line $1"
    echo "#############################"
    exit 1
}

trap 'handle_error $LINENO' ERR

device="-phone"
mode="-light-mode"
orientation="-portrait"
testArguments="-Pandroid.testInstrumentationRunnerArguments.notAnnotation=com.ivzb.urgat.annotations.Screenshot"

echo "#############################"
echo "## Urgat - Running ui tests $device $mode $orientation"
echo "#############################"

./start_emulator.sh "$device" "$mode" "$orientation"

pushd ..

./gradlew connectedDebugAndroidTest $testArguments

popd

./stop_emulator.sh