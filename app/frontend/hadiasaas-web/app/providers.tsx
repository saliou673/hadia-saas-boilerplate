'use client'

import { useEffect, useState, type ReactNode } from 'react'
import {
  configureApiClient,
  getCurrentUserPreferencesQueryKey,
  useGetCurrentUserPreferences,
} from '@api-client'
import { AxiosError } from 'axios'
import {
  QueryCache,
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { SessionProvider, signOut, useSession } from 'next-auth/react'
import { toast } from 'sonner'
import { DirectionProvider } from '@/context/direction-provider'
import { FontProvider, useFont } from '@/context/font-provider'
import { ThemeProvider, useTheme } from '@/context/theme-provider'
import { mapUserPreferencesToAppearanceValues } from '@/lib/user-preferences'
import { handleServerError } from '@/lib/handle-server-error'
import {
  setApiAccessToken,
  setupApiClientInterceptors,
} from '@/lib/apiclient-interceptors'
import { Toaster } from '@/components/ui/sonner'

function makeQueryClient() {
  const isDev = process.env.NODE_ENV === 'development'

  return new QueryClient({
    defaultOptions: {
      queries: {
        retry: (failureCount, error) => {
          if (isDev) return false
          if (failureCount > 3) return false

          return !(
            error instanceof AxiosError &&
            [401, 403].includes(error.response?.status ?? 0)
          )
        },
        refetchOnWindowFocus: !isDev,
        staleTime: 10 * 1000,
      },
      mutations: {
        onError: (error) => {
          handleServerError(error)

          if (error instanceof AxiosError && error.response?.status === 304) {
            toast.error('Content not modified!')
          }
        },
      },
    },
    queryCache: new QueryCache({
      onError: (error) => {
        if (!(error instanceof AxiosError)) return

        if (error.response?.status === 401) {
          toast.error('Session expired!')
          void signOut({ redirect: false }).finally(() => {
            const currentPath = `${window.location.pathname}${window.location.search}`
            window.location.assign(
              `/sign-in?redirect=${encodeURIComponent(currentPath)}`
            )
          })
          return
        }

        if (error.response?.status === 500) {
          toast.error('Internal Server Error!')
          if (!isDev) window.location.assign('/errors/internal-server-error')
        }
      },
    }),
  })
}

type ProvidersProps = {
  children: ReactNode
}

function ApiClientAuthSync() {
  const { data: session } = useSession()

  useEffect(() => {
    const baseURL =
      process.env.NEXT_PUBLIC_API_BASE_URL ?? 'http://localhost:8080'

    configureApiClient({ baseURL, accessToken: session?.accessToken })
    setupApiClientInterceptors(baseURL)
    setApiAccessToken(session?.accessToken)
  }, [session?.accessToken])

  return null
}

function UserPreferenceSync() {
  const { data: session, status } = useSession()
  const { setTheme } = useTheme()
  const { setFont } = useFont()
  const sessionEmail = session?.user?.email ?? null
  const { data: preferences } = useGetCurrentUserPreferences({
    query: {
      enabled: status === 'authenticated' && !!sessionEmail,
      queryKey: [...getCurrentUserPreferencesQueryKey(), sessionEmail],
    },
  })

  useEffect(() => {
    if (status !== 'authenticated' || !preferences) {
      return
    }

    const nextValues = mapUserPreferencesToAppearanceValues(preferences)
    setTheme(nextValues.theme)
    setFont(nextValues.font)
  }, [preferences, setFont, setTheme, status])

  return null
}

export function Providers({ children }: ProvidersProps) {
  const [queryClient] = useState(makeQueryClient)

  return (
    <SessionProvider refetchInterval={60}>
      <QueryClientProvider client={queryClient}>
        <ApiClientAuthSync />
        <ThemeProvider>
          <FontProvider>
            <UserPreferenceSync />
            <DirectionProvider>
              {children}
              <Toaster duration={5000} />
              {process.env.NODE_ENV === 'development' && (
                <ReactQueryDevtools buttonPosition='bottom-left' />
              )}
            </DirectionProvider>
          </FontProvider>
        </ThemeProvider>
      </QueryClientProvider>
    </SessionProvider>
  )
}
