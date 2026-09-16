import { apiFetch } from '$lib/api';
import type {
    IncomingMailPayload,
    IncomingMailRecord,
    IncomingMailStatus
} from '$lib/features/surat-masuk/types';
const API_URL = '/api/v1/incoming-mails';
type ApiResponse<T> = {
    success: boolean;
    message: string;
    data: T;
};
type IncomingMailApiRecord = Omit<
    IncomingMailRecord,
    'notes' | 'fileName' | 'fileType' | 'fileSize'
> & {
    notes: string | null;
    fileName: string | null;
    fileType: string | null;
    fileSize: number | null;
};
function normalizeRecord(record: IncomingMailApiRecord): IncomingMailRecord {
    return {
        ...record,
        notes: record.notes ?? '',
        fileName: record.fileName ?? '',
        fileType: record.fileType ?? '',
        fileSize: record.fileSize ?? 0
    };
}
async function request<T>(
    url: string,
    options: RequestInit = {}
): Promise<T> {
    const headers = new Headers(options.headers);
    headers.set('Accept', 'application/json');
    if (typeof options.body === 'string' && !headers.has('Content-Type')) {
        headers.set('Content-Type', 'application/json');
    }
    const response = await apiFetch(url, {
        ...options,
        headers
    });
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
        throw new Error(
            body?.message || 'Akses ditolak. Periksa izin akun atau muat ulang halaman.'
        );
    }
    if (!response.ok || !body?.success) {
        throw new Error(
            body?.message || `Permintaan gagal dengan status ${response.status}`
        );
    }
    return body.data;
}
export async function getIncomingMails(): Promise<IncomingMailRecord[]> {
    const data = await request<IncomingMailApiRecord[]>(API_URL);
    return data.map(normalizeRecord);
}
export async function getIncomingMail(
    id: number
): Promise<IncomingMailRecord> {
    const data = await request<IncomingMailApiRecord>(
        `${API_URL}/${id}`
    );
    return normalizeRecord(data);
}
export async function createIncomingMail(
    payload: IncomingMailPayload
): Promise<IncomingMailRecord> {
    const data = await request<IncomingMailApiRecord>(API_URL, {
        method: 'POST',
        body: JSON.stringify(payload)
    });
    return normalizeRecord(data);
}
export async function updateIncomingMail(
    id: number,
    payload: IncomingMailPayload
): Promise<IncomingMailRecord> {
    const data = await request<IncomingMailApiRecord>(`${API_URL}/${id}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
    });
    return normalizeRecord(data);
}
export async function updateIncomingMailStatus(
    id: number,
    status: IncomingMailStatus
): Promise<IncomingMailRecord> {
    const data = await request<IncomingMailApiRecord>(
        `${API_URL}/${id}/status`,
        {
            method: 'PATCH',
            body: JSON.stringify({ status })
        }
    );
    return normalizeRecord(data);
}
export async function deleteIncomingMail(id: number): Promise<void> {
    await request<null>(`${API_URL}/${id}`, {
        method: 'DELETE'
    });
}