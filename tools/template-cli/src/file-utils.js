"use strict";

const fs = require("fs");
const path = require("path");

const { shouldIgnoreDir } = require("./template-manifest");

function walkFiles(rootDir) {
  const results = [];

  for (const entry of fs.readdirSync(rootDir, { withFileTypes: true })) {
    const fullPath = path.join(rootDir, entry.name);

    if (entry.isDirectory()) {
      if (shouldIgnoreDir(entry.name)) {
        continue;
      }
      results.push(...walkFiles(fullPath));
    } else {
      results.push(fullPath);
    }
  }

  return results;
}

function pathExists(targetPath) {
  return fs.existsSync(targetPath);
}

function ensureDir(targetPath) {
  fs.mkdirSync(targetPath, { recursive: true });
}

function removeDir(targetPath) {
  fs.rmSync(targetPath, { recursive: true, force: true });
}

function copyDir(sourceDir, targetDir) {
  fs.cpSync(sourceDir, targetDir, {
    recursive: true,
    filter(sourcePath) {
      const baseName = path.basename(sourcePath);
      return !shouldIgnoreDir(baseName);
    },
  });
}

function renameIfExists(fromPath, toPath) {
  if (!pathExists(fromPath) || fromPath === toPath) {
    return false;
  }

  ensureDir(path.dirname(toPath));
  fs.renameSync(fromPath, toPath);
  return true;
}

module.exports = {
  copyDir,
  ensureDir,
  pathExists,
  removeDir,
  renameIfExists,
  walkFiles,
};
