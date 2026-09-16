import { apiFetch } from '$lib/api';

export type ApiResponse<T> = {
    success: boolean;
    message: string;
    data: T;
};

export async function requestApi<T>(path: string, options: RequestInit = {}): Promise<T> {
    const headers = new Headers(options.headers);
    headers.set('Accept', 'application/json');
    if (typeof options.body === 'string' && !headers.has('Content-Type')) {
        headers.set('Content-Type', 'application/json');
    }
    const response = await apiFetch(path, { ...options, headers });
    let body: ApiResponse<T> | null = null;
    try {
        body = await response.json();
    } catch {
        body = null;
    }
    if (response.status === 401) {
        throw new Error('Sesi tidak tersedia atau sudah berakhir. Silakan login kembali.');
    }
    if (response.status === 403) {
        throw new Error(body?.message || 'Akses ditolak. Periksa izin akun Anda.');
    }
    if (!response.ok || !body?.success) {
        throw new Error(body?.message || `Permintaan gagal dengan status ${response.status}`);
    }
    return body.data;
}
