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

DEVICE_TYPES=(
  -phone
  -tablet7
  -tablet10
)

MODES=(
  -light-mode
  -dark-mode
)

ORIENTATIONS=(
  -portrait
  -landscape
)

pushd ..

screenshotsDir="screenshots"

if [ -d $screenshotsDir ]; then
  rm -r $screenshotsDir
fi

mkdir $screenshotsDir

popd

for device in "${DEVICE_TYPES[@]}"; do
  for mode in "${MODES[@]}"; do
    for orientation in "${ORIENTATIONS[@]}"; do
      echo "#############################"
      echo "## Urgat - Running screenshots tests $device $mode $orientation"
      echo "#############################"

      ./run_specific_screenshots_tests.sh "$screenshotsDir" "$device" "$mode" "$orientation"
    done
  done
done