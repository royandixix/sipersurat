import type { UserRole } from '$lib/types/auth';

export type UserStatus = 'ACTIVE' | 'INACTIVE';

export interface UserRecord {
	id: number;
	name: string;
	email: string;
	role: UserRole;
	department: string;
	status: UserStatus;
	createdAt: string;
	lastLogin: string;
}

export interface UserFormPayload {
	name: string;
	email: string;
	role: UserRole;
	department: string;
	status: UserStatus;
	password?: string;
}

export const ROLE_OPTIONS: {
	value: UserRole;
	label: string;
}[] = [
	{
		value: 'SUPER_ADMIN',
		label: 'Super Administrator'
	},
	{
		value: 'ADMIN_PERSURATAN',
		label: 'Admin Persuratan'
	},
	{
		value: 'PIMPINAN',
		label: 'Pimpinan'
	},
	{
		value: 'STAFF',
		label: 'Staff'
	}
];

export const ROLE_LABELS: Record<UserRole, string> = {
	SUPER_ADMIN: 'Super Administrator',
	ADMIN_PERSURATAN: 'Admin Persuratan',
	PIMPINAN: 'Pimpinan',
	STAFF: 'Staff'
};

export const DEPARTMENT_OPTIONS = [
	'Administrator',
	'Tata Usaha',
	'Pimpinan',
	'Sekretariat',
	'Keuangan',
	'Umum',
	'Kepegawaian'
];

export function getInitials(name: string) {
	return name
		.trim()
		.split(/\s+/)
		.slice(0, 2)
		.map((word) => word[0]?.toUpperCase() ?? '')
		.join('');
}