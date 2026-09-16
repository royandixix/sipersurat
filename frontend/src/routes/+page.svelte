<script lang="ts">
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';
    import {
        ArrowRight,
        Eye,
        EyeOff,
        FileText,
        LoaderCircle,
        LockKeyhole,
        Mail
    } from '@lucide/svelte';
    import { apiFetch } from '$lib/api';

    let email = $state('');
    let password = $state('');
    let showPassword = $state(false);
    let rememberEmail = $state(false);
    let loading = $state(false);
    let checking = $state(true);
    let errorMessage = $state('');

    const emailStorageKey = 'sipersurat.saved-email';
    const globeDots = Array.from({ length: 1600 }, (_, index) => {
        const y = 1 - (index / 1599) * 2;
        const radius = Math.sqrt(1 - y * y);
        const angle = index * Math.PI * (3 - Math.sqrt(5));
        const x = Math.cos(angle) * radius;
        const z = Math.sin(angle) * radius;
        return {
            x: 400 + x * 335,
            y: 400 + y * 335,
            opacity: 0.12 + ((z + 1) / 2) * 0.65,
            size: z > 0 ? 3 : 2
        };
    });

    onMount(() => {
        try {
            const savedEmail = localStorage.getItem(emailStorageKey);
            if (savedEmail) {
                email = savedEmail;
                rememberEmail = true;
            }
        } catch {
            // Login tetap tersedia jika penyimpanan browser dibatasi.
        }
        void checkSession();
    });

    async function checkSession() {
        try {
            const response = await apiFetch('/api/v1/auth/me');
            if (response.ok) {
                await goto('/dashboard', { replaceState: true });
            } else if (response.status !== 401) {
                errorMessage = 'Sesi belum dapat diperiksa. Silakan coba masuk.';
            }
        } catch {
            errorMessage = 'Server belum dapat dihubungi. Pastikan backend berjalan.';
        } finally {
            checking = false;
        }
    }

    function saveEmailPreference() {
        try {
            if (rememberEmail) {
                localStorage.setItem(emailStorageKey, email.trim());
            } else {
                localStorage.removeItem(emailStorageKey);
            }
        } catch {
            // Kegagalan menyimpan email tidak membatalkan login.
        }
    }

    async function handleLogin(event: SubmitEvent) {
        event.preventDefault();
        if (loading || checking) return;
        loading = true;
        errorMessage = '';
        try {
            const response = await apiFetch('/api/v1/auth/login', {
                method: 'POST',
                body: new URLSearchParams({
                    email: email.trim(),
                    password
                })
            });
            if (!response.ok) {
                const body = await response.json().catch(() => null);
                errorMessage = body?.message ?? 'Login gagal. Silakan coba lagi.';
                return;
            }
            saveEmailPreference();
            password = '';
            await goto('/dashboard', {
                invalidateAll: true,
                replaceState: true
            });
        } catch {
            errorMessage = 'Tidak dapat menyelesaikan login. Periksa koneksi lalu coba lagi.';
        } finally {
            loading = false;
        }
    }
</script>

<svelte:head>
    <title>Masuk | SiPersurat</title>
    <meta
        name="description"
        content="Masuk ke SiPersurat untuk mengelola surat, disposisi, dan arsip instansi."
    />
</svelte:head>

