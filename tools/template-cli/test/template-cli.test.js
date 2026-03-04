"use strict";

const fs = require("fs");
const os = require("os");
const path = require("path");
const test = require("node:test");
const assert = require("node:assert/strict");

const { generateTemplate } = require("../src/generate-template");
const { deriveNames } = require("../src/names");
const { scanForForbiddenTokens, summarizeTemplateSource } = require("../src/scanner");
const { SOURCE_DRIFT_BASELINE, getTemplateDir } = require("../src/template-manifest");

const repoRoot = path.resolve(__dirname, "..", "..", "..");
const templateDir = getTemplateDir(repoRoot);

test("deriveNames normalizes display, slug, and package token", () => {
  const names = deriveNames("  My Cool App  ");

  assert.equal(names.displayName, "My Cool App");
  assert.equal(names.slug, "my-cool-app");
  assert.equal(names.packageToken, "mycoolapp");
  assert.equal(names.javaBasePackage, "com.mycoolapp");
});

test("deriveNames prefixes package token when input starts with digits", () => {
  const names = deriveNames("123 CRM");

  assert.equal(names.slug, "123-crm");
  assert.equal(names.packageToken, "app123crm");
  assert.equal(names.javaBasePackage, "com.app123crm");
});

test("generateTemplate copies and rewrites the template", () => {
  const tempRoot = fs.mkdtempSync(path.join(os.tmpdir(), "template-cli-"));
  const outputDir = path.join(tempRoot, "generated-app");

  const summary = generateTemplate({
    name: "My Cool App",
    templateDir,
    outputDir,
  });

  assert.equal(summary.names.slug, "my-cool-app");
  assert.ok(fs.existsSync(path.join(outputDir, "backend", "my-cool-app-restapi")));
  assert.ok(
    fs.existsSync(
      path.join(
        outputDir,
        "backend",
        "my-cool-app-restapi",
        "my-cool-app-app",
        "src",
        "main",
        "java",
        "com",
        "mycoolapp",
      ),
    ),
  );

  const violations = scanForForbiddenTokens(outputDir);
  assert.deepEqual(violations, []);

  fs.rmSync(tempRoot, { recursive: true, force: true });
});

test("generateTemplate fails without --force when output already exists", () => {
  const tempRoot = fs.mkdtempSync(path.join(os.tmpdir(), "template-cli-"));
  const outputDir = path.join(tempRoot, "generated-app");
  fs.mkdirSync(outputDir, { recursive: true });

  assert.throws(
    () =>
      generateTemplate({
        name: "My Cool App",
        templateDir,
        outputDir,
      }),
    /Output directory already exists/,
  );

  fs.rmSync(tempRoot, { recursive: true, force: true });
});

test("generateTemplate dry run does not write files", () => {
  const tempRoot = fs.mkdtempSync(path.join(os.tmpdir(), "template-cli-"));
  const outputDir = path.join(tempRoot, "generated-app");

  const summary = generateTemplate({
    name: "My Cool App",
    templateDir,
    outputDir,
    dryRun: true,
  });

  assert.equal(summary.copied, false);
  assert.equal(fs.existsSync(outputDir), false);

  fs.rmSync(tempRoot, { recursive: true, force: true });
});

test("scanForForbiddenTokens reports injected legacy tokens", () => {
  const tempRoot = fs.mkdtempSync(path.join(os.tmpdir(), "template-cli-"));
  const sampleFile = path.join(tempRoot, "sample.yml");
  fs.writeFileSync(sampleFile, "name: hadiasaas\npackage: com.hadiasaas\n", "utf8");

  const violations = scanForForbiddenTokens(tempRoot);

  assert.equal(violations.length, 3);
  assert.deepEqual(
    violations.map((violation) => violation.token),
    ["hadiasaas", "com.hadiasaas", "hadiasaas"],
  );

  fs.rmSync(tempRoot, { recursive: true, force: true });
});

test("template source summary matches the checked-in drift baseline", () => {
  const summary = summarizeTemplateSource(templateDir);
  assert.deepEqual(summary, SOURCE_DRIFT_BASELINE);
});

test("generateTemplate replaces tokens inside .drawio files", () => {
  const tempRoot = fs.mkdtempSync(path.join(os.tmpdir(), "template-cli-drawio-"));
  const tempTemplate = path.join(tempRoot, "template");
  const outputDir = path.join(tempRoot, "output");

  fs.mkdirSync(path.join(tempTemplate, "docs"), { recursive: true });
  fs.mkdirSync(path.join(tempTemplate, "backend", "hadiasaas-restapi", "hadiasaas-app"), {
    recursive: true,
  });

  fs.writeFileSync(
    path.join(tempTemplate, "docs", "architecture.drawio"),
    "<mxfile><diagram>hadiasaas Maitrise TCF maitrice-tcf</diagram></mxfile>",
    "utf8",
  );

  generateTemplate({
    name: "My Cool App",
    templateDir: tempTemplate,
    outputDir,
  });

  const generatedDrawio = fs.readFileSync(path.join(outputDir, "docs", "architecture.drawio"), "utf8");
  assert.match(generatedDrawio, /my-cool-app/);
  assert.doesNotMatch(generatedDrawio, /hadiasaas|Maitrise TCF|maitrice-tcf/);

  fs.rmSync(tempRoot, { recursive: true, force: true });
});
