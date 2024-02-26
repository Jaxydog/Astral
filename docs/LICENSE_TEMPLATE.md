The standard copyright notice is as follows:

```java
/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 * Copyright © 2023–2025 Name A
 * Copyright © 2024–2025 Name B
 *
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

#if( $originalComment.match("(Copyright © \d+(?:[-–]\d+)? [\w\d ]+)") != "" )
#set( $text = $originalComment.getText().replaceAll(" \*[ \/]?", "") )
#set( $added = [] )
#foreach( $line in $text.split("\r?\n")  )
#if( $line.startsWith("Copyright ©") && !$added.contains($line) && $added.add($line) )
$line
#end
#end
#else
Copyright © $today.year $username
#end

This file is part of $project.name.

$project.name is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

$project.name is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with $project.name. If not, see <https://www.gnu.org/licenses/>.
```
