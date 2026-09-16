import { requestApi } from '$lib/api-client';
import type { ArchiveRecord, ArchiveSource } from '$lib/features/arsip/types';

const API_URL = '/api/v1/archives';

export async function getArchives(): Promise<ArchiveRecord[]> {
    return requestApi<ArchiveRecord[]>(API_URL);
}

export async function restoreArchiveApi(source: ArchiveSource, sourceId: number): Promise<ArchiveRecord> {
    return requestApi<ArchiveRecord>(`${API_URL}/${source}/${sourceId}/restore`, { method: 'PATCH' });
}

export async function deleteArchiveApi(source: ArchiveSource, sourceId: number): Promise<void> {
    await requestApi<null>(`${API_URL}/${source}/${sourceId}`, { method: 'DELETE' });
}
