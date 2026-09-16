<script lang="ts">
	import{browser}from'$app/environment';
	import{onMount}from'svelte';
	import{fly}from'svelte/transition';
	import*as Card from'$lib/components/ui/card/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import{Input}from'$lib/components/ui/input/index.js';
	import Archive from'@lucide/svelte/icons/archive';
	import Inbox from'@lucide/svelte/icons/inbox';
	import Send from'@lucide/svelte/icons/send';
	import CalendarCheck from'@lucide/svelte/icons/calendar-check';
	import Search from'@lucide/svelte/icons/search';
	import SlidersHorizontal from'@lucide/svelte/icons/sliders-horizontal';
	import RotateCcw from'@lucide/svelte/icons/rotate-ccw';
	import CircleCheck from'@lucide/svelte/icons/circle-check';
	import TriangleAlert from'@lucide/svelte/icons/triangle-alert';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import X from'@lucide/svelte/icons/x';
	import ArchiveTable from'$lib/features/arsip/components/ArchiveTable.svelte';
	import ArchiveDetailModal from'$lib/features/arsip/components/ArchiveDetailModal.svelte';
	import type{ArchiveRecord,ArchiveSource}from'$lib/features/arsip/types';
	import type{IncomingMailRecord}from'$lib/features/surat-masuk/types';
	import type{OutgoingMailRecord}from'$lib/features/surat-keluar/types';
	import{getArchives,restoreArchiveApi,deleteArchiveApi}from'$lib/features/arsip/api';

	const perPage=6;

	const fallbackIncoming:IncomingMailRecord[]=[
		{
			id:6,
			agendaNumber:'SM-2026-0006',
			letterNumber:'031/BKD/VIII/2026',
			letterDate:'2026-08-07',
			receivedDate:'2026-08-11',
			sender:'Badan Kepegawaian Daerah',
			subject:'Undangan Sosialisasi Sistem Informasi Kepegawaian',
			category:'Undangan',
			priority:'Biasa',
			targetUnit:'Kepegawaian',
			notes:'',
			status:'ARCHIVED',
			fileName:'sosialisasi-simpeg.pdf',
			fileType:'application/pdf',
			fileSize:352000,
			createdAt:'2026-08-11T10:25:00',
			updatedAt:'2026-08-12T15:15:00'
		},
		{
			id:10,
			agendaNumber:'SM-2026-0010',
			letterNumber:'119/DIS/VIII/2026',
			letterDate:'2026-08-05',
			receivedDate:'2026-08-06',
			sender:'Dinas Komunikasi dan Informatika',
			subject:'Pemberitahuan Pemutakhiran Data Sistem Informasi Instansi',
			category:'Pemberitahuan',
			priority:'Biasa',
			targetUnit:'Administrator',
			notes:'Dokumen sudah ditindaklanjuti.',
			status:'ARCHIVED',
			fileName:'pemutakhiran-data.pdf',
			fileType:'application/pdf',
			fileSize:426000,
			createdAt:'2026-08-06T09:20:00',
			updatedAt:'2026-08-08T14:40:00'
		}
	];

	const fallbackOutgoing:OutgoingMailRecord[]=[
		{
			id:6,
			agendaNumber:'SK-2026-0006',
			letterNumber:'006/SK/VIII/2026',
			letterDate:'2026-08-10',
			recipient:'Dinas Kesehatan',
			subject:'Pemberitahuan Jadwal Pemeriksaan Kesehatan Pegawai',
			category:'Pemberitahuan',
			priority:'Biasa',
			sourceUnit:'Umum',
			signer:'Kepala Bagian Umum',
			notes:'',
			status:'ARCHIVED',
			sentDate:'2026-08-10',
			fileName:'jadwal-pemeriksaan.pdf',
			fileType:'application/pdf',
			fileSize:340000,
			createdAt:'2026-08-10T08:15:00',
			updatedAt:'2026-08-11T10:00:00'
		},
		{
			id:8,
			agendaNumber:'SK-2026-0008',
			letterNumber:'008/SK/VIII/2026',
			letterDate:'2026-08-06',
			recipient:'Sekretariat Daerah',
			subject:'Penyampaian Laporan Administrasi Bulanan',
			category:'Eksternal',
			priority:'Penting',
			sourceUnit:'Sekretariat',
			signer:'Sekretaris',
			notes:'Dokumen final telah dikirim dan diarsipkan.',
			status:'ARCHIVED',
			sentDate:'2026-08-07',
			fileName:'laporan-administrasi.pdf',
			fileType:'application/pdf',
			fileSize:582000,
			createdAt:'2026-08-06T13:20:00',
			updatedAt:'2026-08-09T11:10:00'
		}
	];

	let archiveRecords=$state<ArchiveRecord[]>([]);
	let initialized=$state(false);

	let search=$state('');
	let sourceFilter=$state<'ALL'|ArchiveSource>('ALL');
	let categoryFilter=$state('ALL');
	let unitFilter=$state('ALL');
	let dateFilter=$state('');
	let currentPage=$state(1);

	let detailOpen=$state(false);
	let detailRecord=$state<ArchiveRecord|null>(null);
	let deleteTarget=$state<ArchiveRecord|null>(null);

	let notification=$state<{type:'success'|'info';message:string}|null>(null);
	let notificationTimer:ReturnType<typeof setTimeout>|undefined;

	onMount(async()=>{
		if(!browser)return;
		try{archiveRecords=await getArchives();}
		catch(error){archiveRecords=[];notify(error instanceof Error?error.message:'Data arsip gagal dimuat.','info');}
		initialized=true;
	});

	const categories=$derived.by(()=>{
		return uniqueValues(
			archiveRecords.map((record)=>record.category)
		);
	});

	const units=$derived.by(()=>{
		return uniqueValues(
			archiveRecords.map((record)=>record.unit)
		);
	});

	const filteredRecords=$derived(
		archiveRecords.filter((record)=>{
			const keyword=search.trim().toLowerCase();

			const matchesSearch=
				!keyword||
				record.agendaNumber.toLowerCase().includes(keyword)||
				record.letterNumber.toLowerCase().includes(keyword)||
				record.correspondent.toLowerCase().includes(keyword)||
				record.subject.toLowerCase().includes(keyword)||
				record.unit.toLowerCase().includes(keyword);

			const matchesSource=
				sourceFilter==='ALL'||
				record.source===sourceFilter;

			const matchesCategory=
				categoryFilter==='ALL'||
				record.category===categoryFilter;

			const matchesUnit=
				unitFilter==='ALL'||
				record.unit===unitFilter;

			const matchesDate=
				!dateFilter||
				record.archiveDate===dateFilter;

			return(
				matchesSearch&&
				matchesSource&&
				matchesCategory&&
				matchesUnit&&
				matchesDate
			);
		})
	);

	const totalPages=$derived(
		Math.max(
			1,
			Math.ceil(filteredRecords.length/perPage)
		)
	);

	const paginatedRecords=$derived(
		filteredRecords.slice(
			(currentPage-1)*perPage,
			currentPage*perPage
		)
	);

	const incomingCount=$derived(
		archiveRecords.filter(
			(record)=>record.source==='INCOMING'
		).length
	);

	const outgoingCount=$derived(
		archiveRecords.filter(
			(record)=>record.source==='OUTGOING'
		).length
	);

	const currentMonth=$derived(
		new Date().toISOString().slice(0,7)
	);

	const monthCount=$derived(
		archiveRecords.filter(
			(record)=>
				record.archiveDate.startsWith(currentMonth)
		).length
	);

	const hasFilter=$derived(
		search.trim()!==''||
		sourceFilter!=='ALL'||
		categoryFilter!=='ALL'||
		unitFilter!=='ALL'||
		dateFilter!==''
	);

	$effect(()=>{
		if(currentPage>totalPages){
			currentPage=totalPages;
		}
	});

	function extractDate(
		value:string,
		fallback:string
	){
		if(
			value&&
			/^\d{4}-\d{2}-\d{2}/.test(value)
		){
			return value.slice(0,10);
		}

		if(
			fallback&&
			/^\d{4}-\d{2}-\d{2}/.test(fallback)
		){
			return fallback.slice(0,10);
		}

		return new Date().toISOString().slice(0,10);
	}

	function uniqueValues(values:string[]){
		return[
			...new Set(
				values.filter(Boolean)
			)
		].sort((a,b)=>a.localeCompare(b,'id'));
	}


	function notify(
		message:string,
		type:'success'|'info'='success'
	){
		notification={
			type,
			message
		};

		if(notificationTimer){
			clearTimeout(notificationTimer);
		}

		notificationTimer=setTimeout(()=>{
			notification=null;
		},3000);
	}

	function openDetail(record:ArchiveRecord){
		detailRecord=record;
		detailOpen=true;
	}

	async function restoreArchive(record:ArchiveRecord){
		try{
			await restoreArchiveApi(record.source,record.sourceId);
			archiveRecords=archiveRecords.filter((item)=>item.id!==record.id);
			if(detailRecord?.id===record.id){detailOpen=false;detailRecord=null;}
			notify(record.source==='INCOMING'?'Surat masuk berhasil dipulihkan dari arsip.':'Surat keluar berhasil dipulihkan dari arsip.');
		}catch(error){notify(error instanceof Error?error.message:'Arsip gagal dipulihkan.','info');}
	}

	function requestDelete(record:ArchiveRecord){detailOpen=false;detailRecord=null;deleteTarget=record;}

	async function confirmDelete(){
		if(!deleteTarget)return;const target=deleteTarget;
		try{await deleteArchiveApi(target.source,target.sourceId);archiveRecords=archiveRecords.filter((item)=>item.id!==target.id);deleteTarget=null;notify('Dokumen arsip berhasil dihapus permanen.');}
		catch(error){deleteTarget=null;notify(error instanceof Error?error.message:'Arsip gagal dihapus.','info');}
	}

	function resetFilter(){
		search='';
		sourceFilter='ALL';
		categoryFilter='ALL';
		unitFilter='ALL';
		dateFilter='';
		currentPage=1;
	}

	function filterIncoming(){
		sourceFilter='INCOMING';
		currentPage=1;
	}

	function filterOutgoing(){
		sourceFilter='OUTGOING';
		currentPage=1;
	}
