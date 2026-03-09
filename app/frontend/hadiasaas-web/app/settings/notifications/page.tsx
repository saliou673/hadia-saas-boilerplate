'use client'

import dynamic from 'next/dynamic'

const SettingsNotificationsClient = dynamic(
  () => import('./settings-notifications-client'),
  { ssr: false }
)

export default function SettingsNotificationsPage() {
  return <SettingsNotificationsClient />
}
