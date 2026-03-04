"use strict";

function createReplacementPlan(names) {
  return [
    {
      description: "Java package",
      match: /com\.hadiasaas/g,
      replaceWith: names.javaBasePackage,
    },
    {
      description: "Java package path",
      match: /com\/hadiasaas/g,
      replaceWith: names.javaBasePackage.replace(/\./g, "/"),
    },
    {
      description: "Backend root name",
      match: /hadiasaas-restapi/g,
      replaceWith: names.backendRootName,
    },
    {
      description: "App module name",
      match: /hadiasaas-app/g,
      replaceWith: names.moduleNames.app,
    },
    {
      description: "Core module name",
      match: /hadiasaas-core/g,
      replaceWith: names.moduleNames.core,
    },
    {
      description: "Tests module name",
      match: /hadiasaas-tests/g,
      replaceWith: names.moduleNames.tests,
    },
    {
      description: "Integration tests module name",
      match: /hadiasaas-integration-tests/g,
      replaceWith: names.moduleNames.tests,
    },
    {
      description: "Display name REST API",
      match: /Hadia SaaS REST API/g,
      replaceWith: `${names.displayName} REST API`,
    },
    {
      description: "Display name",
      match: /Hadia SaaS/g,
      replaceWith: names.displayName,
    },
    {
      description: "Generic slug token",
      match: /hadiasaas/g,
      replaceWith: names.slug,
    },
  ];
}

module.exports = {
  createReplacementPlan,
};
