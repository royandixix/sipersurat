export type IncomingMailStatus='RECEIVED'|'PENDING_DISPOSITION'|'DISPOSITIONED'|'IN_PROGRESS'|'COMPLETED'|'ARCHIVED';

export interface IncomingMailRecord{
	id:number;
	agendaNumber:string;
	letterNumber:string;
	letterDate:string;
	receivedDate:string;
	sender:string;
	subject:string;
	category:string;
	priority:string;
	targetUnit:string;
	notes:string;
	status:IncomingMailStatus;
	fileName:string;
	fileType:string;
	fileSize:number;
	createdAt:string;
	updatedAt:string;
}

export interface IncomingMailPayload{
	letterNumber:string;
	letterDate:string;
	receivedDate:string;
	sender:string;
	subject:string;
	category:string;
	priority:string;
	targetUnit:string;
	notes:string;
	status:IncomingMailStatus;
	fileName:string;
	fileType:string;
	fileSize:number;
}

export const INCOMING_MAIL_STATUS_OPTIONS:{value:IncomingMailStatus;label:string}[]=[
	{value:'RECEIVED',label:'Diterima'},
	{value:'PENDING_DISPOSITION',label:'Menunggu Disposisi'},
	{value:'DISPOSITIONED',label:'Didisposisikan'},
	{value:'IN_PROGRESS',label:'Diproses'},
	{value:'COMPLETED',label:'Selesai'},
	{value:'ARCHIVED',label:'Diarsipkan'}
];

export const INCOMING_MAIL_STATUS_LABELS:Record<IncomingMailStatus,string>={
	RECEIVED:'Diterima',
	PENDING_DISPOSITION:'Menunggu Disposisi',
	DISPOSITIONED:'Didisposisikan',
	IN_PROGRESS:'Diproses',
	COMPLETED:'Selesai',
	ARCHIVED:'Diarsipkan'
};

export function formatFileSize(bytes:number){
	if(!bytes)return'-';
	if(bytes<1024)return`${bytes} B`;
	if(bytes<1024*1024)return`${(bytes/1024).toFixed(1)} KB`;
	return`${(bytes/(1024*1024)).toFixed(1)} MB`;
}

export function formatDate(value:string){
	if(!value)return'-';
	const date=new Date(`${value}T00:00:00`);
	if(Number.isNaN(date.getTime()))return value;
	return new Intl.DateTimeFormat('id-ID',{day:'2-digit',month:'short',year:'numeric'}).format(date);
}