<script lang="ts">
	import{fly,fade}from'svelte/transition';
	import{Button}from'$lib/components/ui/button/index.js';
	import X from'@lucide/svelte/icons/x';
	import Forward from'@lucide/svelte/icons/forward';
	import FileText from'@lucide/svelte/icons/file-text';
	import User from'@lucide/svelte/icons/user';
	import Building2 from'@lucide/svelte/icons/building-2';
	import CalendarDays from'@lucide/svelte/icons/calendar-days';
	import Pencil from'@lucide/svelte/icons/pencil';
	import CircleCheck from'@lucide/svelte/icons/circle-check';
	import Clock from'@lucide/svelte/icons/clock';
	import History from'@lucide/svelte/icons/history';
	import Play from'@lucide/svelte/icons/play';
	import type{
		DispositionRecord,
		DispositionStatus
	}from'$lib/features/disposisi/types';
	import{
		DISPOSITION_PRIORITY_LABELS,
		DISPOSITION_STATUS_LABELS,
		DISPOSITION_TARGET_LABELS,
		formatDispositionDate,
		formatDispositionDateTime
	}from'$lib/features/disposisi/types';

	type Props={
		open:boolean;
		record:DispositionRecord|null;
		onClose:()=>void;
		onEdit:(record:DispositionRecord)=>void;
		onStatusChange:(record:DispositionRecord,status:DispositionStatus)=>void;
	};

	let{open,record,onClose,onEdit,onStatusChange}:Props=$props();

	function handleKeydown(event:KeyboardEvent){
		if(open&&event.key==='Escape')onClose();
	}


	function statusClass(status:DispositionStatus){
		switch(status){
			case'WAITING':return'bg-amber-50 text-amber-700';
			case'RECEIVED':return'bg-blue-50 text-blue-700';
			case'IN_PROGRESS':return'bg-violet-50 text-violet-700';
			case'COMPLETED':return'bg-emerald-50 text-emerald-700';
		}
	}

	function statusDot(status:DispositionStatus){
		switch(status){
			case'WAITING':return'bg-amber-500';
			case'RECEIVED':return'bg-blue-500';
			case'IN_PROGRESS':return'bg-violet-500';
			case'COMPLETED':return'bg-emerald-500';
		}
	}

	function priorityClass(){
		if(!record)return'';
		if(record.priority==='URGENT')return'bg-red-50 text-red-700';
		if(record.priority==='IMPORTANT')return'bg-amber-50 text-amber-700';
		return'bg-zinc-100 text-zinc-700';
	}
</script>

<svelte:window onkeydown={handleKeydown}/>

