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

./run_unit_tests.sh

./run_ui_tests.sh

./play_store_screenshots.sh

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

git add .
git commit -m "Release $VERSION"
git tag $VERSION
git push

popd
