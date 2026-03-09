"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useSession } from "next-auth/react";
import { useEffect, useMemo, useRef, useState, useSyncExternalStore } from "react";
import { useGetUserDetails } from "@apiclient";
import { UserProfileMenu } from "@/components/user-profile-menu";

type ThemeMode = "light" | "dark" | "system";
const THEME_STORAGE_KEY = "hadia-dashboard-theme";

const sidebarSections = [
  {
    title: "General",
    items: [
      { name: "Dashboard", href: "/dashboard", active: true },
      { name: "Users", href: "#", active: false },
      { name: "Subscriptions", href: "#", active: false },
      { name: "Billing", href: "#", active: false },
    ],
  },
  {
    title: "Product",
    items: [
      { name: "Tasks", href: "#", active: false },
      { name: "Chats", href: "#", active: false },
      { name: "Settings", href: "#", active: false },
    ],
  },
];

const topNav = ["Overview", "Analytics", "Reports", "Notifications"];

const metrics = [
  { title: "Total Revenue", value: "$45,231.89", delta: "+20.1% from last month" },
  { title: "Subscriptions", value: "+2,350", delta: "+180.1% from last month" },
  { title: "Sales", value: "+12,234", delta: "+19% from last month" },
  { title: "Active Now", value: "+573", delta: "+201 since last hour" },
];

