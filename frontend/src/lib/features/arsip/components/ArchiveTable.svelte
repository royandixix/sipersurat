<script lang="ts">
	import*as DropdownMenu from'$lib/components/ui/dropdown-menu/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import MoreHorizontal from'@lucide/svelte/icons/more-horizontal';
	import Eye from'@lucide/svelte/icons/eye';
	import RotateCcw from'@lucide/svelte/icons/rotate-ccw';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import ChevronLeft from'@lucide/svelte/icons/chevron-left';
	import ChevronRight from'@lucide/svelte/icons/chevron-right';
	import Archive from'@lucide/svelte/icons/archive';
	import Inbox from'@lucide/svelte/icons/inbox';
	import Send from'@lucide/svelte/icons/send';
	import type{ArchiveRecord}from'$lib/features/arsip/types';
	import{
		ARCHIVE_SOURCE_LABELS,
		formatArchiveDate
	}from'$lib/features/arsip/types';

	type Props={
		records:ArchiveRecord[];
		currentPage:number;
		totalPages:number;
		totalItems:number;
		perPage:number;
		onPageChange:(page:number)=>void;
		onView:(record:ArchiveRecord)=>void;
		onRestore:(record:ArchiveRecord)=>void;
		onDelete:(record:ArchiveRecord)=>void;
	};

	let{
		records,
		currentPage,
		totalPages,
		totalItems,
		perPage,
		onPageChange,
		onView,
		onRestore,
		onDelete
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
</script>

<div class="hidden overflow-x-auto lg:block">
	<table class="w-full min-w-[1120px] border-collapse">
		<thead class="bg-muted/40">
			<tr class="border-b">
				<th class="w-[175px] px-5 py-3.5 text-left text-xs font-semibold">
					Arsip
				</th>

				<th class="w-[140px] px-4 py-3.5 text-left text-xs font-semibold">
					Jenis
				</th>

				<th class="w-[200px] px-4 py-3.5 text-left text-xs font-semibold">
					Pengirim / Penerima
				</th>

				<th class="min-w-[250px] px-4 py-3.5 text-left text-xs font-semibold">
					Perihal
				</th>

				<th class="w-[150px] px-4 py-3.5 text-left text-xs font-semibold">
					Unit Kerja
				</th>

				<th class="w-[130px] px-4 py-3.5 text-left text-xs font-semibold">
					Tanggal Arsip
				</th>

				<th class="w-[65px] px-5 py-3.5 text-right text-xs font-semibold">
					Aksi
				</th>
			</tr>
		</thead>

		<tbody>
			{#if records.length===0}
				<tr>
					<td colspan="7">
						<div class="flex min-h-[270px] flex-col items-center justify-center px-5 text-center">
							<div class="flex size-11 items-center justify-center rounded-lg bg-muted">
								<Archive class="size-5 text-muted-foreground"/>
							</div>

							<p class="mt-4 text-sm font-semibold">
								Arsip tidak ditemukan
							</p>

							<p class="mt-1 max-w-sm text-xs leading-5 text-muted-foreground">
								Belum ada dokumen yang sesuai dengan pencarian atau filter yang digunakan.
							</p>
						</div>
					</td>
				</tr>
			{:else}
				{#each records as record(record.id)}
					<tr class="border-b transition-colors last:border-b-0 hover:bg-muted/30">
						<td class="px-5 py-4">
							<button
								type="button"
								class="font-mono text-[11px] font-semibold text-blue-600 hover:underline"
								onclick={()=>onView(record)}
							>
								{record.agendaNumber}
							</button>

							<p class="mt-1 max-w-[165px] truncate text-xs font-semibold">
								{record.letterNumber}
							</p>

							<p class="mt-1 text-[10px] text-muted-foreground">
								{formatArchiveDate(record.letterDate)}
							</p>
						</td>

						<td class="px-4 py-4">
							<span
								class={[
									'inline-flex items-center gap-1.5 rounded-md px-2.5 py-1.5 text-[10px] font-semibold',
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
						</td>

						<td class="px-4 py-4">
							<p class="max-w-[190px] truncate text-xs font-semibold">
								{record.correspondent}
							</p>

							<p class="mt-1 text-[10px] text-muted-foreground">
								{record.category}
							</p>
						</td>

						<td class="px-4 py-4">
							<button
								type="button"
								class="line-clamp-2 max-w-[340px] text-left text-xs font-medium leading-5 hover:underline"
								onclick={()=>onView(record)}
							>
								{record.subject}
							</button>
						</td>

						<td class="px-4 py-4">
							<p class="max-w-[145px] truncate text-xs font-medium">
								{record.unit}
							</p>

							<p class="mt-1 text-[10px] text-muted-foreground">
								{record.priority}
							</p>
						</td>

						<td class="px-4 py-4">
							<p class="text-xs font-medium">
								{formatArchiveDate(record.archiveDate)}
							</p>
						</td>

						<td class="px-5 py-4 text-right">
							<DropdownMenu.Root>
								<DropdownMenu.Trigger>
									{#snippet child({props})}
										<button
											{...props}
											type="button"
											class="inline-flex size-8 items-center justify-center rounded-md text-muted-foreground transition hover:bg-muted hover:text-foreground"
										>
											<MoreHorizontal class="size-4"/>
										</button>
									{/snippet}
								</DropdownMenu.Trigger>

								<DropdownMenu.Content
									align="end"
									class="w-48"
								>
									<DropdownMenu.Label>
										Aksi arsip
									</DropdownMenu.Label>

									<DropdownMenu.Separator/>

									<DropdownMenu.Item
										onclick={()=>onView(record)}
									>
										<Eye/>
										Lihat Detail
									</DropdownMenu.Item>

									<DropdownMenu.Item
										onclick={()=>onRestore(record)}
									>
										<RotateCcw/>
										Pulihkan
									</DropdownMenu.Item>

									<DropdownMenu.Separator/>

									<DropdownMenu.Item
										class="text-red-600 focus:text-red-600"
										onclick={()=>onDelete(record)}
									>
										<Trash2/>
										Hapus Permanen
									</DropdownMenu.Item>
								</DropdownMenu.Content>
							</DropdownMenu.Root>
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
				<Archive class="size-5 text-muted-foreground"/>
			</div>

			<p class="mt-4 text-sm font-semibold">
				Arsip tidak ditemukan
			</p>

			<p class="mt-1 text-xs text-muted-foreground">
				Coba ubah pencarian atau filter.
			</p>
		</div>
	{:else}
		{#each records as record(record.id)}
			<div class="p-5">
				<div class="flex items-start justify-between gap-4">
					<div class="min-w-0 flex-1">
						<button
							type="button"
							class="font-mono text-[11px] font-semibold text-blue-600"
							onclick={()=>onView(record)}
						>
							{record.agendaNumber}
						</button>

						<p class="mt-1 truncate text-xs font-semibold">
							{record.letterNumber}
						</p>
					</div>

					<DropdownMenu.Root>
						<DropdownMenu.Trigger>
							{#snippet child({props})}
								<button
									{...props}
									type="button"
									class="flex size-8 shrink-0 items-center justify-center rounded-md hover:bg-muted"
								>
									<MoreHorizontal class="size-4"/>
								</button>
							{/snippet}
						</DropdownMenu.Trigger>

						<DropdownMenu.Content
							align="end"
							class="w-48"
						>
							<DropdownMenu.Item
								onclick={()=>onView(record)}
							>
								<Eye/>
								Detail
							</DropdownMenu.Item>

							<DropdownMenu.Item
								onclick={()=>onRestore(record)}
							>
								<RotateCcw/>
								Pulihkan
							</DropdownMenu.Item>

							<DropdownMenu.Separator/>

							<DropdownMenu.Item
								class="text-red-600"
								onclick={()=>onDelete(record)}
							>
								<Trash2/>
								Hapus Permanen
							</DropdownMenu.Item>
						</DropdownMenu.Content>
					</DropdownMenu.Root>
				</div>

				<div class="mt-3">
					<span
						class={[
							'inline-flex items-center gap-1.5 rounded-md px-2.5 py-1.5 text-[10px] font-semibold',
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

				<button
					type="button"
					class="mt-4 line-clamp-2 w-full text-left text-sm font-semibold leading-5"
					onclick={()=>onView(record)}
				>
					{record.subject}
				</button>

				<p class="mt-2 text-xs text-muted-foreground">
					{record.correspondent}
				</p>

				<div class="mt-4 grid grid-cols-2 gap-3 rounded-lg border bg-muted/30 p-3">
					<div>
						<p class="text-[9px] text-muted-foreground">
							Unit Kerja
						</p>

						<p class="mt-1 truncate text-[11px] font-semibold">
							{record.unit}
						</p>
					</div>

					<div>
						<p class="text-[9px] text-muted-foreground">
							Tanggal Arsip
						</p>

						<p class="mt-1 text-[11px] font-semibold">
							{formatArchiveDate(record.archiveDate)}
						</p>
					</div>
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
		arsip
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

		<p class="text-xs text-muted-foreground">
			dari {totalPages}
		</p>

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