# Contribution Guidelines

When contributing or modifying this project, please create pull requests that clearly outline what has been added,
removed, and / or otherwise modified. It is also important that your commits abide by the rules outlined below.

I will be *significantly* more lenient with contributions from new developers, just be aware that I may modify any
committed code to better suit this project. And as always I'll be around to help if you run into trouble.

Thank you for contributing and happy coding! ~ Jaxydog <3

### Commits & Pull Requests

Commit messages are to be clear and concise. They should properly describe what was changed within the commit.

Pull requests should explain in detail what is being changed and why. It's expected that they will be as informative as
possible within reason, which may vary on a case-by-case basis.

Committed code must abide by the guidelines provided by this document. A single commit must not prevent the project from
building, unless multiple commits are pushed or merged at once. These same rules apply for pull requests.

### Changelog Updates

Changes that you make should be reflected within [CHANGELOG.md](CHANGELOG.md). Any changes that affect the mod's in-game
content directly should be placed within the `Content Changes` category. Any changes that only affect internal structure
or mechanisms should be placed within `Internal Changes`.

Version headers should contain a brief summary of what was added or changed. If the current version's summary is
incomplete, update it. If the current version has been published, either add a new patch or minor release above the
current version, depending on what was changed.

Astral follows [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html).

### Copyright Notices

This project requires you to supply copyright headers within each source file, as instructed by the GNU Project's
[copyright guidelines](https://www.gnu.org/licenses/gpl-howto.html). You can find Astral's sample license template
within [LICENSE_TEMPLATE.md](LICENSE_TEMPLATE.md).

When creating a new file, be sure to prepend this template at the start of the file, and then update the author to use
the current year and your name. If a changed file already contains this header, simply add your own name on a new line.

It should be noted that because Astral is licensed under the GNU Affero General Public License (AGPL-3.0-or-later),
all contributed code must remain free and open source forever. If you are not content with this, please do not
contribute to this project.

### Programming Conventions

Below is a list of rules and guidelines for how code should be written and formatted. This will be very pedantic,
however having these rules ensures that all code within this project remains consistent with multiple contributors. This
list is non-exhaustive, and defined on a best-effort basis. If information is missing or should be clarified, feel free
to update or modify it.

Code should be made as clear and understandable as possible.

- All classes, methods, and fields should be documented.
- Code logic should be easy to follow as possible, within reason. If something is confusing, its explanation or
  reasoning should be provided in a comment.
- The usage of `var` is prohibited, unless describing an incredibly long and complex type.
- Non-static field and member accesses or invocations should be prefixed with `this.`.
    - e.g., `this.methodCall()`, `this.innerField += 1`.
- Static field and member accesses should *not* be prefixed with the class' name.

Code is to be indented using four spaces, not tabs.

- Empty lines never contain indentation.
- Lines should never exceed 120 characters.
    - Anything that exceeds this limit should be 'chopped down'.
- Line endings are always Unix-style (LF), not Windows-style (CRLF).
- Empty brace pairs should never extend onto multiple lines.
    - The only exception to this rule is class declarations.
- If / For / While statements may only be single-line if they are 'simple'.
    - e.g., `while (number >= 5) number /= 2`, `if (!condition) return`.
    - Ternary statements are only permitted for very simple logic.
    - Ternary statements may never be nested.
    - If statements with an else block must always be multi-line.

Spacing should be added for the sake of readability.

- Binary operators (e.g., '+', '/=', '=') should always be surrounded by spaces.
- Commas should always be followed by a space.
- Keywords should always be followed by a space.
- Code within single-line brace pairs should be surrounded by spaces.
- Items should never be aligned through space padding. I find that this usually *hinders* readability.

Names should be short but clear and readable.

- Interface names *do not* start with 'I'.
- Mixins should be named using the format 'ClassNameMixin'
    - e.g., `ItemStackMixin`, `ClientPlayNetworkingMixin`.
- Accessors should be named using the format 'ClassNameAccessor'.
    - e.g., `ItemStackAccessor`, `ClientPlayNetworkingAccessor`.
- Mixin method names should describe their functionality, and should not just be named `methodInject`.
- All variables must be named using camelCase.
- Static, final variables must be named using SCREAMING_SNAKE_CASE.
- Enum variants must be named using SCREAMING_SNAKE_CASE.

Code should be as performant and 'safe' as possible.

- Fields, methods, and inner classes should be private unless wider access is necessary.
- Methods that throw must provide a throws clause.
- Repeated or unnecessary allocations should be minimized.
- Supplier fields should be memoized if at all possible.
- Any field that can be `null` should be annotated with `@Nullable`, and any non-nullable field is encouraged to be
  annotated with `@NotNull`.
- Classes that are intended to be non-extendable should be marked as final or sealed.
- Variables that are not re-assigned to should be marked as final.
- Types used within catch clauses should be as specific as possible.
