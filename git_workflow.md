# Git workflow
### Clone repository
```sh
$ git clone https://github.com/hulthe/DAT255.git
$ cd DAT255
```

### Create branch
```sh
$ git checkout -b your/branch/name
```

#### Branch naming conventions
Group branches by one of the following key words:
  - `fix`
  - `bug`
  - `feat`
  - `doc`

Examples:
  - `feat/advanced_interface`
  - `bug/networking/timeout`
  - `fix/api/documentation`

### Creating commits
A commit should ...
  - ...never do more than one thing
  - ...be runnable on its own

Commit messages should...
  - ...describe what the commit changes
  - ...be formatted according to the 50/72 convention
    - First line is 50 characters or less
    - Then a blank line
    - Remaining text should be wrapped at 72 characters
  - ...be written in infinitive
    - e.g. *Add thing* rather than *Adds*, *Added* or *Adding*

Example:
```
Add search functionality

Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do
eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
aliquip ex ea commodo consequat. Duis aute irure dolor in
reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
pariatur. Excepteur sint occaecat cupidatat non proident, sunt in
culpa qui officia deserunt mollit anim id est laborum.
```

### Creating pull requests
When you feel like your code does what it's supposed to do create a pull request into the `master` branch.

  - A pull request should resolve a user story or a task.
  - In the description there should exist some kind of acceptance criteria for the reviewer to check for, as well as a reference to the user story or task.
  - You **NEVER** merge your own pull request *unless* someone **else** has reviewed it and approved of the merge.

### Reviewing and merging pull requests
When you review a pull request you should always read **ALL** the changes that has been made and test that the code actually works on **YOUR** machine.
If you have any criticism make sure it is constructive (i.e. make a suggestion for a fix).

### Resolving merge conflicts
If you pull and receive a merge conflict, don't panic.
Git is going to recommend a merge, however, a merge will result in a ugly extra, unnecessary, commit.
Instead what you'll want to do is the following:

  1. `git merge --abort`
  2. `git rebase origin/<active_branch> <active_branch>`
  3. Manually resolve all conflicts
  4. Finalize by `git add <conflicting files>`
  5. `git rebase --continue`

**Remember:** This is only a cheat-sheet, git will help you along the way!
