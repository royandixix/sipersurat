<script lang="ts">
	import*as DropdownMenu from'$lib/components/ui/dropdown-menu/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import MoreHorizontal from'@lucide/svelte/icons/more-horizontal';
	import Eye from'@lucide/svelte/icons/eye';
	import Pencil from'@lucide/svelte/icons/pencil';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import ChevronLeft from'@lucide/svelte/icons/chevron-left';
	import ChevronRight from'@lucide/svelte/icons/chevron-right';
	import Forward from'@lucide/svelte/icons/forward';
	import type{
		DispositionRecord,
		DispositionStatus
	}from'$lib/features/disposisi/types';
	import{
		DISPOSITION_PRIORITY_LABELS,
		DISPOSITION_STATUS_LABELS,
		formatDispositionDate
	}from'$lib/features/disposisi/types';

	type Props={
		records:DispositionRecord[];
		currentPage:number;
		totalPages:number;
		totalItems:number;
		perPage:number;
		onPageChange:(page:number)=>void;
		onView:(record:DispositionRecord)=>void;
		onEdit:(record:DispositionRecord)=>void;
		onDelete:(record:DispositionRecord)=>void;
	};

	let{
		records,
		currentPage,
		totalPages,
		totalItems,
		perPage,
		onPageChange,
		onView,
		onEdit,
		onDelete
	}:Props=$props();

	const firstItem=$derived(totalItems===0?0:(currentPage-1)*perPage+1);
	const lastItem=$derived(Math.min(currentPage*perPage,totalItems));

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

	function priorityClass(record:DispositionRecord){
		if(record.priority==='URGENT')return'bg-red-50 text-red-700';
		if(record.priority==='IMPORTANT')return'bg-amber-50 text-amber-700';
		return'bg-zinc-100 text-zinc-700';
	}

	function isOverdue(record:DispositionRecord){
		if(record.status==='COMPLETED'||!record.dueDate)return false;
		return new Date(`${record.dueDate}T23:59:59`).getTime()<Date.now();
	}
</script>