{#if open&&record}
	<div
		class="fixed inset-0 z-[260] flex items-center justify-center bg-black/45 p-4 backdrop-blur-sm"
		in:fade={{duration:160}}
	>
		<button
			type="button"
			class="absolute inset-0 cursor-default"
			aria-label="Tutup detail disposisi"
			onclick={onClose}
		></button>

		<div
			class="relative z-10 flex max-h-[calc(100dvh-32px)] w-full max-w-3xl flex-col overflow-hidden rounded-2xl border bg-background shadow-2xl"
			role="dialog"
			aria-modal="true"
			in:fly={{y:20,duration:220}}
		>
			<div class="flex items-start justify-between gap-4 border-b px-5 py-4 sm:px-6">
				<div class="flex min-w-0 items-start gap-3">
					<div class="flex size-10 shrink-0 items-center justify-center rounded-xl bg-foreground text-background">
						<Forward class="size-5"/>
					</div>

					<div class="min-w-0">
						<div class="flex flex-wrap items-center gap-2">
							<p class="font-mono text-[11px] font-semibold text-blue-600">
								{record.code}
							</p>

							<span class={`inline-flex items-center gap-1.5 rounded-md px-2 py-1 text-[10px] font-medium ${statusClass(record.status)}`}>
								<span class={`size-1.5 rounded-full ${statusDot(record.status)}`}></span>
								{DISPOSITION_STATUS_LABELS[record.status]}
							</span>
						</div>

						<h2 class="mt-1.5 text-base font-bold leading-6">
							Detail Disposisi
						</h2>

						<p class="mt-1 text-xs text-muted-foreground">
							Dibuat oleh {record.createdBy} pada {formatDispositionDateTime(record.createdAt)}
						</p>
					</div>
				</div>

				<button
					type="button"
					class="flex size-9 shrink-0 items-center justify-center rounded-lg text-muted-foreground transition hover:bg-muted hover:text-foreground"
					onclick={onClose}
				>
					<X class="size-4"/>
				</button>
			</div>

			<div class="min-h-0 flex-1 overflow-y-auto px-5 py-5 sm:px-6">
				<section>
					<h3 class="text-sm font-semibold">Informasi Surat</h3>

					<div class="mt-4 rounded-xl border bg-muted/30 p-4">
						<div class="flex items-start gap-3">
							<div class="flex size-10 shrink-0 items-center justify-center rounded-lg bg-background shadow-sm">
								<FileText class="size-4 text-muted-foreground"/>
							</div>

							<div class="min-w-0">
								<div class="flex flex-wrap items-center gap-2">
									<p class="font-mono text-[11px] font-semibold text-blue-600">
										{record.agendaNumber}
									</p>

									<p class="text-[11px] text-muted-foreground">
										{record.letterNumber}
									</p>
								</div>

								<p class="mt-2 text-sm font-semibold leading-6">
									{record.subject}
								</p>

								<p class="mt-1 text-xs text-muted-foreground">
									{record.sender}
								</p>
							</div>
						</div>
					</div>
				</section>

				<section class="mt-6 border-t pt-6">
					<h3 class="text-sm font-semibold">Tujuan & Tindak Lanjut</h3>

					<div class="mt-4 grid gap-3 sm:grid-cols-2">
						<div class="rounded-xl border p-4">
							<div class="flex items-center gap-2 text-muted-foreground">
								{#if record.targetType==='USER'}
									<User class="size-4"/>
								{:else}
									<Building2 class="size-4"/>
								{/if}

								<p class="text-[11px] font-medium">
									{DISPOSITION_TARGET_LABELS[record.targetType]} Tujuan
								</p>
							</div>

							<p class="mt-3 text-sm font-semibold">
								{record.targetName}
							</p>
						</div>

						<div class="rounded-xl border p-4">
							<div class="flex items-center gap-2 text-muted-foreground">
								<CalendarDays class="size-4"/>
								<p class="text-[11px] font-medium">Batas Waktu</p>
							</div>

							<p class="mt-3 text-sm font-semibold">
								{formatDispositionDate(record.dueDate)}
							</p>
						</div>
					</div>

					<div class="mt-3 rounded-xl border p-4">
						<div class="flex flex-wrap items-center justify-between gap-3">
							<p class="text-[11px] font-medium text-muted-foreground">
								Instruksi Disposisi
							</p>

							<span class={`rounded-md px-2.5 py-1 text-[10px] font-medium ${priorityClass()}`}>
								{DISPOSITION_PRIORITY_LABELS[record.priority]}
							</span>
						</div>

						<p class="mt-3 text-sm leading-6">
							{record.instruction}
						</p>
					</div>

					<div class="mt-3 rounded-xl border p-4">
						<p class="text-[11px] font-medium text-muted-foreground">
							Catatan Tambahan
						</p>

						<p class="mt-3 text-sm leading-6">
							{record.notes||'Tidak ada catatan tambahan.'}
						</p>
					</div>
				</section>

				<section class="mt-6 border-t pt-6">
					<div class="flex items-center gap-2">
						<History class="size-4 text-muted-foreground"/>
						<h3 class="text-sm font-semibold">Riwayat Disposisi</h3>
					</div>

					<div class="mt-5">
						{#each record.history??[] as item,index}
							<div class="relative flex gap-4 pb-5 last:pb-0">
								{#if index<(record.history?.length??0)-1}
									<div class="absolute bottom-0 left-[15px] top-8 w-px bg-border"></div>
								{/if}

								<div class={`relative z-10 mt-0.5 flex size-8 shrink-0 items-center justify-center rounded-full ${statusClass(item.status)}`}>
									{#if item.status==='COMPLETED'}
										<CircleCheck class="size-4"/>
									{:else}
										<Clock class="size-4"/>
									{/if}
								</div>

								<div class="min-w-0 flex-1">
									<div class="flex flex-col justify-between gap-1 sm:flex-row sm:items-center">
										<p class="text-xs font-semibold">
											{DISPOSITION_STATUS_LABELS[item.status]}
										</p>

										<p class="text-[10px] text-muted-foreground">
											{formatDispositionDateTime(item.at)}
										</p>
									</div>

									<p class="mt-1 text-xs leading-5 text-muted-foreground">
										{item.note}
									</p>
								</div>
							</div>
						{/each}
					</div>
				</section>
			</div>

			<div class="flex flex-col gap-2 border-t px-5 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6">
				<div class="flex flex-col gap-2 sm:flex-row">
					{#if record.status==='WAITING'}
						<Button
							variant="outline"
							class="rounded-lg"
							onclick={()=>onStatusChange(record,'RECEIVED')}
						>
							<CircleCheck class="size-4"/>
							Tandai Diterima
						</Button>
					{:else if record.status==='RECEIVED'}
						<Button
							variant="outline"
							class="rounded-lg"
							onclick={()=>onStatusChange(record,'IN_PROGRESS')}
						>
							<Play class="size-4"/>
							Mulai Proses
						</Button>
					{:else if record.status==='IN_PROGRESS'}
						<Button
							variant="outline"
							class="rounded-lg"
							onclick={()=>onStatusChange(record,'COMPLETED')}
						>
							<CircleCheck class="size-4"/>
							Selesaikan
						</Button>
					{/if}
				</div>

				<Button
					class="rounded-lg"
					onclick={()=>onEdit(record)}
				>
					<Pencil class="size-4"/>
					Edit Disposisi
				</Button>
			</div>
		</div>
	</div>
{/if}