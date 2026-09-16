import { requestApi } from '$lib/api-client';
import type { UserFormPayload, UserRecord, UserStatus } from '$lib/features/pengguna/types';

const API_URL = '/api/v1/users';

type PageResponse<T> = { content: T[]; page: number; size: number; totalElements: number; totalPages: number; first: boolean; last: boolean };
type UserApiRecord = {
    id: number;
    name: string;
    email: string;
    role: UserRecord['role'];
    department: string;
    status: UserStatus;
    createdAt: string;
    updatedAt: string;
    lastLoginAt: string | null;
};

function normalizeDate(value: string | null, empty = 'Belum pernah login') {
    if (!value) return empty;
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;
    return new Intl.DateTimeFormat('id-ID', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' }).format(date);
}

function normalize(item: UserApiRecord): UserRecord {
    return {
        id: item.id,
        name: item.name,
        email: item.email,
        role: item.role,
        department: item.department,
        status: item.status,
        createdAt: normalizeDate(item.createdAt, '-'),
        lastLogin: normalizeDate(item.lastLoginAt)
    };
}

export async function getUsers(): Promise<UserRecord[]> {
    const page = await requestApi<PageResponse<UserApiRecord>>(`${API_URL}?page=0&size=100`);
    return page.content.map(normalize);
}

export async function createUserApi(payload: UserFormPayload): Promise<UserRecord> {
    return normalize(await requestApi<UserApiRecord>(API_URL, { method: 'POST', body: JSON.stringify(payload) }));
}

export async function updateUserApi(id: number, payload: UserFormPayload): Promise<UserRecord> {
    return normalize(await requestApi<UserApiRecord>(`${API_URL}/${id}`, { method: 'PUT', body: JSON.stringify(payload) }));
}

export async function updateUserStatusApi(id: number, status: UserStatus): Promise<UserRecord> {
    return normalize(await requestApi<UserApiRecord>(`${API_URL}/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) }));
}

export async function resetUserPasswordApi(id: number, newPassword: string): Promise<void> {
    await requestApi<null>(`${API_URL}/${id}/reset-password`, { method: 'PATCH', body: JSON.stringify({ newPassword }) });
}

export async function deleteUserApi(id: number): Promise<void> {
    await requestApi<null>(`${API_URL}/${id}`, { method: 'DELETE' });
}
