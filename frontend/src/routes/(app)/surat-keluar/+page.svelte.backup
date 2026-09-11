<script lang="ts">
	import{browser}from'$app/environment';
	import{onMount}from'svelte';
	import{fly}from'svelte/transition';
	import*as Card from'$lib/components/ui/card/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import{Input}from'$lib/components/ui/input/index.js';
	import Plus from'@lucide/svelte/icons/plus';
	import Search from'@lucide/svelte/icons/search';
	import Send from'@lucide/svelte/icons/send';
	import FilePenLine from'@lucide/svelte/icons/file-pen-line';
	import Clock3 from'@lucide/svelte/icons/clock-3';
	import CircleCheck from'@lucide/svelte/icons/circle-check';
	import SlidersHorizontal from'@lucide/svelte/icons/sliders-horizontal';
	import RotateCcw from'@lucide/svelte/icons/rotate-ccw';
	import TriangleAlert from'@lucide/svelte/icons/triangle-alert';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import X from'@lucide/svelte/icons/x';
	import OutgoingMailFormModal from'$lib/features/surat-keluar/components/OutgoingMailFormModal.svelte';
	import OutgoingMailDetailModal from'$lib/features/surat-keluar/components/OutgoingMailDetailModal.svelte';
	import OutgoingMailTable from'$lib/features/surat-keluar/components/OutgoingMailTable.svelte';
	import type{MasterDataRecord}from'$lib/features/master-data/types';
	import type{
		OutgoingMailPayload,
		OutgoingMailRecord,
		OutgoingMailStatus
	}from'$lib/features/surat-keluar/types';
	import{
		OUTGOING_MAIL_STATUS_OPTIONS
	}from'$lib/features/surat-keluar/types';

	const STORAGE_KEY='sipersurat-outgoing-mail';
	const MASTER_STORAGE_KEY='sipersurat-master-data';
	const perPage=6;

	const fallbackCategories=[
		'Undangan',
		'Permohonan',
		'Pemberitahuan',
		'Internal',
		'Eksternal'
	];

	const fallbackPriorities=[
		'Biasa',
		'Penting',
		'Rahasia',
		'Sangat Rahasia'
	];

	const fallbackUnits=[
		'Administrator',
		'Tata Usaha',
		'Pimpinan',
		'Sekretariat',
		'Keuangan',
		'Kepegawaian',
		'Umum'
	];

	const defaultRecords:OutgoingMailRecord[]=[
		{
			id:1,
			agendaNumber:'SK-2026-0001',
			letterNumber:'001/SK/VIII/2026',
			letterDate:'2026-08-13',
			recipient:'Dinas Pendidikan Kabupaten',
			subject:'Penyampaian Hasil Koordinasi Program Kerja Tahun 2026',
			category:'Pemberitahuan',
			priority:'Biasa',
			sourceUnit:'Tata Usaha',
			signer:'Kepala Administrasi',
			notes:'',
			status:'SENT',
			sentDate:'2026-08-13',
			fileName:'hasil-koordinasi.pdf',
			fileType:'application/pdf',
			fileSize:415000,
			createdAt:'2026-08-13T08:10:00',
			updatedAt:'2026-08-13T10:30:00'
		},
		{
			id:2,
			agendaNumber:'SK-2026-0002',
			letterNumber:'002/SK/VIII/2026',
			letterDate:'2026-08-13',
			recipient:'BPKAD',
			subject:'Permohonan Data Rekonsiliasi Keuangan Semester I',
			category:'Permohonan',
			priority:'Penting',
			sourceUnit:'Keuangan',
			signer:'Kepala Bagian Keuangan',
			notes:'Menunggu persetujuan pimpinan.',
			status:'PENDING_APPROVAL',
			sentDate:'',
			fileName:'permohonan-rekonsiliasi.pdf',
			fileType:'application/pdf',
			fileSize:368000,
			createdAt:'2026-08-13T09:20:00',
			updatedAt:'2026-08-13T09:20:00'
		},
		{
			id:3,
			agendaNumber:'SK-2026-0003',
			letterNumber:'003/SK/VIII/2026',
			letterDate:'2026-08-12',
			recipient:'Sekretariat Daerah',
			subject:'Konfirmasi Kehadiran Evaluasi Kinerja Perangkat Daerah',
			category:'Eksternal',
			priority:'Biasa',
			sourceUnit:'Sekretariat',
			signer:'Sekretaris',
			notes:'',
			status:'APPROVED',
			sentDate:'',
			fileName:'konfirmasi-kehadiran.pdf',
			fileType:'application/pdf',
			fileSize:298000,
			createdAt:'2026-08-12T11:00:00',
			updatedAt:'2026-08-13T08:25:00'
		},
		{
			id:4,
			agendaNumber:'SK-2026-0004',
			letterNumber:'004/SK/VIII/2026',
			letterDate:'2026-08-12',
			recipient:'Inspektorat Daerah',
			subject:'Penyampaian Dokumen Tindak Lanjut Hasil Pemeriksaan',
			category:'Pemberitahuan',
			priority:'Rahasia',
			sourceUnit:'Pimpinan',
			signer:'Pimpinan',
			notes:'Dokumen bersifat terbatas.',
			status:'SENT',
			sentDate:'2026-08-12',
			fileName:'tindak-lanjut.pdf',
			fileType:'application/pdf',
			fileSize:715000,
			createdAt:'2026-08-12T08:30:00',
			updatedAt:'2026-08-12T16:15:00'
		},
		{
			id:5,
			agendaNumber:'SK-2026-0005',
			letterNumber:'005/SK/VIII/2026',
			letterDate:'2026-08-11',
			recipient:'Badan Kepegawaian Daerah',
			subject:'Permohonan Narasumber Sosialisasi Sistem Informasi Kepegawaian',
			category:'Permohonan',
			priority:'Biasa',
			sourceUnit:'Kepegawaian',
			signer:'Kepala Subbag Kepegawaian',
			notes:'',
			status:'DRAFT',
			sentDate:'',
			fileName:'',
			fileType:'',
			fileSize:0,
			createdAt:'2026-08-11T13:20:00',
			updatedAt:'2026-08-11T13:20:00'
		},
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
			id:7,
			agendaNumber:'SK-2026-0007',
			letterNumber:'007/SK/VIII/2026',
			letterDate:'2026-08-13',
			recipient:'Komisi Pemilihan Umum',
			subject:'Konfirmasi Ketersediaan Ruang Pertemuan Koordinasi',
			category:'Eksternal',
			priority:'Penting',
			sourceUnit:'Umum',
			signer:'Kepala Bagian Umum',
			notes:'',
			status:'REJECTED',
			sentDate:'',
			fileName:'konfirmasi-ruangan.pdf',
			fileType:'application/pdf',
			fileSize:285000,
			createdAt:'2026-08-13T11:30:00',
			updatedAt:'2026-08-13T12:15:00'
		}
	];

	let records=$state<OutgoingMailRecord[]>([...defaultRecords]);
	let masterData=$state<MasterDataRecord[]>([]);
	let initialized=$state(false);

	let search=$state('');
	let categoryFilter=$state('ALL');
	let priorityFilter=$state('ALL');
	let statusFilter=$state<'ALL'|OutgoingMailStatus>('ALL');
	let dateFilter=$state('');
	let currentPage=$state(1);

	let formOpen=$state(false);
	let formMode=$state<'create'|'edit'>('create');
	let selectedRecord=$state<OutgoingMailRecord|null>(null);

	let detailOpen=$state(false);
	let detailRecord=$state<OutgoingMailRecord|null>(null);

	let deleteTarget=$state<OutgoingMailRecord|null>(null);

	let notification=$state<{
		type:'success'|'info';
		message:string;
	}|null>(null);

	let notificationTimer:ReturnType<typeof setTimeout>|undefined;

	onMount(()=>{
		if(!browser)return;

		const saved=localStorage.getItem(STORAGE_KEY);

		if(saved){
			try{
				const parsed=JSON.parse(saved);

				if(Array.isArray(parsed)){
					records=parsed;
				}
			}catch{
				records=[...defaultRecords];
			}
		}

		const savedMaster=localStorage.getItem(MASTER_STORAGE_KEY);

		if(savedMaster){
			try{
				const parsed=JSON.parse(savedMaster);

				if(Array.isArray(parsed)){
					masterData=parsed;
				}
			}catch{
				masterData=[];
			}
		}

		initialized=true;
	});

	$effect(()=>{
		if(!browser||!initialized)return;

		localStorage.setItem(
			STORAGE_KEY,
			JSON.stringify(records)
		);
	});

	const categories=$derived.by(()=>{
		const items=masterData
			.filter((item)=>
				item.type==='KATEGORI_SURAT'&&
				item.status==='ACTIVE'
			)
			.map((item)=>item.name);

		return(items.length?items:fallbackCategories).map((value)=>({
			value,
			label:value
		}));
	});

	const priorities=$derived.by(()=>{
		const items=masterData
			.filter((item)=>
				item.type==='SIFAT_SURAT'&&
				item.status==='ACTIVE'
			)
			.map((item)=>item.name);

		return(items.length?items:fallbackPriorities).map((value)=>({
			value,
			label:value
		}));
	});

	const units=$derived.by(()=>{
		const items=masterData
			.filter((item)=>
				item.type==='UNIT_KERJA'&&
				item.status==='ACTIVE'
			)
			.map((item)=>item.name);

		return(items.length?items:fallbackUnits).map((value)=>({
			value,
			label:value
		}));
	});

	const filteredRecords=$derived(
		records.filter((record)=>{
			const keyword=search.trim().toLowerCase();

			const matchesSearch=
				!keyword||
				record.agendaNumber.toLowerCase().includes(keyword)||
				record.letterNumber.toLowerCase().includes(keyword)||
				record.recipient.toLowerCase().includes(keyword)||
				record.subject.toLowerCase().includes(keyword)||
				record.sourceUnit.toLowerCase().includes(keyword)||
				record.signer.toLowerCase().includes(keyword);

			const matchesCategory=
				categoryFilter==='ALL'||
				record.category===categoryFilter;

			const matchesPriority=
				priorityFilter==='ALL'||
				record.priority===priorityFilter;

			const matchesStatus=
				statusFilter==='ALL'||
				record.status===statusFilter;

			const matchesDate=
				!dateFilter||
				record.letterDate===dateFilter;

			return(
				matchesSearch&&
				matchesCategory&&
				matchesPriority&&
				matchesStatus&&
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

	const draftCount=$derived(
		records.filter((record)=>record.status==='DRAFT').length
	);

	const pendingCount=$derived(
		records.filter((record)=>record.status==='PENDING_APPROVAL').length
	);

	const sentCount=$derived(
		records.filter((record)=>
			record.status==='SENT'||
			record.status==='ARCHIVED'
		).length
	);

	const hasFilter=$derived(
		search.trim()!==''||
		categoryFilter!=='ALL'||
		priorityFilter!=='ALL'||
		statusFilter!=='ALL'||
		dateFilter!==''
	);

	const nextAgendaNumber=$derived(
		generateAgendaNumber(records)
	);

	$effect(()=>{
		if(currentPage>totalPages){
			currentPage=totalPages;
		}
	});

	function generateAgendaNumber(items:OutgoingMailRecord[]){
		const year=new Date().getFullYear();

		const max=items.reduce((highest,item)=>{
			const match=item.agendaNumber.match(
				/^SK-(\d{4})-(\d+)$/
			);

			if(
				!match||
				Number(match[1])!==year
			){
				return highest;
			}

			return Math.max(
				highest,
				Number(match[2])
			);
		},0);

		return`SK-${year}-${String(max+1).padStart(4,'0')}`;
	}

	function timestamp(){
		return new Date().toISOString();
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

	function openCreate(){
		formMode='create';
		selectedRecord=null;
		formOpen=true;
	}

	function openEdit(record:OutgoingMailRecord){
		detailOpen=false;
		detailRecord=null;
		formMode='edit';
		selectedRecord=record;
		formOpen=true;
	}

	function openDetail(record:OutgoingMailRecord){
		detailRecord=record;
		detailOpen=true;
	}

	function saveRecord(payload:OutgoingMailPayload){
		const duplicate=records.some((record)=>
			record.id!==selectedRecord?.id&&
			record.letterNumber.toLowerCase()===
			payload.letterNumber.toLowerCase()
		);

		if(duplicate){
			notify(
				'Nomor surat tersebut sudah terdaftar.',
				'info'
			);

			return;
		}

		const now=timestamp();

		if(formMode==='create'){
			const nextId=
				Math.max(
					0,
					...records.map((record)=>record.id)
				)+1;

			records=[
				{
					id:nextId,
					agendaNumber:nextAgendaNumber,
					...payload,
					createdAt:now,
					updatedAt:now
				},
				...records
			];

			notify(
				'Surat keluar berhasil ditambahkan.'
			);
		}else if(selectedRecord){
			const id=selectedRecord.id;

			records=records.map((record)=>
				record.id===id
					?{
						...record,
						...payload,
						updatedAt:now
					}
					:record
			);

			if(detailRecord?.id===id){
				detailRecord=
					records.find((record)=>
						record.id===id
					)??null;
			}

			notify(
				'Surat keluar berhasil diperbarui.'
			);
		}

		formOpen=false;
		selectedRecord=null;
		currentPage=1;
	}

	function changeStatus(
		record:OutgoingMailRecord,
		status:OutgoingMailStatus
	){
		if(record.status===status)return;

		const now=timestamp();
		const today=new Date().toISOString().slice(0,10);

		records=records.map((item)=>{
			if(item.id!==record.id)return item;

			return{
				...item,
				status,
				sentDate:
					status==='SENT'
						?(item.sentDate||today)
						:item.sentDate,
				updatedAt:now
			};
		});

		detailRecord=
			records.find((item)=>
				item.id===record.id
			)??null;

		if(status==='PENDING_APPROVAL'){
			notify('Surat diajukan untuk persetujuan.');
		}else if(status==='APPROVED'){
			notify('Surat berhasil disetujui.');
		}else if(status==='SENT'){
			notify('Surat ditandai telah dikirim.');
		}else if(status==='ARCHIVED'){
			notify('Surat berhasil diarsipkan.');
		}else if(status==='DRAFT'){
			notify('Surat dikembalikan ke Draft.');
		}else{
			notify('Status surat berhasil diperbarui.');
		}
	}

	function confirmDelete(){
		if(!deleteTarget)return;

		const id=deleteTarget.id;

		records=records.filter((record)=>
			record.id!==id
		);

		if(detailRecord?.id===id){
			detailOpen=false;
			detailRecord=null;
		}

		deleteTarget=null;

		notify(
			'Surat keluar berhasil dihapus.'
		);
	}

	function resetFilter(){
		search='';
		categoryFilter='ALL';
		priorityFilter='ALL';
		statusFilter='ALL';
		dateFilter='';
		currentPage=1;
	}
</script>

<svelte:head>
	<title>Surat Keluar | SiPersurat</title>
</svelte:head>

<div class="w-full min-w-0 overflow-x-hidden">
	<div class="mx-auto w-full min-w-0 max-w-[1500px] px-5 py-7 md:px-8 lg:px-10">
		<div class="flex flex-col justify-between gap-6 md:flex-row md:items-start">
			<div
				class="min-w-0"
				in:fly={{y:18,duration:450}}
			>
				<h1 class="text-2xl font-bold tracking-tight md:text-[28px]">
					Surat Keluar
				</h1>

				<p class="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
					Kelola seluruh surat yang dibuat dan diterbitkan oleh instansi, mulai dari penyusunan draft, proses persetujuan, penandatanganan, pengiriman kepada penerima, hingga pengarsipan dokumen secara terpusat.
				</p>
			</div>

			<div
				class="shrink-0"
				in:fly={{y:14,duration:420,delay:100}}
			>
				<Button
					class="h-10 w-full rounded-lg px-4 text-xs font-semibold transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md md:w-auto"
					onclick={openCreate}
				>
					<Plus class="size-4"/>
					Tambah Surat Keluar
				</Button>
			</div>
		</div>

		<section class="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
			<div in:fly={{y:22,duration:450,delay:100}}>
				<Card.Root class="group h-full rounded-xl border shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md">
					<Card.Content class="p-5">
						<div class="flex items-center justify-between">
							<p class="text-xs font-semibold">
								Total Surat Keluar
							</p>

							<div class="flex size-8 items-center justify-center rounded-lg bg-muted text-muted-foreground transition group-hover:bg-foreground group-hover:text-background">
								<Send class="size-4"/>
							</div>
						</div>

						<p class="mt-7 text-[28px] font-bold leading-none">
							{records.length}
						</p>

						<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
							Seluruh surat keluar yang tercatat pada sistem.
						</p>
					</Card.Content>
				</Card.Root>
			</div>

			<div in:fly={{y:22,duration:450,delay:170}}>
				<Card.Root class="group h-full rounded-xl border shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md">
					<Card.Content class="p-5">
						<div class="flex items-center justify-between">
							<p class="text-xs font-semibold">
								Draft
							</p>

							<div class="flex size-8 items-center justify-center rounded-lg bg-zinc-100 text-zinc-700 transition group-hover:scale-110">
								<FilePenLine class="size-4"/>
							</div>
						</div>

						<p class="mt-7 text-[28px] font-bold leading-none">
							{draftCount}
						</p>

						<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
							Surat yang masih dalam tahap penyusunan.
						</p>
					</Card.Content>
				</Card.Root>
			</div>

			<div in:fly={{y:22,duration:450,delay:240}}>
				<Card.Root class="group h-full rounded-xl border shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md">
					<Card.Content class="p-5">
						<div class="flex items-center justify-between">
							<p class="text-xs font-semibold">
								Menunggu Persetujuan
							</p>

							<div class="flex size-8 items-center justify-center rounded-lg bg-amber-50 text-amber-700 transition group-hover:scale-110">
								<Clock3 class="size-4"/>
							</div>
						</div>

						<p class="mt-7 text-[28px] font-bold leading-none">
							{pendingCount}
						</p>

						<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
							Surat yang menunggu persetujuan pejabat terkait.
						</p>
					</Card.Content>
				</Card.Root>
			</div>

			<div in:fly={{y:22,duration:450,delay:310}}>
				<Card.Root class="group h-full rounded-xl border shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md">
					<Card.Content class="p-5">
						<div class="flex items-center justify-between">
							<p class="text-xs font-semibold">
								Sudah Dikirim
							</p>

							<div class="flex size-8 items-center justify-center rounded-lg bg-emerald-50 text-emerald-700 transition group-hover:scale-110">
								<CircleCheck class="size-4"/>
							</div>
						</div>

						<p class="mt-7 text-[28px] font-bold leading-none">
							{sentCount}
						</p>

						<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
							Surat yang telah dikirim atau sudah diarsipkan.
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
							Cari & Filter Surat
						</h2>

						<p class="mt-1 text-xs leading-5 text-muted-foreground">
							Temukan surat berdasarkan agenda, nomor surat, tujuan, perihal, penandatangan, unit kerja, klasifikasi, status, atau tanggal.
						</p>
					</div>

					<div class="relative mt-4">
						<Search class="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

						<Input
							bind:value={search}
							placeholder="Cari nomor surat, tujuan, perihal, atau unit kerja..."
							class="h-11 rounded-lg pl-10 text-sm"
							oninput={()=>currentPage=1}
						/>
					</div>

					<div class="mt-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-[minmax(180px,1fr)_minmax(150px,.8fr)_minmax(180px,.9fr)_minmax(170px,.85fr)_40px]">
						<div class="relative">
							<SlidersHorizontal class="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

							<select
								bind:value={categoryFilter}
								onchange={()=>currentPage=1}
								class="h-10 w-full rounded-lg border border-input bg-background pl-10 pr-3 text-xs font-medium outline-none"
							>
								<option value="ALL">Semua Kategori</option>

								{#each categories as option}
									<option value={option.value}>
										{option.label}
									</option>
								{/each}
							</select>
						</div>

						<select
							bind:value={priorityFilter}
							onchange={()=>currentPage=1}
							class="h-10 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none"
						>
							<option value="ALL">
								Semua Sifat
							</option>

							{#each priorities as option}
								<option value={option.value}>
									{option.label}
								</option>
							{/each}
						</select>

						<select
							bind:value={statusFilter}
							onchange={()=>currentPage=1}
							class="h-10 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none"
						>
							<option value="ALL">
								Semua Status
							</option>

							{#each OUTGOING_MAIL_STATUS_OPTIONS as option}
								<option value={option.value}>
									{option.label}
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
								Daftar Surat Keluar
							</Card.Title>

							<Card.Description class="mt-1.5 max-w-2xl text-xs leading-5">
								Menampilkan seluruh surat yang sedang disusun, menunggu persetujuan, telah disetujui, dikirim, ditolak, maupun diarsipkan.
							</Card.Description>
						</div>

						<div class="w-fit rounded-md bg-muted px-3 py-1.5 text-[11px] font-medium text-muted-foreground">
							{filteredRecords.length} data
						</div>
					</div>
				</Card.Header>

				<OutgoingMailTable
					records={paginatedRecords}
					{currentPage}
					{totalPages}
					totalItems={filteredRecords.length}
					{perPage}
					onPageChange={(page)=>currentPage=page}
					onView={openDetail}
					onEdit={openEdit}
					onDelete={(record)=>deleteTarget=record}
				/>
			</Card.Root>
		</div>
	</div>
</div>

<OutgoingMailFormModal
	open={formOpen}
	mode={formMode}
	record={selectedRecord}
	agendaNumber={nextAgendaNumber}
	{categories}
	{priorities}
	{units}
	onSave={saveRecord}
	onOpenChange={(value)=>{
		formOpen=value;

		if(!value){
			selectedRecord=null;
		}
	}}
/>

<OutgoingMailDetailModal
	open={detailOpen}
	record={detailRecord}
	onClose={()=>{
		detailOpen=false;
		detailRecord=null;
	}}
	onEdit={openEdit}
	onStatusChange={changeStatus}
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
					Hapus surat keluar?
				</h2>

				<p class="mt-1 text-sm leading-6 text-muted-foreground">
					Data surat akan dihapus secara permanen dari daftar Surat Keluar.
				</p>

				<div class="mt-4 rounded-lg border bg-muted/40 p-3.5">
					<p class="font-mono text-xs font-semibold text-blue-600">
						{deleteTarget.agendaNumber}
					</p>

					<p class="mt-1.5 text-sm font-medium leading-5">
						{deleteTarget.subject}
					</p>
				</div>
			</div>

			<div class="flex justify-end gap-2 border-t p-4">
				<Button
					variant="outline"
					class="rounded-lg"
					onclick={()=>deleteTarget=null}
				>
					Batal
				</Button>

				<Button
					variant="destructive"
					class="rounded-lg"
					onclick={confirmDelete}
				>
					<Trash2 class="size-4"/>
					Hapus
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
			class={notification.type==='success'
				?'flex size-9 shrink-0 items-center justify-center rounded-full bg-emerald-50 text-emerald-600'
				:'flex size-9 shrink-0 items-center justify-center rounded-full bg-amber-50 text-amber-600'}
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
			class="flex size-7 shrink-0 items-center justify-center rounded-md text-muted-foreground hover:bg-muted"
			onclick={()=>notification=null}
		>
			<X class="size-4"/>
		</button>
	</div>
{/if}