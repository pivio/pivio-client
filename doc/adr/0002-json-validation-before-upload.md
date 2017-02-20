# 2. JSON validation before upload

Date: 20/02/2017

## Status

Accepted

## Context

Since the server accepts any json values, little typos made it not always clear if a data structure fits the expected values (esp. those defined in http://pivio.io/docs/#section-dataformat). If there was an array instead of a simple value querying those are hard.

## Decision

To have better feedback for the user if a data structure is not the one the server might be expecting (see URL above) we introduced json schema validation.

## Consequences

Other ways into the server (e.g. with curl) are still be able to send 'invalid' data.
