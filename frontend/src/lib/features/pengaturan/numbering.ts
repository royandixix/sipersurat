import type{NumberingConfig}from'$lib/features/pengaturan/types';

export function generateNextNumber(
	existing:string[],
	prefix:string,
	numbering:NumberingConfig,
	startNumber:number
):string{
	const normalizedPrefix=prefix.trim()||'DOC';
	const separator=numbering.separator;
	const leadingPrefix=numbering.resetYearly
		?`${normalizedPrefix}${separator}${numbering.activeYear}${separator}`
		:`${normalizedPrefix}${separator}`;
	const max=existing.reduce((highest,value)=>{
		if(!value.startsWith(leadingPrefix))return highest;
		const sequence=Number(value.slice(leadingPrefix.length));
		if(!Number.isFinite(sequence))return highest;
		return Math.max(highest,sequence);
	},0);
	const next=Math.max(max+1,Math.max(1,startNumber));
	return`${leadingPrefix}${String(next).padStart(Math.max(1,numbering.padding),'0')}`;
}
