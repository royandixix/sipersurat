<script lang="ts">
	import*as DropdownMenu from'$lib/components/ui/dropdown-menu/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import MoreHorizontal from'@lucide/svelte/icons/more-horizontal';
	import Eye from'@lucide/svelte/icons/eye';
	import Pencil from'@lucide/svelte/icons/pencil';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import ChevronLeft from'@lucide/svelte/icons/chevron-left';
	import ChevronRight from'@lucide/svelte/icons/chevron-right';
	import Send from'@lucide/svelte/icons/send';
	import type{
		OutgoingMailRecord,
		OutgoingMailStatus
	}from'$lib/features/surat-keluar/types';
	import{
		formatOutgoingDate,
		OUTGOING_MAIL_STATUS_LABELS
	}from'$lib/features/surat-keluar/types';

	type Props={
		records:OutgoingMailRecord[];
		currentPage:number;
		totalPages:number;
		totalItems:number;
		perPage:number;
		onPageChange:(page:number)=>void;
		onView:(record:OutgoingMailRecord)=>void;
		onEdit:(record:OutgoingMailRecord)=>void;
		onDelete:(record:OutgoingMailRecord)=>void;
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

	function statusDot(status:OutgoingMailStatus){
		switch(status){
			case'DRAFT':return'bg-zinc-500';
			case'PENDING_APPROVAL':return'bg-amber-500';
			case'APPROVED':return'bg-blue-500';
			case'SENT':return'bg-emerald-500';
			case'REJECTED':return'bg-red-500';
			case'ARCHIVED':return'bg-violet-500';
		}
	}
</script>

<div class="hidden overflow-x-auto lg:block">
	<table class="w-full min-w-[1100px] border-collapse">
		<thead class="bg-muted/40">
			<tr class="border-b">
				<th class="w-[175px] px-5 py-3.5 text-left text-xs font-semibold">Surat</th>
				<th class="w-[200px] px-4 py-3.5 text-left text-xs font-semibold">Tujuan</th>
				<th class="min-w-[260px] px-4 py-3.5 text-left text-xs font-semibold">Perihal</th>
				<th class="w-[150px] px-4 py-3.5 text-left text-xs font-semibold">Unit Pembuat</th>
				<th class="w-[120px] px-4 py-3.5 text-left text-xs font-semibold">Kategori</th>
				<th class="w-[155px] px-4 py-3.5 text-left text-xs font-semibold">Status</th>
				<th class="w-[65px] px-5 py-3.5 text-right text-xs font-semibold">Aksi</th>
			</tr>
		</thead>

		<tbody>
			{#if records.length===0}
				<tr>
					<td colspan="7">
						<div class="flex min-h-[260px] flex-col items-center justify-center px-5 text-center">
							<div class="flex size-11 items-center justify-center rounded-lg bg-muted">
								<Send class="size-5 text-muted-foreground"/>
							</div>

							<p class="mt-4 text-sm font-semibold">
								Surat keluar tidak ditemukan
							</p>

							<p class="mt-1 text-xs text-muted-foreground">
								Coba ubah pencarian atau filter yang digunakan.
							</p>
						</div>
					</td>
				</tr>
			{:else}
				{#each records as record(record.id)}
					<tr class="border-b transition-colors last:border-b-0 hover:bg-muted/30">
						<td class="px-5 py-4">
							<button
								class="font-mono text-[11px] font-semibold text-blue-600 hover:underline"
								onclick={()=>onView(record)}
							>
								{record.agendaNumber}
							</button>

							<p class="mt-1 max-w-[165px] truncate text-xs font-semibold">
								{record.letterNumber}
							</p>

							<p class="mt-1 text-[10px] text-muted-foreground">
								{formatOutgoingDate(record.letterDate)}
							</p>
						</td>

						<td class="px-4 py-4">
							<p class="max-w-[190px] truncate text-xs font-semibold">
								{record.recipient}
							</p>

							<p class="mt-1 max-w-[190px] truncate text-[10px] text-muted-foreground">
								{record.signer}
							</p>
						</td>

						<td class="px-4 py-4">
							<button
								class="line-clamp-2 max-w-[340px] text-left text-xs font-medium leading-5 hover:underline"
								onclick={()=>onView(record)}
							>
								{record.subject}
							</button>
						</td>

						<td class="px-4 py-4">
							<p class="max-w-[145px] truncate text-xs font-medium">
								{record.sourceUnit}
							</p>

							<p class="mt-1 text-[10px] text-muted-foreground">
								{record.priority}
							</p>
						</td>

						<td class="px-4 py-4">
							<span class="rounded-md bg-blue-50 px-2.5 py-1.5 text-[10px] font-medium text-blue-700">
								{record.category}
							</span>
						</td>

						<td class="px-4 py-4">
							<span class={`inline-flex items-center gap-1.5 rounded-md px-2.5 py-1.5 text-[10px] font-medium ${statusClass(record.status)}`}>
								<span class={`size-1.5 rounded-full ${statusDot(record.status)}`}></span>
								{OUTGOING_MAIL_STATUS_LABELS[record.status]}
							</span>
						</td>

						<td class="px-5 py-4 text-right">
							<DropdownMenu.Root>
								<DropdownMenu.Trigger>
									{#snippet child({props})}
										<button
											{...props}
											type="button"
											class="inline-flex size-8 items-center justify-center rounded-md text-muted-foreground hover:bg-muted hover:text-foreground"
										>
											<MoreHorizontal class="size-4"/>
										</button>
									{/snippet}
								</DropdownMenu.Trigger>

								<DropdownMenu.Content align="end" class="w-48">
									<DropdownMenu.Label>Aksi surat</DropdownMenu.Label>
									<DropdownMenu.Separator/>

									<DropdownMenu.Item onclick={()=>onView(record)}>
										<Eye/>
										Lihat Detail
									</DropdownMenu.Item>

									<DropdownMenu.Item onclick={()=>onEdit(record)}>
										<Pencil/>
										Edit Surat
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
			<Send class="size-6 text-muted-foreground"/>

			<p class="mt-4 text-sm font-semibold">
				Surat keluar tidak ditemukan
			</p>
		</div>
	{:else}
		{#each records as record(record.id)}
			<div class="p-5">
				<div class="flex items-start justify-between gap-4">
					<div>
						<button
							class="font-mono text-[11px] font-semibold text-blue-600"
							onclick={()=>onView(record)}
						>
							{record.agendaNumber}
						</button>

						<p class="mt-1 text-xs font-semibold">
							{record.letterNumber}
						</p>
					</div>

					<DropdownMenu.Root>
						<DropdownMenu.Trigger>
							{#snippet child({props})}
								<button
									{...props}
									type="button"
									class="flex size-8 items-center justify-center rounded-md hover:bg-muted"
								>
									<MoreHorizontal class="size-4"/>
								</button>
							{/snippet}
						</DropdownMenu.Trigger>

						<DropdownMenu.Content align="end" class="w-44">
							<DropdownMenu.Item onclick={()=>onView(record)}>
								<Eye/>
								Detail
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
					class="mt-4 line-clamp-2 text-left text-sm font-semibold leading-5"
					onclick={()=>onView(record)}
				>
					{record.subject}
				</button>

				<p class="mt-2 text-xs text-muted-foreground">
					Kepada {record.recipient}
				</p>

				<div class="mt-4 flex flex-wrap gap-2">
					<span class="rounded-md bg-blue-50 px-2.5 py-1.5 text-[10px] text-blue-700">
						{record.category}
					</span>

					<span class={`inline-flex items-center gap-1.5 rounded-md px-2.5 py-1.5 text-[10px] ${statusClass(record.status)}`}>
						<span class={`size-1.5 rounded-full ${statusDot(record.status)}`}></span>
						{OUTGOING_MAIL_STATUS_LABELS[record.status]}
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
		surat
	</p>

	<div class="flex items-center gap-2">
		<Button
			variant="outline"
			size="icon"
			class="size-8"
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
			variant="outline"
			size="icon"
			class="size-8"
			disabled={currentPage>=totalPages}
			onclick={()=>onPageChange(currentPage+1)}
		>
			<ChevronRight class="size-4"/>
		</Button>
	</div>
</div>