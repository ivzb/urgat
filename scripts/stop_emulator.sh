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

# Add $JAVA_HOME and $ANDROID_HOME to $PATH
export PATH=$JAVA_HOME/bin:$PATH
export PATH=$ANDROID_HOME/tools/bin/:$PATH
export PATH=$ANDROID_HOME/emulator/:$PATH
export PATH=$ANDROID_HOME/platform-tools/:$PATH
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin/:$PATH

# On Windows (Git Bash/MINGW), SDK tools are .bat files
if [[ "$(uname -s)" == MINGW* || "$(uname -s)" == MSYS* ]]; then
  AVDMANAGER="avdmanager.bat"
else
  AVDMANAGER="avdmanager"
fi

# Kill and delete the created emulator
name=Pixel_Urgat
adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill && sleep 10; done
$AVDMANAGER delete avd -n $name
