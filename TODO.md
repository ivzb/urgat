name and category should be auto-suggested in a dropdown while typing name
make main screens more generic
add preview screens instead of direct edit for todo, expense and note
add focus index to todo view model
UI visual issue when adding new items, compose is showing the date of the previous item?
add onboarding screen
consider moving uiEvent into the uiState
allow only one weight per day and update current one if added - also remove ObserveWeightsUseCase.kt transformation
setup github build pipeline
add button to add/edit investment screen from investments screen
check if DB search is case insensitive
refactor TopAppBars which is currently using hard-coded values, make it generic
add trackers screen empty state
add debounce to add/edit save button because it's possible to click it multiple times and it created multiple entities
add back button to inner screens
make the filter type dynamic so it doesn't rely on hard-coded values but on actual values from the db