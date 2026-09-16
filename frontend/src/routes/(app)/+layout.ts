import { error, redirect } from '@sveltejs/kit';
import type { LayoutLoad } from './$types';
import { apiFetch } from '$lib/api';
export const ssr = false;
export const load: LayoutLoad = async ({ depends, fetch }) => {
    depends('auth:session');
    let response: Response;
    try {
        response = await apiFetch('/api/v1/auth/me', {}, fetch);
    } catch {
        error(503, 'Backend tidak dapat dihubungi. Pastikan backend berjalan.');
    }
    if (response.status === 401) {
        redirect(303, '/');
    }
    if (!response.ok) {
        error(response.status, 'Gagal memeriksa sesi pengguna.');
    }
    return {
        currentUser: await response.json()
    };
};