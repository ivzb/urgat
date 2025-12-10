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

##
# Check if jq is installed
##
if ! command -v jq >/dev/null 2>&1; then
    echo "#############################"
    echo "## Urgat: Error: jq is not installed. Please install it before running this script."
    echo "## brew install jq"
    echo "#############################"
    exit 1
fi

pushd ..

##
# Config
##
PACKAGE_NAME="com.ivzb.urgat"
# internal, alpha, beta, production
TRACK="production"
AAB_FILE="app/build/outputs/bundle/release/app-release.aab"
SERVICE_ACCOUNT_JSON="play_store_service_key.json"
SCOPES="https://www.googleapis.com/auth/androidpublisher"
LANGUAGE_CODE="en-US"
RELEASE_NOTES=$(./scripts/play_store_release_notes.sh)

##
# Get access token
##
gcloud auth activate-service-account --key-file=$SERVICE_ACCOUNT_JSON
ACCESS_TOKEN=$(gcloud auth print-access-token \
    --credential-file-override=$SERVICE_ACCOUNT_JSON \
    --scopes=https://www.googleapis.com/auth/androidpublisher)

if [ -z "$ACCESS_TOKEN" ] || [ "$ACCESS_TOKEN" == "null" ]; then
  echo "#############################"
  echo "## Urgat: Failed to get access token"
  echo "#############################"
  exit 1
fi

echo "#############################"
echo "## Urgat: Got access token"
echo "#############################"

##
# Create an edit
##
EDIT=$(curl -s -X POST \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/$PACKAGE_NAME/edits")

EDIT_ID=$(echo "$EDIT" | jq -r '.id')

if [ -z "$EDIT_ID" ] || [ "$EDIT_ID" == "null" ]; then
  echo "#############################"
  echo "## Urgat: Failed to get edit id: $EDIT"
  echo "#############################"
  exit 1
fi

echo "#############################"
echo "## Urgat: Created edit: $EDIT_ID"
echo "#############################"

##
# Upload app bundle
##

# clear extended attributes
xattr -c "$AAB_FILE"

UPLOAD=$(curl -s -X POST \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/octet-stream" \
  --data-binary @"$AAB_FILE" \
  "https://androidpublisher.googleapis.com/upload/androidpublisher/v3/applications/$PACKAGE_NAME/edits/$EDIT_ID/bundles")

VERSION_CODE=$(echo "$UPLOAD" | jq -r '.versionCode')

if [ -z "$VERSION_CODE" ] || [ "$VERSION_CODE" == "null" ]; then
  echo "#############################"
  echo "## Urgat: ❌ Failed to upload bundle: $UPLOAD"
  echo "#############################"
  exit 1
fi

echo "#############################"
echo "## Urgat: Uploaded bundle, versionCode: $VERSION_CODE"
echo "#############################"

##
# Assign to track
##
TRACK_RESPONSE=$(curl -s -X PUT \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
        "releases": [
          {
            "name": "Automated Release '"$VERSION_CODE"'",
            "versionCodes": ["'"$VERSION_CODE"'"],
            "status": "completed",
            "releaseNotes": [
              {
                "language": "'"$LANGUAGE_CODE"'",
                "text": "'"$RELEASE_NOTES"'"
              }
            ]
          }
        ]
      }' \
  "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/$PACKAGE_NAME/edits/$EDIT_ID/tracks/$TRACK")

TRACK_NAME=$(echo "$TRACK_RESPONSE" | jq -r '.track')

if [ -z "$TRACK_NAME" ] || [ "$TRACK_NAME" == "null" ]; then
  echo "#############################"
  echo "## Urgat: ❌ Failed to assign bundle to track: $TRACK_RESPONSE"
  echo "#############################"
  exit 1
fi

echo "#############################"
echo "## Urgat: Assigned bundle to track: $TRACK"
echo "#############################"

echo "#############################"
echo "## Urgat: Added release notes for version $VERSION_CODE in $LANGUAGE_CODE."
echo "#############################"

PLAY_STORE_SCREENSHOTS="play_store_screenshots"
PLAY_STORE_SCREENSHOTS_PHONE="$PLAY_STORE_SCREENSHOTS/screenshots-phone"
PLAY_STORE_SCREENSHOTS_TABLET_7="$PLAY_STORE_SCREENSHOTS/screenshots-tablet7"
PLAY_STORE_SCREENSHOTS_TABLET_10="$PLAY_STORE_SCREENSHOTS/screenshots-tablet10"

PLAY_STORE_SCREENSHOTS_DIRECTORIES=(
  "$PLAY_STORE_SCREENSHOTS_PHONE:phoneScreenshots"
  "$PLAY_STORE_SCREENSHOTS_TABLET_7:sevenInchScreenshots"
  "$PLAY_STORE_SCREENSHOTS_TABLET_10:tenInchScreenshots"
)

for pair in "${PLAY_STORE_SCREENSHOTS_DIRECTORIES[@]}"; do
    DIR_PATH="${pair%%:*}"
    ASSET_TYPE="${pair##*:}"

    echo "#############################"
    echo "## Urgat: Starting Upload for Device Type: $ASSET_TYPE from $DIR_PATH"
    echo "#############################"

    if [ ! -d "$DIR_PATH" ]; then
        echo "#############################"
        echo "## Urgat: ❌ Error: Directory not found: $DIR_PATH"
        echo "#############################"
        exit 1
    fi

    echo "#############################"
    echo "## Urgat: Clearing existing $ASSET_TYPE assets..."
    echo "#############################"

    DELETED_IMAGES_RESPONSE=$(curl -s -X DELETE \
      "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/$PACKAGE_NAME/edits/$EDIT_ID/listings/$LANGUAGE_CODE/$ASSET_TYPE" \
      -H "Authorization: Bearer ${ACCESS_TOKEN}")

    DELETED_IMAGES=$(echo "$DELETED_IMAGES_RESPONSE" | jq -r '.deleted')

    if [ -z "$DELETED_IMAGES" ] || [ "$DELETED_IMAGES" == "null" ]; then
      echo "#############################"
      echo "## Urgat: ❌ Failed to delete images for $ASSET_TYPE: $DELETED_IMAGES_RESPONSE"
      echo "#############################"
      exit 1
    fi

    for FILE_PATH in "$DIR_PATH"/*; do
        # Skip directories
        if [ -d "$FILE_PATH" ]; then
            continue
        fi

        FILE_NAME=$(basename "$FILE_PATH")
        echo "#############################"
        echo "## Urgat: Uploading $FILE_NAME to $ASSET_TYPE..."
        echo "#############################"

        if [[ "$FILE_NAME" == *.png ]]; then
            CONTENT_TYPE="image/png"
        elif [[ "$FILE_NAME" == *.jpg || "$FILE_NAME" == *.jpeg ]]; then
            CONTENT_TYPE="image/jpeg"
        else
            echo "#############################"
            echo "## Urgat: ❌ Error: Unsupported content type: $FILE_NAME"
            echo "#############################"
            exit 1
        fi

        UPLOAD_RESPONSE=$(curl -s -X POST \
          -H "Authorization: Bearer $ACCESS_TOKEN" \
          -H "Content-Type: $CONTENT_TYPE" \
          --data-binary @"$FILE_PATH" \
          "https://androidpublisher.googleapis.com/upload/androidpublisher/v3/applications/$PACKAGE_NAME/edits/$EDIT_ID/listings/$LANGUAGE_CODE/$ASSET_TYPE?uploadType=media")

        if echo "$UPLOAD_RESPONSE" | grep -q "error"; then
            echo "#############################"
            echo "## Urgat: ❌ ERROR uploading $FILE_NAME: $UPLOAD_RESPONSE"
            echo "#############################"
            exit 1
        else
            IMAGE_ID=$(echo "$UPLOAD_RESPONSE" | jq -r '.id' 2>/dev/null)
            echo "#############################"
            echo "## Urgat: Successfully uploaded $FILE_NAME (Image ID: $IMAGE_ID)"
            echo "#############################"
        fi

    done

    echo "#############################"
    echo "## Urgat: Finished Upload for $ASSET_TYPE"
    echo "#############################"
done

VALIDATE=$(curl -s -X POST \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/$PACKAGE_NAME/edits/$EDIT_ID:validate")

VALIDATE_ID=$(echo "$VALIDATE" | jq -r '.id')

if [ -z "$VALIDATE_ID" ] || [ "$VALIDATE_ID" == "null" ]; then
  echo "#############################"
  echo "## Urgat: Edit is NOT valid: $VALIDATE"
  echo "#############################"
  exit 1
fi

echo "#############################"
echo "## Urgat: Edit is valid"
echo "#############################"

##
# Commit the edit
##
curl -s -X POST \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/$PACKAGE_NAME/edits/$EDIT_ID:commit"

echo "#############################"
echo "## Urgat: Edit committed. Release submitted!"
echo "#############################"

popd