# Template CLI

This CLI generates a renamed application from the canonical [`app/`](../../app) template.

To understand how the generated application works (runtime, architecture, setup), see:

- [`app/README.md`](../../app/README.md)

## Usage

Generate a new app:

```bash
npm run template:init -- --name "My Cool App"
```

Generate into a specific output directory:

```bash
npm run template:init -- --name "My Cool App" --output ./generated/my-cool-app
```

Dry run without writing files:

```bash
npm run template:init -- --name "My Cool App" --dry-run
```

Overwrite an existing generated directory:

```bash
npm run template:init -- --name "My Cool App" --force
```

Check the template source drift baseline:

```bash
npm run template:check-source-drift
```

Run the CLI tests:

```bash
npm run template:test
```

## Naming rules

- App name input drives all derived names.
- Folder, artifact, module, Docker, and container names use kebab-case.
- Java packages use a compact lowercase token under `com.<token>`.

Example:

- Input: `My Cool App`
- Slug: `my-cool-app`
- Java package: `com.mycoolapp`

## Validation

Generation is fail-closed:

- the CLI rewrites known template placeholders
- then scans generated output for forbidden legacy tokens
- generation fails if forbidden tokens remain

The repo also includes a source-drift check for `app/` so future changes that alter legacy-token usage are surfaced explicitly.
