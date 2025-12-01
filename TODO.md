add preview screens instead of direct edit for todo, expense, note and investment
name and category should be auto-suggested in a dropdown while typing name
make main screens more generic
add focus index to todo view model
add onboarding screen
allow only one weight per day and update current one if added - also remove ObserveWeightsUseCase.kt transformation
setup github build pipeline
delete functionality when there is a details screen for each entity
consider making generic add/edit screen with forms abstraction
add multiselect delete
split todos, expenses in 2 column in 10" tablet
check which weight is taken when there are multiple for the same day
add localizations
add budgeting (split into income and expenses)
add budgeting pie charts (for income and expenses)
generate screenshots for google play store via UI tests
check why the submit button is not ime positioned on top of the keyboard for sdk 31
update to jetpack navigation 3
make error tracker
consider showing text field labels because editing investment is unclear
update dependencies
write UI tests
add unit tests for LibraryViewModel sample data use case
deal with too many items - pagination?
check if app db is accessible in release and encrypt it
announce that the info in the app is encrypted
add preview to notes because long notes are not readable
longpress on a note should copy it
select today's weight from DB if exists
add export to csv
consider having sample use case for each entity and use appropriate one in each main view model
rename todo to todos
improve add buttons (e.g. add todo, add note) in landscape
move weight tracker to wellbeing tab
add investments overview / dashboard
check why search if missing from finance screen, but visible for overview
make trackers empty state same as library and finance