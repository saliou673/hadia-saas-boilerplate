"use strict";

const path = require("path");

const { generateTemplate } = require("./generate-template");
const { assertSourceDriftBaseline } = require("./scanner");
const { getTemplateDir } = require("./template-manifest");

function main(argv = process.argv.slice(2), rootDir = process.cwd()) {
  const [command, ...rest] = argv;

  if (!command || command === "--help" || command === "-h") {
    printHelp();
    return 0;
  }

  if (command === "init") {
    const args = parseArgs(rest);
    if (!args.name) {
      throw new Error("Missing required argument: --name \"App Name\"");
    }

    const summary = generateTemplate({
      name: args.name,
      outputDir: args.output,
      templateDir: args.templateDir || getTemplateDir(rootDir),
      force: Boolean(args.force),
      dryRun: Boolean(args["dry-run"]),
    });

    printGenerationSummary(summary, Boolean(args["dry-run"]));
    return 0;
  }

  if (command === "check-source-drift") {
    const summary = assertSourceDriftBaseline(getTemplateDir(rootDir));
    console.log("Template source drift baseline matches.");
    console.log(JSON.stringify(summary, null, 2));
    return 0;
  }

  throw new Error(`Unknown command: ${command}`);
}

function parseArgs(args) {
  const parsed = {};

  for (let index = 0; index < args.length; index += 1) {
    const current = args[index];

    if (!current.startsWith("--")) {
      throw new Error(`Unexpected argument: ${current}`);
    }

    const key = current.slice(2);
    const next = args[index + 1];

    if (next && !next.startsWith("--")) {
      parsed[key] = next;
      index += 1;
    } else {
      parsed[key] = true;
    }
  }

  return parsed;
}

function printGenerationSummary(summary, dryRun) {
  console.log(dryRun ? "Dry run completed." : "Template generation completed.");
  console.log(`Output: ${summary.outputDir}`);
  console.log(`Display name: ${summary.names.displayName}`);
  console.log(`Slug: ${summary.names.slug}`);
  console.log(`Java package: ${summary.names.javaBasePackage}`);
  console.log(`Renamed paths: ${summary.renamedPaths.length}`);
  console.log(`Rewritten files: ${summary.rewrittenFiles.length}`);
}

function printHelp() {
  console.log(
    [
      "Usage:",
      "  node tools/template-cli/src/cli.js init --name \"My Cool App\" [--output ./generated/my-cool-app] [--force] [--dry-run]",
      "  node tools/template-cli/src/cli.js check-source-drift",
    ].join("\n"),
  );
}

if (require.main === module) {
  try {
    process.exitCode = main();
  } catch (error) {
    console.error(error.message);
    process.exitCode = 1;
  }
}

module.exports = {
  main,
  parseArgs,
};
