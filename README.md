### Configuration for release
To publish app bundle to Google Play store, you need to configure gcloud CLI with the following command:

1. Add release shortcut to .zshrc (optional)

```shell
release() {
  pushd ~/android/urgat/scripts/ > /dev/null
  ./play_store_release.sh $@
  popd > /dev/null
}
```

2. Copy `urgat.jks` file to the main project directory
3. Create `keystore.properties` file in the main project directory with the following content:

```
storeFile=<path to urgat.jks>
keyAlias=<alias>
keyPassword=<password>
storePassword=<password>
```

4. Install gcloud CLI and initialize it with the following command:

```shell
brew install gcloud-cli
gcloud init
```

5. Authenticate via browser
6. Pick cloud project to use: urgat

### Play Store release
Use the following command to publish app bundle to Google Play store:

```bash
./play_store_release.sh 25.12.0
./play_store_release.sh 25.12.0 -skip-screenshots
````

### Run unit tests:
Use the following command to run unit tests:

```bash
./run_unit_tests.sh
````

### Run ui tests:
Use the following command to run ui tests:

```bash
./run_ui_tests.sh
````

### Run all screenshot tests:
Use the following command to run all screenshot tests:

```bash
./run_all_screenshots_tests.sh screenshots-dir 
````

### Run specific screenshot tests:
Use the following command to run specific screenshot tests:

```bash
./run_specific_screenshots_tests.sh screenshots-dir -phone -light-mode -portrait
./run_specific_screenshots_tests.sh screenshots-dir -phone -light-mode -portrait -Pandroid.testInstrumentationRunnerArguments.class=com.ivzb.urgat.suites.PlayStoreScreenshotTestSuite
````

### Generate Play Store screenshots
Use the following command to generate screenshots for app release to Google Play store:

```bash
./play_store_screenshots.sh
```

### Generate Play Store release notes
Use the following command to generate release notes for app release to Google Play store.
`./play_store_release_notes.sh` script accepts two arguments: start and end git tags.

```bash
./play_store_release_notes.sh v25.11.0 v25.12.1
```

To include release notes for a feature, improvement, or bug fix on the Google Play Store, 
add the following format to your commit message:

- What's new example: 
```
<new>
Notes: Add titles and tags to organize your thoughts better.
Expenses: Track recurring expenses automatically for smarter budgeting.
To-Dos: Create subtasks and set priorities for easier task management.
</new>
```

- What's new template:
```
<new>

</new>
```

- Improvements example:
```
<improved>
Faster app startup and smoother navigation throughout the app.
Enhanced search functionality across Notes, To-Dos, and Expenses.
</improved>
```

- Improvements template:
```
<improved>

</improved>
```

- Bug fixes example:
```
<fixed>
Fixed an issue where some notifications were not appearing.
</fixed>
```

- Bug fixes template:
```
<fixed>

</fixed>
```

The following release notes will be generated:

```
New
- Notes: Add titles and tags to organize your thoughts better.
- Expenses: Track recurring expenses automatically for smarter budgeting.
- To-Dos: Create subtasks and set priorities for easier task management.

Improved
- Faster app startup and smoother navigation throughout the app.
- Enhanced search functionality across Notes, To-Dos, and Expenses.

Fixed
- Fixed an issue where some notifications were not appearing.
```