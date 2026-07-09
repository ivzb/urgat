#!/bin/bash

# Validates that JAVA_HOME and ANDROID_HOME are set.
# These should be exported in ~/.bashrc (see README.md for setup instructions).

if [[ -z "${JAVA_HOME}" ]]; then
  echo "#############################"
  echo "## Urgat: JAVA_HOME is not set."
  echo "## Add 'export JAVA_HOME=\"/path/to/jdk\"' to ~/.bashrc"
  echo "#############################"
  exit 1
fi

if [[ -z "${ANDROID_HOME}" ]]; then
  echo "#############################"
  echo "## Urgat: ANDROID_HOME is not set."
  echo "## Add 'export ANDROID_HOME=\"/path/to/Android/Sdk\"' to ~/.bashrc"
  echo "#############################"
  exit 1
fi
