"use client";

import { useEffect, useRef, useState } from "react";
import { signOut } from "next-auth/react";
import { UserProfileButton } from "@/components/user-profile-button";

type UserProfileMenuProps = {
  name: string;
  email: string;
  initials: string;
  triggerVariant: "sidebar" | "icon";
};

export function UserProfileMenu({ name, email, initials, triggerVariant }: UserProfileMenuProps) {
  const [open, setOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (!containerRef.current?.contains(event.target as Node)) {
        setOpen(false);
      }
    };

    window.addEventListener("mousedown", handleClickOutside);
    return () => window.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const dropdownPositionClass =
    triggerVariant === "icon" ? "right-0 top-11" : "left-0 right-0 bottom-14";

  return (
    <div ref={containerRef} className="relative">
      <UserProfileButton
        variant={triggerVariant}
        initials={initials}
        name={name}
        email={triggerVariant === "sidebar" ? email : undefined}
        onClick={(event) => {
          event.stopPropagation();
          setOpen((current) => !current);
        }}
      />
      {open ? (
        <div
          className={`absolute z-40 w-56 rounded-md border border-border bg-card p-1 shadow-lg ${dropdownPositionClass}`}
        >
          <div className="border-b border-border px-3 py-2">
            <p className="text-sm font-medium">{name}</p>
            <p className="text-xs text-muted-foreground">{email}</p>
          </div>
          <button type="button" className="mt-1 w-full rounded px-3 py-2 text-left text-sm hover:bg-muted">
            Profile
          </button>
          <button type="button" className="w-full rounded px-3 py-2 text-left text-sm hover:bg-muted">
            Billing
          </button>
          <button type="button" className="w-full rounded px-3 py-2 text-left text-sm hover:bg-muted">
            Settings
          </button>
          <button
            type="button"
            className="w-full rounded px-3 py-2 text-left text-sm text-red-600 hover:bg-red-50 dark:hover:bg-red-950/40"
            onClick={() => {
              setOpen(false);
              signOut({ callbackUrl: "/sign-in" });
            }}
          >
            Sign out
          </button>
        </div>
      ) : null}
    </div>
  );
}
