# Contribution Guidelines

### Submitting Changes

When working on contributions to this project,
please [create a pull request](https://github.com/Jaxydog/Astral/pull/new/1.20) containing your commits and (preferably)
a list of what has been added, removed, and / or otherwise changed.

Please also do your best to ensure that your changes follow the conventions outlined below!

### Programming Conventions

This is fairly pedantic, so I apologize in advance for that fact, however I feel that making the source code highly
regular will make it easier to understand and reason about.

- **Every file (that supports comments) should start with a copyright notice!**
  See [the license template](LICENSE_TEMPLATE.md) for a
  template.
- Code should be written to be as readable as possible on a best-effort basis.
- Code should be indented using 4 spaces.
    - Empty lines should not have any indentation.
- Lines should generally not exceed 120 characters.
- Line endings should be configured to be Unix-style (`\n`), *not* Windows-style (`\r\n`).
- Spaces should always be added in the following areas:
    - Around binary operators (`a + b`, `n |= 0x4`) but *not* around unary operators (`if (!false) {}`, `int a = -5`).
    - After commas (`[1, 2, 3]`, `this.method(this.field, true)`).
    - After keywords (`if (...) {}`, `try {} catch (...) {}`)
- If / While statements may be single-line *only if* they do not contain an `else` block, and their definitions are
  "relatively simple" (e.g. `if (value != null) return a || b`).
- Empty brackets should not continue onto multiple lines, except for in class declarations.
- Mixin classes should always be named `[BaseClass]Mixin`, and accessors should always be `[BaseClass]Access`
- Mixin method names should represent what function they provide (i.e. `modifyPlayerHealth`).
- Ternary statements are only allowed for simple logic that is easy to reason about.
    - Ternary statements should *never* be nested.
- **Absolutely** leave *useful* comments wherever anything may be unclear.

This list is non-exhaustive and may be added to or otherwise amended in the future. For anything not listed here, just
use your best judgement and do your best to keep the codebase clean.

Thank you for contributing! Help is always appreciated <3 ~Jaxydog
