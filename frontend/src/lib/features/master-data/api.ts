import { requestApi } from '$lib/api-client';
import type { MasterDataPayload, MasterDataRecord, MasterDataType } from '$lib/features/master-data/types';

const API_URL = '/api/v1/master-data';

type MasterDataApiRecord = Omit<MasterDataRecord, 'createdAt'> & { createdAt: string; updatedAt?: string };

function normalize(item: MasterDataApiRecord): MasterDataRecord {
    return { ...item, createdAt: item.createdAt ?? '' };
}

export async function getMasterData(): Promise<MasterDataRecord[]> {
    return (await requestApi<MasterDataApiRecord[]>(API_URL)).map(normalize);
}

export async function createMasterData(type: MasterDataType, payload: MasterDataPayload): Promise<MasterDataRecord> {
    return normalize(await requestApi<MasterDataApiRecord>(`${API_URL}/${type}`, {
        method: 'POST', body: JSON.stringify(payload)
    }));
}

export async function updateMasterData(id: number, payload: MasterDataPayload): Promise<MasterDataRecord> {
    return normalize(await requestApi<MasterDataApiRecord>(`${API_URL}/${id}`, {
        method: 'PUT', body: JSON.stringify(payload)
    }));
}

export async function toggleMasterDataStatus(id: number): Promise<MasterDataRecord> {
    return normalize(await requestApi<MasterDataApiRecord>(`${API_URL}/${id}/toggle-status`, { method: 'PATCH' }));
}

export async function deleteMasterData(id: number): Promise<void> {
    await requestApi<null>(`${API_URL}/${id}`, { method: 'DELETE' });
}
