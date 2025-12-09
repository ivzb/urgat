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

# Check for ImageMagick (magick or convert)
if ! command -v magick >/dev/null 2>&1 && ! command -v convert >/dev/null 2>&1; then
    echo "❌ ImageMagick is not installed."
    echo "Install with: brew install imagemagick"
    exit 1
fi

pushd ..

SCREENSHOTS_DIR=screenshots

if [ ! -d "$SCREENSHOTS_DIR" ]; then
    echo "#############################"
    echo "## Urgat: Error: Screenshots folder does not exist. Run ./run_ui_tests.sh first to generate screenshots."\
    echo "#############################"
    exit 1
fi

PLAY_STORE_SCREENSHOTS_DIR=play_store_screenshots

if [ -d $PLAY_STORE_SCREENSHOTS_DIR ]; then
  rm -r $PLAY_STORE_SCREENSHOTS_DIR
fi

mkdir $PLAY_STORE_SCREENSHOTS_DIR

popd

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
#  -landscape
)

testArguments=-Pandroid.testInstrumentationRunnerArguments.class=com.ivzb.urgat.suites.PlayStoreScreenshotTestSuite

for device in "${DEVICE_TYPES[@]}"; do
  for mode in "${MODES[@]}"; do
    for orientation in "${ORIENTATIONS[@]}"; do
      echo "#############################"
      echo "## Urgat - Running PlayStore screenshots tests $device $mode $orientation"
      echo "#############################"

      ./run_ui_tests.sh "$device" "$mode" "$orientation" "$testArguments"
    done
  done
done

pushd ..

files=(
  "LibraryScreenshotTest_screenshotTest_LibraryScreen_sampleData.png:01_library_screen.png"
  "TodoScreenshotTest_screenshotTest_TodoScreen_sampleData.png:02_todos_screen.png"
  "AddEditTodoScreenTest_screenshotTest_AddEditTodoScreen_editTodo.png:03_edit_todo_screen.png"
  "NotesScreenshotTest_screenshotTest_NotesScreen_sampleData.png:04_notes_screen.png"
  "FinanceScreenshotTest_screenshotTest_FinanceScreen_sampleData.png:05_finance_screen.png"
  "ExpensesScreenshotTest_screenshotTest_ExpensesScreen_sampleData.png:06_expenses_screen.png"
  "InvestmentsScreenshotTest_screenshotTest_InvestmentsScreen_sampleData.png:08_investments_screen.png"
  "WellbeingScreenshotTest_screenshotTest_WellbeingScreen_sampleData.png:10_wellbeing_screen.png"
)

echo "#############################"
echo "## Urgat - Copying PlayStore screenshots $device $mode $orientation"
echo "#############################"

for device in "${DEVICE_TYPES[@]}"; do
  PLAY_STORE_SCREENSHOTS_DEVICE_DIR=$PLAY_STORE_SCREENSHOTS_DIR/screenshots$device
  mkdir $PLAY_STORE_SCREENSHOTS_DEVICE_DIR

  for pair in "${files[@]}"; do
      src="${pair%%:*}"
      dest="${pair##*:}"

      # combine light/dark mode portrait
      magick \
        $SCREENSHOTS_DIR/screenshots$device-light-mode-portrait/$src \
        $SCREENSHOTS_DIR/screenshots$device-dark-mode-portrait/$src \
        +append \
        $PLAY_STORE_SCREENSHOTS_DEVICE_DIR/$dest

  done
done

popd