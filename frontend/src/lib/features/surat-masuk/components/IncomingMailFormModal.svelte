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
	import type{
		IncomingMailPayload,
		IncomingMailRecord,
		IncomingMailStatus
	}from'$lib/features/surat-masuk/types';
	import{
		formatFileSize,
		INCOMING_MAIL_STATUS_OPTIONS
	}from'$lib/features/surat-masuk/types';

	type Option={
		value:string;
		label:string;
	};

	type Props={
		open:boolean;
		mode:'create'|'edit';
		record?:IncomingMailRecord|null;
		agendaNumber:string;
		categories:Option[];
		priorities:Option[];
		units:Option[];
		onSave:(payload:IncomingMailPayload)=>void;
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

	let form=$state<IncomingMailPayload>({
		letterNumber:'',
		letterDate:'',
		receivedDate:'',
		sender:'',
		subject:'',
		category:'',
		priority:'',
		targetUnit:'',
		notes:'',
		status:'PENDING_DISPOSITION',
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
				receivedDate:record.receivedDate,
				sender:record.sender,
				subject:record.subject,
				category:record.category,
				priority:record.priority,
				targetUnit:record.targetUnit,
				notes:record.notes,
				status:record.status,
				fileName:record.fileName,
				fileType:record.fileType,
				fileSize:record.fileSize
			};
		}else{
			form={
				letterNumber:'',
				letterDate:today,
				receivedDate:today,
				sender:'',
				subject:'',
				category:categories[0]?.value??'',
				priority:priorities[0]?.value??'',
				targetUnit:units[0]?.value??'',
				notes:'',
				status:'PENDING_DISPOSITION',
				fileName:'',
				fileType:'',
				fileSize:0
			};
		}

		errors={};
		lastSignature=signature;

		requestAnimationFrame(()=>{
			const body=dialogElement?.querySelector<HTMLElement>('.dialog-body');
			if(body)body.scrollTop=0;
		});
	});

	$effect(()=>{
		if(typeof document==='undefined'||!open)return;

		const previousOverflow=document.body.style.overflow;
		document.body.style.overflow='hidden';

		return()=>{
			document.body.style.overflow=previousOverflow;
		};
	});

	function validate(){
		const next:Record<string,string>={};

		if(!form.letterNumber.trim()){
			next.letterNumber='Nomor surat wajib diisi.';
		}

		if(!form.letterDate){
			next.letterDate='Tanggal surat wajib diisi.';
		}

		if(!form.receivedDate){
			next.receivedDate='Tanggal diterima wajib diisi.';
		}

		if(!form.sender.trim()){
			next.sender='Pengirim wajib diisi.';
		}

		if(!form.subject.trim()){
			next.subject='Perihal wajib diisi.';
		}

		if(!form.category){
			next.category='Kategori surat wajib dipilih.';
		}

		if(!form.priority){
			next.priority='Sifat surat wajib dipilih.';
		}

		if(!form.targetUnit){
			next.targetUnit='Unit tujuan wajib dipilih.';
		}

		errors=next;

		return Object.keys(next).length===0;
	}

	function submit(){
		if(!validate())return;

		onSave({
			letterNumber:form.letterNumber.trim(),
			letterDate:form.letterDate,
			receivedDate:form.receivedDate,
			sender:form.sender.trim(),
			subject:form.subject.trim(),
			category:form.category,
			priority:form.priority,
			targetUnit:form.targetUnit,
			notes:form.notes.trim(),
			status:form.status,
			fileName:form.fileName,
			fileType:form.fileType,
			fileSize:form.fileSize
		});
	}

	function close(){
		onOpenChange(false);
	}

	function handleCancel(event:Event){
		event.preventDefault();
		close();
	}

	function handleClose(){
		if(open){
			onOpenChange(false);
		}
	}

	function handleBackdrop(event:MouseEvent){
		if(event.target===dialogElement){
			close();
		}
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

		if(fileInput){
			fileInput.value='';
		}
	}

	function statusChange(event:Event){
		form.status=(
			event.currentTarget as HTMLSelectElement
		).value as IncomingMailStatus;
	}
</script>

