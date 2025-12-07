#!/bin/bash

set -e

handle_error() {
    echo "## Urgat - $0 - An error occurred on line $1\n"
    usage
    exit 1
}

trap 'handle_error $LINENO' ERR

pushd ..

SCREENSHOTS_DIR=screenshots

if [ ! -d "$SCREENSHOTS_DIR" ]; then
    echo "Error: Screenshots folder does not exist. Run ./run_ui_tests.sh first to generate screenshots."
    exit 1
fi

PLAY_STORE_SCREENSHOTS_DIR=play_store_screenshots

if [ -d $PLAY_STORE_SCREENSHOTS_DIR ]; then
  rm -r $PLAY_STORE_SCREENSHOTS_DIR
fi

mkdir $PLAY_STORE_SCREENSHOTS_DIR

DEVICE_TYPES=(
  phone
  tablet7
  tablet10
)

for device in "${DEVICE_TYPES[@]}"; do
  PLAY_STORE_SCREENSHOTS_DEVICE_DIR=$PLAY_STORE_SCREENSHOTS_DIR/$device
  mkdir $PLAY_STORE_SCREENSHOTS_DEVICE_DIR

  cp $SCREENSHOTS_DIR/screenshots-$device-light-mode-portrait/LibraryScreenshotTest_screenshotTest_LibraryScreen_sampleData.png $PLAY_STORE_SCREENSHOTS_DEVICE_DIR/01_library_screen.png
  cp $SCREENSHOTS_DIR/screenshots-$device-light-mode-portrait/TodoScreenshotTest_screenshotTest_TodoScreen_sampleData.png  $PLAY_STORE_SCREENSHOTS_DEVICE_DIR/02_todos_screen.png
  cp $SCREENSHOTS_DIR/screenshots-$device-light-mode-portrait/AddEditTodoScreenTest_screenshotTest_AddEditTodoScreen_editTodo.png  $PLAY_STORE_SCREENSHOTS_DEVICE_DIR/03_edit_todo_screen.png
  cp $SCREENSHOTS_DIR/screenshots-$device-light-mode-portrait/NotesScreenshotTest_screenshotTest_NotesScreen_sampleData.png  $PLAY_STORE_SCREENSHOTS_DEVICE_DIR/04_notes_screen.png
  cp $SCREENSHOTS_DIR/screenshots-$device-light-mode-portrait/FinanceScreenshotTest_screenshotTest_FinanceScreen_sampleData.png  $PLAY_STORE_SCREENSHOTS_DEVICE_DIR/05_finance_screen.png
  cp $SCREENSHOTS_DIR/screenshots-$device-light-mode-portrait/ExpensesScreenshotTest_screenshotTest_ExpensesScreen_sampleData.png  $PLAY_STORE_SCREENSHOTS_DEVICE_DIR/06_expenses_screen.png
  cp $SCREENSHOTS_DIR/screenshots-$device-light-mode-portrait/InvestmentsScreenshotTest_screenshotTest_InvestmentsScreen_sampleData.png  $PLAY_STORE_SCREENSHOTS_DEVICE_DIR/08_investments_screen.png
  cp $SCREENSHOTS_DIR/screenshots-$device-light-mode-portrait/WellbeingScreenshotTest_screenshotTest_WellbeingScreen_sampleData.png  $PLAY_STORE_SCREENSHOTS_DEVICE_DIR/10_wellbeing_screen.png

done

popd