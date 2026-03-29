# Urgat Mobile App – New Feature Creation

## Overview

This skill guides the creation of new features in the Urgat mobile app. The project uses the MVI (Model-View-Intent) pattern with Jetpack Compose UI.
The project structure is the following

app/src/main/java/com/urgat/

data/
di/
domain/
navigation/
ui/
util/

Important: Before writing any code, read existing files in the relevant feature area to understand the app's architecture and conventions.

## Feature Discovery Process
1. Understand the Feature
What problem does this feature solve?
What is its purpose and expected outcome?
Who is the target user?

2. Determine Feature Placement
Is this feature for adding or editing information?
Does it belong as a sub-tab of a main screen (Library / Finance / Wellbeing)?
Or is it a sub-tab of an inner screen?

2. Propose Location
Suggest the appropriate module/package for this feature.
Confirm with the user whether the suggested location is correct or if it should be placed elsewhere.

3. If it is sub-tab to an existing screen start by looking for the class that implement the Page interface and add it there. Continue by handling the newly added page in the existing screen.