const API_ORIGIN = 'http://localhost:8081';
type CsrfResponse = {
    headerName: string;
    token: string;
};
export async function apiFetch(
    path: string,
    options: RequestInit = {}
): Promise<Response> {
    const url = new URL(path, API_ORIGIN);
    if (url.origin !== API_ORIGIN) {
        throw new Error('Alamat API tidak diizinkan');
    }
    const method = (options.method ?? 'GET').toUpperCase();
    const headers = new Headers(options.headers);
    if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) {
        const csrfResponse = await fetch(`${API_ORIGIN}/api/v1/auth/csrf`, {
            credentials: 'include',
            cache: 'no-store'
        });
        if (!csrfResponse.ok) {
            throw new Error('Gagal mengambil token keamanan. Coba muat ulang halaman.');
        }
        const csrf: CsrfResponse = await csrfResponse.json();
        headers.set(csrf.headerName, csrf.token);
    }
    return fetch(url.toString(), {
        ...options,
        method,
        headers,
        credentials: 'include',
        cache: 'no-store'
    });
}
export async function logout(): Promise<void> {
    const response = await apiFetch('/api/v1/auth/logout', {
        method: 'POST'
    });
    if (!response.ok) {
        throw new Error('Logout gagal. Silakan coba lagi.');
    }
}