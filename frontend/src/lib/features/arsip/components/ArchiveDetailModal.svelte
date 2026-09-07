<script lang="ts">
	import{Button}from'$lib/components/ui/button/index.js';
	import X from'@lucide/svelte/icons/x';
	import Archive from'@lucide/svelte/icons/archive';
	import FileText from'@lucide/svelte/icons/file-text';
	import CalendarDays from'@lucide/svelte/icons/calendar-days';
	import Building2 from'@lucide/svelte/icons/building-2';
	import Inbox from'@lucide/svelte/icons/inbox';
	import Send from'@lucide/svelte/icons/send';
	import RotateCcw from'@lucide/svelte/icons/rotate-ccw';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import type{ArchiveRecord}from'$lib/features/arsip/types';
	import{
		ARCHIVE_SOURCE_LABELS,
		formatArchiveDate,
		formatArchiveFileSize
	}from'$lib/features/arsip/types';

	type Props={
		open:boolean;
		record:ArchiveRecord|null;
		onClose:()=>void;
		onRestore:(record:ArchiveRecord)=>void;
		onDelete:(record:ArchiveRecord)=>void;
	};

	let{
		open,
		record,
		onClose,
		onRestore,
		onDelete
	}:Props=$props();

	let dialogElement=$state<HTMLDialogElement|null>(null);

	$effect(()=>{
		if(!dialogElement)return;

		if(open&&record&&!dialogElement.open){
			dialogElement.showModal();

			requestAnimationFrame(()=>{
				const body=dialogElement?.querySelector<HTMLElement>('.archive-detail-body');

				if(body){
					body.scrollTop=0;
				}
			});
		}

		if((!open||!record)&&dialogElement.open){
			dialogElement.close();
		}
	});

	function close(){
		onClose();
	}

	function handleCancel(event:Event){
		event.preventDefault();
		close();
	}

	function handleClose(){
		if(open){
			onClose();
		}
	}

	function handleBackdrop(event:MouseEvent){
		if(event.target===dialogElement){
			close();
		}
	}
</script>

<dialog
	bind:this={dialogElement}
	class="m-auto border-0 bg-transparent p-0 text-foreground outline-none backdrop:bg-black/35 backdrop:backdrop-blur-md"
	style="position:fixed;inset:0;margin:auto;width:min(680px,calc(100vw - 24px));height:min(680px,calc(100dvh - 24px));max-width:none;max-height:none;"
	aria-label="Detail Arsip"
	oncancel={handleCancel}
	onclose={handleClose}
	onclick={handleBackdrop}