<main class="grid min-h-dvh bg-white font-sans text-zinc-900 lg:grid-cols-[1.05fr_0.95fr]">
    <section class="flex min-h-dvh flex-col">
        <header class="flex items-center justify-between px-6 py-7 sm:px-10">
            <a
                href="/"
                class="inline-flex items-center gap-2.5 rounded-md focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-orange-600"
                aria-label="SiPersurat — halaman masuk"
            >
                <span class="flex size-10 items-center justify-center rounded-xl bg-orange-600 text-white">
                    <Mail size={23} strokeWidth={2} aria-hidden="true" />
                </span>
                <span class="text-xl font-semibold tracking-tight">SiPersurat</span>
            </a>
            <span class="text-xs font-medium text-zinc-500 sm:text-sm">
                Bahasa Indonesia
            </span>
        </header>

        <div class="flex flex-1 items-center justify-center px-6 py-12 sm:px-10">
            <div class="w-full max-w-[410px]">
                <div class="mb-9">
                    <p class="mb-3 text-xs font-semibold tracking-[0.18em] text-orange-700">
                        SELAMAT DATANG KEMBALI
                    </p>
                    <h1 class="text-3xl font-semibold tracking-tight sm:text-[34px]">
                        Masuk ke SiPersurat
                    </h1>
                    <p class="mt-3 text-sm leading-6 text-zinc-500">
                        Kelola persuratan instansi dengan lebih mudah
                        melalui satu tempat.
                    </p>
                </div>

                <form onsubmit={handleLogin} class="space-y-5" aria-busy={loading || checking}>
                    <div class="space-y-2">
                        <label for="email" class="block text-sm font-medium text-zinc-800">
                            Email
                        </label>
                        <input
                            id="email"
                            name="email"
                            type="email"
                            bind:value={email}
                            autocomplete="username"
                            placeholder="nama@instansi.com"
                            required
                            disabled={loading || checking}
                            class="h-12 w-full rounded-lg border border-zinc-300 bg-white px-3.5 text-sm text-zinc-900 outline-none transition placeholder:text-zinc-400 focus:border-orange-500 focus:ring-3 focus:ring-orange-100 disabled:cursor-not-allowed disabled:bg-zinc-50"
                        />
                    </div>

                    <div class="space-y-2">
                        <label for="password" class="block text-sm font-medium text-zinc-800">
                            Password
                        </label>
                        <div class="relative">
                            <input
                                id="password"
                                name="password"
                                type={showPassword ? 'text' : 'password'}
                                bind:value={password}
                                autocomplete="current-password"
                                placeholder="Masukkan password"
                                required
                                disabled={loading || checking}
                                class="h-12 w-full rounded-lg border border-zinc-300 bg-white py-3 pr-12 pl-3.5 text-sm text-zinc-900 outline-none transition placeholder:text-zinc-400 focus:border-orange-500 focus:ring-3 focus:ring-orange-100 disabled:cursor-not-allowed disabled:bg-zinc-50"
                            />
                            <button
                                type="button"
                                onclick={() => showPassword = !showPassword}
                                aria-label={showPassword ? 'Sembunyikan password' : 'Tampilkan password'}
                                aria-controls="password"
                                aria-pressed={showPassword}
                                disabled={loading || checking}
                                class="absolute inset-y-0 right-0 flex w-12 items-center justify-center rounded-r-lg text-zinc-500 transition hover:text-zinc-900 focus-visible:outline-2 focus-visible:outline-orange-500 disabled:opacity-50"
                            >
                                {#if showPassword}
                                    <EyeOff size={19} aria-hidden="true" />
                                {:else}
                                    <Eye size={19} aria-hidden="true" />
                                {/if}
                            </button>
                        </div>
                    </div>

                    <label class="flex cursor-pointer items-center gap-2.5 py-1 text-sm text-zinc-600">
                        <input
                            type="checkbox"
                            bind:checked={rememberEmail}
                            disabled={loading || checking}
                            class="size-4 rounded border-zinc-300 accent-orange-600 focus:ring-orange-500"
                        />
                        Simpan email di perangkat ini
                    </label>

                    {#if errorMessage}
                        <div
                            role="alert"
                            class="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm leading-6 text-red-700"
                        >
                            {errorMessage}
                        </div>
                    {/if}

                    <button
                        type="submit"
                        disabled={loading || checking}
                        class="flex h-12 w-full items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-blue-600 disabled:cursor-not-allowed disabled:opacity-60"
                    >
                        {#if checking || loading}
                            <LoaderCircle size={18} class="motion-safe:animate-spin" aria-hidden="true" />
                            {checking ? 'Memeriksa sesi…' : 'Sedang masuk…'}
                        {:else}
                            Masuk
                            <ArrowRight size={17} aria-hidden="true" />
                        {/if}
                    </button>
                </form>

                <div class="mt-7 text-center text-sm leading-6 text-zinc-500">
                    <p>Belum memiliki akun atau lupa password?</p>
                    <p class="font-medium text-zinc-700">
                        Hubungi administrator instansi.
                    </p>
                </div>

                <div class="mt-10 flex items-center justify-center gap-2 border-t border-zinc-100 pt-6 text-xs text-zinc-500">
                    <LockKeyhole size={14} aria-hidden="true" />
                    Gunakan akun pribadi untuk mengakses sistem.
                </div>
            </div>
        </div>

        <footer class="px-6 py-6 text-center text-xs text-zinc-400 sm:px-10 lg:text-left">
            SiPersurat · Sistem Pengelolaan Persuratan
        </footer>
    </section>

    <aside class="relative hidden min-h-dvh overflow-hidden bg-orange-600 text-white lg:flex lg:flex-col">
        <div
            aria-hidden="true"
            class="absolute inset-0 bg-linear-to-br from-orange-700 via-orange-600 to-orange-500"
        ></div>

        <svg
            viewBox="0 0 800 800"
            fill="currentColor"
            aria-hidden="true"
            focusable="false"
            class="pointer-events-none absolute top-1/2 -right-[38%] w-[145%] -translate-y-1/2 text-orange-100"
        >
            {#each globeDots as dot, index (index)}
                <rect
                    x={dot.x}
                    y={dot.y}
                    width={dot.size}
                    height={dot.size}
                    opacity={dot.opacity}
                />
            {/each}
        </svg>

        <div class="relative z-10 flex items-center justify-between px-10 py-9">
            <span class="text-xs font-medium tracking-[0.16em] text-orange-100">
                PERSURATAN DIGITAL
            </span>
            <span class="rounded-full border border-white/30 px-3 py-1.5 text-xs font-medium">
                SiPersurat
            </span>
        </div>

        <div class="relative z-10 flex flex-1 items-center px-10 py-20 xl:px-16">
            <div class="max-w-lg">
                <div class="mb-7 flex size-12 items-center justify-center rounded-xl border border-white/30 bg-orange-700/30">
                    <FileText size={24} aria-hidden="true" />
                </div>
                <p class="mb-4 text-xs font-semibold tracking-[0.2em] text-orange-100">
                    SATU TEMPAT UNTUK SETIAP SURAT
                </p>
                <h2 class="text-4xl leading-[1.15] font-semibold tracking-tight xl:text-5xl">
                    Surat tertata.<br />
                    Kerja lebih<br />
                    terarah.
                </h2>
                <p class="mt-6 max-w-sm text-base leading-7 text-white">
                    Kelola surat masuk, surat keluar, disposisi,
                    dan arsip dalam satu alur yang terorganisasi.
                </p>
                <div class="mt-9 flex flex-wrap gap-2">
                    <span class="rounded-full border border-white/40 bg-orange-800/20 px-3.5 py-2 text-xs">
                        Surat masuk
                    </span>
                    <span class="rounded-full border border-white/40 bg-orange-800/20 px-3.5 py-2 text-xs">
                        Surat keluar
                    </span>
                    <span class="rounded-full border border-white/40 bg-orange-800/20 px-3.5 py-2 text-xs">
                        Disposisi & arsip
                    </span>
                </div>
            </div>
        </div>

        <div class="relative z-10 flex items-center justify-between border-t border-white/20 px-10 py-7 text-xs text-orange-100 xl:px-16">
            <span>Administrasi yang lebih terorganisasi.</span>
            <Mail size={18} aria-hidden="true" />
        </div>
    </aside>
</main>