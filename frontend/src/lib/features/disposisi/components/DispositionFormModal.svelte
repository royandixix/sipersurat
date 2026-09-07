<script lang="ts">
	import{Button}from'$lib/components/ui/button/index.js';
	import{Input}from'$lib/components/ui/input/index.js';
	import X from'@lucide/svelte/icons/x';
	import Forward from'@lucide/svelte/icons/forward';
	import Save from'@lucide/svelte/icons/save';
	import FileText from'@lucide/svelte/icons/file-text';
	import CircleAlert from'@lucide/svelte/icons/circle-alert';
	import Building2 from'@lucide/svelte/icons/building-2';
	import User from'@lucide/svelte/icons/user';
	import type{
		DispositionMailOption,
		DispositionOption,
		DispositionPayload,
		DispositionPriority,
		DispositionRecord,
		DispositionStatus,
		DispositionTargetType
	}from'$lib/features/disposisi/types';
	import{
		DISPOSITION_PRIORITY_OPTIONS,
		DISPOSITION_STATUS_OPTIONS
	}from'$lib/features/disposisi/types';

	type Props={
		open:boolean;
		mode:'create'|'edit';
		record?:DispositionRecord|null;
		code:string;
		mails:DispositionMailOption[];
		users:DispositionOption[];
		units:DispositionOption[];
		onSave:(payload:DispositionPayload)=>void;
		onOpenChange:(value:boolean)=>void;
	};

	let{
		open,
		mode,
		record=null,
		code,
		mails,
		users,
		units,
		onSave,
		onOpenChange
	}:Props=$props();

	let dialogElement=$state<HTMLDialogElement|null>(null);
	let lastSignature='';
	let errors=$state<Record<string,string>>({});

	let form=$state<DispositionPayload>({
		incomingMailId:0,
		targetType:'UNIT',
		targetId:'',
		targetName:'',
		instruction:'',
		priority:'NORMAL',
		dueDate:'',
		notes:'',
		status:'WAITING'
	});

	const selectedMail=$derived(
		mails.find((item)=>item.id===form.incomingMailId)??null
	);

	const targetOptions=$derived(
		form.targetType==='USER'?users:units
	);

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

		const signature=`${mode}-${record?.id??'new'}`;

		if(signature===lastSignature)return;

		if(mode==='edit'&&record){
			form={
				incomingMailId:record.incomingMailId,
				targetType:record.targetType,
				targetId:record.targetId,
				targetName:record.targetName,
				instruction:record.instruction,
				priority:record.priority,
				dueDate:record.dueDate,
				notes:record.notes,
				status:record.status
			};
		}else{
			const due=new Date();
			due.setDate(due.getDate()+3);

			form={
				incomingMailId:mails[0]?.id??0,
				targetType:'UNIT',
				targetId:units[0]?.value??'',
				targetName:units[0]?.label??'',
				instruction:'',
				priority:'NORMAL',
				dueDate:due.toISOString().slice(0,10),
				notes:'',
				status:'WAITING'
			};
		}

		errors={};
		lastSignature=signature;

		requestAnimationFrame(()=>{
			const body=dialogElement?.querySelector<HTMLElement>('.dialog-body');
			if(body)body.scrollTop=0;
		});
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

	function mailChange(event:Event){
		form.incomingMailId=Number((event.currentTarget as HTMLSelectElement).value);
	}

	function targetTypeChange(event:Event){
		form.targetType=(event.currentTarget as HTMLSelectElement).value as DispositionTargetType;
		const options=form.targetType==='USER'?users:units;
		form.targetId=options[0]?.value??'';
		form.targetName=options[0]?.label??'';
	}

	function targetChange(event:Event){
		const value=(event.currentTarget as HTMLSelectElement).value;
		const options=form.targetType==='USER'?users:units;
		const option=options.find((item)=>item.value===value);
		form.targetId=value;
		form.targetName=option?.label??'';
	}

	function priorityChange(event:Event){
		form.priority=(event.currentTarget as HTMLSelectElement).value as DispositionPriority;
	}

	function statusChange(event:Event){
		form.status=(event.currentTarget as HTMLSelectElement).value as DispositionStatus;
	}

	function validate(){
		const next:Record<string,string>={};
		if(!form.incomingMailId)next.incomingMailId='Surat masuk wajib dipilih.';
		if(!form.targetId)next.targetId='Tujuan disposisi wajib dipilih.';
		if(!form.instruction.trim())next.instruction='Instruksi disposisi wajib diisi.';
		if(!form.dueDate)next.dueDate='Batas waktu wajib diisi.';
		errors=next;
		return Object.keys(next).length===0;
	}

	function submit(){
		if(!validate())return;

		onSave({
			incomingMailId:form.incomingMailId,
			targetType:form.targetType,
			targetId:form.targetId,
			targetName:form.targetName,
			instruction:form.instruction.trim(),
			priority:form.priority,
			dueDate:form.dueDate,
			notes:form.notes.trim(),
			status:form.status
		});
	}