>
	{#if record}
		<div class="flex h-full min-h-0 flex-col overflow-hidden rounded-2xl border bg-background shadow-2xl">
			<header class="flex shrink-0 items-start justify-between gap-4 border-b px-5 py-4 sm:px-6">
				<div class="flex min-w-0 items-start gap-3">
					<div class="flex size-10 shrink-0 items-center justify-center rounded-xl bg-foreground text-background">
						<Archive class="size-5"/>
					</div>

					<div class="min-w-0">
						<div class="flex flex-wrap items-center gap-2">
							<p class="font-mono text-[10px] font-semibold text-blue-600">
								{record.agendaNumber}
							</p>

							<span
								class={[
									'inline-flex items-center gap-1.5 rounded-md px-2 py-1 text-[10px] font-semibold',
									record.source==='INCOMING'
										?'bg-blue-50 text-blue-700'
										:'bg-violet-50 text-violet-700'
								]}
							>
								{#if record.source==='INCOMING'}
									<Inbox class="size-3"/>
								{:else}
									<Send class="size-3"/>
								{/if}

								{ARCHIVE_SOURCE_LABELS[record.source]}
							</span>
						</div>

						<h2 class="mt-1.5 text-base font-bold">
							Detail Arsip
						</h2>

						<p class="mt-1 text-[11px] leading-5 text-muted-foreground">
							Informasi lengkap dokumen yang telah masuk ke arsip digital SiPersurat.
						</p>
					</div>
				</div>

				<button
					type="button"
					class="flex size-9 shrink-0 items-center justify-center rounded-lg text-muted-foreground transition hover:bg-muted hover:text-foreground"
					aria-label="Tutup"
					onclick={close}
				>
					<X class="size-4"/>
				</button>
			</header>

			<div class="archive-detail-body min-h-0 flex-1 overflow-y-auto overscroll-contain px-5 py-5 sm:px-6">
				<section>
					<h3 class="text-sm font-semibold">
						Informasi Dokumen
					</h3>

					<div class="mt-4 rounded-xl border bg-muted/30 p-4">
						<p class="text-[10px] font-medium text-muted-foreground">
							Perihal
						</p>

						<p class="mt-2 text-sm font-semibold leading-6">
							{record.subject}
						</p>

						<div class="mt-5 grid gap-4 sm:grid-cols-2">
							<div>
								<p class="text-[10px] text-muted-foreground">
									Nomor Agenda
								</p>

								<p class="mt-1 font-mono text-xs font-semibold">
									{record.agendaNumber}
								</p>
							</div>

							<div>
								<p class="text-[10px] text-muted-foreground">
									Nomor Surat
								</p>

								<p class="mt-1 text-xs font-semibold">
									{record.letterNumber}
								</p>
							</div>

							<div>
								<p class="text-[10px] text-muted-foreground">
									Tanggal Surat
								</p>

								<p class="mt-1 text-xs font-semibold">
									{formatArchiveDate(record.letterDate)}
								</p>
							</div>

							<div>
								<p class="text-[10px] text-muted-foreground">
									Tanggal Arsip
								</p>

								<p class="mt-1 text-xs font-semibold">
									{formatArchiveDate(record.archiveDate)}
								</p>
							</div>
						</div>
					</div>
				</section>

				<section class="mt-6 border-t pt-6">
					<h3 class="text-sm font-semibold">
						Klasifikasi
					</h3>

					<div class="mt-4 grid gap-3 sm:grid-cols-2">
						<div class="rounded-xl border p-4">
							<p class="text-[10px] text-muted-foreground">
								{record.source==='INCOMING'
									?'Pengirim'
									:'Penerima'}
							</p>

							<p class="mt-2 text-sm font-semibold leading-5">
								{record.correspondent}
							</p>
						</div>

						<div class="rounded-xl border p-4">
							<div class="flex items-center gap-2 text-muted-foreground">
								<Building2 class="size-4"/>

								<p class="text-[10px]">
									Unit Kerja
								</p>
							</div>

							<p class="mt-2 text-sm font-semibold">
								{record.unit}
							</p>
						</div>

						<div class="rounded-xl border p-4">
							<p class="text-[10px] text-muted-foreground">
								Kategori
							</p>

							<p class="mt-2 text-sm font-semibold">
								{record.category}
							</p>
						</div>

						<div class="rounded-xl border p-4">
							<p class="text-[10px] text-muted-foreground">
								Sifat Surat
							</p>

							<p class="mt-2 text-sm font-semibold">
								{record.priority}
							</p>
						</div>
					</div>
				</section>

				<section class="mt-6 border-t pt-6">
					<div class="flex items-center gap-2">
						<CalendarDays class="size-4 text-muted-foreground"/>

						<h3 class="text-sm font-semibold">
							Informasi Pengarsipan
						</h3>
					</div>

					<div class="mt-4 rounded-xl border p-4">
						<div class="grid gap-4 sm:grid-cols-2">
							<div>
								<p class="text-[10px] text-muted-foreground">
									Jenis Dokumen
								</p>

								<p class="mt-1 text-xs font-semibold">
									{ARCHIVE_SOURCE_LABELS[record.source]}
								</p>
							</div>

							<div>
								<p class="text-[10px] text-muted-foreground">
									Diarsipkan
								</p>

								<p class="mt-1 text-xs font-semibold">
									{formatArchiveDate(record.archiveDate)}
								</p>
							</div>
						</div>
					</div>
				</section>

				<section class="mt-6 border-t pt-6">
					<h3 class="text-sm font-semibold">
						Dokumen & Catatan
					</h3>

					<div class="mt-4 rounded-xl border p-4">
						<div class="flex items-start gap-3">
							<div class="flex size-10 shrink-0 items-center justify-center rounded-lg bg-muted text-muted-foreground">
								<FileText class="size-4"/>
							</div>

							<div class="min-w-0 flex-1">
								<p class="truncate text-xs font-semibold">
									{record.fileName||'Tidak ada lampiran'}
								</p>

								<p class="mt-1 text-[10px] text-muted-foreground">
									{record.fileName
										?formatArchiveFileSize(record.fileSize)
										:'Dokumen tidak memiliki lampiran.'}
								</p>
							</div>
						</div>
					</div>

					<div class="mt-3 rounded-xl border p-4">
						<p class="text-[10px] text-muted-foreground">
							Catatan
						</p>

						<p class="mt-2 text-xs leading-5">
							{record.notes||'Tidak ada catatan tambahan.'}
						</p>
					</div>
				</section>
			</div>

			<footer class="flex shrink-0 flex-col gap-2 border-t bg-background px-5 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6">
				<Button
					type="button"
					variant="outline"
					class="rounded-lg"
					onclick={()=>onRestore(record)}
				>
					<RotateCcw class="size-4"/>
					Pulihkan dari Arsip
				</Button>

				<Button
					type="button"
					variant="destructive"
					class="rounded-lg"
					onclick={()=>onDelete(record)}
				>
					<Trash2 class="size-4"/>
					Hapus Permanen
				</Button>
			</footer>
		</div>
	{/if}
</dialog>