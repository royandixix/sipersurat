<script lang="ts">
	import{Button}from'$lib/components/ui/button/index.js';
	import X from'@lucide/svelte/icons/x';
	import Send from'@lucide/svelte/icons/send';
	import FileText from'@lucide/svelte/icons/file-text';
	import CalendarDays from'@lucide/svelte/icons/calendar-days';
	import Building2 from'@lucide/svelte/icons/building-2';
	import UserRoundCheck from'@lucide/svelte/icons/user-round-check';
	import Pencil from'@lucide/svelte/icons/pencil';
	import CircleCheck from'@lucide/svelte/icons/circle-check';
	import Clock3 from'@lucide/svelte/icons/clock-3';
	import Archive from'@lucide/svelte/icons/archive';
	import Undo2 from'@lucide/svelte/icons/undo-2';
	import type{
		OutgoingMailRecord,
		OutgoingMailStatus
	}from'$lib/features/surat-keluar/types';
	import{
		formatOutgoingDate,
		formatOutgoingDateTime,
		formatOutgoingFileSize,
		OUTGOING_MAIL_STATUS_LABELS
	}from'$lib/features/surat-keluar/types';

	type Props={
		open:boolean;
		record:OutgoingMailRecord|null;
		onClose:()=>void;
		onEdit:(record:OutgoingMailRecord)=>void;
		onStatusChange:(record:OutgoingMailRecord,status:OutgoingMailStatus)=>void;
	};

	let{open,record,onClose,onEdit,onStatusChange}:Props=$props();

	let dialogElement=$state<HTMLDialogElement|null>(null);

	$effect(()=>{
		if(!dialogElement)return;
		if(open&&record&&!dialogElement.open)dialogElement.showModal();
		if((!open||!record)&&dialogElement.open)dialogElement.close();
	});

	function close(){
		onClose();
	}

	function handleCancel(event:Event){
		event.preventDefault();
		close();
	}

	function handleClose(){
		if(open)onClose();
	}

	function handleBackdrop(event:MouseEvent){
		if(event.target===dialogElement)close();
	}

	function statusClass(status:OutgoingMailStatus){
		switch(status){
			case'DRAFT':return'bg-zinc-100 text-zinc-700';
			case'PENDING_APPROVAL':return'bg-amber-50 text-amber-700';
			case'APPROVED':return'bg-blue-50 text-blue-700';
			case'SENT':return'bg-emerald-50 text-emerald-700';
			case'REJECTED':return'bg-red-50 text-red-700';
			case'ARCHIVED':return'bg-violet-50 text-violet-700';
		}
	}
</script>

<dialog
	bind:this={dialogElement}
	class="detail-dialog"
	aria-label="Detail Surat Keluar"
	oncancel={handleCancel}
	onclose={handleClose}
	onclick={handleBackdrop}
