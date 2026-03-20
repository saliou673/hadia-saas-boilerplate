"use client";

import {
    AxiosHeaders,
    type AxiosError,
    type InternalAxiosRequestConfig,
} from "axios";
import { axiosInstance } from "@api-client";
import { getSession } from "next-auth/react";

let currentAccessToken: string | undefined;
let interceptorsInstalled = false;
let refreshSessionPromise: Promise<string | undefined> | null = null;

type RetriableConfig = InternalAxiosRequestConfig & { _retry?: boolean };

export function setApiAccessToken(accessToken?: string) {
    currentAccessToken = accessToken;
}

async function refreshTokenFromSession(): Promise<string | undefined> {
    if (!refreshSessionPromise) {
        refreshSessionPromise = getSession()
            .then((session) => session?.accessToken)
            .finally(() => {
                refreshSessionPromise = null;
            });
    }

    const token = await refreshSessionPromise;
    setApiAccessToken(token);
    return token;
}

/**
 * Serializes query params to the flat dot-notation format that Spring MVC
 * expects for @ModelAttribute binding.
 *
 * The generated API client wraps params as { filter: {...}, pageable: {...} }.
 * Spring binds these as individual query params WITHOUT the wrapper prefix,
 * e.g. email.contains=foo&status.equals=ACTIVE&page=0&size=10.
 *
 * This serializer strips the top-level object key and expands nested objects
 * using dot notation, matching what the integration tests confirm Spring expects.
 */
function flattenToQueryString(
    value: unknown,
    prefix: string,
    parts: string[]
): void {
    if (value === null || value === undefined) return;
    if (Array.isArray(value)) {
        for (const item of value) {
            if (item !== null && item !== undefined) {
                parts.push(
                    `${encodeURIComponent(prefix)}=${encodeURIComponent(String(item))}`
                );
            }
        }
    } else if (typeof value === "object") {
        for (const [k, v] of Object.entries(value as Record<string, unknown>)) {
            if (v !== null && v !== undefined) {
                flattenToQueryString(v, prefix ? `${prefix}.${k}` : k, parts);
            }
        }
    } else {
        parts.push(
            `${encodeURIComponent(prefix)}=${encodeURIComponent(String(value))}`
        );
    }
}

function springParamsSerializer(params: unknown): string {
    if (!params || typeof params !== "object" || Array.isArray(params)) {
        return "";
    }
    const parts: string[] = [];
    for (const [key, value] of Object.entries(
        params as Record<string, unknown>
    )) {
        if (value === null || value === undefined) continue;
        if (typeof value === "object" && !Array.isArray(value)) {
            // Expand nested object WITHOUT the top-level key as prefix.
            // { filter: { email: { contains: "foo" } } } → email.contains=foo
            // { pageable: { page: 0, size: 10 } } → page=0&size=10
            for (const [nestedKey, nestedValue] of Object.entries(
                value as Record<string, unknown>
            )) {
                if (nestedValue !== null && nestedValue !== undefined) {
                    flattenToQueryString(nestedValue, nestedKey, parts);
                }
            }
        } else {
            flattenToQueryString(value, key, parts);
        }
    }
    return parts.join("&");
}

export function setupApiClientInterceptors(baseURL: string) {
    axiosInstance.defaults.baseURL = baseURL;
    axiosInstance.defaults.paramsSerializer = springParamsSerializer;

    if (interceptorsInstalled) {
        return;
    }

    interceptorsInstalled = true;

    axiosInstance.interceptors.request.use((config) => {
        const nextConfig = config;
        const headers = AxiosHeaders.from(nextConfig.headers);

        if (currentAccessToken) {
            headers.set("Authorization", `Bearer ${currentAccessToken}`);
        } else {
            headers.delete("Authorization");
        }

        nextConfig.headers = headers;
        return nextConfig;
    });

    axiosInstance.interceptors.response.use(
        (response) => response,
        async (error: AxiosError) => {
            const status = error.response?.status;
            const originalRequest = error.config as RetriableConfig | undefined;

            if (
                typeof window === "undefined" ||
                status !== 401 ||
                !originalRequest ||
                originalRequest._retry
            ) {
                return Promise.reject(error);
            }

            originalRequest._retry = true;
            const refreshedToken = await refreshTokenFromSession();

            if (!refreshedToken) {
                return Promise.reject(error);
            }

            originalRequest.headers = AxiosHeaders.from(
                originalRequest.headers
            );
            originalRequest.headers.set(
                "Authorization",
                `Bearer ${refreshedToken}`
            );

            return axiosInstance(originalRequest);
        }
    );
}
