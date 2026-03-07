"use client";

import { useFormik } from "formik";
import { useRouter } from "next/navigation";
import { useMemo } from "react";
import * as Yup from "yup";
import { useAuthenticate, type LoginRequest } from "@apiclient";

type LoginResponseShape = {
  accessToken?: string;
  refreshToken?: string;
  challengeId?: string;
};

function readApiError(error: unknown): string {
  const maybeAxiosError = error as {
    response?: {
      data?: {
        message?: string;
        errors?: Record<string, string>;
      };
    };
  };

  const data = maybeAxiosError.response?.data;
  if (data?.message) return data.message;
  if (data?.errors) {
    const firstError = Object.values(data.errors)[0];
    if (firstError) return firstError;
  }

  return "Authentication failed. Please check your credentials.";
}

export default function SignInPage() {
  const router = useRouter();
  const apiBaseUrl = useMemo(
    () => process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080",
    [],
  );

  const authenticateMutation = useAuthenticate({
    client: {
      baseURL: apiBaseUrl,
      headers: { "Content-Type": "application/json" },
    },
  });

  const formik = useFormik<LoginRequest>({
    initialValues: {
      email: "",
      password: "",
      rememberMe: false,
    },
    validationSchema: Yup.object({
      email: Yup.string().email("Invalid email format").required("Email is required"),
      password: Yup.string().min(4, "Password must be at least 4 characters").required("Password is required"),
      rememberMe: Yup.boolean().optional(),
    }),
    onSubmit: async (values, helpers) => {
      helpers.setStatus(undefined);

      try {
        const response = (await authenticateMutation.mutateAsync({
          data: values,
        })) as LoginResponseShape;

        if (response.accessToken) {
          localStorage.setItem("accessToken", response.accessToken);
        }
        if (response.refreshToken) {
          localStorage.setItem("refreshToken", response.refreshToken);
        }

        if (response.challengeId) {
          router.push(`/otp?challengeId=${encodeURIComponent(response.challengeId)}`);
          return;
        }

        router.push("/dashboard");
      } catch (error) {
        helpers.setStatus(readApiError(error));
      }
    },
  });

  return (
    <div className="container mx-auto grid h-svh max-w-none items-center justify-center px-8">
      <div className="mx-auto flex w-full flex-col justify-center space-y-2 py-8 sm:w-[480px] sm:p-8">
        <div className="mb-4 flex items-center justify-center">
          <svg
            id="shadcn-admin-logo"
            viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg"
            height="24"
            width="24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            className="me-2 size-6"
          >
            <title>Shadcn-Admin</title>
            <path d="M15 6v12a3 3 0 1 0 3-3H6a3 3 0 1 0 3 3V6a3 3 0 1 0-3 3h12a3 3 0 1 0-3-3" />
          </svg>
          <h1 className="text-xl font-medium">Shadcn Admin</h1>
        </div>

        <div className="flex flex-col gap-4 rounded-xl border bg-card py-6 text-card-foreground shadow-sm">
          <div className="grid auto-rows-min grid-rows-[auto_auto] items-start gap-1.5 px-6">
            <h2 className="text-lg leading-none font-semibold tracking-tight">Sign in</h2>
            <p className="text-sm text-muted-foreground">
              Enter your email and password below to <br />
              log into your account
            </p>
          </div>

          <div className="px-6">
            <form className="grid gap-3" onSubmit={formik.handleSubmit}>
              <div className="grid gap-2">
                <label htmlFor="email" className="text-sm font-medium">
                  Email
                </label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="name@example.com"
                  value={formik.values.email}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  className="flex h-9 w-full min-w-0 rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-xs outline-none transition-[color,box-shadow] placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-[3px] focus-visible:ring-ring/50"
                />
                {formik.touched.email && formik.errors.email ? (
                  <p className="text-sm text-red-600">{formik.errors.email}</p>
                ) : null}
              </div>

              <div className="relative grid gap-2">
                <label htmlFor="password" className="text-sm font-medium">
                  Password
                </label>
                <a
                  href="/forgot-password"
                  className="absolute end-0 -top-0.5 text-sm font-medium text-muted-foreground hover:opacity-75"
                >
                  Forgot password?
                </a>
                <input
                  id="password"
                  name="password"
                  type="password"
                  placeholder="********"
                  value={formik.values.password}
                  onChange={formik.handleChange}
                  onBlur={formik.handleBlur}
                  className="flex h-9 w-full min-w-0 rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-xs outline-none transition-[color,box-shadow] placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-[3px] focus-visible:ring-ring/50"
                />
                {formik.touched.password && formik.errors.password ? (
                  <p className="text-sm text-red-600">{formik.errors.password}</p>
                ) : null}
              </div>

              <label className="mt-1 inline-flex items-center gap-2 text-sm text-muted-foreground">
                <input
                  id="rememberMe"
                  name="rememberMe"
                  type="checkbox"
                  checked={Boolean(formik.values.rememberMe)}
                  onChange={formik.handleChange}
                />
                Remember me
              </label>

              {formik.status ? <p className="text-sm text-red-600">{String(formik.status)}</p> : null}

              <button
                type="submit"
                disabled={authenticateMutation.isPending}
                className="mt-2 inline-flex h-9 items-center justify-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-all hover:bg-primary/90 disabled:opacity-60"
              >
                {authenticateMutation.isPending ? "Signing in..." : "Sign in"}
              </button>

              <div className="relative my-2">
                <div className="absolute inset-0 flex items-center">
                  <span className="w-full border-t" />
                </div>
                <div className="relative flex justify-center text-xs uppercase">
                  <span className="bg-background px-2 text-muted-foreground">Or continue with</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  className="inline-flex h-9 items-center justify-center gap-2 rounded-md border bg-background px-4 py-2 text-sm font-medium transition-all hover:bg-muted"
                >
                  GitHub
                </button>
                <button
                  type="button"
                  className="inline-flex h-9 items-center justify-center gap-2 rounded-md border bg-background px-4 py-2 text-sm font-medium transition-all hover:bg-muted"
                >
                  Facebook
                </button>
              </div>
            </form>
          </div>

          <div className="flex items-center px-6">
            <p className="px-8 text-center text-sm text-muted-foreground">
              By clicking sign in, you agree to our{" "}
              <a href="/terms" className="underline underline-offset-4 hover:text-primary">
                Terms of Service
              </a>{" "}
              and{" "}
              <a href="/privacy" className="underline underline-offset-4 hover:text-primary">
                Privacy Policy
              </a>
              .
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
