#!/usr/bin/env bash
# Auditoria Kotlin/JVM portátil; não substitui os testes Gradle/Android.
set -euo pipefail
cd "$(dirname "$0")/.."
command -v kotlinc >/dev/null || { echo 'Kotlin compiler indisponível; use :core:test no Gradle'; exit 2; }
out=$(mktemp -d); trap 'rm -rf "$out"' EXIT
mapfile -t sources < <(find core/src/main/kotlin -name '*.kt' | sort)
kotlinc "${sources[@]}" tools/math_audit_probe.kt -include-runtime -d "$out/probe.jar"
java -jar "$out/probe.jar"
