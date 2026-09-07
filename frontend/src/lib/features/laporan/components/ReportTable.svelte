<script lang="ts">
	import{Button}from'$lib/components/ui/button/index.js';
	import ChevronLeft from'@lucide/svelte/icons/chevron-left';
	import ChevronRight from'@lucide/svelte/icons/chevron-right';
	import BarChart3 from'@lucide/svelte/icons/bar-chart-3';
	import Inbox from'@lucide/svelte/icons/inbox';
	import Send from'@lucide/svelte/icons/send';
	import Forward from'@lucide/svelte/icons/forward';
	import Archive from'@lucide/svelte/icons/archive';
	import type{
		ReportRecord,
		ReportSource
	}from'$lib/features/laporan/types';
	import{
		formatReportDate,
		REPORT_SOURCE_LABELS
	}from'$lib/features/laporan/types';

	type Props={
		records:ReportRecord[];
		currentPage:number;
		totalPages:number;
		totalItems:number;
		perPage:number;
		onPageChange:(page:number)=>void;
	};

	let{
		records,
		currentPage,
		totalPages,
		totalItems,
		perPage,
		onPageChange
	}:Props=$props();

	const firstItem=$derived(
		totalItems===0
			?0
			:(currentPage-1)*perPage+1
	);

	const lastItem=$derived(
		Math.min(
			currentPage*perPage,
			totalItems
		)
	);

	function sourceClass(source:ReportSource){
		switch(source){
			case'INCOMING':
				return'inline-flex items-center gap-1.5 rounded-md bg-blue-50 px-2.5 py-1.5 text-[10px] font-semibold text-blue-700';
			case'OUTGOING':
				return'inline-flex items-center gap-1.5 rounded-md bg-violet-50 px-2.5 py-1.5 text-[10px] font-semibold text-violet-700';
			case'DISPOSITION':
				return'inline-flex items-center gap-1.5 rounded-md bg-amber-50 px-2.5 py-1.5 text-[10px] font-semibold text-amber-700';
			case'ARCHIVE':
				return'inline-flex items-center gap-1.5 rounded-md bg-emerald-50 px-2.5 py-1.5 text-[10px] font-semibold text-emerald-700';
		}
	}

	function statusClass(status:string){
		const value=status.toLowerCase();

		if(
			value.includes('selesai')||
			value.includes('dikirim')||
			value.includes('diarsip')
		){
			return'inline-flex rounded-md bg-emerald-50 px-2.5 py-1.5 text-[10px] font-medium text-emerald-700';
		}

		if(
			value.includes('menunggu')||
			value.includes('draft')
		){
			return'inline-flex rounded-md bg-amber-50 px-2.5 py-1.5 text-[10px] font-medium text-amber-700';
		}

		if(
			value.includes('ditolak')
		){
			return'inline-flex rounded-md bg-red-50 px-2.5 py-1.5 text-[10px] font-medium text-red-700';
		}

		if(
			value.includes('diproses')||
			value.includes('disetujui')||
			value.includes('diterima')||
			value.includes('disposisi')
		){
			return'inline-flex rounded-md bg-blue-50 px-2.5 py-1.5 text-[10px] font-medium text-blue-700';
		}

		return'inline-flex rounded-md bg-muted px-2.5 py-1.5 text-[10px] font-medium text-muted-foreground';
	}
</script>

