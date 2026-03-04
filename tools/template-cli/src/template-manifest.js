"use strict";

const path = require("path");

const TEXT_FILE_EXTENSIONS = new Set([
  ".drawio",
  ".env",
  ".gitattributes",
  ".gitignore",
  ".html",
  ".java",
  ".json",
  ".md",
  ".properties",
  ".txt",
  ".xml",
  ".yaml",
  ".yml",
]);

const TEXT_BASENAMES = new Set([
  ".env",
  ".env.example",
  ".gitattributes",
  ".gitignore",
  "Dockerfile",
]);

const IGNORED_DIR_NAMES = new Set([
  ".git",
  "node_modules",
  "target",
]);

const FORBIDDEN_TOKENS = [
  { token: "com.hadiasaas", description: "template Java package" },
  { token: "com.maitrisetcf", description: "stale Java package" },
  { token: "hadiasaas-restapi", description: "template backend module name" },
  { token: "maitrisetcf-restapi", description: "stale backend module name" },
  { token: "hadiasaas-app", description: "template app module name" },
  { token: "maitrisetcf-app", description: "stale app module name" },
  { token: "hadiasaas-core", description: "template core module name" },
  { token: "maitrisetcf-core", description: "stale core module name" },
  { token: "hadiasaas-tests", description: "template tests module name" },
  { token: "maitrisetcf-tests", description: "stale tests module name" },
  { token: "Maitrise TCF", description: "template display name" },
  { token: "hadiasaas", description: "template slug token" },
  { token: "maitrisetcf", description: "stale slug token" },
  { token: "maitrise-tcf", description: "stale org slug token" },
  { token: "maitrice-tcf", description: "stale org slug token typo" },
];

const SOURCE_DRIFT_BASELINE = {
  fileCount: 346,
  lineCount: 1636,
  tokenMatchCount: 3537,
  hash: "e9bb1b1254214735e4b673e1cef18ba316a31dd61cf33e4dec7a72a977f61a84",
};

function getTemplateDir(rootDir) {
  return path.join(rootDir, "app");
}

function isTextFile(filePath) {
  const base = path.basename(filePath);
  if (TEXT_BASENAMES.has(base)) {
    return true;
  }

  return TEXT_FILE_EXTENSIONS.has(path.extname(filePath));
}

function shouldIgnoreDir(dirName) {
  return IGNORED_DIR_NAMES.has(dirName);
}

module.exports = {
  FORBIDDEN_TOKENS,
  SOURCE_DRIFT_BASELINE,
  getTemplateDir,
  isTextFile,
  shouldIgnoreDir,
};
