#!/bin/bash

set -e

handle_error() {
    echo "## Urgat - $0 - An error occurred on line $1\n"
    usage
    exit 1
}

trap 'handle_error $LINENO' ERR

if [[ $# -ne 1 ]]; then
    echo "Usage: $0 <version>"
    exit 1
fi

VERSION="$1"

##
# Run unit tests
##
./run_unit_tests.sh

##
# Run ui tests
##
./run_ui_tests.sh

##
# Generate play store screenshots
##
./play_store_screenshots.sh

##
# Generate play store release notes
##

# Write release notes to temp file
tmpfile=$(mktemp)

{
  printf "## Release notes %s\n\n" "$VERSION"
  ./play_store_release_notes.sh
} > "$tmpfile"

# Prepend tmpfile + existing file
cat ../RELEASE_NOTES.md >> "$tmpfile"
mv "$tmpfile" ../RELEASE_NOTES.md

pushd ..

##
# Update build.gradle app version
##
VERSION_NAME=$VERSION
VERSION_CODE=$(echo "$VERSION" | tr '.' '_')
APP_GRADLE=app/build.gradle.kts

sed -i '' "s/versionCode = .*/versionCode = ${VERSION_CODE}/" "$APP_GRADLE"
sed -i '' "s/versionName = .*/versionName = \"${VERSION_NAME}\"/" "$APP_GRADLE"

APP_BUNDLE=app-release.aab

if [[ -f "$APP_BUNDLE" ]]; then
  rm -r $APP_BUNDLE
fi

##
# Build signed App Bundle
##
cp app/build/outputs/bundle/release/app-release.aab $APP_BUNDLE

##
# Commit changes
##
git add .
git commit -m "Release $VERSION"
git tag $VERSION
git push origin master --tags

popd