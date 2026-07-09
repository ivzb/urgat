#!/bin/bash

set -e

handle_error() {
    echo "#############################"
    echo "## Urgat - $0 - An error occurred on line $1"
    echo "#############################"
    exit 1
}

trap 'handle_error $LINENO' ERR

pushd ..

echo "#############################"
echo "## Urgat - Running unit tests $device $mode"
echo "#############################"

./gradlew testDebugUnitTest

popd