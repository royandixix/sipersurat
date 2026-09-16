import { requestApi } from '$lib/api-client';
import type { ReportRecord } from '$lib/features/laporan/types';

export async function getReports(): Promise<ReportRecord[]> {
    return requestApi<ReportRecord[]>('/api/v1/reports');
}
