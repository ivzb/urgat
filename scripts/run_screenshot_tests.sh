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

if [ -d screenshots ]; then
  rm -r screenshots
fi

mkdir screenshots

popd

for device in "${DEVICE_TYPES[@]}"; do
  for mode in "${MODES[@]}"; do
    for orientation in "${ORIENTATIONS[@]}"; do
      echo "#############################"
      echo "## Urgat - Running screenshots tests $device $mode $orientation"
      echo "#############################"

      ./run_ui_tests.sh "$device" "$mode" "$orientation"
    done
  done
done