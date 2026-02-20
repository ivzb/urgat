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

SCREENSHOTS_DIR=screenshots_temp

if [ -d $SCREENSHOTS_DIR ]; then
  rm -r $SCREENSHOTS_DIR
fi

mkdir $SCREENSHOTS_DIR

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

      ./run_specific_screenshots_tests.sh "$SCREENSHOTS_DIR" "$device" "$mode" "$orientation" "$testArguments"
    done
  done
done

pushd ..

files=(
  "LibraryScreenshotTest_screenshotTest_LibraryScreen_sampleData_todos.png:01_library_screen.png"
  "AddEditTodoScreenshotTest_screenshotTest_AddEditTodoScreen_editTodo.png:02_edit_todo_screen.png"
  "FinanceScreenshotTest_screenshotTest_FinanceScreen_sampleData_expenses_transactions.png:03_finance_expenses_transactions_screen.png"
  "ExpensesScreenshotTest_screenshotTest_ExpensesScreen_sampleData_monthly:04_expenses_monthly_screen.png"
  "FinanceScreenshotTest_screenshotTest_FinanceScreen_sampleData_investments_transactions.png:05_finance_investments_transactions_screen.png"
  "FinanceScreenshotTest_screenshotTest_FinanceScreen_sampleData_investments_all_time.png:06_finance_investments_all_time_screen.png"
  "WellbeingScreenshotTest_screenshotTest_WellbeingScreen_sampleData.png:07_wellbeing_screen.png"
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

echo "#############################"
echo "## Urgat - Removing temp screenshots dir"
echo "#############################"

rm -r $SCREENSHOTS_DIR

popd