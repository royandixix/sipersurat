export type OutgoingMailStatus='DRAFT'|'PENDING_APPROVAL'|'APPROVED'|'SENT'|'REJECTED'|'ARCHIVED';

export interface OutgoingMailRecord{
	id:number;
	agendaNumber:string;
	letterNumber:string;
	letterDate:string;
	recipient:string;
	subject:string;
	category:string;
	priority:string;
	sourceUnit:string;
	signer:string;
	notes:string;
	status:OutgoingMailStatus;
	sentDate:string;
	fileName:string;
	fileType:string;
	fileSize:number;
	createdAt:string;
	updatedAt:string;
}

export interface OutgoingMailPayload{
	letterNumber:string;
	letterDate:string;
	recipient:string;
	subject:string;
	category:string;
	priority:string;
	sourceUnit:string;
	signer:string;
	notes:string;
	status:OutgoingMailStatus;
	sentDate:string;
	fileName:string;
	fileType:string;
	fileSize:number;
}

export const OUTGOING_MAIL_STATUS_OPTIONS:{value:OutgoingMailStatus;label:string}[]=[
	{value:'DRAFT',label:'Draft'},
	{value:'PENDING_APPROVAL',label:'Menunggu Persetujuan'},
	{value:'APPROVED',label:'Disetujui'},
	{value:'SENT',label:'Dikirim'},
	{value:'REJECTED',label:'Ditolak'},
	{value:'ARCHIVED',label:'Diarsipkan'}
];

export const OUTGOING_MAIL_STATUS_LABELS:Record<OutgoingMailStatus,string>={
	DRAFT:'Draft',
	PENDING_APPROVAL:'Menunggu Persetujuan',
	APPROVED:'Disetujui',
	SENT:'Dikirim',
	REJECTED:'Ditolak',
	ARCHIVED:'Diarsipkan'
};

export function formatOutgoingDate(value:string){
	if(!value)return'-';
	const date=new Date(`${value}T00:00:00`);
	if(Number.isNaN(date.getTime()))return value;
	return new Intl.DateTimeFormat('id-ID',{
		day:'2-digit',
		month:'short',
		year:'numeric'
	}).format(date);
}

export function formatOutgoingDateTime(value:string){
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

export function formatOutgoingFileSize(bytes:number){
	if(!bytes)return'-';
	if(bytes<1024)return`${bytes} B`;
	if(bytes<1024*1024)return`${(bytes/1024).toFixed(1)} KB`;
	return`${(bytes/(1024*1024)).toFixed(1)} MB`;
}