import { requestApi } from '$lib/api-client';
import type { SystemSettings } from '$lib/features/pengaturan/types';

export async function getSettings(): Promise<SystemSettings> {
    return requestApi<SystemSettings>('/api/v1/settings');
}

export async function saveSettingsApi(settings: SystemSettings): Promise<SystemSettings> {
    return requestApi<SystemSettings>('/api/v1/settings', { method: 'PUT', body: JSON.stringify({ settings }) });
}

export async function resetSettingsApi(): Promise<SystemSettings> {
    return requestApi<SystemSettings>('/api/v1/settings/reset', { method: 'POST' });
}

export async function getBackup(): Promise<unknown> {
    return requestApi<unknown>('/api/v1/settings/backup');
}

export async function restoreBackupApi(backup: unknown): Promise<SystemSettings> {
    return requestApi<SystemSettings>('/api/v1/settings/restore', { method: 'POST', body: JSON.stringify(backup) });
}

export async function resetOperationalDataApi(): Promise<void> {
    await requestApi<null>('/api/v1/settings/operational-data', { method: 'DELETE' });
}
