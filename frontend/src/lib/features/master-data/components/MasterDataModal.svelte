<script lang="ts">
	import {Button} from '$lib/components/ui/button/index.js';
	import {Input} from '$lib/components/ui/input/index.js';
	import X from '@lucide/svelte/icons/x';
	import Plus from '@lucide/svelte/icons/plus';
	import Pencil from '@lucide/svelte/icons/pencil';
	import Save from '@lucide/svelte/icons/save';
	import CircleAlert from '@lucide/svelte/icons/circle-alert';
	import Check from '@lucide/svelte/icons/check';
	import type{MasterDataPayload,MasterDataRecord,MasterDataStatus,MasterDataType}from '$lib/features/master-data/types';
	import{MASTER_DATA_CONFIG,generateMasterCode}from '$lib/features/master-data/types';

	type Props={
		open:boolean;
		mode:'create'|'edit';
		type:MasterDataType;
		record?:MasterDataRecord|null;
		onSave:(payload:MasterDataPayload)=>void;
		onOpenChange:(value:boolean)=>void;
	};

	let{open,mode,type,record=null,onSave,onOpenChange}:Props=$props();
	let dialogElement=$state<HTMLDialogElement|null>(null);
	let lastSignature='';

	let form=$state<MasterDataPayload>({
		name:'',
		code:'',
		description:'',
		status:'ACTIVE'
	});

	let errors=$state<{
		name?:string;
		code?:string;
	}>({});

	const config=$derived(MASTER_DATA_CONFIG[type]);

	$effect(()=>{
		if(!dialogElement)return;
		if(open&&!dialogElement.open)dialogElement.showModal();
		if(!open&&dialogElement.open)dialogElement.close();
	});

	$effect(()=>{
		if(!open){
			lastSignature='';
			return;
		}
		const signature=`${mode}-${type}-${record?.id??'new'}`;
		if(signature===lastSignature)return;
		if(mode==='edit'&&record){
			form={
				name:record.name,
				code:record.code,
				description:record.description,
				status:record.status
			};
		}else{
			form={
				name:'',
				code:'',
				description:'',
				status:'ACTIVE'
			};
		}
		errors={};
		lastSignature=signature;
	});

	function validate(){
		const next:typeof errors={};
		if(!form.name.trim())next.name=`Nama ${config.singular.toLowerCase()} wajib diisi.`;
		if(!form.code.trim()&&!form.name.trim())next.code='Kode wajib diisi.';
		errors=next;
		return Object.keys(next).length===0;
	}

	function submit(){
		if(!validate())return;
		onSave({
			name:form.name.trim(),
			code:generateMasterCode(form.code.trim()||form.name.trim()),
			description:form.description.trim(),
			status:form.status
		});
	}

	function close(){
		onOpenChange(false);
	}

	function handleCancel(event:Event){
		event.preventDefault();
		close();
	}

	function handleBackdrop(event:MouseEvent){
		if(event.target===dialogElement)close();
	}

	function selectStatus(status:MasterDataStatus){
		form.status=status;
	}

	function createCode(){
		if(!form.name.trim())return;
		form.code=generateMasterCode(form.name);
	}
</script>

