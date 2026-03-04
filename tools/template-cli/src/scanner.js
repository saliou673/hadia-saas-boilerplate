"use strict";

const crypto = require("crypto");
const fs = require("fs");
const path = require("path");

const { FORBIDDEN_TOKENS, SOURCE_DRIFT_BASELINE, isTextFile } = require("./template-manifest");
const { walkFiles } = require("./file-utils");

function scanForForbiddenTokens(rootDir) {
  const violations = [];

  for (const filePath of walkFiles(rootDir)) {
    if (!isTextFile(filePath)) {
      continue;
    }

    const content = fs.readFileSync(filePath, "utf8");
    const lines = content.split(/\r?\n/);
    const relativePath = path.relative(rootDir, filePath).split(path.sep).join("/");

    for (let index = 0; index < lines.length; index += 1) {
      const line = lines[index];

      for (const entry of FORBIDDEN_TOKENS) {
        if (line.includes(entry.token)) {
          violations.push({
            file: relativePath,
            line: index + 1,
            token: entry.token,
            description: entry.description,
          });
        }
      }
    }
  }

  return violations;
}

function summarizeTemplateSource(rootDir) {
  const entries = [];
  let tokenMatchCount = 0;

  for (const filePath of walkFiles(rootDir)) {
    if (!isTextFile(filePath)) {
      continue;
    }

    const content = fs.readFileSync(filePath, "utf8");
    const lines = content.split(/\r?\n/);
    const relativePath = path.relative(rootDir, filePath).split(path.sep).join("/");

    for (let index = 0; index < lines.length; index += 1) {
      const line = lines[index];
      const matchedTokens = FORBIDDEN_TOKENS.filter((entry) => line.includes(entry.token))
        .map((entry) => entry.token)
        .sort();

      if (matchedTokens.length === 0) {
        continue;
      }

      tokenMatchCount += matchedTokens.length;
      entries.push(`${relativePath}:${index + 1}:${matchedTokens.join(",")}:${line}`);
    }
  }

  return {
    fileCount: new Set(entries.map((entry) => entry.split(":")[0])).size,
    lineCount: entries.length,
    tokenMatchCount,
    hash: crypto.createHash("sha256").update(entries.join("\n")).digest("hex"),
  };
}

function assertSourceDriftBaseline(rootDir) {
  const summary = summarizeTemplateSource(rootDir);
  const matchesBaseline =
    summary.fileCount === SOURCE_DRIFT_BASELINE.fileCount &&
    summary.lineCount === SOURCE_DRIFT_BASELINE.lineCount &&
    summary.tokenMatchCount === SOURCE_DRIFT_BASELINE.tokenMatchCount &&
    summary.hash === SOURCE_DRIFT_BASELINE.hash;

  if (!matchesBaseline) {
    const error = new Error(
      [
        "Template source drift detected.",
        `Expected: ${JSON.stringify(SOURCE_DRIFT_BASELINE)}`,
        `Actual: ${JSON.stringify(summary)}`,
        "Review the new legacy-token usage, then update replacement rules or refresh the baseline intentionally.",
      ].join("\n"),
    );
    error.summary = summary;
    throw error;
  }

  return summary;
}

module.exports = {
  assertSourceDriftBaseline,
  scanForForbiddenTokens,
  summarizeTemplateSource,
};
