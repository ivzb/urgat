## Technical debt
- make proper urgat CLI with multiple commands instead of having multiple scripts
- consider extracting a framework
- add focus index to todo view model
- setup github build pipeline
- make error tracker
- consider having sample use case for each entity and use appropriate one in each main view model
- write UI tests
- consider making generic add/edit screen with forms abstraction
- make add/edit screens generic

## Improvements
- add localizations
- add paste from clipboard button for todos and notes
- show user message when clear todos button is clicked - and tell how many todos were cleared
- combine investment category items with the same name into single item with multiple rows
- change add note icon
- change investment cost to market price
- add optional fee to investment
- add checkbox which file to be imported/exported
- move the exclude from analytics checkbox to preview screen

## Bugs
- check why the submit button is not ime positioned on top of the keyboard for sdk 31
- check how to show text field labels because editing investment is unclear
- there is a diff in some pixels when doing screenshots - it might be caused by the font - check how to fix it or look for another solution for screenshots
- fix edge to edge recommendation from play store
- fix pie chart in landscape and tablet (put them in 2 columns side by side)
- when lists are not long enough the FAB stays on top of last item 
- close keyboard when going back from add/edit investment to analytics screen / transactions

## Spikes
- check if app db is accessible in release and encrypt it
- check if list items should be improved in dark mode
- check what happens if one side of the notes has a lot more text than the other side
- review @Previews and documents obvious issues
- review screenshots and documents obvious UI issues
- inspect all classes using inheritance and change them to composition
- consider: read current prices on app start for all owned assets and calculate current market price?
- check if it's possible to achieve staggered layout in notes without 2 columns because they have 2 different scrolls

## Features
- consider adding version control (git) support in the app - maybe something like export -> push to git repo
- encrypt the db and announce that the info in the app is encrypted in play store app description
- make tablet support list/detail
- long-press on a note should copy it
- add multiselect delete
- add onboarding screen
- name and category should be auto-suggested in a dropdown while typing name
- add preview screens with switch button between preview/edit for each individual todo, expense, note and investment (long todos and notes are not readable)
- add info cards on the place of top buttons (use current design for top header buttons - check git history)
- add investments overview / dashboard
- add category to notes
- add tags to expenses
- add links feature to library
- calculate current price and show profit/loss in investments screen for each asset
- calculate current price of assets based on latest transaction market price
- handle share link to Urgat and save it
- add system unlock with biometric/pin when opening the app
- show expense categories as chips at the bottom of add expense
- make expenses chart like github contributions
- make todos a ticketing system following TODOS.md structure - add categories and priorities - then visualise the todos split by categories
- make the app hledger compatible - https://hledger.org/
- make another entity in finance tab (FIRE) - calculate FIRE value by avg(summed expenses for every month) * 12 * 25 - calculate money, % and time left according to current growth rate
- make remaining budget which is calculated by salary - investments - expenses
- add budgeting (split into income and expenses)
- add budgeting pie charts (for income and expenses)