</script>

<dialog
	bind:this={dialogElement}
	class="disposition-dialog"
	aria-labelledby="disposition-title"
	oncancel={handleCancel}
	onclose={handleClose}
	onclick={handleBackdrop}
>
	<div class="dialog-shell">
		<header class="dialog-header">
			<div class="flex min-w-0 items-center gap-3">
				<div class="flex size-10 shrink-0 items-center justify-center rounded-xl bg-foreground text-background">
					<Forward class="size-5"/>
				</div>

				<div class="min-w-0">
					<h2
						id="disposition-title"
						class="text-[17px] font-bold tracking-tight"
					>
						{mode==='create'?'Buat Disposisi':'Edit Disposisi'}
					</h2>

					<p class="mt-0.5 text-[11px] font-medium leading-5 text-muted-foreground sm:text-[12px]">
						{mode==='create'
							?'Teruskan surat masuk kepada pengguna atau unit kerja untuk mendapatkan arahan dan tindak lanjut.'
							:'Perbarui data disposisi, tujuan penerima, instruksi, dan proses tindak lanjut.'}
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
						<h3>Surat yang Didisposisikan</h3>
						<p>Pilih surat masuk yang membutuhkan arahan atau tindak lanjut.</p>
					</div>

					<span>
						{mode==='create'?code:record?.code}
					</span>
				</div>

				<div class="field mt-4">
					<label for="disposition-mail">
						Surat Masuk
						<span>*</span>
					</label>

					<select
						id="disposition-mail"
						value={form.incomingMailId}
						onchange={mailChange}
						disabled={mode==='edit'}
					>
						<option value="0" disabled>
							Pilih surat masuk
						</option>

						{#each mails as mail}
							<option value={mail.id}>
								{mail.agendaNumber} — {mail.sender}
							</option>
						{/each}
					</select>

					{#if errors.incomingMailId}
						<p class="error">
							<CircleAlert class="size-3"/>
							{errors.incomingMailId}
						</p>
					{/if}
				</div>

				{#if selectedMail}
					<div class="mail-card">
						<div class="mail-icon">
							<FileText class="size-4"/>
						</div>

						<div class="min-w-0 flex-1">
							<div class="flex flex-wrap items-center gap-x-2 gap-y-1">
								<strong>
									{selectedMail.agendaNumber}
								</strong>

								<span>
									{selectedMail.letterNumber}
								</span>
							</div>

							<h4>
								{selectedMail.subject}
							</h4>

							<p>
								{selectedMail.sender}
							</p>
						</div>
					</div>
				{/if}
			</section>

			<section class="form-section section-border">
				<div class="section-heading">
					<div>
						<h3>Tujuan Disposisi</h3>
						<p>
							Tentukan penerima surat yang bertanggung jawab melakukan tindak lanjut.
						</p>
					</div>
				</div>

				<div class="grid gap-4 sm:grid-cols-2">
					<div class="field">
						<label for="target-type">
							Jenis Tujuan
						</label>

						<select
							id="target-type"
							value={form.targetType}
							onchange={targetTypeChange}
						>
							<option value="UNIT">
								Unit Kerja
							</option>

							<option value="USER">
								Pengguna
							</option>
						</select>
					</div>

					<div class="field">
						<label for="target-value">
							{form.targetType==='USER'
								?'Pengguna Tujuan'
								:'Unit Kerja Tujuan'}
							<span>*</span>
						</label>

						<div class="select-icon-wrap">
							{#if form.targetType==='USER'}
								<User class="pointer-events-none absolute left-3 top-1/2 z-[1] size-4 -translate-y-1/2 text-muted-foreground"/>
							{:else}
								<Building2 class="pointer-events-none absolute left-3 top-1/2 z-[1] size-4 -translate-y-1/2 text-muted-foreground"/>
							{/if}

							<select
								id="target-value"
								value={form.targetId}
								onchange={targetChange}
							>
								<option value="" disabled>
									Pilih tujuan
								</option>

								{#each targetOptions as option}
									<option value={option.value}>
										{option.label}
									</option>
								{/each}
							</select>
						</div>

						{#if errors.targetId}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.targetId}
							</p>
						{/if}
					</div>
				</div>
			</section>

			<section class="form-section section-border">
				<div class="section-heading">
					<div>
						<h3>Instruksi & Tindak Lanjut</h3>

						<p>
							Berikan arahan yang jelas agar penerima mengetahui tindakan yang perlu dilakukan.
						</p>
					</div>
				</div>

				<div class="field">
					<label for="instruction">
						Instruksi Disposisi
						<span>*</span>
					</label>

					<textarea
						id="instruction"
						bind:value={form.instruction}
						rows="3"
						placeholder="Contoh: Mohon ditelaah dan siapkan bahan tanggapan..."
					></textarea>

					{#if errors.instruction}
						<p class="error">
							<CircleAlert class="size-3"/>
							{errors.instruction}
						</p>
					{/if}
				</div>
			</section>

			<section class="form-section section-border">
				<div class="section-heading">
					<div>
						<h3>Pengaturan Disposisi</h3>

						<p>
							Atur prioritas, batas waktu penyelesaian, dan status proses disposisi.
						</p>
					</div>
				</div>

				<div class="grid gap-4 sm:grid-cols-2">
					<div class="field">
						<label for="priority">
							Prioritas
						</label>

						<select
							id="priority"
							value={form.priority}
							onchange={priorityChange}
						>
							{#each DISPOSITION_PRIORITY_OPTIONS as option}
								<option value={option.value}>
									{option.label}
								</option>
							{/each}
						</select>
					</div>

					<div class="field">
						<label for="due-date">
							Batas Waktu
							<span>*</span>
						</label>

						<Input
							id="due-date"
							type="date"
							bind:value={form.dueDate}
							class="h-11 rounded-lg text-[12px] font-medium"
						/>

						{#if errors.dueDate}
							<p class="error">
								<CircleAlert class="size-3"/>
								{errors.dueDate}
							</p>
						{/if}
					</div>
				</div>

				<div class="field mt-4">
					<label for="disposition-status">
						Status
					</label>

					<select
						id="disposition-status"
						value={form.status}
						onchange={statusChange}
					>
						{#each DISPOSITION_STATUS_OPTIONS as option}
							<option value={option.value}>
								{option.label}
							</option>
						{/each}
					</select>
				</div>
			</section>

			<section class="form-section section-border">
				<div class="section-heading">
					<div>
						<h3>Catatan Tambahan</h3>

						<p>
							Tambahkan informasi pendukung jika diperlukan oleh penerima disposisi.
						</p>
					</div>
				</div>

				<div class="field">
					<label for="disposition-notes">
						Catatan
					</label>

					<textarea
						id="disposition-notes"
						bind:value={form.notes}
						rows="2"
						placeholder="Tambahkan catatan jika diperlukan..."
					></textarea>
				</div>
			</section>
		</div>

		<footer class="dialog-footer">
			<p class="hidden text-[11px] font-medium text-muted-foreground sm:block">
				<span class="text-red-500">*</span>
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
					{mode==='create'
						?'Simpan Disposisi'
						:'Simpan Perubahan'}
				</Button>
			</div>
		</footer>
	</div>
</dialog>

<style>
	.disposition-dialog{
		width:min(680px,calc(100vw - 32px));
		max-width:680px;
		max-height:calc(100dvh - 40px);
		margin:auto;
		padding:0;
		border:0;
		border-radius:18px;
		background:transparent;
		color:var(--foreground);
		overflow:visible;
	}

	.disposition-dialog::backdrop{
		background:rgb(15 23 42/.34);
		backdrop-filter:blur(7px);
		-webkit-backdrop-filter:blur(7px);
	}

	.disposition-dialog[open]{
		animation:modal-in .18s ease-out;
	}

	.dialog-shell{
		display:flex;
		max-height:calc(100dvh - 40px);
		flex-direction:column;
		overflow:hidden;
		border:1px solid var(--border);
		border-radius:18px;
		background:var(--background);
		box-shadow:0 28px 80px rgb(0 0 0/.24);
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
		overscroll-behavior:contain;
		padding:20px;
		scrollbar-gutter:stable;
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

	.form-section{
		padding:0;
	}

	.section-border{
		margin-top:22px;
		padding-top:20px;
		border-top:1px solid var(--border);
	}

	.section-heading{
		display:flex;
		align-items:flex-start;
		justify-content:space-between;
		gap:16px;
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
		flex-shrink:0;
		border-radius:7px;
		background:var(--muted);
		padding:6px 9px;
		font-family:ui-monospace,SFMono-Regular,Menlo,monospace;
		font-size:9px;
		font-weight:700;
		color:var(--muted-foreground);
	}

	.field label{
		display:block;
		margin-bottom:7px;
		font-size:11px;
		font-weight:650;
	}

	.field label span{
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
		padding:0 34px 0 12px;
	}

	.field textarea{
		resize:none;
		padding:11px 12px;
		line-height:1.55;
	}

	.field select:focus,
	.field textarea:focus{
		border-color:var(--ring);
		box-shadow:0 0 0 3px color-mix(in oklab,var(--ring) 15%,transparent);
	}

	.field select:disabled{
		cursor:not-allowed;
		opacity:.62;
	}

	.select-icon-wrap{
		position:relative;
	}

	.select-icon-wrap select{
		padding-left:38px;
	}

	.mail-card{
		display:flex;
		align-items:flex-start;
		gap:11px;
		margin-top:12px;
		border:1px solid var(--border);
		border-radius:11px;
		background:color-mix(in oklab,var(--muted) 34%,transparent);
		padding:12px;
	}

	.mail-icon{
		display:flex;
		width:34px;
		height:34px;
		flex-shrink:0;
		align-items:center;
		justify-content:center;
		border:1px solid var(--border);
		border-radius:8px;
		background:var(--background);
		color:var(--muted-foreground);
	}

	.mail-card strong{
		font-family:ui-monospace,SFMono-Regular,Menlo,monospace;
		font-size:9px;
		font-weight:700;
		color:#2563eb;
	}

	.mail-card span{
		font-size:9px;
		color:var(--muted-foreground);
	}

	.mail-card h4{
		margin-top:5px;
		font-size:12px;
		font-weight:700;
		line-height:1.45;
	}

	.mail-card p{
		margin-top:3px;
		font-size:10px;
		font-weight:500;
		color:var(--muted-foreground);
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
		.disposition-dialog{
			width:calc(100vw - 16px);
			max-height:calc(100dvh - 16px);
		}

		.dialog-shell{
			max-height:calc(100dvh - 16px);
			border-radius:14px;
		}

		.dialog-header{
			padding:14px;
		}

		.dialog-body{
			padding:16px;
		}

		.dialog-footer{
			padding:12px 14px;
		}

		.section-border{
			margin-top:18px;
			padding-top:18px;
		}
	}
</style>