#!/bin/bash

set -e

handle_error() {
    echo "## Urgat - $0 - An error occurred on line $1\n"
    usage
    exit 1
}

trap 'handle_error $LINENO' ERR

if [[ $# -ne 2 ]]; then
    echo "Usage: $0 <start git tag> <end git tag>"
    exit 1
fi

START_GIT_TAG="$1"
END_GIT_TAG="$2"

# Validate that tags exist in the repo
if ! git rev-parse "$START_GIT_TAG" >/dev/null 2>&1; then
    echo "Error: start tag '$START_GIT_TAG' does not exist."
    exit 1
fi

if ! git rev-parse "$END_GIT_TAG" >/dev/null 2>&1; then
    echo "Error: end tag '$END_GIT_TAG' does not exist."
    exit 1
fi

extract_tag_content() {
    local start_git_tag="$1"
    local end_git_tag="$2"
    local search_key="$3"

    # Get content between tags from commits
    content=$(git log "${start_git_tag}..${end_git_tag}" --pretty=format:%B | awk -v tag="$search_key" '
      $0 ~ "<"tag">" {inside=1; next}
      $0 ~ "</"tag">" {inside=0; next}
      inside {print "- " $0}
    ')

    if [[ -n "$content" ]]; then
        key="$(echo "$search_key" | awk '{print toupper(substr($0,1,1)) substr($0,2)}')"
        printf "%s:\n%s\n\n" "$key" "$content"
    fi
}

extract_tag_content "$START_GIT_TAG" "$END_GIT_TAG" new
extract_tag_content "$START_GIT_TAG" "$END_GIT_TAG" improved
extract_tag_content "$START_GIT_TAG" "$END_GIT_TAG" fixed