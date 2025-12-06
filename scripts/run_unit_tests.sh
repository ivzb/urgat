#!/bin/bash

set -e

handle_error() {
    echo "## Urgat - $0 - An error occurred on line $1\n"
    usage
    exit 1
}

trap 'handle_error $LINENO' ERR

pushd ..

echo "## Urgat - Running unit tests $device $mode"

./gradlew testDebugUnitTest

popd