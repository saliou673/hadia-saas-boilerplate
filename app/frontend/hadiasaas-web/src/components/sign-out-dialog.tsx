import { usePathname, useSearchParams } from 'next/navigation'
import { signOut } from 'next-auth/react'
import { ConfirmDialog } from '@/components/confirm-dialog'

interface SignOutDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function SignOutDialog({ open, onOpenChange }: SignOutDialogProps) {
  const pathname = usePathname()
  const searchParams = useSearchParams()

  const handleSignOut = async () => {
    const query = searchParams.toString()
    const currentPath = query ? `${pathname}?${query}` : pathname
    await signOut({
      redirect: true,
      callbackUrl: `/sign-in?redirect=${encodeURIComponent(currentPath)}`,
    })
  }

  return (
    <ConfirmDialog
      open={open}
      onOpenChange={onOpenChange}
      title='Sign out'
      desc='Are you sure you want to sign out? You will need to sign in again to access your account.'
      confirmText='Sign out'
      destructive
      handleConfirm={handleSignOut}
      className='sm:max-w-sm'
    />
  )
}
