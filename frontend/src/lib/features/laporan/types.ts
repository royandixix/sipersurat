export type ReportSource='INCOMING'|'OUTGOING'|'DISPOSITION'|'ARCHIVE';

export interface ReportRecord{
	id:string;
	source:ReportSource;
	date:string;
	agendaNumber:string;
	letterNumber:string;
	correspondent:string;
	subject:string;
	category:string;
	unit:string;
	status:string;
}

export interface MonthlyReport{
	month:number;
	label:string;
	incoming:number;
	outgoing:number;
	disposition:number;
	archive:number;
	total:number;
}

export const REPORT_SOURCE_LABELS:Record<ReportSource,string>={
	INCOMING:'Surat Masuk',
	OUTGOING:'Surat Keluar',
	DISPOSITION:'Disposisi',
	ARCHIVE:'Arsip'
};

export function formatReportDate(value:string){
	if(!value)return'-';

	const normalized=value.length>=10
		?value.slice(0,10)
		:value;

	const date=new Date(`${normalized}T00:00:00`);

	if(Number.isNaN(date.getTime()))return value;

	return new Intl.DateTimeFormat('id-ID',{
		day:'2-digit',
		month:'short',
		year:'numeric'
	}).format(date);
}