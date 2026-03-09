import type { ButtonHTMLAttributes } from "react";

type UserProfileButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  name: string;
  email?: string;
  initials: string;
  variant: "sidebar" | "icon";
};

export function UserProfileButton({
  name,
  email,
  initials,
  variant,
  className = "",
  ...props
}: UserProfileButtonProps) {
  if (variant === "icon") {
    return (
      <button
        type="button"
        className={`grid h-9 w-9 place-items-center rounded-full border border-border bg-muted text-sm font-semibold ${className}`}
        {...props}
      >
        {initials}
      </button>
    );
  }

  return (
    <button
      type="button"
      className={`flex w-full items-center gap-3 rounded-md border border-border px-3 py-2 text-left hover:bg-muted ${className}`}
      {...props}
    >
      <span className="grid h-8 w-8 place-items-center rounded-md bg-muted text-xs font-semibold">
        {initials}
      </span>
      <span className="flex-1">
        <span className="block text-sm font-medium">{name}</span>
        {email ? <span className="block text-xs text-muted-foreground">{email}</span> : null}
      </span>
    </button>
  );
}
