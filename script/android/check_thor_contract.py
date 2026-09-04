#!/usr/bin/env python3

"""Verify the stable AYN Thor native/Java context and action identifiers."""

from pathlib import Path
import re
import sys


REPOSITORY_ROOT = Path(__file__).resolve().parents[2]
NATIVE_HEADER = REPOSITORY_ROOT / "src/fheroes2/game/thor_ui.h"
JAVA_PRESENTATION = REPOSITORY_ROOT / "android/app/src/main/java/org/fheroes2/ThorSecondScreenPresentation.java"


def parse_native_enum(source: str, enum_name: str, prefix: str) -> dict[str, int]:
    match = re.search(rf"enum class {enum_name}\s*:\s*int32_t\s*\{{(.*?)\}};", source, re.DOTALL)
    if match is None:
        raise ValueError(f"Unable to find native enum {enum_name}.")

    body = re.sub(r"/\*.*?\*/", "", match.group(1), flags=re.DOTALL)
    body = re.sub(r"//.*", "", body)
    identifiers: dict[str, int] = {}
    current_value = -1
    for entry in body.split(","):
        entry = entry.strip()
        if not entry:
            continue

        parts = [part.strip() for part in entry.split("=", maxsplit=1)]
        name = parts[0]
        if len(parts) == 2:
            current_value = int(parts[1], 0)
        else:
            current_value += 1
        identifiers[prefix + name] = current_value

    return identifiers


def parse_java_identifiers(source: str) -> dict[str, int]:
    return {
        match.group(1): int(match.group(2), 0)
        for match in re.finditer(r"(?:private\s+)?static final int ((?:CONTEXT|ACTION)_[A-Z0-9_]+)\s*=\s*(\d+);", source)
    }


def main() -> int:
    native_source = NATIVE_HEADER.read_text(encoding="utf-8")
    java_source = JAVA_PRESENTATION.read_text(encoding="utf-8")

    native_identifiers = parse_native_enum(native_source, "UiContext", "CONTEXT_")
    native_identifiers.update(parse_native_enum(native_source, "Action", "ACTION_"))
    java_identifiers = parse_java_identifiers(java_source)

    errors: list[str] = []
    for name, native_value in native_identifiers.items():
        if name not in java_identifiers:
            errors.append(f"Java is missing {name}={native_value}.")
        elif java_identifiers[name] != native_value:
            errors.append(f"{name} differs: native={native_value}, Java={java_identifiers[name]}.")

    for name, java_value in java_identifiers.items():
        if name not in native_identifiers:
            errors.append(f"Native code is missing Java identifier {name}={java_value}.")

    if errors:
        print("AYN Thor identifier contract check failed:", file=sys.stderr)
        for error in errors:
            print(f"- {error}", file=sys.stderr)
        return 1

    context_count = sum(name.startswith("CONTEXT_") for name in native_identifiers)
    action_count = sum(name.startswith("ACTION_") for name in native_identifiers)
    print(f"AYN Thor identifier contract passed: {context_count} contexts and {action_count} actions match.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
