make proper urgat CLI with multiple commands instead of having multiple scripts
consider extracting a framework
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
check why the submit button is not ime positioned on top of the keyboard for sdk 31
update to jetpack navigation 3
make error tracker
consider showing text field labels because editing investment is unclear
update dependencies
add unit tests for LibraryViewModel sample data use case
deal with too many items - pagination?
check if app db is accessible in release and encrypt it
announce that the info in the app is encrypted
add preview to notes because long notes are not readable
longpress on a note should copy it
select today's weight from DB if exists
add export to csv
consider having sample use case for each entity and use appropriate one in each main view model
add investments overview / dashboard
check why search if missing from finance screen, but visible for overview
add category to notes
add tags to expenses
make tablet support list/detail
write UI tests
there is a diff in some pixels when doing screenshots - it might be caused by the font - check how to fix it or look for another solution for screenshots
encrypt the db
check what happens if one side of the nots has a lot more text than the other side
make the investments tab show net worth. then show each investment combined, then show transactions
calculate total net worth, calculate 25 x yearly costs, calculate % to FI
make remaining budget which is calculated by salary - investments - expenses
check if list items should be improved in dark mode
make splash screen dark mode
review @Previews and fix obvious issues
review screenshots and fix obvious UI issues
add info cards on the place of top buttons (use current design for top header buttons - check git history)
make todos, expenses and investments 2 per row in landscape because there is a lot of empty space
group expenses by month
add links feature to library