</script>

<svelte:head>
	<title>Arsip | SiPersurat</title>
</svelte:head>

<div class="w-full min-w-0 overflow-x-hidden">
	<div class="mx-auto w-full min-w-0 max-w-[1500px] px-5 py-7 md:px-8 lg:px-10">
		<div
			class="min-w-0"
			in:fly={{y:18,duration:450}}
		>
			<h1 class="text-2xl font-bold tracking-tight md:text-[28px]">
				Arsip
			</h1>

			<p class="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
				Kelola dokumen persuratan yang telah selesai diproses dan disimpan sebagai arsip digital. Arsip menggabungkan surat masuk dan surat keluar sehingga dokumen dapat ditemukan kembali dengan cepat berdasarkan nomor, perihal, klasifikasi, unit kerja, maupun tanggal pengarsipan.
			</p>
		</div>

		<section class="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
			<div in:fly={{y:22,duration:450,delay:100}}>
				<Card.Root class="group h-full rounded-xl border shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md">
					<Card.Content class="p-5">
						<div class="flex items-center justify-between gap-4">
							<p class="text-xs font-semibold">
								Total Arsip
							</p>

							<div class="flex size-8 items-center justify-center rounded-lg bg-muted text-muted-foreground transition group-hover:bg-foreground group-hover:text-background">
								<Archive class="size-4"/>
							</div>
						</div>

						<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
							{archiveRecords.length}
						</p>

						<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
							Seluruh dokumen yang tersimpan pada arsip digital.
						</p>
					</Card.Content>
				</Card.Root>
			</div>

			<div in:fly={{y:22,duration:450,delay:170}}>
				<button
					type="button"
					class="group h-full w-full text-left"
					onclick={filterIncoming}
				>
					<Card.Root class="h-full rounded-xl border shadow-none transition-all duration-200 group-hover:-translate-y-1 group-hover:border-blue-200 group-hover:shadow-md">
						<Card.Content class="p-5">
							<div class="flex items-center justify-between gap-4">
								<p class="text-xs font-semibold">
									Surat Masuk
								</p>

								<div class="flex size-8 items-center justify-center rounded-lg bg-blue-50 text-blue-700 transition group-hover:scale-110">
									<Inbox class="size-4"/>
								</div>
							</div>

							<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
								{incomingCount}
							</p>

							<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
								Surat masuk yang telah selesai dan diarsipkan.
							</p>
						</Card.Content>
					</Card.Root>
				</button>
			</div>

			<div in:fly={{y:22,duration:450,delay:240}}>
				<button
					type="button"
					class="group h-full w-full text-left"
					onclick={filterOutgoing}
				>
					<Card.Root class="h-full rounded-xl border shadow-none transition-all duration-200 group-hover:-translate-y-1 group-hover:border-violet-200 group-hover:shadow-md">
						<Card.Content class="p-5">
							<div class="flex items-center justify-between gap-4">
								<p class="text-xs font-semibold">
									Surat Keluar
								</p>

								<div class="flex size-8 items-center justify-center rounded-lg bg-violet-50 text-violet-700 transition group-hover:scale-110">
									<Send class="size-4"/>
								</div>
							</div>

							<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
								{outgoingCount}
							</p>

							<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
								Surat keluar yang telah dikirim dan diarsipkan.
							</p>
						</Card.Content>
					</Card.Root>
				</button>
			</div>

			<div in:fly={{y:22,duration:450,delay:310}}>
				<Card.Root class="group h-full rounded-xl border shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md">
					<Card.Content class="p-5">
						<div class="flex items-center justify-between gap-4">
							<p class="text-xs font-semibold">
								Arsip Bulan Ini
							</p>

							<div class="flex size-8 items-center justify-center rounded-lg bg-emerald-50 text-emerald-700 transition group-hover:scale-110">
								<CalendarCheck class="size-4"/>
							</div>
						</div>

						<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
							{monthCount}
						</p>

						<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
							Dokumen yang ditambahkan ke arsip pada bulan berjalan.
						</p>
					</Card.Content>
				</Card.Root>
			</div>
		</section>

		<div in:fly={{y:20,duration:450,delay:380}}>
			<Card.Root class="mt-6 rounded-xl border shadow-none">
				<Card.Content class="p-5">
					<div>
						<h2 class="text-sm font-semibold">
							Cari & Filter Arsip
						</h2>

						<p class="mt-1 text-xs leading-5 text-muted-foreground">
							Temukan dokumen berdasarkan nomor agenda, nomor surat, pengirim atau penerima, perihal, jenis dokumen, kategori, unit kerja, dan tanggal arsip.
						</p>
					</div>

					<div class="relative mt-4">
						<Search class="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

						<Input
							bind:value={search}
							placeholder="Cari nomor surat, pengirim, penerima, perihal, atau unit kerja..."
							class="h-11 rounded-lg pl-10 text-sm"
							oninput={()=>currentPage=1}
						/>
					</div>

					<div class="mt-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-[minmax(160px,.8fr)_minmax(170px,.9fr)_minmax(170px,.9fr)_minmax(170px,.8fr)_40px]">
						<div class="relative">
							<SlidersHorizontal class="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

							<select
								bind:value={sourceFilter}
								onchange={()=>currentPage=1}
								class="h-10 w-full rounded-lg border border-input bg-background pl-10 pr-3 text-xs font-medium outline-none transition focus:border-ring focus:ring-2 focus:ring-ring/20"
							>
								<option value="ALL">
									Semua Jenis
								</option>

								<option value="INCOMING">
									Surat Masuk
								</option>

								<option value="OUTGOING">
									Surat Keluar
								</option>
							</select>
						</div>

						<select
							bind:value={categoryFilter}
							onchange={()=>currentPage=1}
							class="h-10 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none transition focus:border-ring focus:ring-2 focus:ring-ring/20"
						>
							<option value="ALL">
								Semua Kategori
							</option>

							{#each categories as category}
								<option value={category}>
									{category}
								</option>
							{/each}
						</select>

						<select
							bind:value={unitFilter}
							onchange={()=>currentPage=1}
							class="h-10 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none transition focus:border-ring focus:ring-2 focus:ring-ring/20"
						>
							<option value="ALL">
								Semua Unit
							</option>

							{#each units as unit}
								<option value={unit}>
									{unit}
								</option>
							{/each}
						</select>

						<Input
							type="date"
							bind:value={dateFilter}
							onchange={()=>currentPage=1}
							class="h-10 rounded-lg text-xs"
						/>

						<div>
							{#if hasFilter}
								<Button
									type="button"
									variant="outline"
									size="icon"
									class="size-10 rounded-lg transition hover:-rotate-12"
									onclick={resetFilter}
								>
									<RotateCcw class="size-4"/>
								</Button>
							{/if}
						</div>
					</div>
				</Card.Content>
			</Card.Root>
		</div>

		<div in:fly={{y:24,duration:500,delay:460}}>
			<Card.Root class="mt-6 min-w-0 overflow-hidden rounded-xl border shadow-none">
				<Card.Header class="border-b px-5 py-5 sm:px-6">
					<div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
						<div>
							<Card.Title class="text-base font-semibold">
								Daftar Arsip
							</Card.Title>

							<Card.Description class="mt-1.5 max-w-2xl text-xs leading-5">
								Menampilkan seluruh surat masuk dan surat keluar yang telah selesai diproses dan disimpan sebagai arsip digital.
							</Card.Description>
						</div>

						<div class="w-fit rounded-md bg-muted px-3 py-1.5 text-[11px] font-medium text-muted-foreground">
							{filteredRecords.length} data
						</div>
					</div>
				</Card.Header>

				<ArchiveTable
					records={paginatedRecords}
					{currentPage}
					{totalPages}
					totalItems={filteredRecords.length}
					{perPage}
					onPageChange={(page)=>currentPage=page}
					onView={openDetail}
					onRestore={restoreArchive}
					onDelete={requestDelete}
				/>
			</Card.Root>
		</div>
	</div>
</div>

<ArchiveDetailModal
	open={detailOpen}
	record={detailRecord}
	onClose={()=>{
		detailOpen=false;
		detailRecord=null;
	}}
	onRestore={restoreArchive}
	onDelete={requestDelete}
/>

{#if deleteTarget}
	<div class="fixed inset-0 z-[280] flex items-center justify-center bg-black/35 p-4 backdrop-blur-md">
		<div
			class="w-full max-w-md overflow-hidden rounded-xl border bg-background shadow-2xl"
			in:fly={{y:15,duration:220}}
		>
			<div class="p-5">
				<div class="flex size-10 items-center justify-center rounded-lg bg-red-50 text-red-600">
					<TriangleAlert class="size-5"/>
				</div>

				<h2 class="mt-4 text-lg font-bold">
					Hapus arsip permanen?
				</h2>

				<p class="mt-1 text-sm leading-6 text-muted-foreground">
					Dokumen akan dihapus dari sumber data utama dan tidak dapat dipulihkan kembali dari halaman Arsip.
				</p>

				<div class="mt-4 rounded-lg border bg-muted/40 p-3.5">
					<div class="flex flex-wrap items-center gap-2">
						<p class="font-mono text-xs font-semibold text-blue-600">
							{deleteTarget.agendaNumber}
						</p>

						<span class="rounded-md bg-muted px-2 py-1 text-[10px] font-medium text-muted-foreground">
							{deleteTarget.source==='INCOMING'
								?'Surat Masuk'
								:'Surat Keluar'}
						</span>
					</div>

					<p class="mt-2 text-sm font-medium leading-5">
						{deleteTarget.subject}
					</p>
				</div>
			</div>

			<div class="flex justify-end gap-2 border-t p-4">
				<Button
					type="button"
					variant="outline"
					class="rounded-lg"
					onclick={()=>deleteTarget=null}
				>
					Batal
				</Button>

				<Button
					type="button"
					variant="destructive"
					class="rounded-lg"
					onclick={confirmDelete}
				>
					<Trash2 class="size-4"/>
					Hapus Permanen
				</Button>
			</div>
		</div>
	</div>
{/if}

{#if notification}
	<div
		class="fixed bottom-5 right-5 z-[300] flex w-[calc(100%-40px)] max-w-sm items-start gap-3 rounded-xl border bg-background p-4 shadow-xl"
		in:fly={{x:30,duration:250}}
	>
		<div
			class={[
				'flex size-9 shrink-0 items-center justify-center rounded-full',
				notification.type==='success'
					?'bg-emerald-50 text-emerald-600'
					:'bg-amber-50 text-amber-600'
			]}
		>
			{#if notification.type==='success'}
				<CircleCheck class="size-4"/>
			{:else}
				<TriangleAlert class="size-4"/>
			{/if}
		</div>

		<div class="min-w-0 flex-1">
			<p class="text-xs font-semibold">
				Informasi
			</p>

			<p class="mt-1 text-xs leading-5 text-muted-foreground">
				{notification.message}
			</p>
		</div>

		<button
			type="button"
			class="flex size-7 shrink-0 items-center justify-center rounded-md text-muted-foreground transition hover:bg-muted hover:text-foreground"
			aria-label="Tutup notifikasi"
			onclick={()=>notification=null}
		>
			<X class="size-4"/>
		</button>
	</div>
{/if}