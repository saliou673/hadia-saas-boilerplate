'use client'

import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'
import { Apps } from '@/features/apps'

export default function AppsClient() {
  return (
    <AuthenticatedLayout>
      <Apps />
    </AuthenticatedLayout>
  )
}
