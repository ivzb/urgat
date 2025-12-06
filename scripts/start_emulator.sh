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

usage()
{
cat << EOF
usage:
    $0 -phone/-tablet -light-mode/-dark-mode
        example:
        - "$ $0 -phone -light-mode
        - "$ $0 -tablet -dark-mode
EOF
}

getDevice() {
  if [ "$1" = "-phone" ]; then
    IS_PHONE=1
  elif [ "$1" = "-tablet" ]; then
    IS_TABLET=1
  else
    usage
    exit 1
  fi
}

getMode() {
  if [ "$2" = "-light-mode" ]; then
    IS_LIGHT_MODE=1
  elif [ "$2" = "-dark-mode" ]; then
    IS_DARK_MODE=1
  else
    usage
    exit 1
  fi
}

getDevice "$@"
getMode "$@"

# Add $JAVA_HOME and $ANDROID_HOME to $PATH
export PATH=$JAVA_HOME/bin:$PATH
export PATH=$ANDROID_HOME/tools/bin/:$PATH
export PATH=$ANDROID_HOME/emulator/:$PATH
export PATH=$ANDROID_HOME/platform-tools/:$PATH
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin/:$PATH

# Kill all running emulators
adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; done

if [[ $(uname -m) == 'x86_64' ]]; then
  ABI_ARCH=x86_64
  CPU_ARCH=x86_64
elif [[ $(uname -m) == 'arm64' ]]; then
  ABI_ARCH=arm64-v8a
  CPU_ARCH=arm64
else
  echo "Unsupported CPU architecture"
  exit 1
fi

# Install the emulator system image if it's not already
sdk="system-images;android-36;google_apis;$ABI_ARCH"
installedImage=$(sdkmanager --list_installed | grep -o $sdk)

if [[ $installedImage = $sdk ]]; then
  echo "The required SDK is already installed";
else
  yes | sdkmanager --licenses
  yes | sdkmanager --install $sdk
  yes | sdkmanager --licenses
fi

name=Pixel_Urgat
echo no | avdmanager create avd --force --name $name --abi $ABI_ARCH --package $sdk

sysDir=$(cat $HOME/.android/avd/$name.avd/config.ini | grep "image.sysdir.1=")

# Edit the created emulator config
cfg="AvdId=$name
PlayStore.enabled=true
abi.type=$ABI_ARCH
avd.ini.displayname=$name
avd.ini.encoding=UTF-8
disk.dataPartition.size=30G
fastboot.chosenSnapshotFile=
fastboot.forceChosenSnapshotBoot=no
fastboot.forceColdBoot=yes
fastboot.forceFastBoot=no
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
hw.gpu.enabled=no
hw.gpu.mode=off
hw.initialOrientation=Portrait
hw.gyroscope=yes
hw.keyboard=yes
hw.lcd.density=480
hw.lcd.height=2856
hw.lcd.width=1280
hw.mainKeys=no
hw.ramSize=12288
hw.sdCard=yes
hw.sensors.orientation=yes
hw.sensors.proximity=yes
hw.sensors.light=yes
hw.sensors.magnetic_field=yes
hw.sensors.pressure=yes
hw.trackBall=no
$sysDir
runtime.network.latency=none
runtime.network.speed=full
showDeviceFrame=no
skin.dynamic=yes
skin.name=1440x2560
skin.path=_no_skin
skin.path.backup=_no_skin
tag.display=Google Play
tag.id=google_apis_playstore
vm.heapSize=1024
PlayStore.enabled=true
target=android-36"

echo "$cfg" > $HOME/.android/avd/$name.avd/config.ini

# Start the emulator
# -gpu swiftshader_indirect is recommended for deterministic screenshots
nohup emulator -avd $name -no-snapshot -no-boot-anim -wipe-data -gpu swiftshader_indirect &

# Wait for device to boot
adb wait-for-device

bootCompleted=$(adb shell getprop sys.boot_completed | tr -d '\r')

while [ "$bootCompleted" != "1" ]; do
    sleep 2
    bootCompleted=$(adb shell getprop sys.boot_completed | tr -d '\r')
done

# Enable developer options
adb shell settings put global development_settings_enabled 1

# Disable animations
adb shell settings put global window_animation_scale 0.0
adb shell settings put global transition_animation_scale 0.0
adb shell settings put global animator_duration_scale 0.0

# Set light/dark mode
if [[ $IS_LIGHT_MODE -eq 1 ]]; then
  adb shell cmd uimode night no
elif [[ $IS_DARK_MODE -eq 1 ]]; then
  adb shell cmd uimode night yes
fi

# Disable chrome welcome screen
adb shell 'echo "chrome --disable-fre --no-default-browser-check --no-first-run" > /data/local/tmp/chrome-command-line'