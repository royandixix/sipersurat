import { requestApi } from '$lib/api-client';
import type { OutgoingMailPayload, OutgoingMailRecord, OutgoingMailStatus } from '$lib/features/surat-keluar/types';

const API_URL = '/api/v1/outgoing-mails';

type OutgoingMailApiRecord = Omit<OutgoingMailRecord, 'notes' | 'sentDate' | 'fileName' | 'fileType' | 'fileSize'> & {
    notes: string | null;
    sentDate: string | null;
    fileName: string | null;
    fileType: string | null;
    fileSize: number | null;
};

function normalizeRecord(record: OutgoingMailApiRecord): OutgoingMailRecord {
    return {
        ...record,
        notes: record.notes ?? '',
        sentDate: record.sentDate ?? '',
        fileName: record.fileName ?? '',
        fileType: record.fileType ?? '',
        fileSize: record.fileSize ?? 0
    };
}

function preparePayload(payload: OutgoingMailPayload) {
    return {
        ...payload,
        sentDate: payload.sentDate || null,
        fileName: payload.fileName || null,
        fileType: payload.fileType || null,
        fileSize: payload.fileSize ?? 0
    };
}

export async function getOutgoingMails(): Promise<OutgoingMailRecord[]> {
    const data = await requestApi<OutgoingMailApiRecord[]>(API_URL);
    return data.map(normalizeRecord);
}

export async function getOutgoingMail(id: number): Promise<OutgoingMailRecord> {
    return normalizeRecord(await requestApi<OutgoingMailApiRecord>(`${API_URL}/${id}`));
}

export async function createOutgoingMail(payload: OutgoingMailPayload): Promise<OutgoingMailRecord> {
    return normalizeRecord(await requestApi<OutgoingMailApiRecord>(API_URL, {
        method: 'POST',
        body: JSON.stringify(preparePayload(payload))
    }));
}

export async function updateOutgoingMail(id: number, payload: OutgoingMailPayload): Promise<OutgoingMailRecord> {
    return normalizeRecord(await requestApi<OutgoingMailApiRecord>(`${API_URL}/${id}`, {
        method: 'PUT',
        body: JSON.stringify(preparePayload(payload))
    }));
}

export async function updateOutgoingMailStatus(id: number, status: OutgoingMailStatus): Promise<OutgoingMailRecord> {
    return normalizeRecord(await requestApi<OutgoingMailApiRecord>(`${API_URL}/${id}/status`, {
        method: 'PATCH',
        body: JSON.stringify({ status })
    }));
}

export async function deleteOutgoingMail(id: number): Promise<void> {
    await requestApi<null>(`${API_URL}/${id}`, { method: 'DELETE' });
}
