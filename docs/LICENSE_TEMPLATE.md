The standard copyright notice is as follows:

```java
/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright (C) 2024-2025 Name A, Name B, Name C
 *
 * This file is part of Astral.
 *
 * Astral is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Astral is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Astral. If not, see <https://www.gnu.org/licenses/>.
 */
```

If you are using IntelliJ, or any other editor that supports Velocity template headers, you may use this template:

```text
SPDX-License-Identifier: AGPL-3.0-or-later

Copyright (C) $originalComment.match("Copyright \(C\) (\d+)", 1, "-", "$today.year")$today.year $originalComment.match("Copyright \(C\) \d+(?:-\d+)? ([\w\d, ]+)")

This file is part of $project.name.

$project.name is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

$project.name is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with $project.name. If not, see <https://www.gnu.org/licenses/>.
```
