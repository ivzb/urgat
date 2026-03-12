# Skill: Reading Git SHA Diffs Efficiently

## Purpose
Use this skill when you need to understand, document, or replicate changes from one or more git commits. 
It enables efficient extraction of diffs from single or multiple commit SHAs so an agent can analyze them, write documentation, or apply similar changes elsewhere.

---

## Prerequisites
- The workspace must be a git repository.
- Commit SHAs must be reachable from the current branch (or any local ref).
- **Submodules must be ignored.** Do not recurse into, inspect, or run git commands against submodules.
- **Always operate in the current working directory.** Never `cd` into other directories or look for other git repositories. All git commands must run against the repo rooted in the current directory.
- **Only inspect the exact SHAs provided by the user.** Do not walk history, infer ranges, or include any commits that were not explicitly listed.

---

## Commands

### 1. Single Commit Diff
Show the full diff introduced by one commit:
```bash
git --no-pager diff --ignore-submodules <SHA>~1 <SHA>
```
- `<SHA>~1` resolves to the first parent of `<SHA>`; this usually gives you what that commit changed relative to its parent.
  - This will fail for a root commit (which has no parent).
  - For a merge commit, this compares against the *first* parent only; use `<SHA>^2`, `<SHA>^3`, etc. if you need the diff to other parents, or use `git show <SHA>` with appropriate options.

### 2. Multiple Commits (Cherry-Picked, One by One)
When commits are scattered across the history, iterate over each SHA individually:
```bash
for SHA in <SHA1> <SHA2> <SHA3>; do
  echo "========== Commit $SHA =========="
  git --no-pager log --format="%H %s" -1 "$SHA"
  echo "---"
  git --no-pager diff --ignore-submodules "$SHA~1" "$SHA"
  echo ""
done
```
This prints a clear separator, the commit message, and the diff for each commit.

### 3. Stat Overview Before Full Diff
To get a quick summary of what files changed (useful to scope the work before reading full diffs):
```bash
git --no-pager diff --ignore-submodules --stat <SHA>~1 <SHA>
```
Or for multiple SHAs:
```bash
for SHA in <SHA1> <SHA2> <SHA3>; do
  echo "=== $SHA ==="
  git --no-pager diff --ignore-submodules --stat "$SHA~1" "$SHA"
done
```

### 4. Filter by Path or Extension
When diffs are large, narrow them down:
```bash
# Only Kotlin files
git --no-pager diff --ignore-submodules <SHA>~1 <SHA> -- '*.kt'

# Only a specific directory
git --no-pager diff --ignore-submodules <SHA>~1 <SHA> -- path/to/directory/

# Exclude test files
git --no-pager diff --ignore-submodules <SHA>~1 <SHA> -- . ':!**/test/**' ':!**/androidTest/**'
```

### 5. Name-Only List (For Planning)
When you just need to know which files were touched:
```bash
git --no-pager diff --ignore-submodules --name-only <SHA>~1 <SHA>
```
Or with status indicators (A=added, M=modified, D=deleted, R=renamed):
```bash
git --no-pager diff --ignore-submodules --name-status <SHA>~1 <SHA>
```

### 6. Commit Messages & Metadata
To understand intent before reading code:
```bash
git --no-pager log --format="commit %H%nAuthor: %an%nDate: %ad%n%n    %s%n%n    %b" <SHA1> <SHA2> <SHA3> --no-walk
```
`--no-walk` prevents git from walking the history between them; it shows only the listed commits.

---

## Agent Workflow

### Step A — Scope
1. Receive one or more commit SHAs from the user.
2. Run **name-status** for all SHAs to understand the surface area.
3. Decide whether to read full diffs, or filter by path/extension.

### Step B — Read
4. Run full diffs (or filtered diffs) using the commands above.
5. If the output is very large (>500 lines per commit), split by file path:
   ```bash
   git --no-pager diff --ignore-submodules <SHA>~1 <SHA> -- <specific_file>
   ```

### Step C — Analyze & Act
6. Summarize the changes per commit (files touched, what was added/removed/modified).
7. Depending on the task:
   - **Documentation**: Write a changelog or technical summary based on the diffs.
   - **Replication**: Identify the pattern in the diffs and apply analogous changes to the target files.

---

## Tips & Edge Cases

| Scenario | Solution |
|---|---|
| SHA doesn't exist locally | Run `git fetch --all` first |
| Diff output is too large for context | Use `--stat` first, then read per-file diffs |
| Binary files in diff | Add `--no-binary` to skip binary content |
| Need surrounding context lines | Use `-U<n>` e.g. `git --no-pager diff --ignore-submodules -U10 <SHA>~1 <SHA>` for 10 lines of context |
| Whitespace-only changes cluttering diff | Add `-w` to ignore whitespace |
| Renamed files not showing properly | Add `-M` (or `-M90%` for 90% similarity threshold) |
| zsh treats `$VAR` as one token (no word-split) | Always use arrays: `SHAS=(sha1 sha2)` and iterate with `"${SHAS[@]}"` — works in both bash and zsh |

---

## Template: Multi-SHA Comprehensive Read

Copy-paste ready. Replace the SHA list:

> **⚠️ Shell compatibility note:** In **zsh** (default on macOS), `$VAR` does **not** 
> word-split on spaces like bash does. Using `for SHA in $SHAS` with a plain string 
> will treat the entire value as one token, causing "File name too long" errors.
> Always use an **array** `( )` as shown below — this works correctly in both bash and zsh.

```bash
SHAS=(abc1234 def5678 ghi9012)

echo "===== FILE OVERVIEW ====="
for SHA in "${SHAS[@]}"; do
  echo "--- $SHA: $(git --no-pager log --format='%s' -1 $SHA) ---"
  git --no-pager diff --ignore-submodules --name-status "$SHA~1" "$SHA"
  echo ""
done

echo "===== FULL DIFFS ====="
for SHA in "${SHAS[@]}"; do
  echo "=========================================="
  echo "Commit: $SHA"
  git --no-pager log --format="Message: %s%nAuthor: %an | Date: %ad" -1 "$SHA"
  echo "=========================================="
  git --no-pager diff --ignore-submodules "$SHA~1" "$SHA"
  echo ""
done
```