"use strict";

function deriveNames(appName) {
  if (typeof appName !== "string") {
    throw new Error("App name must be a string.");
  }

  const trimmed = appName.trim();
  if (!trimmed) {
    throw new Error("App name is required.");
  }

  const normalized = trimmed.normalize("NFKD").replace(/[\u0300-\u036f]/g, "");
  const words = normalized
    .replace(/[^A-Za-z0-9]+/g, " ")
    .trim()
    .split(/\s+/)
    .filter(Boolean);

  if (words.length === 0) {
    throw new Error("App name must contain at least one letter or number.");
  }

  const displayName = words.map(capitalizeWord).join(" ");
  const slug = words.join("-").toLowerCase();
  let packageToken = words.join("").toLowerCase();

  if (!packageToken) {
    throw new Error("App name could not be normalized into a Java package token.");
  }

  if (/^\d/.test(packageToken)) {
    packageToken = `app${packageToken}`;
  }

  return {
    rawName: appName,
    displayName,
    slug,
    packageToken,
    javaBasePackage: `com.${packageToken}`,
    backendRootName: `${slug}-restapi`,
    moduleNames: {
      app: `${slug}-app`,
      core: `${slug}-core`,
      tests: `${slug}-tests`,
    },
    dockerWorkdirName: slug,
    dbIdentifier: `${slug}-db`,
  };
}

function capitalizeWord(word) {
  if (!word) {
    return word;
  }

  return `${word[0].toUpperCase()}${word.slice(1).toLowerCase()}`;
}

module.exports = {
  deriveNames,
};
