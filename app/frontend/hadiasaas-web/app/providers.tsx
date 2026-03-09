'use client'

import { useState, type ReactNode } from 'react'
import { AxiosError } from 'axios'
import { QueryCache, QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { SessionProvider } from 'next-auth/react'
import { toast } from 'sonner'
import { DirectionProvider } from '@/context/direction-provider'
import { FontProvider } from '@/context/font-provider'
import { ThemeProvider } from '@/context/theme-provider'
import { handleServerError } from '@/lib/handle-server-error'
import { useAuthStore } from '@/stores/auth-store'
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
          useAuthStore.getState().auth.reset()
          window.location.assign('/sign-in')
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

export function Providers({ children }: ProvidersProps) {
  const [queryClient] = useState(makeQueryClient)

  return (
    <SessionProvider>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider>
          <FontProvider>
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
