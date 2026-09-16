import type{OutgoingMailPayload,OutgoingMailRecord,OutgoingMailStatus}from'$lib/features/surat-keluar/types';

const API_URL='http://localhost:8081/api/v1/outgoing-mails';

type ApiResponse<T>={
	success:boolean;
	message:string;
	data:T;
};

type OutgoingMailApiRecord=Omit<OutgoingMailRecord,'notes'|'sentDate'|'fileName'|'fileType'|'fileSize'>&{
	notes:string|null;
	sentDate:string|null;
	fileName:string|null;
	fileType:string|null;
	fileSize:number|null;
};

function normalizeRecord(record:OutgoingMailApiRecord):OutgoingMailRecord{
	return{
		...record,
		notes:record.notes??'',
		sentDate:record.sentDate??'',
		fileName:record.fileName??'',
		fileType:record.fileType??'',
		fileSize:record.fileSize??0
	};
}

function preparePayload(payload:OutgoingMailPayload){
	return{
		...payload,
		sentDate:payload.sentDate||null,
		fileName:payload.fileName||null,
		fileType:payload.fileType||null,
		fileSize:payload.fileSize??0
	};
}

async function request<T>(url:string,options?:RequestInit):Promise<T>{
	const response=await fetch(url,{
		...options,
		headers:{
			'Content-Type':'application/json',
			...options?.headers
		}
	});
	let body:ApiResponse<T>|null=null;
	try{
		body=await response.json();
	}catch{
		body=null;
	}
	if(!response.ok||!body?.success){
		throw new Error(body?.message||`Permintaan gagal dengan status ${response.status}`);
	}
	return body.data;
}

export async function getOutgoingMails():Promise<OutgoingMailRecord[]>{
	const data=await request<OutgoingMailApiRecord[]>(API_URL);
	return data.map(normalizeRecord);
}

export async function getOutgoingMail(id:number):Promise<OutgoingMailRecord>{
	const data=await request<OutgoingMailApiRecord>(`${API_URL}/${id}`);
	return normalizeRecord(data);
}

export async function createOutgoingMail(payload:OutgoingMailPayload):Promise<OutgoingMailRecord>{
	const data=await request<OutgoingMailApiRecord>(API_URL,{
		method:'POST',
		body:JSON.stringify(preparePayload(payload))
	});
	return normalizeRecord(data);
}

export async function updateOutgoingMail(id:number,payload:OutgoingMailPayload):Promise<OutgoingMailRecord>{
	const data=await request<OutgoingMailApiRecord>(`${API_URL}/${id}`,{
		method:'PUT', 
		body:JSON.stringify(preparePayload(payload))
	});
	return normalizeRecord(data);
}

export async function updateOutgoingMailStatus(id:number,status:OutgoingMailStatus):Promise<OutgoingMailRecord>{
	const data=await request<OutgoingMailApiRecord>(`${API_URL}/${id}/status`,{
		method:'PATCH',
		body:JSON.stringify({status})
	});
	return normalizeRecord(data);
}

export async function deleteOutgoingMail(id:number):Promise<void>{
	await request<null>(`${API_URL}/${id}`,{
		method:'DELETE'
	});
}