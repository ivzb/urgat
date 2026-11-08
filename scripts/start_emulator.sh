#!/bin/bash

set -e

# Validate environment variables
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/env.sh"

handle_error() {
    echo "#############################"
    echo "## Urgat - $0 - An error occurred on line $1"
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
  DEVICE="${1#-}"
  if [ "$1" = "-phone" ]; then
    IS_PHONE=1
  elif [ "$1" = "-tablet7" ]; then
    IS_TABLET7=1
  elif [ "$1" = "-tablet10" ]; then
    IS_TABLET10=1
  else
    usage
    exit 1
  fi
}

getMode() {
  if [ "$2" = "-light-mode" ]; then
    IS_LIGHT_MODE=1
  elif [ "$2" = "-dark-mode" ]; then
    IS_LIGHT_MODE=0
  else
    usage
    exit 1
  fi
}

getOrientation() {
  if [ "$3" = "-portrait" ]; then
    ORIENTATION="portrait"
  elif [ "$3" = "-landscape" ]; then
    ORIENTATION="landscape"
  else
    usage
    exit 1
  fi
}

getDevice "$@"
getMode "$@"
getOrientation "$@"

# Add $JAVA_HOME and $ANDROID_HOME to $PATH
export PATH=$JAVA_HOME/bin:$PATH
export PATH=$ANDROID_HOME/tools/bin/:$PATH
export PATH=$ANDROID_HOME/emulator/:$PATH
export PATH=$ANDROID_HOME/platform-tools/:$PATH
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin/:$PATH

# On Windows (Git Bash/MINGW), SDK tools are .bat files
if [[ "$(uname -s)" == MINGW* || "$(uname -s)" == MSYS* ]]; then
  SDKMANAGER="sdkmanager.bat"
  AVDMANAGER="avdmanager.bat"
else
  SDKMANAGER="sdkmanager"
  AVDMANAGER="avdmanager"
fi

# Kill all running emulators and wait for them to exit (and save their Quick Boot snapshots)
"$SCRIPT_DIR/stop_emulator.sh"

if [[ $(uname -m) == 'x86_64' ]]; then
  ABI_ARCH=x86_64
  CPU_ARCH=x86_64
elif [[ $(uname -m) == 'arm64' || $(uname -m) == 'aarch64' ]]; then
  ABI_ARCH=arm64-v8a
  CPU_ARCH=arm64
else
  echo "#############################"
  echo "## Urgat: Unsupported CPU architecture"
  echo "#############################"
  exit 1
fi

# Install the emulator system image if it's not already
# (checking the folder directly is much faster than `sdkmanager --list_installed`)
sdk="system-images;android-36;google_apis;$ABI_ARCH"
sysDirPath="system-images/android-36/google_apis/$ABI_ARCH/"

if [[ -f "$ANDROID_HOME/$sysDirPath/system.img" ]]; then
  echo "#############################"
  echo "## Urgat: The required SDK is already installed";
  echo "#############################"
else
  yes | $SDKMANAGER --licenses
  android --sdk="$ANDROID_HOME" sdk install $sdk
  yes | $SDKMANAGER --licenses
fi

# One AVD per device/orientation is kept between runs so it can resume from its Quick Boot snapshot
name="Urgat_${DEVICE}_${ORIENTATION}"
avdDir="$HOME/.android/avd/$name.avd"

if [[ $IS_PHONE -eq 1 ]]; then
  lcd="hw.lcd.density=480
hw.lcd.width=1280
hw.lcd.height=2856"

  if [[ $ORIENTATION == "portrait" ]]; then
    screenSize="1280x2856"
  else
    screenSize="2856x1280"
  fi
elif [[ $IS_TABLET7 -eq 1 ]]; then
  lcd="hw.lcd.density=320
hw.lcd.width=1200
hw.lcd.height=1920"

  if [[ $ORIENTATION == "portrait" ]]; then
    screenSize="1200x1920"
  else
    screenSize="1920x1200"
  fi
elif [[ $IS_TABLET10 -eq 1 ]]; then
  lcd="hw.lcd.density=320
hw.lcd.width=1600
hw.lcd.height=2560"

  if [[ $ORIENTATION == "portrait" ]]; then
    screenSize="1600x2560"
  else
    screenSize="2560x1600"
  fi
fi

