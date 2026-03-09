'use client'

import dynamic from 'next/dynamic'

const HelpCenterClient = dynamic(() => import('./help-center-client'), {
  ssr: false,
})

export default function HelpCenterPage() {
  return <HelpCenterClient />
}