<div class="hidden overflow-x-auto lg:block">
	<table class="w-full min-w-[1100px] border-collapse">
		<thead class="bg-muted/50">
			<tr class="border-b">
				<th class="w-[150px] px-5 py-3.5 text-left text-xs font-semibold">Disposisi</th>
				<th class="w-[190px] px-4 py-3.5 text-left text-xs font-semibold">Surat</th>
				<th class="min-w-[240px] px-4 py-3.5 text-left text-xs font-semibold">Perihal</th>
				<th class="w-[180px] px-4 py-3.5 text-left text-xs font-semibold">Tujuan</th>
				<th class="w-[110px] px-4 py-3.5 text-left text-xs font-semibold">Prioritas</th>
				<th class="w-[130px] px-4 py-3.5 text-left text-xs font-semibold">Batas Waktu</th>
				<th class="w-[160px] px-4 py-3.5 text-left text-xs font-semibold">Status</th>
				<th class="w-[65px] px-5 py-3.5 text-right text-xs font-semibold">Aksi</th>
			</tr>
		</thead>

		<tbody>
			{#if records.length===0}
				<tr>
					<td colspan="8">
						<div class="flex min-h-[260px] flex-col items-center justify-center px-5 text-center">
							<div class="flex size-11 items-center justify-center rounded-lg bg-muted">
								<Forward class="size-5 text-muted-foreground"/>
							</div>

							<p class="mt-4 text-sm font-semibold">
								Disposisi tidak ditemukan
							</p>

							<p class="mt-1 text-xs text-muted-foreground">
								Coba ubah pencarian atau filter yang sedang digunakan.
							</p>
						</div>
					</td>
				</tr>
			{:else}
				{#each records as record(record.id)}
					<tr class="border-b transition-colors last:border-b-0 hover:bg-muted/30">
						<td class="px-5 py-4 align-middle">
							<button
								class="font-mono text-xs font-semibold text-blue-600 hover:underline"
								onclick={()=>onView(record)}
							>
								{record.code}
							</button>

							<p class="mt-1 text-[11px] text-muted-foreground">
								{record.agendaNumber}
							</p>
						</td>

						<td class="px-4 py-4 align-middle">
							<p class="max-w-[180px] truncate text-xs font-semibold">
								{record.letterNumber}
							</p>

							<p class="mt-1 max-w-[180px] truncate text-[11px] text-muted-foreground">
								{record.sender}
							</p>
						</td>

						<td class="px-4 py-4 align-middle">
							<button
								class="line-clamp-2 max-w-[330px] text-left text-xs font-medium leading-5 hover:underline"
								onclick={()=>onView(record)}
							>
								{record.subject}
							</button>
						</td>

						<td class="px-4 py-4 align-middle">
							<p class="max-w-[175px] truncate text-xs font-semibold">
								{record.targetName}
							</p>

							<p class="mt-1 text-[11px] text-muted-foreground">
								{record.targetType==='USER'?'Pengguna':'Unit Kerja'}
							</p>
						</td>

						<td class="px-4 py-4 align-middle">
							<span class={`inline-flex rounded-md px-2.5 py-1.5 text-[11px] font-medium ${priorityClass(record)}`}>
								{DISPOSITION_PRIORITY_LABELS[record.priority]}
							</span>
						</td>

						<td class="px-4 py-4 align-middle">
							<p class={`text-xs font-medium ${isOverdue(record)?'text-red-600':''}`}>
								{formatDispositionDate(record.dueDate)}
							</p>

							{#if isOverdue(record)}
								<p class="mt-1 text-[10px] font-medium text-red-600">
									Terlambat
								</p>
							{/if}
						</td>

						<td class="px-4 py-4 align-middle">
							<span class={`inline-flex items-center gap-1.5 rounded-md px-2.5 py-1.5 text-[11px] font-medium ${statusClass(record.status)}`}>
								<span class={`size-1.5 rounded-full ${statusDot(record.status)}`}></span>
								{DISPOSITION_STATUS_LABELS[record.status]}
							</span>
						</td>

						<td class="px-5 py-4 text-right align-middle">
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

								<DropdownMenu.Content align="end" class="w-48">
									<DropdownMenu.Label>Aksi disposisi</DropdownMenu.Label>
									<DropdownMenu.Separator/>

									<DropdownMenu.Item onclick={()=>onView(record)}>
										<Eye/>
										Lihat Detail
									</DropdownMenu.Item>

									<DropdownMenu.Item onclick={()=>onEdit(record)}>
										<Pencil/>
										Edit Disposisi
									</DropdownMenu.Item>

									<DropdownMenu.Separator/>

									<DropdownMenu.Item
										class="text-red-600 focus:text-red-600"
										onclick={()=>onDelete(record)}
									>
										<Trash2/>
										Hapus
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
		<div class="flex min-h-[240px] flex-col items-center justify-center p-5 text-center">
			<div class="flex size-11 items-center justify-center rounded-lg bg-muted">
				<Forward class="size-5 text-muted-foreground"/>
			</div>

			<p class="mt-4 text-sm font-semibold">
				Disposisi tidak ditemukan
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
							class="font-mono text-xs font-semibold text-blue-600 hover:underline"
							onclick={()=>onView(record)}
						>
							{record.code}
						</button>

						<p class="mt-1 text-[11px] text-muted-foreground">
							{record.agendaNumber}
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

						<DropdownMenu.Content align="end" class="w-48">
							<DropdownMenu.Item onclick={()=>onView(record)}>
								<Eye/>
								Lihat Detail
							</DropdownMenu.Item>

							<DropdownMenu.Item onclick={()=>onEdit(record)}>
								<Pencil/>
								Edit
							</DropdownMenu.Item>

							<DropdownMenu.Separator/>

							<DropdownMenu.Item
								class="text-red-600"
								onclick={()=>onDelete(record)}
							>
								<Trash2/>
								Hapus
							</DropdownMenu.Item>
						</DropdownMenu.Content>
					</DropdownMenu.Root>
				</div>

				<button
					class="mt-4 line-clamp-2 w-full text-left text-sm font-semibold leading-5"
					onclick={()=>onView(record)}
				>
					{record.subject}
				</button>

				<p class="mt-2 text-xs text-muted-foreground">
					{record.sender}
				</p>

				<div class="mt-4 rounded-lg border bg-muted/30 p-3">
					<p class="text-[10px] text-muted-foreground">
						Tujuan Disposisi
					</p>

					<p class="mt-1 text-xs font-semibold">
						{record.targetName}
					</p>
				</div>

				<div class="mt-4 flex flex-wrap gap-2">
					<span class={`rounded-md px-2.5 py-1.5 text-[11px] font-medium ${priorityClass(record)}`}>
						{DISPOSITION_PRIORITY_LABELS[record.priority]}
					</span>

					<span class={`inline-flex items-center gap-1.5 rounded-md px-2.5 py-1.5 text-[11px] font-medium ${statusClass(record.status)}`}>
						<span class={`size-1.5 rounded-full ${statusDot(record.status)}`}></span>
						{DISPOSITION_STATUS_LABELS[record.status]}
					</span>

					<span class={`rounded-md bg-muted px-2.5 py-1.5 text-[11px] font-medium ${isOverdue(record)?'text-red-600':'text-muted-foreground'}`}>
						{formatDispositionDate(record.dueDate)}
					</span>
				</div>
			</div>
		{/each}
	{/if}
</div>

<div class="flex flex-col gap-3 border-t px-5 py-3.5 sm:flex-row sm:items-center sm:justify-between">
	<p class="text-xs text-muted-foreground">
		Menampilkan
		<span class="font-semibold text-foreground">{firstItem}–{lastItem}</span>
		dari
		<span class="font-semibold text-foreground">{totalItems}</span>
		disposisi
	</p>

	<div class="flex items-center gap-2">
		<Button
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