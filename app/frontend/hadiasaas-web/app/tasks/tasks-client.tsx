'use client'

import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'
import { Tasks } from '@/features/tasks'

export default function TasksClient() {
  return (
    <AuthenticatedLayout>
      <Tasks />
    </AuthenticatedLayout>
  )
}
