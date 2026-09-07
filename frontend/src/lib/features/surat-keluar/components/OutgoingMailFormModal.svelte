<script lang="ts">
	import{Button}from'$lib/components/ui/button/index.js';
	import{Input}from'$lib/components/ui/input/index.js';
	import X from'@lucide/svelte/icons/x';
	import Plus from'@lucide/svelte/icons/plus';
	import Pencil from'@lucide/svelte/icons/pencil';
	import Save from'@lucide/svelte/icons/save';
	import UploadCloud from'@lucide/svelte/icons/upload-cloud';
	import FileText from'@lucide/svelte/icons/file-text';
	import CircleAlert from'@lucide/svelte/icons/circle-alert';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import Send from'@lucide/svelte/icons/send';
	import type{
		OutgoingMailPayload,
		OutgoingMailRecord,
		OutgoingMailStatus
	}from'$lib/features/surat-keluar/types';
	import{
		formatOutgoingFileSize,
		OUTGOING_MAIL_STATUS_OPTIONS
	}from'$lib/features/surat-keluar/types';

	type Option={
		value:string;
		label:string;
	};

	type Props={
		open:boolean;
		mode:'create'|'edit';
		record?:OutgoingMailRecord|null;
		agendaNumber:string;
		categories:Option[];
		priorities:Option[];
		units:Option[];
		onSave:(payload:OutgoingMailPayload)=>void;
		onOpenChange:(value:boolean)=>void;
	};

	let{
		open,
		mode,
		record=null,
		agendaNumber,
		categories,
		priorities,
		units,
		onSave,
		onOpenChange
	}:Props=$props();

	let dialogElement=$state<HTMLDialogElement|null>(null);
	let fileInput=$state<HTMLInputElement|null>(null);
	let lastSignature='';

	let form=$state<OutgoingMailPayload>({
		letterNumber:'',
		letterDate:'',
		recipient:'',
		subject:'',
		category:'',
		priority:'',
		sourceUnit:'',
		signer:'',
		notes:'',
		status:'DRAFT',
		sentDate:'',
		fileName:'',
		fileType:'',
		fileSize:0
	});

	let errors=$state<Record<string,string>>({});

	$effect(()=>{
		if(!dialogElement)return;

		if(open&&!dialogElement.open){
			dialogElement.showModal();

			requestAnimationFrame(()=>{
				const body=dialogElement?.querySelector<HTMLElement>('.dialog-body');
				if(body)body.scrollTop=0;
			});
		}

		if(!open&&dialogElement.open){
			dialogElement.close();
		}
	});

	$effect(()=>{
		if(!open){
			lastSignature='';
			return;
		}

		const signature=`${mode}-${record?.id??'new'}`;

		if(signature===lastSignature)return;

		const today=new Date().toISOString().slice(0,10);

		if(mode==='edit'&&record){
			form={
				letterNumber:record.letterNumber,
				letterDate:record.letterDate,
				recipient:record.recipient,
				subject:record.subject,
				category:record.category,
				priority:record.priority,
				sourceUnit:record.sourceUnit,
				signer:record.signer,
				notes:record.notes,
				status:record.status,
				sentDate:record.sentDate,
				fileName:record.fileName,
				fileType:record.fileType,
				fileSize:record.fileSize
			};
		}else{
			form={
				letterNumber:'',
				letterDate:today,
				recipient:'',
				subject:'',
				category:categories[0]?.value??'',
				priority:priorities[0]?.value??'',
				sourceUnit:units[0]?.value??'',
				signer:'',
				notes:'',
				status:'DRAFT',
				sentDate:'',
				fileName:'',
				fileType:'',
				fileSize:0
			};
		}

		errors={};
		lastSignature=signature;
	});

	function close(){
		onOpenChange(false);
	}

	function handleCancel(event:Event){
		event.preventDefault();
		close();
	}

	function handleClose(){
		if(open)onOpenChange(false);
	}

	function handleBackdrop(event:MouseEvent){
		if(event.target===dialogElement)close();
	}

	function handleFile(event:Event){
		const input=event.currentTarget as HTMLInputElement;
		const file=input.files?.[0];

		if(!file)return;

		form.fileName=file.name;
		form.fileType=file.type||'application/octet-stream';
		form.fileSize=file.size;
	}

	function removeFile(){
		form.fileName='';
		form.fileType='';
		form.fileSize=0;

		if(fileInput)fileInput.value='';
	}

	function statusChange(event:Event){
		form.status=(event.currentTarget as HTMLSelectElement).value as OutgoingMailStatus;

		if(form.status==='SENT'&&!form.sentDate){
			form.sentDate=new Date().toISOString().slice(0,10);
		}
	}

	function validate(){
		const next:Record<string,string>={};

		if(!form.letterNumber.trim())next.letterNumber='Nomor surat wajib diisi.';
		if(!form.letterDate)next.letterDate='Tanggal surat wajib diisi.';
		if(!form.recipient.trim())next.recipient='Tujuan surat wajib diisi.';
		if(!form.subject.trim())next.subject='Perihal wajib diisi.';
		if(!form.category)next.category='Kategori wajib dipilih.';
		if(!form.priority)next.priority='Sifat surat wajib dipilih.';
		if(!form.sourceUnit)next.sourceUnit='Unit pembuat wajib dipilih.';
		if(!form.signer.trim())next.signer='Penandatangan wajib diisi.';
		if(form.status==='SENT'&&!form.sentDate)next.sentDate='Tanggal kirim wajib diisi.';

		errors=next;

		return Object.keys(next).length===0;
	}

	function submit(){
		if(!validate())return;

		onSave({
			letterNumber:form.letterNumber.trim(),
			letterDate:form.letterDate,
			recipient:form.recipient.trim(),
			subject:form.subject.trim(),
			category:form.category,
			priority:form.priority,
			sourceUnit:form.sourceUnit,
			signer:form.signer.trim(),
			notes:form.notes.trim(),
			status:form.status,
			sentDate:form.sentDate,
			fileName:form.fileName,
			fileType:form.fileType,
			fileSize:form.fileSize
		});
	}
