import { requestApi } from '$lib/api-client';
import type { DispositionPayload, DispositionRecord, DispositionStatus } from '$lib/features/disposisi/types';

const API_URL = '/api/v1/dispositions';

type DispositionApiRecord = Omit<DispositionRecord, 'targetId' | 'dueDate' | 'notes' | 'createdBy' | 'history'> & {
    targetId: string | null;
    dueDate: string | null;
    notes: string | null;
    createdBy: string | null;
    history: Array<{ id: number; status: DispositionStatus; note: string; at: string }> | null;
};

function normalize(item: DispositionApiRecord): DispositionRecord {
    return {
        ...item,
        targetId: item.targetId ?? '',
        dueDate: item.dueDate ?? '',
        notes: item.notes ?? '',
        createdBy: item.createdBy ?? 'Sistem',
        history: item.history ?? []
    };
}

export async function getDispositions(): Promise<DispositionRecord[]> {
    return (await requestApi<DispositionApiRecord[]>(API_URL)).map(normalize);
}

export async function createDisposition(payload: DispositionPayload): Promise<DispositionRecord> {
    return normalize(await requestApi<DispositionApiRecord>(API_URL, {
        method: 'POST', body: JSON.stringify({ ...payload, dueDate: payload.dueDate || null })
    }));
}

export async function updateDisposition(id: number, payload: DispositionPayload): Promise<DispositionRecord> {
    return normalize(await requestApi<DispositionApiRecord>(`${API_URL}/${id}`, {
        method: 'PUT', body: JSON.stringify({ ...payload, dueDate: payload.dueDate || null })
    }));
}

export async function updateDispositionStatus(id: number, status: DispositionStatus): Promise<DispositionRecord> {
    return normalize(await requestApi<DispositionApiRecord>(`${API_URL}/${id}/status`, {
        method: 'PATCH', body: JSON.stringify({ status })
    }));
}

export async function deleteDisposition(id: number): Promise<void> {
    await requestApi<null>(`${API_URL}/${id}`, { method: 'DELETE' });
}
