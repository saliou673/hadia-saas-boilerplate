export const API_DOCS_URL = 'http://localhost:8080/api/docs'

// Kubb output is generated under ./gen
export * from './gen'
export { QueryClient, QueryClientProvider } from '@tanstack/react-query'
export { axiosInstance } from '@kubb/plugin-client/clients/axios'

import { getConfig, setConfig } from '@kubb/plugin-client/clients/axios'

type ApiClientConfig = {
  baseURL?: string
  accessToken?: string
}

export function configureApiClient(config: ApiClientConfig) {
  const current = getConfig()
  const currentHeaders = (current.headers ?? {}) as Record<string, string>
  const headers = { ...currentHeaders }

  if (config.accessToken) {
    headers.Authorization = `Bearer ${config.accessToken}`
  } else {
    delete headers.Authorization
  }

  setConfig({
    ...current,
    baseURL: config.baseURL ?? current.baseURL,
    headers,
  })
}
