import NextAuth from 'next-auth'
import Credentials from 'next-auth/providers/credentials'
import { z } from 'zod'

const authSecret =
  process.env.AUTH_SECRET ??
  process.env.NEXTAUTH_SECRET ??
  (process.env.NODE_ENV === 'development'
    ? 'dev-only-secret-change-me'
    : undefined)

const credentialsSchema = z.object({
  email: z.email(),
  password: z.string().min(7),
})

export const { handlers, auth, signIn, signOut } = NextAuth({
  secret: authSecret,
  session: { strategy: 'jwt' },
  providers: [
    Credentials({
      name: 'Credentials',
      credentials: {
        email: { label: 'Email', type: 'email' },
        password: { label: 'Password', type: 'password' },
      },
      async authorize(rawCredentials) {
        const parsed = credentialsSchema.safeParse(rawCredentials)
        if (!parsed.success) return null

        const { email } = parsed.data

        return {
          id: email,
          email,
          name: email.split('@')[0],
        }
      },
    }),
  ],
  pages: {
    signIn: '/sign-in',
  },
})
