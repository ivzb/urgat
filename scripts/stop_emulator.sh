#!/bin/sh

set -e

if [[ -z "${JAVA_HOME}" || -z "${ANDROID_HOME}" ]]; then
  echo "JAVA_HOME or ANDROID_HOME is undefined"
  exit 1
fi

handle_error() {
    echo "## Urgat - $0 - An error occurred on line $1\n"
    usage
    exit 1
}

trap 'handle_error $LINENO' ERR

# Add $JAVA_HOME and $ANDROID_HOME to $PATH
export PATH=$JAVA_HOME/bin:$PATH
export PATH=$ANDROID_HOME/tools/bin/:$PATH
export PATH=$ANDROID_HOME/emulator/:$PATH
export PATH=$ANDROID_HOME/platform-tools/:$PATH
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin/:$PATH

# Kill and delete the created emulator
name=Pixel_Urgat
adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill && sleep 10; done
avdmanager delete avd -n $name