<dialog
	bind:this={dialogElement}
	class="incoming-mail-dialog"
	aria-labelledby="incoming-mail-form-title"
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
					<h2 id="incoming-mail-form-title">
						{mode==='create'
							?'Tambah Surat Masuk'
							:'Edit Surat Masuk'}
					</h2>


					<p>
						{mode==='create'
							?'Catat surat yang baru diterima ke SiPersurat.'
							:'Perbarui informasi surat masuk yang dipilih.'}
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
			<section class="form-section">
				<div class="section-heading">
					<div>
						<h3>Informasi Surat</h3>
						<p>
							Data identitas utama surat yang diterima.
						</p>
					</div>

					<span>
						{mode==='create'
							?agendaNumber
							:(record?.agendaNumber??agendaNumber)}
					</span>
				</div>

				<div class="form-grid">
					<div class="field">
						<label for="agenda-number">
							Nomor Agenda
						</label>

						<Input
							id="agenda-number"
							value={mode==='create'
								?agendaNumber
								:(record?.agendaNumber??agendaNumber)}
							disabled
							class="h-11 rounded-lg bg-muted/40 text-[12px] font-semibold"
						/>
					</div>

					<div class="field">
						<label for="letter-number">
							Nomor Surat
							<span>*</span>
						</label>

						<Input
							id="letter-number"
							bind:value={form.letterNumber}
							placeholder="Contoh: 005/UND/VIII/2026"
							class="h-11 rounded-lg text-[12px] font-medium"
						/>

						{#if errors.letterNumber}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.letterNumber}
							</p>
						{/if}
					</div>

					<div class="field">
						<label for="letter-date">
							Tanggal Surat
							<span>*</span>
						</label>

						<Input
							id="letter-date"
							type="date"
							bind:value={form.letterDate}
							class="h-11 rounded-lg text-[12px] font-medium"
						/>

						{#if errors.letterDate}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.letterDate}
							</p>
						{/if}
					</div>

					<div class="field">
						<label for="received-date">
							Tanggal Diterima
							<span>*</span>
						</label>

						<Input
							id="received-date"
							type="date"
							bind:value={form.receivedDate}
							class="h-11 rounded-lg text-[12px] font-medium"
						/>

						{#if errors.receivedDate}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.receivedDate}
							</p>
						{/if}
					</div>

					<div class="field full">
						<label for="sender">
							Pengirim
							<span>*</span>
						</label>

						<Input
							id="sender"
							bind:value={form.sender}
							placeholder="Nama instansi, organisasi, atau pengirim"
							class="h-11 rounded-lg text-[12px] font-medium"
						/>

						{#if errors.sender}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.sender}
							</p>
						{/if}
					</div>

					<div class="field full">
						<label for="subject">
							Perihal
							<span>*</span>
						</label>

						<Input
							id="subject"
							bind:value={form.subject}
							placeholder="Perihal atau ringkasan isi surat"
							class="h-11 rounded-lg text-[12px] font-medium"
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

			<section class="form-section">
				<div class="section-heading">
					<div>
						<h3>Klasifikasi</h3>
						<p>
							Kategori, sifat, unit tujuan, dan status surat.
						</p>
					</div>
				</div>

				<div class="form-grid">
					<div class="field">
						<label for="category">
							Kategori Surat
							<span>*</span>
						</label>

						<select
							id="category"
							bind:value={form.category}
						>
							<option
								value=""
								disabled
							>
								Pilih kategori
							</option>

							{#each categories as option}
								<option value={option.value}>
									{option.label}
								</option>
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
						<label for="priority">
							Sifat Surat
							<span>*</span>
						</label>

						<select
							id="priority"
							bind:value={form.priority}
						>
							<option
								value=""
								disabled
							>
								Pilih sifat
							</option>

							{#each priorities as option}
								<option value={option.value}>
									{option.label}
								</option>
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
						<label for="unit">
							Unit Tujuan
							<span>*</span>
						</label>

						<select
							id="unit"
							bind:value={form.targetUnit}
						>
							<option
								value=""
								disabled
							>
								Pilih unit kerja
							</option>

							{#each units as option}
								<option value={option.value}>
									{option.label}
								</option>
							{/each}
						</select>

						{#if errors.targetUnit}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.targetUnit}
							</p>
						{/if}
					</div>

					<div class="field">
						<label for="status">
							Status
						</label>

						<select
							id="status"
							value={form.status}
							onchange={statusChange}
						>
							{#each INCOMING_MAIL_STATUS_OPTIONS as option}
								<option value={option.value}>
									{option.label}
								</option>
							{/each}
						</select>
					</div>
				</div>
			</section>

			<section class="form-section">
				<div class="section-heading">
					<div>
						<h3>Dokumen & Catatan</h3>
						<p>
							Lampirkan file surat dan informasi tambahan bila diperlukan.
						</p>
					</div>
				</div>

				<div class="field">
					<label for="incoming-mail-file">
						Lampiran
					</label>

					<input
						id="incoming-mail-file"
						bind:this={fileInput}
						class="hidden"
						type="file"
						accept=".pdf,.jpg,.jpeg,.png"
						onchange={handleFile}
					/>

					{#if form.fileName}
						<div class="file-card">
							<div class="file-icon">
								<FileText class="size-5"/>
							</div>

							<div class="file-info">
								<strong>
									{form.fileName}
								</strong>

								<span>
									{formatFileSize(form.fileSize)}
								</span>
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

							<strong>
								Pilih dokumen surat
							</strong>

							<span>
								PDF, JPG, JPEG, atau PNG
							</span>
						</button>
					{/if}
				</div>

				<div class="field mt-4">
					<label for="notes">
						Catatan
					</label>

					<textarea
						id="notes"
						bind:value={form.notes}
						rows="3"
						placeholder="Tambahkan catatan bila diperlukan..."
					></textarea>
				</div>
			</section>
		</div>

		<footer class="dialog-footer">
			<p class="required-text">
				<span>*</span>
				Wajib diisi
			</p>

			<div class="footer-actions">
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

					{mode==='create'
						?'Simpan Surat'
						:'Simpan Perubahan'}
				</Button>
			</div>
		</footer>
	</div>
</dialog>

<style>
	.incoming-mail-dialog{
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

	.incoming-mail-dialog::backdrop{
		background:rgb(15 23 42/.38);
		backdrop-filter:blur(7px);
		-webkit-backdrop-filter:blur(7px);
	}

	.incoming-mail-dialog[open]{
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
		background:var(--background);
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
		line-height:1.5;
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
		transition:background .15s ease,color .15s ease;
	}

	.close-button:hover{
		background:var(--muted);
		color:var(--foreground);
	}

	.dialog-body{
		min-height:0;
		flex:1;
		overflow-x:hidden;
		overflow-y:auto;
		overscroll-behavior:contain;
		padding:20px;
		scrollbar-gutter:stable;
		-webkit-overflow-scrolling:touch;
	}

	.dialog-body::-webkit-scrollbar{
		width:7px;
	}

	.dialog-body::-webkit-scrollbar-track{
		background:transparent;
	}

	.dialog-body::-webkit-scrollbar-thumb{
		border:2px solid transparent;
		border-radius:999px;
		background:color-mix(in oklab,var(--muted-foreground) 40%,transparent);
		background-clip:padding-box;
	}

	.form-section+.form-section{
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
		letter-spacing:-.01em;
	}

	.section-heading p{
		margin-top:3px;
		font-size:10px;
		font-weight:500;
		line-height:1.55;
		color:var(--muted-foreground);
	}

	.section-heading>span{
		display:inline-flex;
		flex-shrink:0;
		align-items:center;
		border-radius:7px;
		background:var(--muted);
		padding:5px 8px;
		font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,monospace;
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
	.required-text span{
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
		color:var(--foreground);
		outline:none;
		transition:border-color .15s ease,box-shadow .15s ease;
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

	.field select:hover,
	.field textarea:hover{
		border-color:color-mix(in oklab,var(--foreground) 25%,var(--input));
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
		transition:border-color .15s ease,background .15s ease,transform .15s ease;
	}

	.upload-box:hover{
		border-color:color-mix(in oklab,var(--foreground) 55%,transparent);
		background:color-mix(in oklab,var(--muted) 65%,transparent);
	}

	.upload-box:active{
		transform:scale(.995);
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
		color:var(--muted-foreground);
	}

	.upload-box strong{
		margin-top:8px;
		font-size:11px;
		font-weight:700;
	}

	.upload-box span{
		margin-top:2px;
		font-size:9px;
		font-weight:500;
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
		color:var(--muted-foreground);
	}

	.file-info{
		min-width:0;
		flex:1;
	}

	.file-info strong{
		display:block;
		overflow:hidden;
		font-size:11px;
		font-weight:700;
		text-overflow:ellipsis;
		white-space:nowrap;
	}

	.file-info span{
		display:block;
		margin-top:2px;
		font-size:9px;
		font-weight:500;
		color:var(--muted-foreground);
	}

	.file-remove{
		display:flex;
		width:32px;
		height:32px;
		flex-shrink:0;
		align-items:center;
		justify-content:center;
		border-radius:8px;
		color:#dc2626;
		transition:background .15s ease;
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

	.required-text{
		font-size:10px;
		font-weight:500;
		color:var(--muted-foreground);
	}

	.footer-actions{
		display:flex;
		gap:8px;
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
		.incoming-mail-dialog{
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

		.header-icon{
			width:36px;
			height:36px;
			border-radius:10px;
		}

		.header-main h2{
			font-size:15px;
		}

		.header-main p{
			font-size:10px;
		}

		.dialog-body{
			padding:16px;
		}

		.form-grid{
			grid-template-columns:1fr;
			gap:13px;
		}

		.field.full{
			grid-column:auto;
		}

		.form-section+.form-section{
			margin-top:19px;
			padding-top:19px;
		}

		.section-heading{
			margin-bottom:12px;
		}

		.dialog-footer{
			padding:12px 14px;
		}

		.required-text{
			display:none;
		}

		.footer-actions{
			width:100%;
		}
	}

	@media(max-width:420px){
		.incoming-mail-dialog{
			width:calc(100vw - 12px);
			max-height:calc(100dvh - 12px);
		}

		.dialog-shell{
			max-height:calc(100dvh - 12px);
		}

		.dialog-header{
			padding:12px;
		}

		.dialog-body{
			padding:14px;
		}

		.dialog-footer{
			padding:10px 12px;
		}

		.section-heading{
			gap:8px;
		}

		.section-heading>span{
			padding:4px 6px;
			font-size:8px;
		}
	}
</style>