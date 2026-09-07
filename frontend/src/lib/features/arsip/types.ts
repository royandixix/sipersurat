export type ArchiveSource='INCOMING'|'OUTGOING';

export interface ArchiveRecord{
	id:string;
	sourceId:number;
	source:ArchiveSource;
	agendaNumber:string;
	letterNumber:string;
	letterDate:string;
	archiveDate:string;
	correspondent:string;
	subject:string;
	category:string;
	priority:string;
	unit:string;
	notes:string;
	fileName:string;
	fileType:string;
	fileSize:number;
}

export const ARCHIVE_SOURCE_LABELS:Record<ArchiveSource,string>={
	INCOMING:'Surat Masuk',
	OUTGOING:'Surat Keluar'
};

export function formatArchiveDate(value:string){
	if(!value)return'-';

	const date=new Date(`${value}T00:00:00`);

	if(Number.isNaN(date.getTime()))return value;

	return new Intl.DateTimeFormat('id-ID',{
		day:'2-digit',
		month:'short',
		year:'numeric'
	}).format(date);
}

export function formatArchiveFileSize(bytes:number){
	if(!bytes)return'-';

	if(bytes<1024){
		return`${bytes} B`;
	}

	if(bytes<1024*1024){
		return`${(bytes/1024).toFixed(1)} KB`;
	}

	return`${(bytes/(1024*1024)).toFixed(1)} MB`;
}