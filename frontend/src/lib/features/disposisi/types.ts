export type DispositionStatus='WAITING'|'RECEIVED'|'IN_PROGRESS'|'COMPLETED';
export type DispositionPriority='NORMAL'|'IMPORTANT'|'URGENT';
export type DispositionTargetType='USER'|'UNIT';

export interface DispositionHistory{
	id:number;
	status:DispositionStatus;
	note:string;
	at:string;
}

export interface DispositionRecord{
	id:number;
	code:string;
	incomingMailId:number;
	agendaNumber:string;
	letterNumber:string;
	sender:string;
	subject:string;
	targetType:DispositionTargetType;
	targetId:string;
	targetName:string;
	instruction:string;
	priority:DispositionPriority;
	dueDate:string;
	notes:string;
	status:DispositionStatus;
	createdBy:string;
	createdAt:string;
	updatedAt:string;
	history:DispositionHistory[];
}

export interface DispositionPayload{
	incomingMailId:number;
	targetType:DispositionTargetType;
	targetId:string;
	targetName:string;
	instruction:string;
	priority:DispositionPriority;
	dueDate:string;
	notes:string;
	status:DispositionStatus;
}

export interface DispositionMailOption{
	id:number;
	agendaNumber:string;
	letterNumber:string;
	sender:string;
	subject:string;
}

export interface DispositionOption{
	value:string;
	label:string;
}

export const DISPOSITION_STATUS_OPTIONS:{value:DispositionStatus;label:string}[]=[
	{value:'WAITING',label:'Menunggu Penerima'},
	{value:'RECEIVED',label:'Diterima'},
	{value:'IN_PROGRESS',label:'Diproses'},
	{value:'COMPLETED',label:'Selesai'}
];

export const DISPOSITION_STATUS_LABELS:Record<DispositionStatus,string>={
	WAITING:'Menunggu Penerima',
	RECEIVED:'Diterima',
	IN_PROGRESS:'Diproses',
	COMPLETED:'Selesai'
};

export const DISPOSITION_PRIORITY_OPTIONS:{value:DispositionPriority;label:string}[]=[
	{value:'NORMAL',label:'Normal'},
	{value:'IMPORTANT',label:'Penting'},
	{value:'URGENT',label:'Segera'}
];

export const DISPOSITION_PRIORITY_LABELS:Record<DispositionPriority,string>={
	NORMAL:'Normal',
	IMPORTANT:'Penting',
	URGENT:'Segera'
};

export const DISPOSITION_TARGET_LABELS:Record<DispositionTargetType,string>={
	USER:'Pengguna',
	UNIT:'Unit Kerja'
};

export function formatDispositionDate(value:string){
	if(!value)return'-';
	const date=new Date(`${value}T00:00:00`);
	if(Number.isNaN(date.getTime()))return value;
	return new Intl.DateTimeFormat('id-ID',{
		day:'2-digit',
		month:'short',
		year:'numeric'
	}).format(date);
}

export function formatDispositionDateTime(value:string){
	if(!value)return'-';
	const date=new Date(value);
	if(Number.isNaN(date.getTime()))return value;
	return new Intl.DateTimeFormat('id-ID',{
		day:'2-digit',
		month:'short',
		year:'numeric',
		hour:'2-digit',
		minute:'2-digit'
	}).format(date);
}