</script>

<dialog
	bind:this={dialogElement}
	class="outgoing-dialog"
	aria-labelledby="outgoing-mail-form-title"
	oncancel={handleCancel}
	onclose={handleClose}
	onclick={handleBackdrop}
>
	<div class="dialog-shell">
		<header class="dialog-header">
			<div class="header-main">
				<div class="header-icon">
					{#if mode==='create'}
						<Plus class="size-5"/>
					{:else}
						<Pencil class="size-5"/>
					{/if}
				</div>

				<div class="min-w-0">
					<h2 id="outgoing-mail-form-title">
						{mode==='create'?'Tambah Surat Keluar':'Edit Surat Keluar'}
					</h2>

					<p>
						{mode==='create'
							?'Catat dan kelola surat yang akan diterbitkan atau dikirim.'
							:'Perbarui informasi surat keluar yang dipilih.'}
					</p>
				</div>
			</div>

			<button
				type="button"
				class="close-button"
				aria-label="Tutup modal"
				onclick={close}
			>
				<X class="size-4"/>
			</button>
		</header>

		<div class="dialog-body">
			<section>
				<div class="section-heading">
					<div>
						<h3>Informasi Surat</h3>
						<p>Data identitas dan tujuan surat keluar.</p>
					</div>

					<span>
						{mode==='create'?agendaNumber:(record?.agendaNumber??agendaNumber)}
					</span>
				</div>

				<div class="form-grid">
					<div class="field">
						<label for="outgoing-agenda">Nomor Agenda</label>

						<Input
							id="outgoing-agenda"
							value={mode==='create'?agendaNumber:(record?.agendaNumber??agendaNumber)}
							disabled
							class="h-11 rounded-lg bg-muted/40 text-[12px] font-semibold"
						/>
					</div>

					<div class="field">
						<label for="outgoing-number">
							Nomor Surat
							<span>*</span>
						</label>

						<Input
							id="outgoing-number"
							bind:value={form.letterNumber}
							placeholder="Contoh: 014/SK/VIII/2026"
							class="h-11 rounded-lg text-[12px]"
						/>

						{#if errors.letterNumber}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.letterNumber}
							</p>
						{/if}
					</div>

					<div class="field">
						<label for="outgoing-date">
							Tanggal Surat
							<span>*</span>
						</label>

						<Input
							id="outgoing-date"
							type="date"
							bind:value={form.letterDate}
							class="h-11 rounded-lg text-[12px]"
						/>

						{#if errors.letterDate}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.letterDate}
							</p>
						{/if}
					</div>

					<div class="field">
						<label for="outgoing-recipient">
							Tujuan Surat
							<span>*</span>
						</label>

						<Input
							id="outgoing-recipient"
							bind:value={form.recipient}
							placeholder="Instansi atau penerima"
							class="h-11 rounded-lg text-[12px]"
						/>

						{#if errors.recipient}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.recipient}
							</p>
						{/if}
					</div>

					<div class="field full">
						<label for="outgoing-subject">
							Perihal
							<span>*</span>
						</label>

						<Input
							id="outgoing-subject"
							bind:value={form.subject}
							placeholder="Perihal atau ringkasan surat"
							class="h-11 rounded-lg text-[12px]"
						/>

						{#if errors.subject}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.subject}
							</p>
						{/if}
					</div>
				</div>
			</section>

			<section class="section-border">
				<div class="section-heading">
					<div>
						<h3>Klasifikasi & Penanggung Jawab</h3>
						<p>Kategori, sifat surat, unit pembuat, dan penandatangan.</p>
					</div>
				</div>

				<div class="form-grid">
					<div class="field">
						<label for="outgoing-category">
							Kategori
							<span>*</span>
						</label>

						<select id="outgoing-category" bind:value={form.category}>
							<option value="" disabled>Pilih kategori</option>

							{#each categories as option}
								<option value={option.value}>{option.label}</option>
							{/each}
						</select>

						{#if errors.category}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.category}
							</p>
						{/if}
					</div>

					<div class="field">
						<label for="outgoing-priority">
							Sifat Surat
							<span>*</span>
						</label>

						<select id="outgoing-priority" bind:value={form.priority}>
							<option value="" disabled>Pilih sifat surat</option>

							{#each priorities as option}
								<option value={option.value}>{option.label}</option>
							{/each}
						</select>

						{#if errors.priority}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.priority}
							</p>
						{/if}
					</div>

					<div class="field">
						<label for="outgoing-unit">
							Unit Pembuat
							<span>*</span>
						</label>

						<select id="outgoing-unit" bind:value={form.sourceUnit}>
							<option value="" disabled>Pilih unit kerja</option>

							{#each units as option}
								<option value={option.value}>{option.label}</option>
							{/each}
						</select>

						{#if errors.sourceUnit}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.sourceUnit}
							</p>
						{/if}
					</div>

					<div class="field">
						<label for="outgoing-signer">
							Penandatangan
							<span>*</span>
						</label>

						<Input
							id="outgoing-signer"
							bind:value={form.signer}
							placeholder="Nama pejabat penandatangan"
							class="h-11 rounded-lg text-[12px]"
						/>

						{#if errors.signer}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.signer}
							</p>
						{/if}
					</div>
				</div>
			</section>

			<section class="section-border">
				<div class="section-heading">
					<div>
						<h3>Status Pengiriman</h3>
						<p>Atur status proses dan tanggal pengiriman surat.</p>
					</div>

					<Send class="size-4 text-muted-foreground"/>
				</div>

				<div class="form-grid">
					<div class="field">
						<label for="outgoing-status">Status</label>

						<select
							id="outgoing-status"
							value={form.status}
							onchange={statusChange}
						>
							{#each OUTGOING_MAIL_STATUS_OPTIONS as option}
								<option value={option.value}>{option.label}</option>
							{/each}
						</select>
					</div>

					<div class="field">
						<label for="outgoing-sent-date">
							Tanggal Dikirim
							{#if form.status==='SENT'}
								<span>*</span>
							{/if}
						</label>

						<Input
							id="outgoing-sent-date"
							type="date"
							bind:value={form.sentDate}
							disabled={form.status!=='SENT'&&form.status!=='ARCHIVED'}
							class="h-11 rounded-lg text-[12px]"
						/>

						{#if errors.sentDate}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.sentDate}
							</p>
						{/if}
					</div>
				</div>
			</section>

			<section class="section-border">
				<div class="section-heading">
					<div>
						<h3>Dokumen & Catatan</h3>
						<p>Lampirkan dokumen final dan catatan tambahan bila diperlukan.</p>
					</div>
				</div>

				<div class="field">
					<label for="outgoing-file">Lampiran</label>

					<input
						id="outgoing-file"
						bind:this={fileInput}
						class="hidden"
						type="file"
						accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
						onchange={handleFile}
					/>

					{#if form.fileName}
						<div class="file-card">
							<div class="file-icon">
								<FileText class="size-5"/>
							</div>

							<div class="min-w-0 flex-1">
								<strong>{form.fileName}</strong>
								<span>{formatOutgoingFileSize(form.fileSize)}</span>
							</div>

							<button
								type="button"
								class="file-remove"
								aria-label="Hapus lampiran"
								onclick={removeFile}
							>
								<Trash2 class="size-4"/>
							</button>
						</div>
					{:else}
						<button
							type="button"
							class="upload-box"
							onclick={()=>fileInput?.click()}
						>
							<div>
								<UploadCloud class="size-5"/>
							</div>

							<strong>Pilih dokumen surat</strong>
							<span>PDF, DOC, DOCX, JPG, JPEG, atau PNG</span>
						</button>
					{/if}
				</div>

				<div class="field mt-4">
					<label for="outgoing-notes">Catatan</label>

					<textarea
						id="outgoing-notes"
						bind:value={form.notes}
						rows="3"
						placeholder="Tambahkan catatan bila diperlukan..."
					></textarea>
				</div>
			</section>
		</div>

		<footer class="dialog-footer">
			<p>
				<span>*</span>
				Wajib diisi
			</p>

			<div class="flex w-full gap-2 sm:w-auto">
				<Button
					type="button"
					variant="outline"
					class="h-10 flex-1 rounded-lg px-5 text-[12px] font-semibold sm:flex-none"
					onclick={close}
				>
					Batal
				</Button>

				<Button
					type="button"
					class="h-10 flex-1 rounded-lg px-5 text-[12px] font-semibold sm:flex-none"
					onclick={submit}
				>
					<Save class="size-4"/>
					{mode==='create'?'Simpan Surat':'Simpan Perubahan'}
				</Button>
			</div>
		</footer>
	</div>
</dialog>

<style>
	.outgoing-dialog{
		width:min(760px,calc(100vw - 32px));
		max-width:760px;
		max-height:calc(100dvh - 32px);
		margin:auto;
		padding:0;
		border:0;
		border-radius:18px;
		background:transparent;
		color:var(--foreground);
		overflow:visible;
		outline:none;
	}

	.outgoing-dialog::backdrop{
		background:rgb(15 23 42/.38);
		backdrop-filter:blur(7px);
		-webkit-backdrop-filter:blur(7px);
	}

	.outgoing-dialog[open]{
		animation:modal-in .18s ease-out;
	}

	.dialog-shell{
		display:flex;
		width:100%;
		max-height:calc(100dvh - 32px);
		flex-direction:column;
		overflow:hidden;
		border:1px solid var(--border);
		border-radius:18px;
		background:var(--background);
		box-shadow:0 28px 80px rgb(0 0 0/.24);
	}

	.dialog-header{
		display:flex;
		flex-shrink:0;
		align-items:center;
		justify-content:space-between;
		gap:16px;
		padding:16px 18px;
		border-bottom:1px solid var(--border);
	}

	.header-main{
		display:flex;
		min-width:0;
		align-items:center;
		gap:12px;
	}

	.header-icon{
		display:flex;
		width:40px;
		height:40px;
		flex-shrink:0;
		align-items:center;
		justify-content:center;
		border-radius:12px;
		background:var(--foreground);
		color:var(--background);
	}

	.header-main h2{
		font-size:17px;
		font-weight:700;
		letter-spacing:-.02em;
	}

	.header-main p{
		margin-top:2px;
		font-size:11px;
		font-weight:500;
		color:var(--muted-foreground);
	}

	.close-button{
		display:flex;
		width:34px;
		height:34px;
		flex-shrink:0;
		align-items:center;
		justify-content:center;
		border-radius:9px;
		color:var(--muted-foreground);
		transition:.15s ease;
	}

	.close-button:hover{
		background:var(--muted);
		color:var(--foreground);
	}

	.dialog-body{
		min-height:0;
		flex:1;
		overflow-y:auto;
		overscroll-behavior:contain;
		padding:20px;
		scrollbar-gutter:stable;
	}

	.section-border{
		margin-top:22px;
		padding-top:22px;
		border-top:1px solid var(--border);
	}

	.section-heading{
		display:flex;
		align-items:flex-start;
		justify-content:space-between;
		gap:12px;
		margin-bottom:14px;
	}

	.section-heading h3{
		font-size:13px;
		font-weight:700;
	}

	.section-heading p{
		margin-top:3px;
		font-size:10px;
		font-weight:500;
		line-height:1.55;
		color:var(--muted-foreground);
	}

	.section-heading>span{
		flex-shrink:0;
		border-radius:7px;
		background:var(--muted);
		padding:5px 8px;
		font-family:ui-monospace,SFMono-Regular,Menlo,monospace;
		font-size:9px;
		font-weight:700;
		color:var(--muted-foreground);
	}

	.form-grid{
		display:grid;
		grid-template-columns:repeat(2,minmax(0,1fr));
		gap:14px;
	}

	.field{
		min-width:0;
	}

	.field.full{
		grid-column:1/-1;
	}

	.field label{
		display:block;
		margin-bottom:6px;
		font-size:11px;
		font-weight:650;
	}

	.field label span,
	.dialog-footer p span{
		color:#ef4444;
	}

	.field select,
	.field textarea{
		width:100%;
		border:1px solid var(--input);
		border-radius:9px;
		background:var(--background);
		font-size:12px;
		font-weight:500;
		outline:none;
		transition:.15s ease;
	}

	.field select{
		height:44px;
		padding:0 34px 0 11px;
	}

	.field textarea{
		resize:none;
		padding:10px 11px;
		line-height:1.55;
	}

	.field select:focus,
	.field textarea:focus{
		border-color:var(--ring);
		box-shadow:0 0 0 3px color-mix(in oklab,var(--ring) 15%,transparent);
	}

	.error{
		display:flex;
		align-items:center;
		gap:4px;
		margin-top:5px;
		font-size:9px;
		font-weight:500;
		color:#ef4444;
	}

	.upload-box{
		display:flex;
		width:100%;
		min-height:108px;
		flex-direction:column;
		align-items:center;
		justify-content:center;
		border:1px dashed color-mix(in oklab,var(--foreground) 28%,transparent);
		border-radius:12px;
		background:color-mix(in oklab,var(--muted) 38%,transparent);
		text-align:center;
		transition:.15s ease;
	}

	.upload-box:hover{
		background:color-mix(in oklab,var(--muted) 65%,transparent);
	}

	.upload-box>div{
		display:flex;
		width:36px;
		height:36px;
		align-items:center;
		justify-content:center;
		border:1px solid var(--border);
		border-radius:10px;
		background:var(--background);
	}

	.upload-box strong{
		margin-top:8px;
		font-size:11px;
		font-weight:700;
	}

	.upload-box span{
		margin-top:2px;
		font-size:9px;
		color:var(--muted-foreground);
	}

	.file-card{
		display:flex;
		align-items:center;
		gap:10px;
		border:1px solid var(--border);
		border-radius:11px;
		background:color-mix(in oklab,var(--muted) 24%,transparent);
		padding:10px 11px;
	}

	.file-icon{
		display:flex;
		width:38px;
		height:38px;
		flex-shrink:0;
		align-items:center;
		justify-content:center;
		border-radius:9px;
		background:var(--muted);
	}

	.file-card strong{
		display:block;
		overflow:hidden;
		font-size:11px;
		text-overflow:ellipsis;
		white-space:nowrap;
	}

	.file-card span{
		display:block;
		margin-top:2px;
		font-size:9px;
		color:var(--muted-foreground);
	}

	.file-remove{
		display:flex;
		width:32px;
		height:32px;
		align-items:center;
		justify-content:center;
		border-radius:8px;
		color:#dc2626;
	}

	.file-remove:hover{
		background:#fef2f2;
	}

	.dialog-footer{
		display:flex;
		flex-shrink:0;
		align-items:center;
		justify-content:space-between;
		gap:12px;
		padding:13px 18px;
		border-top:1px solid var(--border);
		background:var(--background);
	}

	.dialog-footer>p{
		font-size:10px;
		font-weight:500;
		color:var(--muted-foreground);
	}

	@keyframes modal-in{
		from{
			opacity:0;
			transform:translateY(8px) scale(.985);
		}
		to{
			opacity:1;
			transform:translateY(0) scale(1);
		}
	}

	@media(max-width:640px){
		.outgoing-dialog{
			width:calc(100vw - 16px);
			max-height:calc(100dvh - 16px);
		}

		.dialog-shell{
			max-height:calc(100dvh - 16px);
			border-radius:15px;
		}

		.dialog-header{
			padding:14px;
		}

		.dialog-body{
			padding:16px;
		}

		.form-grid{
			grid-template-columns:1fr;
		}

		.field.full{
			grid-column:auto;
		}

		.dialog-footer{
			padding:12px 14px;
		}

		.dialog-footer>p{
			display:none;
		}
	}
</style>