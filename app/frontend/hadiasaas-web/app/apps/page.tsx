'use client'

import dynamic from 'next/dynamic'

const AppsClient = dynamic(() => import('./apps-client'), { ssr: false })

export default function AppsPage() {
  return <AppsClient />
}
