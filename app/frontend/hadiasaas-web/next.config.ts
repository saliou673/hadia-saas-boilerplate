import type { NextConfig } from "next";
import path from "node:path";

const nextConfig: NextConfig = {
    reactStrictMode: true,
    outputFileTracingRoot: path.join(__dirname),
    transpilePackages: ["hadiasaas-apiclient"],
    turbopack: {
        root: __dirname,
    },
};

export default nextConfig;
