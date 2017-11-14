#!/bin/bash
# ------------------------------------------------------------------
# Simple wrapper to create the dependencies.json form node modules
# ------------------------------------------------------------------


# --- body ---------------------------------------------------------

cd /source
/node_modules/license-checker/bin/license-checker --json > dependencies_docker.json