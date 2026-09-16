export type UserRole =
	| 'SUPER_ADMIN'
	| 'ADMIN_PERSURATAN'
	| 'PIMPINAN'
	| 'STAFF';

export interface AuthUser {
	id: number;
	name: string;
	email: string;
	role: UserRole;
	initials: string;
}

export const currentUser: AuthUser = {
	id: 1,
	name: 'Super Administrator',
	email: 'admin@sipersurat.id',
	role: 'SUPER_ADMIN',
	initials: 'SA'
};