<dialog bind:this={dialogElement} class="master-dialog" oncancel={handleCancel} onclick={handleBackdrop}>
	<div class="dialog-shell">
		<header class="dialog-header">
			<div class="flex min-w-0 items-center gap-3">
				<div class="flex size-10 shrink-0 items-center justify-center rounded-xl bg-foreground text-background">
					{#if mode==='create'}
						<Plus class="size-5"/>
					{:else}
						<Pencil class="size-5"/>
					{/if}
				</div>
				<div class="min-w-0">
					<h2 class="text-[17px] font-bold tracking-tight">{mode==='create'?`Tambah ${config.singular}`:`Edit ${config.singular}`}</h2>
					<p class="mt-0.5 text-[12px] font-medium text-muted-foreground">{mode==='create'?`Tambahkan ${config.singular.toLowerCase()} baru ke SiPersurat.`:`Perbarui data ${config.singular.toLowerCase()}.`}</p>
				</div>
			</div>
			<button type="button" class="close-button" onclick={close}>
				<X class="size-4"/>
			</button>
		</header>
		<div class="dialog-body">
			<div class="grid gap-4 sm:grid-cols-2">
				<div class="field">
					<label for={`${type}-name`}>Nama <span>*</span></label>
					<Input id={`${type}-name`} bind:value={form.name} placeholder={`Nama ${config.singular.toLowerCase()}`} class="h-11 rounded-lg text-[13px] font-medium"/>
					{#if errors.name}
						<p class="error"><CircleAlert class="size-3"/>{errors.name}</p>
					{/if}
				</div>
				<div class="field">
					<div class="flex items-center justify-between">
						<label for={`${type}-code`}>Kode <span>*</span></label>
						<button type="button" class="generate-button" onclick={createCode}>Buat otomatis</button>
					</div>
					<Input id={`${type}-code`} bind:value={form.code} placeholder="CONTOH_KODE" class="h-11 rounded-lg font-mono text-[12px] uppercase"/>
					{#if errors.code}
						<p class="error"><CircleAlert class="size-3"/>{errors.code}</p>
					{/if}
				</div>
			</div>
			<div class="field mt-5">
				<label for={`${type}-description`}>Deskripsi</label>
				<textarea id={`${type}-description`} bind:value={form.description} placeholder={`Tambahkan deskripsi ${config.singular.toLowerCase()}...`} rows="3"></textarea>
			</div>
			<div class="field mt-5">
				<label>Status</label>
				<div class="status-grid">
					<button type="button" class:status-selected={form.status==='ACTIVE'} class="status-button" onclick={()=>selectStatus('ACTIVE')}>
						<span class="size-2 rounded-full bg-emerald-500"></span>
						<span>Aktif</span>
						{#if form.status==='ACTIVE'}<Check class="ml-auto size-4"/>{/if}
					</button>
					<button type="button" class:status-selected={form.status==='INACTIVE'} class="status-button" onclick={()=>selectStatus('INACTIVE')}>
						<span class="size-2 rounded-full bg-zinc-400"></span>
						<span>Nonaktif</span>
						{#if form.status==='INACTIVE'}<Check class="ml-auto size-4"/>{/if}
					</button>
				</div>
			</div>
		</div>
		<footer class="dialog-footer">
			<p class="hidden text-[11px] font-medium text-muted-foreground sm:block"><span class="text-red-500">*</span> Wajib diisi</p>
			<div class="flex w-full gap-2 sm:w-auto">
				<Button type="button" variant="outline" class="h-10 flex-1 rounded-lg px-5 text-[12px] font-semibold sm:flex-none" onclick={close}>Batal</Button>
				<Button type="button" class="h-10 flex-1 rounded-lg px-5 text-[12px] font-semibold sm:flex-none" onclick={submit}>
					<Save class="size-4"/>
					{mode==='create'?'Simpan':'Simpan Perubahan'}
				</Button>
			</div>
		</footer>
	</div>
</dialog>

<style>
	.master-dialog{
		width:min(650px,calc(100vw - 32px));
		max-width:650px;
		max-height:calc(100dvh - 40px);
		margin:auto;
		padding:0;
		border:0;
		border-radius:18px;
		background:transparent;
		color:var(--foreground);
		overflow:visible;
	}
	.master-dialog::backdrop{
		background:rgb(15 23 42/.42);
		backdrop-filter:blur(5px);
	}
	.master-dialog[open]{
		animation:modal-in .16s ease-out;
	}
	.dialog-shell{
		display:flex;
		max-height:calc(100dvh - 40px);
		flex-direction:column;
		overflow:hidden;
		border:1px solid var(--border);
		border-radius:18px;
		background:var(--background);
		box-shadow:0 24px 70px rgb(0 0 0/.22);
	}
	.dialog-header{
		display:flex;
		align-items:center;
		justify-content:space-between;
		gap:16px;
		padding:16px 18px;
		border-bottom:1px solid var(--border);
	}
	.dialog-body{
		min-height:0;
		overflow-y:auto;
		padding:20px;
	}
	.dialog-footer{
		display:flex;
		align-items:center;
		justify-content:space-between;
		gap:12px;
		padding:13px 18px;
		border-top:1px solid var(--border);
	}
	.close-button{
		display:flex;
		width:34px;
		height:34px;
		align-items:center;
		justify-content:center;
		border-radius:9px;
		color:var(--muted-foreground);
	}
	.close-button:hover{
		background:var(--muted);
		color:var(--foreground);
	}
	.field label{
		display:block;
		margin-bottom:7px;
		font-size:12px;
		font-weight:650;
	}
	.field label span{
		color:#ef4444;
	}
	.field textarea{
		width:100%;
		resize:none;
		border:1px solid var(--input);
		border-radius:9px;
		background:var(--background);
		padding:11px 12px;
		font-size:13px;
		font-weight:500;
		line-height:1.5;
		outline:none;
	}
	.field textarea:focus{
		border-color:var(--ring);
		box-shadow:0 0 0 3px color-mix(in oklab,var(--ring) 15%,transparent);
	}
	.generate-button{
		margin-bottom:7px;
		font-size:10px;
		font-weight:600;
		color:var(--muted-foreground);
	}
	.generate-button:hover{
		color:var(--foreground);
	}
	.status-grid{
		display:grid;
		grid-template-columns:repeat(2,minmax(0,1fr));
		gap:8px;
	}
	.status-button{
		display:flex;
		height:42px;
		align-items:center;
		gap:8px;
		border:1px solid var(--border);
		border-radius:9px;
		padding:0 12px;
		font-size:12px;
		font-weight:600;
	}
	.status-button:hover{
		background:var(--muted);
	}
	.status-button.status-selected{
		border-color:var(--foreground);
		background:var(--muted);
	}
	.error{
		display:flex;
		align-items:center;
		gap:4px;
		margin-top:5px;
		font-size:10px;
		font-weight:500;
		color:#ef4444;
	}
	@keyframes modal-in{
		from{opacity:0;transform:translateY(8px) scale(.98)}
		to{opacity:1;transform:translateY(0) scale(1)}
	}
	@media(max-width:640px){
		.master-dialog{
			width:calc(100vw - 16px);
			max-height:calc(100dvh - 16px);
		}
		.dialog-shell{
			max-height:calc(100dvh - 16px);
		}
		.dialog-body{
			padding:16px;
		}
		.status-grid{
			grid-template-columns:1fr;
		}
	}
</style>