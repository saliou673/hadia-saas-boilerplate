'use client'

import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'
import { ComingSoon } from '@/components/coming-soon'

export default function HelpCenterClient() {
  return (
    <AuthenticatedLayout>
      <ComingSoon />
    </AuthenticatedLayout>
  )
}