<div class="hidden overflow-x-auto lg:block">
	<table class="w-full min-w-[1150px] border-collapse">
		<thead class="bg-muted/40">
			<tr class="border-b">
				<th class="w-[125px] px-5 py-3.5 text-left text-xs font-semibold">
					Tanggal
				</th>

				<th class="w-[150px] px-4 py-3.5 text-left text-xs font-semibold">
					Jenis
				</th>

				<th class="w-[180px] px-4 py-3.5 text-left text-xs font-semibold">
					Nomor Surat
				</th>

				<th class="min-w-[270px] px-4 py-3.5 text-left text-xs font-semibold">
					Perihal
				</th>

				<th class="w-[200px] px-4 py-3.5 text-left text-xs font-semibold">
					Pengirim / Penerima
				</th>

				<th class="w-[150px] px-4 py-3.5 text-left text-xs font-semibold">
					Unit
				</th>

				<th class="w-[160px] px-4 py-3.5 text-left text-xs font-semibold">
					Status
				</th>
			</tr>
		</thead>

		<tbody>
			{#if records.length===0}
				<tr>
					<td colspan="7">
						<div class="flex min-h-[270px] flex-col items-center justify-center px-5 text-center">
							<div class="flex size-11 items-center justify-center rounded-lg bg-muted">
								<BarChart3 class="size-5 text-muted-foreground"/>
							</div>

							<p class="mt-4 text-sm font-semibold">
								Data laporan tidak ditemukan
							</p>

							<p class="mt-1 max-w-sm text-xs leading-5 text-muted-foreground">
								Coba ubah periode, pencarian, atau filter yang sedang digunakan.
							</p>
						</div>
					</td>
				</tr>
			{:else}
				{#each records as record(record.id)}
					<tr class="border-b transition-colors last:border-b-0 hover:bg-muted/30">
						<td class="px-5 py-4">
							<p class="text-xs font-medium">
								{formatReportDate(record.date)}
							</p>
						</td>

						<td class="px-4 py-4">
							<span class={sourceClass(record.source)}>
								{#if record.source==='INCOMING'}
									<Inbox class="size-3"/>
								{:else if record.source==='OUTGOING'}
									<Send class="size-3"/>
								{:else if record.source==='DISPOSITION'}
									<Forward class="size-3"/>
								{:else}
									<Archive class="size-3"/>
								{/if}

								{REPORT_SOURCE_LABELS[record.source]}
							</span>
						</td>

						<td class="px-4 py-4">
							<p class="font-mono text-[10px] font-semibold text-blue-600">
								{record.agendaNumber}
							</p>

							<p class="mt-1 max-w-[175px] truncate text-xs font-semibold">
								{record.letterNumber}
							</p>
						</td>

						<td class="px-4 py-4">
							<p class="line-clamp-2 max-w-[350px] text-xs font-medium leading-5">
								{record.subject}
							</p>

							<p class="mt-1 text-[10px] text-muted-foreground">
								{record.category}
							</p>
						</td>

						<td class="px-4 py-4">
							<p class="max-w-[190px] truncate text-xs font-medium">
								{record.correspondent}
							</p>
						</td>

						<td class="px-4 py-4">
							<p class="max-w-[145px] truncate text-xs font-medium">
								{record.unit}
							</p>
						</td>

						<td class="px-4 py-4">
							<span class={statusClass(record.status)}>
								{record.status}
							</span>
						</td>
					</tr>
				{/each}
			{/if}
		</tbody>
	</table>
</div>

<div class="divide-y lg:hidden">
	{#if records.length===0}
		<div class="flex min-h-[250px] flex-col items-center justify-center p-5 text-center">
			<div class="flex size-11 items-center justify-center rounded-lg bg-muted">
				<BarChart3 class="size-5 text-muted-foreground"/>
			</div>

			<p class="mt-4 text-sm font-semibold">
				Data laporan tidak ditemukan
			</p>

			<p class="mt-1 text-xs text-muted-foreground">
				Coba ubah filter laporan.
			</p>
		</div>
	{:else}
		{#each records as record(record.id)}
			<div class="p-5">
				<div class="flex flex-wrap items-center justify-between gap-3">
					<span class={sourceClass(record.source)}>
						{#if record.source==='INCOMING'}
							<Inbox class="size-3"/>
						{:else if record.source==='OUTGOING'}
							<Send class="size-3"/>
						{:else if record.source==='DISPOSITION'}
							<Forward class="size-3"/>
						{:else}
							<Archive class="size-3"/>
						{/if}

						{REPORT_SOURCE_LABELS[record.source]}
					</span>

					<p class="text-[10px] text-muted-foreground">
						{formatReportDate(record.date)}
					</p>
				</div>

				<div class="mt-4">
					<p class="font-mono text-[10px] font-semibold text-blue-600">
						{record.agendaNumber}
					</p>

					<p class="mt-1 text-xs font-semibold">
						{record.letterNumber}
					</p>
				</div>

				<p class="mt-3 text-sm font-semibold leading-5">
					{record.subject}
				</p>

				<p class="mt-2 text-xs text-muted-foreground">
					{record.correspondent}
				</p>

				<div class="mt-4 grid grid-cols-2 gap-3 rounded-lg border bg-muted/30 p-3">
					<div>
						<p class="text-[9px] text-muted-foreground">
							Unit
						</p>

						<p class="mt-1 truncate text-[11px] font-semibold">
							{record.unit}
						</p>
					</div>

					<div>
						<p class="text-[9px] text-muted-foreground">
							Kategori
						</p>

						<p class="mt-1 truncate text-[11px] font-semibold">
							{record.category}
						</p>
					</div>
				</div>

				<div class="mt-3">
					<span class={statusClass(record.status)}>
						{record.status}
					</span>
				</div>
			</div>
		{/each}
	{/if}
</div>

<div class="flex flex-col gap-3 border-t px-5 py-3.5 sm:flex-row sm:items-center sm:justify-between">
	<p class="text-xs text-muted-foreground">
		Menampilkan
		<span class="font-semibold text-foreground">
			{firstItem}–{lastItem}
		</span>
		dari
		<span class="font-semibold text-foreground">
			{totalItems}
		</span>
		data
	</p>

	<div class="flex items-center gap-2">
		<Button
			type="button"
			variant="outline"
			size="icon"
			class="size-8 rounded-md"
			disabled={currentPage<=1}
			onclick={()=>onPageChange(currentPage-1)}
		>
			<ChevronLeft class="size-4"/>
		</Button>

		<div class="flex h-8 min-w-8 items-center justify-center rounded-md bg-foreground px-2 text-xs font-semibold text-background">
			{currentPage}
		</div>

		<span class="text-xs text-muted-foreground">
			dari {totalPages}
		</span>

		<Button
			type="button"
			variant="outline"
			size="icon"
			class="size-8 rounded-md"
			disabled={currentPage>=totalPages}
			onclick={()=>onPageChange(currentPage+1)}
		>
			<ChevronRight class="size-4"/>
		</Button>
	</div>
</div>