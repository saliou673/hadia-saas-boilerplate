import { NextResponse, type NextRequest } from "next/server";
import { getToken } from "next-auth/jwt";

const PUBLIC_PATHS = new Set(["/sign-in", "/forgot-password"]);
const authSecret = process.env.NEXT_PUBLIC_AUTH_SECRET ?? "dev-only-secret-change-me";

function isPublicPath(pathname: string): boolean {
  return PUBLIC_PATHS.has(pathname);
}

export async function proxy(request: NextRequest) {
  const { pathname, search } = request.nextUrl;
  const sessionToken = await getToken({
    req: request,
    secret: authSecret,
  });
  const isPublic = isPublicPath(pathname);

  if (isPublic && sessionToken && pathname === "/sign-in") {
    return NextResponse.redirect(new URL("/dashboard", request.url));
  }

  if (!isPublic && !sessionToken) {
    const signInUrl = new URL("/sign-in", request.url);
    const redirectTo = `${pathname}${search}`;
    signInUrl.searchParams.set("redirect", redirectTo);
    return NextResponse.redirect(signInUrl);
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    "/((?!api|_next/static|_next/image|favicon.ico|.*\\..*).*)",
  ],
};
