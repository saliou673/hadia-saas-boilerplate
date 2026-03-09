"use client";

import { QueryClient, QueryClientProvider } from "@apiclient";
import { SessionProvider, useSession } from "next-auth/react";
import { useEffect, useMemo, useState } from "react";
import { setApiAccessToken, setupApiClientInterceptors } from "@/lib/apiclient-interceptors";

type ProvidersProps = {
  children: React.ReactNode;
};

function ApiClientAuthSync() {
  const { data: session } = useSession();
  const apiBaseUrl = useMemo(
    () => process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080",
    [],
  );

  useEffect(() => {
    setupApiClientInterceptors(apiBaseUrl);
  }, [apiBaseUrl]);

  useEffect(() => {
    setApiAccessToken(session?.accessToken);
  }, [session?.accessToken]);

  return null;
}

export function Providers({ children }: ProvidersProps) {
  const [queryClient] = useState(() => new QueryClient());

  return (
    <SessionProvider refetchInterval={60}>
      <ApiClientAuthSync />
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    </SessionProvider>
  );
}