>
	{#if record}
		<div class="dialog-shell">
			<header class="flex shrink-0 items-start justify-between gap-4 border-b px-5 py-4">
				<div class="flex min-w-0 items-start gap-3">
					<div class="flex size-10 shrink-0 items-center justify-center rounded-xl bg-foreground text-background">
						<Send class="size-5"/>
					</div>

					<div class="min-w-0">
						<div class="flex flex-wrap items-center gap-2">
							<p class="font-mono text-[10px] font-semibold text-blue-600">
								{record.agendaNumber}
							</p>

							<span class={`rounded-md px-2 py-1 text-[10px] font-semibold ${statusClass(record.status)}`}>
								{OUTGOING_MAIL_STATUS_LABELS[record.status]}
							</span>
						</div>

						<h2 class="mt-1.5 text-base font-bold">
							Detail Surat Keluar
						</h2>

						<p class="mt-1 text-[11px] text-muted-foreground">
							Diperbarui {formatOutgoingDateTime(record.updatedAt)}
						</p>
					</div>
				</div>

				<button
					type="button"
					class="flex size-9 shrink-0 items-center justify-center rounded-lg text-muted-foreground hover:bg-muted"
					aria-label="Tutup"
					onclick={close}
				>
					<X class="size-4"/>
				</button>
			</header>

			<div class="min-h-0 flex-1 overflow-y-auto px-5 py-5">
				<section>
					<h3 class="text-sm font-semibold">Informasi Surat</h3>

					<div class="mt-4 rounded-xl border bg-muted/30 p-4">
						<p class="text-[10px] font-medium text-muted-foreground">Perihal</p>

						<p class="mt-2 text-sm font-semibold leading-6">
							{record.subject}
						</p>

						<div class="mt-4 grid gap-3 sm:grid-cols-2">
							<div>
								<p class="text-[10px] text-muted-foreground">Nomor Surat</p>
								<p class="mt-1 text-xs font-semibold">{record.letterNumber}</p>
							</div>

							<div>
								<p class="text-[10px] text-muted-foreground">Tanggal Surat</p>
								<p class="mt-1 text-xs font-semibold">{formatOutgoingDate(record.letterDate)}</p>
							</div>
						</div>
					</div>
				</section>

				<section class="mt-6 border-t pt-6">
					<h3 class="text-sm font-semibold">Tujuan & Penanggung Jawab</h3>

					<div class="mt-4 grid gap-3 sm:grid-cols-2">
						<div class="rounded-xl border p-4">
							<div class="flex items-center gap-2 text-muted-foreground">
								<Building2 class="size-4"/>
								<p class="text-[10px]">Tujuan Surat</p>
							</div>

							<p class="mt-3 text-sm font-semibold">{record.recipient}</p>
						</div>

						<div class="rounded-xl border p-4">
							<div class="flex items-center gap-2 text-muted-foreground">
								<UserRoundCheck class="size-4"/>
								<p class="text-[10px]">Penandatangan</p>
							</div>

							<p class="mt-3 text-sm font-semibold">{record.signer}</p>
						</div>

						<div class="rounded-xl border p-4">
							<p class="text-[10px] text-muted-foreground">Unit Pembuat</p>
							<p class="mt-2 text-sm font-semibold">{record.sourceUnit}</p>
						</div>

						<div class="rounded-xl border p-4">
							<p class="text-[10px] text-muted-foreground">Klasifikasi</p>
							<p class="mt-2 text-sm font-semibold">
								{record.category} · {record.priority}
							</p>
						</div>
					</div>
				</section>

				<section class="mt-6 border-t pt-6">
					<h3 class="text-sm font-semibold">Pengiriman</h3>

					<div class="mt-4 rounded-xl border p-4">
						<div class="flex items-center gap-2 text-muted-foreground">
							<CalendarDays class="size-4"/>
							<p class="text-[10px]">Tanggal Dikirim</p>
						</div>

						<p class="mt-3 text-sm font-semibold">
							{record.sentDate?formatOutgoingDate(record.sentDate):'Belum dikirim'}
						</p>
					</div>
				</section>

				<section class="mt-6 border-t pt-6">
					<h3 class="text-sm font-semibold">Dokumen & Catatan</h3>

					<div class="mt-4 rounded-xl border p-4">
						<div class="flex items-start gap-3">
							<div class="flex size-9 shrink-0 items-center justify-center rounded-lg bg-muted">
								<FileText class="size-4"/>
							</div>

							<div class="min-w-0">
								<p class="text-xs font-semibold">
									{record.fileName||'Tidak ada lampiran'}
								</p>

								<p class="mt-1 text-[10px] text-muted-foreground">
									{record.fileName?formatOutgoingFileSize(record.fileSize):'Dokumen belum dilampirkan'}
								</p>
							</div>
						</div>
					</div>

					<div class="mt-3 rounded-xl border p-4">
						<p class="text-[10px] text-muted-foreground">Catatan</p>

						<p class="mt-2 text-xs leading-5">
							{record.notes||'Tidak ada catatan tambahan.'}
						</p>
					</div>
				</section>
			</div>

			<footer class="flex shrink-0 flex-col gap-2 border-t px-5 py-4 sm:flex-row sm:items-center sm:justify-between">
				<div class="flex flex-wrap gap-2">
					{#if record.status==='DRAFT'}
						<Button
							variant="outline"
							class="rounded-lg"
							onclick={()=>onStatusChange(record,'PENDING_APPROVAL')}
						>
							<Clock3 class="size-4"/>
							Ajukan Persetujuan
						</Button>
					{:else if record.status==='PENDING_APPROVAL'}
						<Button
							variant="outline"
							class="rounded-lg"
							onclick={()=>onStatusChange(record,'APPROVED')}
						>
							<CircleCheck class="size-4"/>
							Setujui
						</Button>
					{:else if record.status==='APPROVED'}
						<Button
							variant="outline"
							class="rounded-lg"
							onclick={()=>onStatusChange(record,'SENT')}
						>
							<Send class="size-4"/>
							Tandai Dikirim
						</Button>
					{:else if record.status==='REJECTED'}
						<Button
							variant="outline"
							class="rounded-lg"
							onclick={()=>onStatusChange(record,'DRAFT')}
						>
							<Undo2 class="size-4"/>
							Kembalikan ke Draft
						</Button>
					{:else if record.status==='SENT'}
						<Button
							variant="outline"
							class="rounded-lg"
							onclick={()=>onStatusChange(record,'ARCHIVED')}
						>
							<Archive class="size-4"/>
							Arsipkan
						</Button>
					{/if}
				</div>

				<Button
					class="rounded-lg"
					onclick={()=>onEdit(record)}
				>
					<Pencil class="size-4"/>
					Edit Surat
				</Button>
			</footer>
		</div>
	{/if}
</dialog>

<style>
	.detail-dialog{
		width:min(700px,calc(100vw - 32px));
		max-width:700px;
		max-height:calc(100dvh - 32px);
		margin:auto;
		padding:0;
		border:0;
		background:transparent;
		color:var(--foreground);
		overflow:visible;
	}

	.detail-dialog::backdrop{
		background:rgb(15 23 42/.38);
		backdrop-filter:blur(7px);
		-webkit-backdrop-filter:blur(7px);
	}

	.dialog-shell{
		display:flex;
		max-height:calc(100dvh - 32px);
		flex-direction:column;
		overflow:hidden;
		border:1px solid var(--border);
		border-radius:18px;
		background:var(--background);
		box-shadow:0 28px 80px rgb(0 0 0/.24);
	}

	@media(max-width:640px){
		.detail-dialog{
			width:calc(100vw - 16px);
			max-height:calc(100dvh - 16px);
		}

		.dialog-shell{
			max-height:calc(100dvh - 16px);
			border-radius:14px;
		}
	}
</style>