import NextAuth from "next-auth";
import Credentials from "next-auth/providers/credentials";
import type { JWT } from "next-auth/jwt";

type LoginResponse = {
  accessToken?: string;
  refreshToken?: string;
  challengeId?: string;
};

const apiBaseUrl = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
const authSecret = process.env.NEXT_PUBLIC_AUTH_SECRET ?? "dev-only-secret-change-me";

function decodeJwtExpiration(accessToken: string): number {
  try {
    const payload = JSON.parse(Buffer.from(accessToken.split(".")[1], "base64").toString("utf8")) as { exp?: number };
    if (!payload.exp) return Date.now() + 5 * 60 * 1000;
    return payload.exp * 1000;
  } catch {
    return Date.now() + 5 * 60 * 1000;
  }
}

async function refreshAccessToken(token: JWT): Promise<JWT> {
  if (!token.refreshToken) {
    return { ...token, error: "RefreshAccessTokenError" };
  }

  try {
    const response = await fetch(`${apiBaseUrl}/api/v1/auth/refresh`, {
      method: "POST",
      headers: {
        "Content-Type": "text/plain",
        Accept: "application/json",
      },
      body: token.refreshToken,
      cache: "no-store",
    });

    if (!response.ok) {
      throw new Error(`Refresh failed with status ${response.status}`);
    }

    const refreshed = (await response.json()) as LoginResponse;
    if (!refreshed.accessToken) {
      throw new Error("Refresh response did not include access token");
    }

    return {
      ...token,
      accessToken: refreshed.accessToken,
      refreshToken: refreshed.refreshToken ?? token.refreshToken,
      accessTokenExpires: decodeJwtExpiration(refreshed.accessToken),
      error: undefined,
    };
  } catch {
    return { ...token, error: "RefreshAccessTokenError" };
  }
}

export const { handlers, signIn, signOut, auth } = NextAuth({
  secret: authSecret,
  session: { strategy: "jwt" },
  trustHost: true,
  pages: {
    signIn: "/sign-in",
  },
  providers: [
    Credentials({
      name: "Credentials",
      credentials: {
        email: { label: "Email", type: "email" },
        password: { label: "Password", type: "password" },
        rememberMe: { label: "Remember me", type: "text" },
      },
      async authorize(credentials) {
        const email = String(credentials?.email ?? "").trim();
        const password = String(credentials?.password ?? "");
        const rememberMe = String(credentials?.rememberMe ?? "false") === "true";

        if (!email || !password) return null;

        const response = await fetch(`${apiBaseUrl}/api/v1/auth/login`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
          },
          body: JSON.stringify({ email, password, rememberMe }),
          cache: "no-store",
        });

        if (response.status === 202) {
          throw new Error("Two-factor authentication is required.");
        }

        if (!response.ok) return null;

        const result = (await response.json()) as LoginResponse;
        if (!result.accessToken || !result.refreshToken) return null;

        return {
          id: email,
          email,
          accessToken: result.accessToken,
          refreshToken: result.refreshToken,
          accessTokenExpires: decodeJwtExpiration(result.accessToken),
        };
      },
    }),
  ],
  callbacks: {
    async jwt({ token, user }) {
      if (user) {
        return {
          ...token,
          accessToken: user.accessToken,
          refreshToken: user.refreshToken,
          accessTokenExpires: user.accessTokenExpires,
          error: undefined,
        };
      }

      if (token.accessToken && token.accessTokenExpires && Date.now() < token.accessTokenExpires) {
        return token;
      }

      return refreshAccessToken(token);
    },
    async session({ session, token }) {
      session.error = token.error;
      session.accessToken = token.accessToken;
      return session;
    },
  },
});
