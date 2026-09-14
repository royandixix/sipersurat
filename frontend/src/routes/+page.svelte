<script lang="ts">
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';
    import { apiFetch } from '$lib/api';
    let email = $state('');
    let password = $state('');
    let loading = $state(false);
    let checking = $state(true);
    let errorMessage = $state('');
    onMount(() => {
        void checkSession();
    });
    async function checkSession() {
        try {
            const response = await apiFetch('/api/v1/auth/me');
            if (response.ok) {
                await goto('/dashboard', { replaceState: true });
            } else if (response.status !== 401) {
                errorMessage = 'Gagal memeriksa sesi. Silakan coba login.';
            }
        } catch {
            errorMessage = 'Backend belum dapat dihubungi. Pastikan backend berjalan.';
        } finally {
            checking = false;
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
            password = '';
            await goto('/dashboard', { invalidateAll: true });
        } catch (error) {
            errorMessage = error instanceof Error
                ? error.message
                : 'Tidak dapat menghubungi backend.';
        } finally {
            loading = false;
        }
    }
</script>
<svelte:head>
    <title>Login | SiPersurat</title>
    <meta name="description" content="Masuk ke sistem pengelolaan persuratan." />
</svelte:head>
<main class="login-page">
    <section class="login-card">
        <div class="brand">SP</div>
        <p class="eyebrow">SISTEM PENGELOLAAN PERSURATAN</p>
        <h1>Masuk ke SiPersurat</h1>
        <p class="description">Gunakan akun yang telah diberikan oleh administrator.</p>
        <form onsubmit={handleLogin}>
            <label for="email">Email</label>
            <input
                id="email"
                name="email"
                type="email"
                bind:value={email}
                placeholder="nama@instansi.com"
                autocomplete="username"
                required
                disabled={loading || checking}
            />
            <label for="password">Password</label>
            <input
                id="password"
                name="password"
                type="password"
                bind:value={password}
                placeholder="Masukkan password"
                autocomplete="current-password"
                required
                disabled={loading || checking}
            />
            {#if errorMessage}
                <p class="error" role="alert">{errorMessage}</p>
            {/if}
            <button type="submit" disabled={loading || checking}>
                {checking ? 'Memeriksa sesi…' : loading ? 'Sedang masuk…' : 'Masuk'}
            </button>
        </form>
        <p class="help">Belum memiliki akun? Hubungi administrator instansi.</p>
    </section>
</main>
<style>
    .login-page {
        min-height: 100dvh;
        display: grid;
        place-items: center;
        padding: 24px;
        background: #f1f5f9;
        color: #0f172a;
        font-family: system-ui, sans-serif;
    }
    .login-card {
        width: 100%;
        max-width: 440px;
        padding: 36px;
        border: 1px solid #e2e8f0;
        border-radius: 20px;
        background: white;
        box-shadow: 0 16px 48px rgb(15 23 42 / 7%);
    }
    .brand {
        display: grid;
        place-items: center;
        width: 52px;
        height: 52px;
        margin-bottom: 24px;
        border-radius: 14px;
        background: #1d4ed8;
        color: white;
        font-size: 20px;
        font-weight: 800;
    }
    .eyebrow {
        margin-bottom: 10px;
        color: #475569;
        font-size: 10px;
        font-weight: 700;
        letter-spacing: 1px;
    }
    h1 {
        margin: 0 0 10px;
        font-size: 26px;
        font-weight: 750;
    }
    .description, .help {
        color: #64748b;
        font-size: 14px;
        line-height: 1.6;
    }
    form {
        display: grid;
        gap: 10px;
        margin-top: 28px;
    }
    label {
        font-size: 14px;
        font-weight: 600;
    }
    input {
        box-sizing: border-box;
        width: 100%;
        margin-bottom: 10px;
        padding: 12px 14px;
        border: 1px solid #cbd5e1;
        border-radius: 9px;
        font: inherit;
    }
    input:focus {
        outline: 3px solid #dbeafe;
        border-color: #2563eb;
    }
    button {
        margin-top: 6px;
        padding: 13px;
        border: none;
        border-radius: 9px;
        background: #1d4ed8;
        color: white;
        font: inherit;
        font-weight: 600;
        cursor: pointer;
    }
    button:disabled {
        opacity: 0.6;
        cursor: wait;
    }
    .error {
        margin: 0;
        padding: 12px;
        border-radius: 8px;
        background: #fef2f2;
        color: #b91c1c;
        font-size: 14px;
    }
    .help {
        margin-top: 24px;
        font-size: 12px;
    }
    @media (max-width: 480px) {
        .login-card { padding: 24px; }
    }
</style>