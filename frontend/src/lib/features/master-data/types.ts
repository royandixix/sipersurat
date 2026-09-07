export type MasterDataType='ROLE'|'UNIT_KERJA'|'KATEGORI_SURAT'|'SIFAT_SURAT';
export type MasterDataStatus='ACTIVE'|'INACTIVE';

export interface MasterDataRecord{
	id:number;
	type:MasterDataType;
	name:string;
	code:string;
	description:string;
	status:MasterDataStatus;
	usageCount:number;
	createdAt:string;
}

export interface MasterDataPayload{
	name:string;
	code:string;
	description:string;
	status:MasterDataStatus;
}

export const MASTER_DATA_CONFIG={
	ROLE:{
		label:'Role',
		singular:'Role',
		title:'Role Pengguna',
		description:'Kelola role dan hak akses pengguna SiPersurat.',
		searchPlaceholder:'Cari nama atau kode role...',
		usageLabel:'pengguna'
	},
	UNIT_KERJA:{
		label:'Unit Kerja',
		singular:'Unit Kerja',
		title:'Unit Kerja',
		description:'Kelola unit kerja atau divisi pada sistem.',
		searchPlaceholder:'Cari unit kerja...',
		usageLabel:'pengguna'
	},
	KATEGORI_SURAT:{
		label:'Kategori Surat',
		singular:'Kategori Surat',
		title:'Kategori Surat',
		description:'Kelola kategori untuk klasifikasi surat.',
		searchPlaceholder:'Cari kategori surat...',
		usageLabel:'surat'
	},
	SIFAT_SURAT:{
		label:'Sifat Surat',
		singular:'Sifat Surat',
		title:'Sifat Surat',
		description:'Kelola tingkat sifat dan kerahasiaan surat.',
		searchPlaceholder:'Cari sifat surat...',
		usageLabel:'surat'
	}
} as const;

export const MASTER_DATA_TABS:MasterDataType[]=[
	'ROLE',
	'UNIT_KERJA',
	'KATEGORI_SURAT',
	'SIFAT_SURAT'
];

export function generateMasterCode(value:string){
	return value
		.normalize('NFD')
		.replace(/[\u0300-\u036f]/g,'')
		.toUpperCase()
		.trim()
		.replace(/[^A-Z0-9]+/g,'_')
		.replace(/^_+|_+$/g,'');
}