export default function DashboardPage() {
  const router = useRouter();
  const { data: session, status } = useSession();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [settingsOpen, setSettingsOpen] = useState(false);
  const [themeMenuOpen, setThemeMenuOpen] = useState(false);
  const [searchOpen, setSearchOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [activeRouteIndex, setActiveRouteIndex] = useState(0);
  const searchInputRef = useRef<HTMLInputElement>(null);

  const theme = useSyncExternalStore(
    (onStoreChange) => {
      if (typeof window === "undefined") return () => undefined;
      const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
      const onThemeChange = () => onStoreChange();
      window.addEventListener("storage", onThemeChange);
      window.addEventListener("hadia-theme-change", onThemeChange);
      mediaQuery.addEventListener("change", onThemeChange);
      return () => {
        window.removeEventListener("storage", onThemeChange);
        window.removeEventListener("hadia-theme-change", onThemeChange);
        mediaQuery.removeEventListener("change", onThemeChange);
      };
    },
    () => {
      const storedTheme = window.localStorage.getItem(THEME_STORAGE_KEY);
      if (storedTheme === "light" || storedTheme === "dark" || storedTheme === "system") {
        return storedTheme as ThemeMode;
      }
      return "system";
    },
    () => "system",
  );

  const systemPrefersDark = useSyncExternalStore(
    (onStoreChange) => {
      if (typeof window === "undefined") return () => undefined;
      const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
      const onChange = () => onStoreChange();
      mediaQuery.addEventListener("change", onChange);
      return () => mediaQuery.removeEventListener("change", onChange);
    },
    () => window.matchMedia("(prefers-color-scheme: dark)").matches,
    () => false,
  );

  const setTheme = (nextTheme: ThemeMode) => {
    window.localStorage.setItem(THEME_STORAGE_KEY, nextTheme);
    window.dispatchEvent(new Event("hadia-theme-change"));
  };

  const { data: currentUser } = useGetUserDetails({
    query: {
      enabled: status === "authenticated" && !!session?.accessToken,
    },
  });

  const userEmail = currentUser?.email ?? session?.user?.email ?? "unknown@hadiasaas.com";
  const userFirstName = currentUser?.firstName?.trim() || userEmail.split("@")[0] || "User";
  const userInitials = userFirstName.slice(0, 2).toUpperCase();

  const searchableRoutes = useMemo(() => {
    const flattened = sidebarSections.flatMap((section) =>
      section.items
        .filter((item) => item.href.startsWith("/"))
        .map((item) => ({
          title: item.name,
          href: item.href,
          section: section.title,
        })),
    );

    const extras = [
      { title: "Home", href: "/", section: "General" },
      { title: "Sign In", href: "/sign-in", section: "Pages" },
    ];

    const unique = new Map<string, { title: string; href: string; section: string }>();
    [...flattened, ...extras].forEach((route) => {
      unique.set(route.href, route);
    });

    return [...unique.values()];
  }, []);

  const filteredRoutes = useMemo(() => {
    const query = searchQuery.trim().toLowerCase();
    if (!query) {
      return searchableRoutes;
    }

    return searchableRoutes.filter(
      (route) => route.title.toLowerCase().includes(query) || route.href.toLowerCase().includes(query),
    );
  }, [searchQuery, searchableRoutes]);

  useEffect(() => {
    const root = document.documentElement;
    const shouldUseDark = theme === "dark" || (theme === "system" && systemPrefersDark);
    root.classList.toggle("dark", shouldUseDark);
    window.localStorage.setItem(THEME_STORAGE_KEY, theme);
  }, [theme, systemPrefersDark]);

  useEffect(() => {
    const handleShortcut = (event: KeyboardEvent) => {
      if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === "k") {
        event.preventDefault();
        setSearchOpen(true);
        setTimeout(() => {
          searchInputRef.current?.focus();
        }, 0);
      }
    };

    window.addEventListener("keydown", handleShortcut);
    return () => window.removeEventListener("keydown", handleShortcut);
  }, []);

  const goToRoute = (href: string) => {
    setSearchOpen(false);
    setSearchQuery("");
    router.push(href);
  };

  return (
    <div className="min-h-screen bg-background text-foreground">
      <div className="flex min-h-screen">
        <aside
          className={`fixed inset-y-0 left-0 z-40 w-72 border-r border-border bg-card transition-all duration-300 lg:static lg:translate-x-0 ${
            sidebarCollapsed ? "lg:w-20" : "lg:w-72"
          } ${
            sidebarOpen ? "translate-x-0" : "-translate-x-full"
          }`}
        >
          <div
            className={`flex h-16 items-center justify-between border-b border-border ${
              sidebarCollapsed ? "px-2" : "px-4"
            }`}
          >
            <div>
              {sidebarCollapsed ? (
                <p className="grid h-9 w-9 place-items-center rounded-md bg-muted text-sm font-semibold">SA</p>
              ) : (
                <>
                  <p className="text-xs text-muted-foreground">Workspace</p>
                  <p className="text-sm font-semibold">Shadcn Admin</p>
                </>
              )}
            </div>
            <button
              type="button"
              className="rounded-md border border-border px-2 py-1 text-xs lg:hidden"
              onClick={() => setSidebarOpen(false)}
            >
              Close
            </button>
          </div>
          <div className={`space-y-8 ${sidebarCollapsed ? "p-2" : "p-4"}`}>
            {sidebarSections.map((section) => (
              <div key={section.title}>
                {!sidebarCollapsed ? (
                  <p className="mb-2 px-2 text-xs uppercase tracking-wide text-muted-foreground">
                    {section.title}
                  </p>
                ) : null}
                <ul className="space-y-1">
                  {section.items.map((item) => (
                    <li key={item.name}>
                      <Link
                        href={item.href}
                        title={item.name}
                        className={`flex items-center rounded-md text-sm transition ${
                          item.active
                            ? "bg-muted text-foreground"
                            : "text-muted-foreground hover:bg-muted hover:text-foreground"
                        } ${
                          sidebarCollapsed
                            ? "h-10 justify-center px-0 py-0 text-xs font-semibold uppercase"
                            : "px-3 py-2"
                        }`}
                      >
                        {sidebarCollapsed ? item.name.slice(0, 1) : item.name}
                      </Link>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
          <div className="absolute inset-x-0 bottom-0 border-t border-border p-4">
            <UserProfileMenu
              triggerVariant={sidebarCollapsed ? "icon" : "sidebar"}
              initials={userInitials}
              name={userFirstName}
              email={userEmail}
            />
          </div>
        </aside>

        <main className="flex-1">
          <header className="sticky top-0 z-30 border-b border-border bg-background/90 backdrop-blur">
            <div className="flex h-16 items-center gap-3 px-4 lg:px-6">
              <button
                type="button"
                className="rounded-md border border-border p-2 lg:hidden"
                onClick={() => setSidebarOpen(true)}
              >
                <span className="sr-only">Open sidebar</span>
                <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M3 6h18M3 12h18M3 18h18" />
                </svg>
              </button>
              <button
                type="button"
                className="hidden rounded-md border border-border p-2 lg:inline-flex"
                onClick={() => setSidebarCollapsed((current) => !current)}
              >
                <span className="sr-only">Toggle sidebar</span>
                <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
                  {sidebarCollapsed ? <path d="M9 6l6 6-6 6" /> : <path d="M15 18l-6-6 6-6" />}
                </svg>
              </button>

              <nav className="hidden items-center gap-5 text-sm lg:flex">
                {topNav.map((item, index) => (
                  <a
                    key={item}
                    href="#"
                    className={index === 0 ? "font-medium text-foreground" : "text-muted-foreground"}
                  >
                    {item}
                  </a>
                ))}
              </nav>

              <div className="ml-auto flex items-center gap-2">
                <div className="relative hidden sm:block">
                  <span className="pointer-events-none absolute left-2 top-2 text-muted-foreground">
                    <svg
                      viewBox="0 0 24 24"
                      className="h-4 w-4"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                    >
                      <circle cx="11" cy="11" r="8" />
                      <path d="M21 21l-4.3-4.3" />
                    </svg>
                  </span>
                  <input
                    ref={searchInputRef}
                    type="search"
                    placeholder="Search routes..."
                    value={searchQuery}
                    onFocus={() => setSearchOpen(true)}
                    onBlur={() => {
                      window.setTimeout(() => setSearchOpen(false), 120);
                    }}
                    onChange={(event) => {
                      setSearchQuery(event.target.value);
                      setActiveRouteIndex(0);
                    }}
                    onKeyDown={(event) => {
                      if (!filteredRoutes.length) {
                        return;
                      }

                      if (event.key === "ArrowDown") {
                        event.preventDefault();
                        setActiveRouteIndex((current) => Math.min(current + 1, filteredRoutes.length - 1));
                      } else if (event.key === "ArrowUp") {
                        event.preventDefault();
                        setActiveRouteIndex((current) => Math.max(current - 1, 0));
                      } else if (event.key === "Enter") {
                        event.preventDefault();
                        goToRoute(filteredRoutes[activeRouteIndex]?.href ?? filteredRoutes[0].href);
                      }
                    }}
                    className="h-9 w-44 rounded-md border border-input bg-muted/40 pl-8 pr-2 text-sm outline-none ring-ring placeholder:text-muted-foreground focus:ring-2 lg:w-56"
                  />
                  {searchOpen ? (
                    <div className="absolute right-0 top-11 z-40 w-72 rounded-md border border-border bg-card p-1 shadow-lg">
                      {filteredRoutes.length ? (
                        <ul className="max-h-72 overflow-y-auto">
                          {filteredRoutes.map((route, index) => (
                            <li key={route.href}>
                              <button
                                type="button"
                                className={`flex w-full items-center justify-between rounded px-2 py-2 text-left text-sm ${
                                  index === activeRouteIndex ? "bg-muted text-foreground" : "text-muted-foreground hover:bg-muted"
                                }`}
                                onMouseDown={(event) => event.preventDefault()}
                                onClick={() => goToRoute(route.href)}
                              >
                                <span>{route.title}</span>
                                <span className="text-xs">{route.section}</span>
                              </button>
                            </li>
                          ))}
                        </ul>
                      ) : (
                        <p className="px-2 py-2 text-sm text-muted-foreground">No routes found.</p>
                      )}
                    </div>
                  ) : null}
                </div>

                <div className="relative">
                  <button
                    type="button"
                    className="rounded-full border border-border p-2"
                    onClick={() => {
                      setThemeMenuOpen((current) => !current);
                    }}
                  >
                    <span className="sr-only">Toggle theme</span>
                    <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M12 3v2M12 19v2M5.6 5.6l1.4 1.4M17 17l1.4 1.4M3 12h2M19 12h2M5.6 18.4l1.4-1.4M17 7l1.4-1.4" />
                      <circle cx="12" cy="12" r="4" />
                    </svg>
                  </button>
                  {themeMenuOpen ? (
                    <div className="absolute right-0 top-11 z-40 w-36 rounded-md border border-border bg-card p-1 shadow-lg">
                      {(["light", "dark", "system"] as ThemeMode[]).map((mode) => (
                        <button
                          key={mode}
                          type="button"
                          className={`flex w-full items-center rounded px-2 py-1.5 text-left text-sm capitalize hover:bg-muted ${
                            theme === mode ? "text-foreground" : "text-muted-foreground"
                          }`}
                          onClick={() => {
                            setTheme(mode);
                            setThemeMenuOpen(false);
                          }}
                        >
                          <span className="flex-1">{mode}</span>
                          {theme === mode ? "✓" : null}
                        </button>
                      ))}
                    </div>
                  ) : null}
                </div>

                <button
                  type="button"
                  className="rounded-full border border-border p-2"
                  onClick={() => {
                    setSettingsOpen(true);
                    setThemeMenuOpen(false);
                  }}
                >
                  <span className="sr-only">Open settings</span>
                  <svg viewBox="0 0 24 24" className="h-4 w-4" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="3" />
                    <path d="M19.4 15a1.7 1.7 0 0 0 .3 1.8l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.8-.3 1.7 1.7 0 0 0-1 1.6V21a2 2 0 1 1-4 0v-.2a1.7 1.7 0 0 0-1-1.6 1.7 1.7 0 0 0-1.8.3l-.1.1a2 2 0 0 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.8 1.7 1.7 0 0 0-1.6-1H3a2 2 0 1 1 0-4h.2a1.7 1.7 0 0 0 1.6-1 1.7 1.7 0 0 0-.3-1.8l-.1-.1a2 2 0 0 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.8.3h.1A1.7 1.7 0 0 0 10 3.2V3a2 2 0 1 1 4 0v.2a1.7 1.7 0 0 0 1 1.6h.1a1.7 1.7 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.8v.1a1.7 1.7 0 0 0 1.6 1H21a2 2 0 1 1 0 4h-.2a1.7 1.7 0 0 0-1.6 1z" />
                  </svg>
                </button>

                <UserProfileMenu
                  triggerVariant="icon"
                  initials={userInitials}
                  name={userFirstName}
                  email={userEmail}
                />
              </div>
            </div>
          </header>

          <section className="space-y-6 px-4 py-6 lg:px-6">
            <div className="flex items-center justify-between">
              <h1 className="text-2xl font-bold tracking-tight">Dashboard</h1>
              <button
                type="button"
                className="rounded-md border border-border bg-foreground px-4 py-2 text-sm font-medium text-background"
              >
                Download
              </button>
            </div>

            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
              {metrics.map((metric) => (
                <article key={metric.title} className="rounded-xl border border-border bg-card p-5">
                  <p className="text-sm text-muted-foreground">{metric.title}</p>
                  <p className="mt-2 text-2xl font-bold">{metric.value}</p>
                  <p className="mt-1 text-xs text-muted-foreground">{metric.delta}</p>
                </article>
              ))}
            </div>

            <div className="grid gap-4 xl:grid-cols-3">
              <article className="rounded-xl border border-border bg-card p-5 xl:col-span-2">
                <h2 className="text-lg font-semibold">Overview</h2>
                <div className="mt-4 grid h-64 place-items-center rounded-lg border border-dashed border-border text-sm text-muted-foreground">
                  Chart area
                </div>
              </article>

              <article className="rounded-xl border border-border bg-card p-5">
                <h2 className="text-lg font-semibold">Recent Sales</h2>
                <p className="text-sm text-muted-foreground">You made 265 sales this month.</p>
                <ul className="mt-4 space-y-3">
                  {["Olivia Martin", "Jackson Lee", "Isabella Nguyen", "William Kim"].map((name, idx) => (
                    <li key={name} className="flex items-center justify-between text-sm">
                      <span>{name}</span>
                      <span className="font-medium">+${(idx + 1) * 199}.00</span>
                    </li>
                  ))}
                </ul>
              </article>
            </div>
          </section>
        </main>
      </div>

      {sidebarOpen ? (
        <button
          type="button"
          aria-label="Close sidebar overlay"
          className="fixed inset-0 z-30 bg-black/40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      ) : null}

      <aside
        className={`fixed inset-y-0 right-0 z-50 w-80 border-l border-border bg-card p-5 transition-transform duration-300 ${
          settingsOpen ? "translate-x-0" : "translate-x-full"
        }`}
      >
        <div className="mb-5 flex items-center justify-between">
          <h2 className="text-lg font-semibold">Theme Settings</h2>
          <button
            type="button"
            className="rounded-md border border-border px-2 py-1 text-xs"
            onClick={() => setSettingsOpen(false)}
          >
            Close
          </button>
        </div>
        <p className="mb-4 text-sm text-muted-foreground">
          Inspired by the template config drawer. Switch theme and tune layout preferences here.
        </p>
        <div className="space-y-3">
          {(["light", "dark", "system"] as ThemeMode[]).map((mode) => (
            <button
              key={mode}
              type="button"
              className={`w-full rounded-md border px-3 py-2 text-left text-sm capitalize ${
                theme === mode
                  ? "border-foreground bg-muted text-foreground"
                  : "border-border text-muted-foreground hover:bg-muted"
              }`}
              onClick={() => setTheme(mode)}
            >
              {mode}
            </button>
          ))}
        </div>
      </aside>
    </div>
  );
}
