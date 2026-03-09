'use client'

import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'
import { Chats } from '@/features/chats'

export default function ChatsClient() {
  return (
    <AuthenticatedLayout>
      <Chats />
    </AuthenticatedLayout>
  )
}
