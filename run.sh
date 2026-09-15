#!/usr/bin/env bash
# Käivita rakendus: loeb võtme .env-ist ja paneb Spring Booti tööle (H2 profiil, port 8080).
set -e
cd "$(dirname "$0")"
set -a; . ./.env; set +a
cd app && exec ./gradlew bootRun "$@"
