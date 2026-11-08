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
  IS_WINDOWS=1
else
  IS_WINDOWS=0
fi

isEmulatorRunning() {
  if [[ $IS_WINDOWS -eq 1 ]]; then
    ps -W | grep -q "qemu-system"
  else
    pgrep -f "qemu-system" >/dev/null
  fi
}

# Kill all running emulators. The AVDs are kept so the next start can resume from the
# Quick Boot snapshot saved on exit (delete them with `avdmanager delete avd -n <name>`).
adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill || true; done

# Wait for the emulators to finish saving their snapshots and exit
for i in $(seq 1 120); do
  isEmulatorRunning || break
  sleep 1
done
