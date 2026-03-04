"use strict";

const fs = require("fs");
const path = require("path");

const { copyDir, pathExists, removeDir, renameIfExists, walkFiles } = require("./file-utils");
const { deriveNames } = require("./names");
const { createReplacementPlan } = require("./replacements");
const { scanForForbiddenTokens } = require("./scanner");
const { isTextFile } = require("./template-manifest");

function generateTemplate(options) {
  const names = deriveNames(options.name);
  const templateDir = path.resolve(options.templateDir);
  const outputDir = path.resolve(options.outputDir || path.join("generated", names.slug));
  const dryRun = Boolean(options.dryRun);
  const force = Boolean(options.force);

  if (!pathExists(templateDir)) {
    throw new Error(`Template directory not found: ${templateDir}`);
  }

  if (pathExists(outputDir)) {
    if (!force) {
      throw new Error(`Output directory already exists: ${outputDir}. Use --force to overwrite it.`);
    }

    if (!dryRun) {
      removeDir(outputDir);
    }
  }

  const summary = {
    names,
    templateDir,
    outputDir,
    copied: !dryRun,
    renamedPaths: [],
    rewrittenFiles: [],
    validationViolations: [],
  };

  if (dryRun) {
    return summary;
  }

  copyDir(templateDir, outputDir);
  applyPathRenames(outputDir, names, summary);
  rewriteTextFiles(outputDir, names, summary);

  const violations = scanForForbiddenTokens(outputDir);
  if (violations.length > 0) {
    summary.validationViolations = violations;
    throw createValidationError(violations, outputDir);
  }

  return summary;
}

function applyPathRenames(outputDir, names, summary) {
  const backendRoot = path.join(outputDir, "backend");
  const currentBackendRoot = path.join(backendRoot, names.backendRootName);
  const backendRootCandidates = [
    path.join(backendRoot, "hadiasaas-restapi"),
    path.join(backendRoot, "maitrisetcf-restapi"),
  ];

  for (const legacyBackendRoot of backendRootCandidates) {
    if (renameIfExists(legacyBackendRoot, currentBackendRoot)) {
      summary.renamedPaths.push({
        from: relativeToOutput(outputDir, legacyBackendRoot),
        to: relativeToOutput(outputDir, currentBackendRoot),
      });
      break;
    }
  }

  const moduleRenameGroups = [
    ["hadiasaas-app", "maitrisetcf-app", names.moduleNames.app],
    ["hadiasaas-core", "maitrisetcf-core", names.moduleNames.core],
    ["hadiasaas-tests", "maitrisetcf-tests", names.moduleNames.tests],
  ];

  for (const [primaryLegacyName, fallbackLegacyName, nextName] of moduleRenameGroups) {
    const nextPath = path.join(currentBackendRoot, nextName);
    for (const legacyName of [primaryLegacyName, fallbackLegacyName]) {
      const legacyPath = path.join(currentBackendRoot, legacyName);
      if (renameIfExists(legacyPath, nextPath)) {
        summary.renamedPaths.push({
          from: relativeToOutput(outputDir, legacyPath),
          to: relativeToOutput(outputDir, nextPath),
        });
        break;
      }
    }
  }

  const packageRenameGroups = [
    [path.join(currentBackendRoot, names.moduleNames.app, "src", "main", "java", "com"), "hadiasaas", "maitrisetcf"],
    [path.join(currentBackendRoot, names.moduleNames.core, "src", "main", "java", "com"), "hadiasaas", "maitrisetcf"],
    [path.join(currentBackendRoot, names.moduleNames.tests, "src", "test", "java", "com"), "hadiasaas", "maitrisetcf"],
  ];

  for (const [packageRoot, primaryLegacyName, fallbackLegacyName] of packageRenameGroups) {
    const nextPath = path.join(packageRoot, names.packageToken);
    for (const legacyName of [primaryLegacyName, fallbackLegacyName]) {
      const legacyPath = path.join(packageRoot, legacyName);
      if (renameIfExists(legacyPath, nextPath)) {
        summary.renamedPaths.push({
          from: relativeToOutput(outputDir, legacyPath),
          to: relativeToOutput(outputDir, nextPath),
        });
        break;
      }
    }
  }
}

function rewriteTextFiles(outputDir, names, summary) {
  const replacements = createReplacementPlan(names);

  for (const filePath of walkFiles(outputDir)) {
    if (!isTextFile(filePath)) {
      continue;
    }

    const original = fs.readFileSync(filePath, "utf8");
    let next = original;

    for (const replacement of replacements) {
      next = next.replace(replacement.match, replacement.replaceWith);
    }

    if (next !== original) {
      fs.writeFileSync(filePath, next, "utf8");
      summary.rewrittenFiles.push(relativeToOutput(outputDir, filePath));
    }
  }
}

function createValidationError(violations, outputDir) {
  const preview = violations
    .slice(0, 20)
    .map((violation) => `- ${violation.file}:${violation.line} still contains ${violation.token}`)
    .join("\n");

  return new Error(
    [
      `Generated output still contains forbidden legacy tokens under ${outputDir}.`,
      preview,
      violations.length > 20 ? `- ... ${violations.length - 20} more violation(s)` : "",
    ]
      .filter(Boolean)
      .join("\n"),
  );
}

function relativeToOutput(outputDir, targetPath) {
  return path.relative(outputDir, targetPath).split(path.sep).join("/");
}

module.exports = {
  generateTemplate,
};
