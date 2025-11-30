### Release notes
Use the following command to generate release notes for app release to Google Play store.
`release_notes.sh` script accepts two arguments: start and end git tags.

```bash
./release_notes.sh v25.11.0 v25.12.1
```

To include release notes for a feature, improvement, or bug fix on the Google Play Store, 
add the following format to your commit message:

- What's new: 
```
<new>
Notes: Add titles and tags to organize your thoughts better.
Expenses: Track recurring expenses automatically for smarter budgeting.
To-Dos: Create subtasks and set priorities for easier task management.
</new>
```

- Improvements:
```
<improved>
Faster app startup and smoother navigation throughout the app.
Enhanced search functionality across Notes, To-Dos, and Expenses.
</improved>
```

- Bug fixes:
```
<fixed>
Fixed an issue where some notifications were not appearing.
</fixed>
```


The following release notes will be generated:

```
New
- Notes: Add titles and tags to organize your thoughts better.
- Expenses: Track recurring expenses automatically for smarter budgeting.
- To-Dos: Create subtasks and set priorities for easier task management.

Improved
- Faster app startup and smoother navigation throughout the app.
- Enhanced search functionality across Notes, To-Dos, and Expenses.

Fixed
- Fixed an issue where some notifications were not appearing.
```