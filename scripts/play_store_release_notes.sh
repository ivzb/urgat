#!/bin/bash

set -e

handle_error() {
    echo "## Urgat - $0 - An error occurred on line $1\n"
    usage
    exit 1
}

trap 'handle_error $LINENO' ERR

HAS_RELEASE_NOTES=0

extract_tag_content() {
    local last_git_tag=$(git describe --tags --abbrev=0)
    local search_key="$1"

    # Get content between tags from commits
    content=$(git log "${last_git_tag}..HEAD" --pretty=format:%B | awk -v tag="$search_key" '
      $0 ~ "<"tag">" {inside=1; next}
      $0 ~ "</"tag">" {inside=0; next}
      inside {print "- " $0}
    ')

    if [[ -n "$content" ]]; then
        HAS_RELEASE_NOTES=1
        key="$(echo "$search_key" | awk '{print toupper(substr($0,1,1)) substr($0,2)}')"
        printf "%s:\n%s\n\n" "$key" "$content"
    fi
}

extract_tag_content new
extract_tag_content improved
extract_tag_content fixed

if [[ $HAS_RELEASE_NOTES -eq 0 ]]; then
    printf "No release notes.\n\n"
fi