cfg="AvdId=$name
PlayStore.enabled=false
abi.type=$ABI_ARCH
avd.ini.displayname=$name
avd.ini.encoding=UTF-8
disk.dataPartition.size=10G
fastboot.chosenSnapshotFile=
fastboot.forceChosenSnapshotBoot=no
fastboot.forceColdBoot=no
fastboot.forceFastBoot=yes
hw.accelerometer=yes
hw.arc=false
hw.audioInput=yes
hw.battery=yes
hw.camera.back=virtualscene
hw.camera.front=emulated
hw.cpu.arch=$CPU_ARCH
hw.cpu.ncore=4
hw.dPad=no
hw.device.hash2=MD5:fd01af33f0c601eb671ec483a68b1b00
hw.device.manufacturer=Google
hw.device.name=pixel_9_pro
hw.gps=yes
hw.gpu.enabled=yes
hw.gpu.mode=host
hw.initialOrientation=$ORIENTATION
hw.gyroscope=yes
hw.keyboard=yes
$lcd
hw.mainKeys=no
hw.ramSize=4096
hw.sdCard=yes
hw.sensors.orientation=yes
hw.sensors.proximity=yes
hw.sensors.light=yes
hw.sensors.magnetic_field=yes
hw.sensors.pressure=yes
hw.trackBall=no
image.sysdir.1=$sysDirPath
runtime.network.latency=none
runtime.network.speed=full
showDeviceFrame=no
skin.dynamic=yes
skin.name=$screenSize
skin.path=_no_skin
skin.path.backup=_no_skin
tag.display=Google APIs
tag.id=google_apis
vm.heapSize=512
target=android-36"

if [[ ! -d "$avdDir" ]]; then
  echo no | $AVDMANAGER create avd --force --name $name --abi $ABI_ARCH --package $sdk
fi

# Only rewrite the config when it changed, otherwise the Quick Boot snapshot stays valid
if [[ ! -f "$avdDir/config.ini" || "$(cat "$avdDir/config.ini")" != "$cfg" ]]; then
  echo "$cfg" > "$avdDir/config.ini"
  rm -rf "$avdDir/snapshots/default_boot"
fi

# Start the emulator
# -gpu host renders on the host GPU; swiftshader_indirect (CPU rendering) is very slow
# -no-window runs it headless so no emulator window pops up
if [[ "$(uname -s)" == MINGW* || "$(uname -s)" == MSYS* ]]; then
  cmd //c "start /b emulator -avd $name -no-boot-anim -gpu host -no-window" &
else
  nohup emulator -avd $name -no-boot-anim -gpu host -no-window &
fi

# Wait for device to boot
adb wait-for-device

bootCompleted=$(adb shell getprop sys.boot_completed | tr -d '\r')

while [ "$bootCompleted" != "1" ]; do
    sleep 2
    bootCompleted=$(adb shell getprop sys.boot_completed | tr -d '\r')
done

# The device is reused between runs, so remove test outputs from previous runs
adb shell rm -rf /storage/emulated/0/googletest/test_outputfiles

# Enable developer options
adb shell settings put global development_settings_enabled 1

# Disable animations
adb shell settings put global window_animation_scale 0.0
adb shell settings put global transition_animation_scale 0.0
adb shell settings put global animator_duration_scale 0.0

# Set light/dark mode
if [[ $IS_LIGHT_MODE -eq 1 ]]; then
  adb shell cmd uimode night no
elif [[ $IS_LIGHT_MODE -eq 0 ]]; then
  adb shell cmd uimode night yes
fi

# Disable chrome welcome screen
adb shell 'echo "chrome --disable-fre --no-default-browser-check --no-first-run" > /data/local/tmp/chrome-command-line'

# After a cold boot Android runs heavy background work (Google services, dexopt) for a minute or more,
# which makes tests flaky (e.g. Espresso focus timeouts). Wait until the device CPU usage settles.
cpuBusyPercent() {
  adb shell 'head -1 /proc/stat; sleep 2; head -1 /proc/stat' | tr -d '\r' | awk '
    { total[NR] = $2+$3+$4+$5+$6+$7+$8+$9; idle[NR] = $5+$6 }
    END { dt = total[2]-total[1]; print (dt > 0) ? int(100 * (dt - (idle[2]-idle[1])) / dt) : 100 }'
}

echo "#############################"
echo "## Urgat: Waiting for the device to become idle"
echo "#############################"

idleSamples=0
for i in $(seq 1 90); do
  busy=$(cpuBusyPercent)
  echo "Device CPU usage: $busy%"
  if [[ $busy -lt 30 ]]; then
    idleSamples=$((idleSamples + 1))
    if [[ $idleSamples -ge 2 ]]; then
      break
    fi
  else
    idleSamples=0
  fi
done