## Technical debt
- make proper urgat CLI with multiple commands instead of having multiple scripts
- consider extracting a framework
- make main screens more generic
- add focus index to todo view model
- setup github build pipeline
- consider making generic add/edit screen with forms abstraction
- make error tracker
- add unit tests for LibraryViewModel sample data use case
- consider having sample use case for each entity and use appropriate one in each main view model
- write UI tests
- write unit tests for Group.kt functions

## Improvements
- allow only one weight per day and update current one if added - also remove ObserveWeightsUseCase.kt transformation
- add localizations
- deal with too many items - pagination?
- select today's weight from DB if exists
- make splash screen dark mode

## Bugs
- check why the submit button is not ime positioned on top of the keyboard for sdk 31
- check how to show text field labels because editing investment is unclear
- there is a diff in some pixels when doing screenshots - it might be caused by the font - check how to fix it or look for another solution for screenshots
- left column scroll doesn't work when there are a lot of notes

## Spikes
- check if app db is accessible in release and encrypt it
- check if list items should be improved in dark mode
- check what happens if one side of the nots has a lot more text than the other side
- review @Previews and documents obvious issues
- review screenshots and documents obvious UI issues
- inspect all classes using inheritance and change them to composition
- consider renaming Investment to InvestmentTransaction
- check if it's possible to combine all async checks internally and not in the view model (becomes too ugly with many async checks)
- consider renaming amount to shares in Investment

## Features
- add drawer with app version, settings, sample data button, etc
- encrypt the db and announce that the info in the app is encrypted in play store app description
- add export to csv
- make tablet support list/detail
- longpress on a note should copy it
- add multiselect delete
- add onboarding screen
- name and category should be auto-suggested in a dropdown while typing name
- add preview screens with switch button between preview/edit for each individual todo, expense, note and investment (long todos and notes are not readable)
- add info cards on the place of top buttons (use current design for top header buttons - check git history)
- add budgeting (split into income and expenses)
- add budgeting pie charts (for income and expenses)
- add investments overview / dashboard
- add category to notes
- add tags to expenses
- add links feature to library
- calculate total net worth, calculate 25 x yearly costs, calculate % to FI
- make remaining budget which is calculated by salary - investments - expenses
- make investments net worth calculate current price and show profit/loss in investments screen for each asset
- make the investments tab show net worth. then show each investment combined, then show transactions
- make net worth pie chart showing color for each category (list with colors and numbers below) and total net worth in the middler of the chart - https://play.google.com/store/apps/details?id=com.gianmarcodavid.networth&hl=en_AU
- investments screen: on asset click