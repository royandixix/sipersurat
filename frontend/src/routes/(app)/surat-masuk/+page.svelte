<script lang="ts">
	import{browser}from'$app/environment';
	import{onMount}from'svelte';
	import{fly}from'svelte/transition';
	import*as Card from'$lib/components/ui/card/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import{Input}from'$lib/components/ui/input/index.js';
	import Search from'@lucide/svelte/icons/search';
	import Plus from'@lucide/svelte/icons/plus';
	import Inbox from'@lucide/svelte/icons/inbox';
	import CalendarCheck from'@lucide/svelte/icons/calendar-check';
	import Forward from'@lucide/svelte/icons/forward';
	import CircleCheck from'@lucide/svelte/icons/circle-check';
	import RotateCcw from'@lucide/svelte/icons/rotate-ccw';
	import SlidersHorizontal from'@lucide/svelte/icons/sliders-horizontal';
	import X from'@lucide/svelte/icons/x';
	import TriangleAlert from'@lucide/svelte/icons/triangle-alert';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import IncomingMailFormModal from'$lib/features/surat-masuk/components/IncomingMailFormModal.svelte';
	import IncomingMailDetailModal from'$lib/features/surat-masuk/components/IncomingMailDetailModal.svelte';
	import IncomingMailTable from'$lib/features/surat-masuk/components/IncomingMailTable.svelte';
	import type{MasterDataRecord}from'$lib/features/master-data/types';
	import type{IncomingMailPayload,IncomingMailRecord,IncomingMailStatus}from'$lib/features/surat-masuk/types';
	import{INCOMING_MAIL_STATUS_OPTIONS}from'$lib/features/surat-masuk/types';

	const STORAGE_KEY='sipersurat-incoming-mail';
	const MASTER_STORAGE_KEY='sipersurat-master-data';
	const perPage=6;

	const defaultRecords:IncomingMailRecord[]=[
		{id:1,agendaNumber:'SM-2026-0001',letterNumber:'005/UND/VIII/2026',letterDate:'2026-08-12',receivedDate:'2026-08-13',sender:'Dinas Pendidikan Kabupaten',subject:'Undangan Rapat Koordinasi Program Kerja Tahun 2026',category:'Undangan',priority:'Penting',targetUnit:'Tata Usaha',notes:'Mohon diteruskan kepada pimpinan untuk disposisi.',status:'PENDING_DISPOSITION',fileName:'undangan-rapat-koordinasi.pdf',fileType:'application/pdf',fileSize:428000,createdAt:'13 Agu 2026, 08:35',updatedAt:'13 Agu 2026, 08:35'},
		{id:2,agendaNumber:'SM-2026-0002',letterNumber:'117/DPK/VIII/2026',letterDate:'2026-08-11',receivedDate:'2026-08-13',sender:'Dinas Pekerjaan Umum',subject:'Permohonan Data Pendukung Kegiatan Infrastruktur',category:'Permohonan',priority:'Biasa',targetUnit:'Sekretariat',notes:'',status:'RECEIVED',fileName:'permohonan-data.pdf',fileType:'application/pdf',fileSize:312000,createdAt:'13 Agu 2026, 09:05',updatedAt:'13 Agu 2026, 09:05'},
		{id:3,agendaNumber:'SM-2026-0003',letterNumber:'021/BPKAD/VIII/2026',letterDate:'2026-08-10',receivedDate:'2026-08-12',sender:'BPKAD',subject:'Pemberitahuan Rekonsiliasi Laporan Keuangan Semester I',category:'Pemberitahuan',priority:'Penting',targetUnit:'Keuangan',notes:'Jadwal rekonsiliasi terlampir.',status:'DISPOSITIONED',fileName:'rekonsiliasi-keuangan.pdf',fileType:'application/pdf',fileSize:516000,createdAt:'12 Agu 2026, 10:20',updatedAt:'12 Agu 2026, 13:10'},
		{id:4,agendaNumber:'SM-2026-0004',letterNumber:'045/SET/VIII/2026',letterDate:'2026-08-09',receivedDate:'2026-08-12',sender:'Sekretariat Daerah',subject:'Penyampaian Jadwal Evaluasi Kinerja Perangkat Daerah',category:'Pemberitahuan',priority:'Biasa',targetUnit:'Pimpinan',notes:'',status:'IN_PROGRESS',fileName:'jadwal-evaluasi.pdf',fileType:'application/pdf',fileSize:287000,createdAt:'12 Agu 2026, 11:40',updatedAt:'13 Agu 2026, 08:10'},
		{id:5,agendaNumber:'SM-2026-0005',letterNumber:'088/INS/VIII/2026',letterDate:'2026-08-08',receivedDate:'2026-08-11',sender:'Inspektorat Daerah',subject:'Permintaan Dokumen Tindak Lanjut Hasil Pemeriksaan',category:'Permohonan',priority:'Rahasia',targetUnit:'Pimpinan',notes:'Dokumen bersifat terbatas.',status:'COMPLETED',fileName:'permintaan-dokumen.pdf',fileType:'application/pdf',fileSize:624000,createdAt:'11 Agu 2026, 09:15',updatedAt:'12 Agu 2026, 16:30'},
		{id:6,agendaNumber:'SM-2026-0006',letterNumber:'031/BKD/VIII/2026',letterDate:'2026-08-07',receivedDate:'2026-08-11',sender:'Badan Kepegawaian Daerah',subject:'Undangan Sosialisasi Sistem Informasi Kepegawaian',category:'Undangan',priority:'Biasa',targetUnit:'Kepegawaian',notes:'',status:'ARCHIVED',fileName:'sosialisasi-simpeg.pdf',fileType:'application/pdf',fileSize:352000,createdAt:'11 Agu 2026, 10:25',updatedAt:'12 Agu 2026, 15:15'},
		{id:7,agendaNumber:'SM-2026-0007',letterNumber:'145/DINKES/VIII/2026',letterDate:'2026-08-12',receivedDate:'2026-08-13',sender:'Dinas Kesehatan',subject:'Pemberitahuan Pelaksanaan Pemeriksaan Kesehatan Berkala',category:'Pemberitahuan',priority:'Biasa',targetUnit:'Umum',notes:'',status:'PENDING_DISPOSITION',fileName:'pemeriksaan-kesehatan.pdf',fileType:'application/pdf',fileSize:405000,createdAt:'13 Agu 2026, 10:10',updatedAt:'13 Agu 2026, 10:10'},
		{id:8,agendaNumber:'SM-2026-0008',letterNumber:'072/KPU/VIII/2026',letterDate:'2026-08-12',receivedDate:'2026-08-13',sender:'Komisi Pemilihan Umum',subject:'Permohonan Fasilitasi Ruang Pertemuan Koordinasi',category:'Permohonan',priority:'Penting',targetUnit:'Umum',notes:'',status:'RECEIVED',fileName:'permohonan-fasilitas.pdf',fileType:'application/pdf',fileSize:298000,createdAt:'13 Agu 2026, 10:45',updatedAt:'13 Agu 2026, 10:45'},
		{id:9,agendaNumber:'SM-2026-0009',letterNumber:'014/PRO/VIII/2026',letterDate:'2026-08-12',receivedDate:'2026-08-13',sender:'Bagian Protokol',subject:'Undangan Rapat Persiapan Kegiatan Kenegaraan',category:'Internal',priority:'Sangat Rahasia',targetUnit:'Pimpinan',notes:'Distribusi dokumen dibatasi.',status:'PENDING_DISPOSITION',fileName:'undangan-protokol.pdf',fileType:'application/pdf',fileSize:551000,createdAt:'13 Agu 2026, 11:05',updatedAt:'13 Agu 2026, 11:05'}
	];

	const fallbackCategories=['Undangan','Permohonan','Pemberitahuan','Internal','Eksternal'];
	const fallbackPriorities=['Biasa','Penting','Rahasia','Sangat Rahasia'];
	const fallbackUnits=['Administrator','Tata Usaha','Pimpinan','Sekretariat','Keuangan','Umum','Kepegawaian'];

	let records=$state<IncomingMailRecord[]>([...defaultRecords]);
	let masterData=$state<MasterDataRecord[]>([]);
	let initialized=$state(false);
	let search=$state('');
	let categoryFilter=$state('ALL');
	let priorityFilter=$state('ALL');
	let statusFilter=$state<'ALL'|IncomingMailStatus>('ALL');
	let dateFilter=$state('');
	let currentPage=$state(1);
	let formOpen=$state(false);
	let formMode=$state<'create'|'edit'>('create');
	let selectedRecord=$state<IncomingMailRecord|null>(null);
	let detailOpen=$state(false);
	let detailRecord=$state<IncomingMailRecord|null>(null);
	let deleteTarget=$state<IncomingMailRecord|null>(null);
	let notification=$state<{type:'success'|'info';message:string}|null>(null);
	let notificationTimer:ReturnType<typeof setTimeout>|undefined;

	onMount(()=>{
		if(!browser)return;

		const saved=localStorage.getItem(STORAGE_KEY);

		if(saved){
			try{
				const parsed=JSON.parse(saved);
				if(Array.isArray(parsed))records=parsed;
			}catch{
				records=[...defaultRecords];
			}
		}

		const savedMaster=localStorage.getItem(MASTER_STORAGE_KEY);

		if(savedMaster){
			try{
				const parsed=JSON.parse(savedMaster);
				if(Array.isArray(parsed))masterData=parsed;
			}catch{
				masterData=[];
			}
		}

		initialized=true;
	});

	$effect(()=>{
		if(!browser||!initialized)return;
		localStorage.setItem(STORAGE_KEY,JSON.stringify(records));
	});

	const categories=$derived.by(()=>{
		const items=masterData
			.filter((item)=>item.type==='KATEGORI_SURAT'&&item.status==='ACTIVE')
			.map((item)=>item.name);

		return(items.length?items:fallbackCategories).map((value)=>({
			value,
			label:value
		}));
	});

	const priorities=$derived.by(()=>{
		const items=masterData
			.filter((item)=>item.type==='SIFAT_SURAT'&&item.status==='ACTIVE')
			.map((item)=>item.name);

		return(items.length?items:fallbackPriorities).map((value)=>({
			value,
			label:value
		}));
	});

	const units=$derived.by(()=>{
		const items=masterData
			.filter((item)=>item.type==='UNIT_KERJA'&&item.status==='ACTIVE')
			.map((item)=>item.name);

		return(items.length?items:fallbackUnits).map((value)=>({
			value,
			label:value
		}));
	});

	const filteredRecords=$derived(records.filter((record)=>{
		const keyword=search.trim().toLowerCase();

		const matchesSearch=
			!keyword||
			record.agendaNumber.toLowerCase().includes(keyword)||
			record.letterNumber.toLowerCase().includes(keyword)||
			record.sender.toLowerCase().includes(keyword)||
			record.subject.toLowerCase().includes(keyword)||
			record.targetUnit.toLowerCase().includes(keyword);

		const matchesCategory=categoryFilter==='ALL'||record.category===categoryFilter;
		const matchesPriority=priorityFilter==='ALL'||record.priority===priorityFilter;
		const matchesStatus=statusFilter==='ALL'||record.status===statusFilter;
		const matchesDate=!dateFilter||record.receivedDate===dateFilter;

		return matchesSearch&&matchesCategory&&matchesPriority&&matchesStatus&&matchesDate;
	}));

	const totalPages=$derived(Math.max(1,Math.ceil(filteredRecords.length/perPage)));

	const paginatedRecords=$derived(
		filteredRecords.slice((currentPage-1)*perPage,currentPage*perPage)
	);

	const todayValue=$derived(new Date().toISOString().slice(0,10));

	const todayCount=$derived(
		records.filter((record)=>record.receivedDate===todayValue).length
	);

	const pendingCount=$derived(
		records.filter((record)=>record.status==='PENDING_DISPOSITION').length
	);

	const completedCount=$derived(
		records.filter((record)=>record.status==='COMPLETED'||record.status==='ARCHIVED').length
	);

	const hasFilter=$derived(
		search.trim()!==''||
		categoryFilter!=='ALL'||
		priorityFilter!=='ALL'||
		statusFilter!=='ALL'||
		dateFilter!==''
	);

	const nextAgendaNumber=$derived(generateAgendaNumber(records));

	$effect(()=>{
		if(currentPage>totalPages)currentPage=totalPages;
	});

	function generateAgendaNumber(items:IncomingMailRecord[]){
		const year=new Date().getFullYear();

		const max=items.reduce((highest,item)=>{
			const match=item.agendaNumber.match(/^SM-(\d{4})-(\d+)$/);

			if(!match||Number(match[1])!==year)return highest;

			return Math.max(highest,Number(match[2]));
		},0);

		return`SM-${year}-${String(max+1).padStart(4,'0')}`;
	}

	function timestamp(){
		return new Intl.DateTimeFormat('id-ID',{
			day:'2-digit',
			month:'short',
			year:'numeric',
			hour:'2-digit',
			minute:'2-digit'
		}).format(new Date()).replace(' pukul ',', ');
	}

	function notify(message:string,type:'success'|'info'='success'){
		notification={type,message};

		if(notificationTimer)clearTimeout(notificationTimer);

		notificationTimer=setTimeout(()=>{
			notification=null;
		},3000);
	}

	function openCreate(){
		formMode='create';
		selectedRecord=null;
		formOpen=true;
	}

	function openEdit(record:IncomingMailRecord){
		detailOpen=false;
		detailRecord=null;
		formMode='edit';
		selectedRecord=record;
		formOpen=true;
	}

	function openDetail(record:IncomingMailRecord){
		detailRecord=record;
		detailOpen=true;
	}

	function saveRecord(payload:IncomingMailPayload){
		const duplicate=records.some((record)=>
			record.id!==selectedRecord?.id&&
			record.letterNumber.toLowerCase()===payload.letterNumber.toLowerCase()
		);

		if(duplicate){
			notify('Nomor surat tersebut sudah terdaftar.','info');
			return;
		}

		const now=timestamp();

		if(formMode==='create'){
			const nextId=Math.max(0,...records.map((record)=>record.id))+1;

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

			notify('Surat masuk berhasil ditambahkan.');
		}else if(selectedRecord){
			const id=selectedRecord.id;

			records=records.map((record)=>
				record.id===id
					?{...record,...payload,updatedAt:now}
					:record
			);

			if(detailRecord?.id===id){
				detailRecord=records.find((record)=>record.id===id)??null;
			}

			notify('Surat masuk berhasil diperbarui.');
		}

		formOpen=false;
		selectedRecord=null;
		currentPage=1;
	}

	function changeStatus(record:IncomingMailRecord,status:IncomingMailStatus){
		records=records.map((item)=>
			item.id===record.id
				?{...item,status,updatedAt:timestamp()}
				:item
		);

		detailRecord=records.find((item)=>item.id===record.id)??null;

		if(status==='ARCHIVED'){
			notify('Surat berhasil diarsipkan.');
		}else if(status==='COMPLETED'){
			notify('Surat ditandai selesai.');
		}else{
			notify('Status surat berhasil diperbarui.');
		}
	}

	function archiveRecord(record:IncomingMailRecord){
		changeStatus(record,'ARCHIVED');
	}

	function confirmDelete(){
		if(!deleteTarget)return;

		const id=deleteTarget.id;

		records=records.filter((record)=>record.id!==id);

		if(detailRecord?.id===id){
			detailOpen=false;
			detailRecord=null;
		}

		deleteTarget=null;

		notify('Surat masuk berhasil dihapus.');
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
	<title>Surat Masuk | SiPersurat</title>
</svelte:head>

<div class="w-full min-w-0 overflow-x-hidden">
	<div class="mx-auto w-full min-w-0 max-w-[1500px] px-5 py-7 md:px-8 lg:px-10">
		<div class="flex flex-col justify-between gap-6 md:flex-row md:items-start">
			<div
				class="min-w-0"
				in:fly={{y:18,duration:450}}
			>
				<h1 class="text-2xl font-bold tracking-tight md:text-[28px]">
					Surat Masuk
				</h1>

				<p class="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
					Kelola pencatatan, klasifikasi, dokumen, dan proses tindak lanjut seluruh surat yang diterima oleh instansi secara terpusat. Pantau status surat mulai dari diterima, menunggu disposisi, diproses, hingga selesai dan diarsipkan.
				</p>
			</div>

			<div
				class="shrink-0"
				in:fly={{y:14,duration:420,delay:100}}
			>
				<Button
					type="button"
					class="h-10 w-full rounded-lg px-4 text-xs font-semibold transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md md:w-auto"
					onclick={openCreate}
				>
					<Plus class="size-4"/>
					Tambah Surat Masuk
				</Button>
			</div>
		</div>

		<section class="mt-8 grid min-w-0 gap-4 sm:grid-cols-2 xl:grid-cols-4">
			<div in:fly={{y:22,duration:450,delay:100}}>
				<Card.Root class="group h-full min-w-0 rounded-xl border shadow-none transition-all duration-200 hover:-translate-y-1 hover:border-foreground/20 hover:shadow-md">
					<Card.Content class="p-5">
						<div class="flex items-center justify-between gap-4">
							<p class="text-xs font-semibold">
								Total Surat
							</p>

							<div class="flex size-8 items-center justify-center rounded-lg bg-muted text-muted-foreground transition-all duration-200 group-hover:bg-foreground group-hover:text-background">
								<Inbox class="size-4"/>
							</div>
						</div>

						<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
							{records.length}
						</p>

						<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
							Seluruh surat masuk yang telah tercatat pada sistem.
						</p>
					</Card.Content>
				</Card.Root>
			</div>

			<div in:fly={{y:22,duration:450,delay:170}}>
				<Card.Root class="group h-full min-w-0 rounded-xl border shadow-none transition-all duration-200 hover:-translate-y-1 hover:border-foreground/20 hover:shadow-md">
					<Card.Content class="p-5">
						<div class="flex items-center justify-between gap-4">
							<p class="text-xs font-semibold">
								Hari Ini
							</p>

							<div class="flex size-8 items-center justify-center rounded-lg bg-blue-50 text-blue-600 transition-all duration-200 group-hover:scale-110">
								<CalendarCheck class="size-4"/>
							</div>
						</div>

						<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
							{todayCount}
						</p>

						<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
							Surat baru yang diterima dan dicatat pada hari ini.
						</p>
					</Card.Content>
				</Card.Root>
			</div>

			<div in:fly={{y:22,duration:450,delay:240}}>
				<Card.Root class="group h-full min-w-0 rounded-xl border shadow-none transition-all duration-200 hover:-translate-y-1 hover:border-foreground/20 hover:shadow-md">
					<Card.Content class="p-5">
						<div class="flex items-center justify-between gap-4">
							<p class="text-xs font-semibold">
								Perlu Disposisi
							</p>

							<div class="flex size-8 items-center justify-center rounded-lg bg-amber-50 text-amber-700 transition-all duration-200 group-hover:scale-110">
								<Forward class="size-4"/>
							</div>
						</div>

						<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
							{pendingCount}
						</p>

						<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
							Surat yang masih menunggu arahan atau disposisi pimpinan.
						</p>
					</Card.Content>
				</Card.Root>
			</div>

			<div in:fly={{y:22,duration:450,delay:310}}>
				<Card.Root class="group h-full min-w-0 rounded-xl border shadow-none transition-all duration-200 hover:-translate-y-1 hover:border-foreground/20 hover:shadow-md">
					<Card.Content class="p-5">
						<div class="flex items-center justify-between gap-4">
							<p class="text-xs font-semibold">
								Selesai
							</p>

							<div class="flex size-8 items-center justify-center rounded-lg bg-emerald-50 text-emerald-600 transition-all duration-200 group-hover:scale-110">
								<CircleCheck class="size-4"/>
							</div>
						</div>

						<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
							{completedCount}
						</p>

						<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
							Surat yang proses tindak lanjutnya telah selesai atau diarsipkan.
						</p>
					</Card.Content>
				</Card.Root>
			</div>
		</section>

		<div in:fly={{y:20,duration:450,delay:380}}>
			<Card.Root class="mt-6 min-w-0 rounded-xl border shadow-none transition-shadow duration-200 hover:shadow-sm">
				<Card.Content class="p-5">
					<div class="flex min-w-0 flex-col gap-4">
						<div>
							<h2 class="text-sm font-semibold">
								Cari & Filter Surat
							</h2>

							<p class="mt-1 text-xs leading-5 text-muted-foreground">
								Temukan surat berdasarkan nomor, pengirim, perihal, kategori, sifat, status, unit kerja, atau tanggal diterima.
							</p>
						</div>

						<div class="relative min-w-0">
							<Search class="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

							<Input
								bind:value={search}
								placeholder="Cari nomor surat, pengirim, perihal, atau unit kerja..."
								class="h-11 w-full rounded-lg pl-10 text-sm transition-shadow focus:shadow-sm"
								oninput={()=>currentPage=1}
							/>
						</div>

						<div class="grid min-w-0 gap-3 sm:grid-cols-2 xl:grid-cols-[minmax(180px,1fr)_minmax(150px,.8fr)_minmax(170px,.9fr)_minmax(170px,.85fr)_40px]">
							<div class="relative min-w-0">
								<SlidersHorizontal class="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

								<select
									bind:value={categoryFilter}
									onchange={()=>currentPage=1}
									class="h-10 w-full min-w-0 rounded-lg border border-input bg-background pl-10 pr-8 text-xs font-medium outline-none transition-all duration-200 hover:border-foreground/30 focus:border-ring focus:ring-2 focus:ring-ring/20"
								>
									<option value="ALL">
										Semua Kategori
									</option>

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
								class="h-10 w-full min-w-0 rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none transition-all duration-200 hover:border-foreground/30 focus:border-ring focus:ring-2 focus:ring-ring/20"
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
								class="h-10 w-full min-w-0 rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none transition-all duration-200 hover:border-foreground/30 focus:border-ring focus:ring-2 focus:ring-ring/20"
							>
								<option value="ALL">
									Semua Status
								</option>

								{#each INCOMING_MAIL_STATUS_OPTIONS as option}
									<option value={option.value}>
										{option.label}
									</option>
								{/each}
							</select>

							<Input
								type="date"
								bind:value={dateFilter}
								onchange={()=>currentPage=1}
								class="h-10 w-full min-w-0 rounded-lg text-xs"
							/>

							<div class="flex">
								{#if hasFilter}
									<Button
										type="button"
										variant="outline"
										size="icon"
										class="size-10 rounded-lg transition-all duration-200 hover:-rotate-12"
										onclick={resetFilter}
									>
										<RotateCcw class="size-4"/>
									</Button>
								{:else}
									<div class="hidden size-10 xl:block"></div>
								{/if}
							</div>
						</div>
					</div>
				</Card.Content>
			</Card.Root>
		</div>

		<div in:fly={{y:24,duration:500,delay:460}}>
			<Card.Root class="mt-6 min-w-0 overflow-hidden rounded-xl border shadow-none transition-shadow duration-200 hover:shadow-sm">
				<Card.Header class="border-b px-5 py-5 sm:px-6">
					<div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
						<div class="min-w-0">
							<Card.Title class="text-base font-semibold">
								Daftar Surat Masuk
							</Card.Title>

							<Card.Description class="mt-1.5 max-w-2xl text-xs leading-5">
								Menampilkan seluruh surat yang telah diterima beserta informasi pengirim, klasifikasi, sifat surat, status pemrosesan, dan unit kerja tujuan.
							</Card.Description>
						</div>

						<div class="w-fit shrink-0 rounded-md bg-muted px-3 py-1.5 text-[11px] font-medium text-muted-foreground">
							{filteredRecords.length} data
						</div>
					</div>
				</Card.Header>

				<div class="min-w-0">
					<IncomingMailTable
						records={paginatedRecords}
						{currentPage}
						{totalPages}
						totalItems={filteredRecords.length}
						{perPage}
						onPageChange={(page)=>currentPage=page}
						onView={openDetail}
						onEdit={openEdit}
						onArchive={archiveRecord}
						onDelete={(record)=>deleteTarget=record}
					/>
				</div>
			</Card.Root>
		</div>
	</div>
</div>

<IncomingMailFormModal
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
		if(!value)selectedRecord=null;
	}}
/>

<IncomingMailDetailModal
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
	<div class="fixed inset-0 z-[250] flex items-center justify-center bg-black/40 p-4 backdrop-blur-sm">
		<div
			class="w-full max-w-md overflow-hidden rounded-xl border bg-background shadow-2xl"
			in:fly={{y:15,duration:220}}
		>
			<div class="p-5">
				<div class="flex size-10 items-center justify-center rounded-lg bg-red-50 text-red-600">
					<TriangleAlert class="size-5"/>
				</div>

				<h2 class="mt-4 text-lg font-bold">
					Hapus surat masuk?
				</h2>

				<p class="mt-1 text-sm leading-6 text-muted-foreground">
					Data surat akan dihapus dari daftar Surat Masuk dan tidak lagi ditampilkan pada halaman ini.
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
			onclick={()=>notification=null}
		>
			<X class="size-4"/>
		</button>
	</div>
{/if}
<style>
	.page-shell{width:100%;max-width:1600px;margin:0 auto;padding:28px 36px 40px}
	.page-heading{display:flex;align-items:center;justify-content:space-between;gap:18px}
	.page-heading h1{font-size:30px;font-weight:700;letter-spacing:-.035em}
	.page-heading p{margin-top:5px;font-size:12px;font-weight:500;color:var(--muted-foreground)}
	.stats-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:12px;margin-top:24px}
	.stat-card{border-radius:12px;box-shadow:none}
	.stat-content{display:flex;align-items:center;gap:12px;padding:15px!important}
	.stat-icon{display:flex;width:42px;height:42px;flex-shrink:0;align-items:center;justify-content:center;border-radius:10px}
	.stat-icon.neutral{background:#f4f4f4;color:#525252}
	.stat-icon.blue{background:#edf5ff;color:#0f62fe}
	.stat-icon.amber{background:#fff8e1;color:#8a3800}
	.stat-icon.green{background:#defbe6;color:#198038}
	.stat-content>div:last-child{min-width:0}
	.stat-content p{font-size:9px;font-weight:600;color:#6f6f6f}
	.stat-content strong{display:block;margin-top:1px;font-size:21px;font-weight:700;letter-spacing:-.02em}
	.stat-content span{display:block;margin-top:1px;font-size:8px;color:#8d8d8d}
	.filter-card{margin-top:14px;border-radius:12px;box-shadow:none}
	.filter-content{display:flex;align-items:center;gap:10px;padding:12px!important}
	.search-box{position:relative;min-width:260px;flex:1}
	.search-box>svg{position:absolute;top:50%;left:11px;z-index:1;transform:translateY(-50%);color:#6f6f6f}
	.search-input{height:38px!important;border-radius:8px!important;padding-left:34px!important;font-size:10px!important}
	.filters{display:flex;align-items:center;gap:7px}
	.select-wrap{position:relative;display:flex;align-items:center}
	.select-wrap>svg{position:absolute;left:9px;color:#6f6f6f;pointer-events:none}
	.select-wrap select{height:38px;min-width:126px;border:1px solid var(--input);border-radius:8px;background:#fff;padding:0 28px 0 10px;font-size:9px;font-weight:550;color:#525252;outline:none}
	.select-wrap:has(svg) select{padding-left:29px}
	.select-wrap select:focus{border-color:#8d8d8d;box-shadow:none}
	.date-filter{width:135px!important;height:38px!important;border-radius:8px!important;font-size:9px!important}
	.reset-button{width:38px!important;height:38px!important;border-radius:8px!important}
	.table-card{margin-top:14px;overflow:hidden;border-radius:12px;box-shadow:none}
	.table-header{display:flex!important;flex-direction:row!important;align-items:center!important;justify-content:space-between!important;border-bottom:1px solid var(--border);padding:14px!important}
	.table-header>span{display:inline-flex;border-radius:999px;background:#f4f4f4;padding:5px 9px;font-size:8px;font-weight:600;color:#6f6f6f}
	.confirm-layer{position:fixed;inset:0;z-index:250;display:flex;align-items:center;justify-content:center;padding:16px;background:rgb(15 23 42/.45);backdrop-filter:blur(3px)}
	.confirm-card{width:100%;max-width:410px;overflow:hidden;border:1px solid var(--border);border-radius:16px;background:#fff;box-shadow:0 24px 70px rgb(0 0 0/.22)}
	.confirm-body{padding:18px}
	.warning-icon{display:flex;width:42px;height:42px;align-items:center;justify-content:center;border-radius:10px;background:#fff1f1;color:#da1e28}
	.confirm-body h2{margin-top:13px;font-size:16px;font-weight:700}
	.confirm-body>p{margin-top:4px;font-size:11px;font-weight:500;line-height:1.55;color:#6f6f6f}
	.target-card{margin-top:12px;border:1px solid #e0e0e0;border-radius:10px;background:#f4f4f4;padding:10px}
	.target-card strong{display:block;font-family:ui-monospace,SFMono-Regular,Menlo,monospace;font-size:9px;color:#0f62fe}
	.target-card span{display:block;margin-top:4px;font-size:10px;font-weight:600;line-height:1.45}
	.confirm-footer{display:flex;justify-content:flex-end;gap:7px;border-top:1px solid var(--border);padding:11px 18px}
	.notification-card{position:fixed;right:20px;bottom:20px;z-index:300;display:flex;width:calc(100% - 40px);max-width:360px;align-items:flex-start;gap:10px;border:1px solid var(--border);border-radius:12px;background:#fff;padding:12px;box-shadow:0 12px 35px rgb(0 0 0/.14)}
	.notification-icon{display:flex;width:34px;height:34px;flex-shrink:0;align-items:center;justify-content:center;border-radius:50%;background:#defbe6;color:#198038}
	.notification-icon.notification-info{background:#fff8e1;color:#8a3800}
	.notification-card>div:nth-child(2){min-width:0;flex:1}
	.notification-card strong{font-size:10px;font-weight:700}
	.notification-card p{margin-top:2px;font-size:9px;font-weight:500;line-height:1.45;color:#6f6f6f}
	.notification-card>button{display:flex;width:28px;height:28px;align-items:center;justify-content:center;color:#6f6f6f}
	@media(max-width:1180px){.stats-grid{grid-template-columns:repeat(2,minmax(0,1fr))}.filter-content{align-items:stretch;flex-direction:column}.filters{flex-wrap:wrap}.select-wrap{flex:1}.select-wrap select{width:100%}.date-filter{flex:1;width:auto!important}}
	@media(max-width:700px){.page-shell{padding:20px 14px 30px}.page-heading{align-items:flex-start;flex-direction:column}.page-heading h1{font-size:25px}.page-heading>:global(button){width:100%}.stats-grid{grid-template-columns:1fr 1fr;gap:8px;margin-top:18px}.stat-content{padding:12px!important}.stat-icon{width:36px;height:36px}.stat-content strong{font-size:18px}.stat-content span{display:none}.filters{display:grid;grid-template-columns:repeat(2,minmax(0,1fr))}.date-filter{width:100%!important}.reset-button{width:100%!important}.table-header>span{display:none}}
	@media(max-width:420px){.stats-grid{grid-template-columns:1fr}.filters{grid-template-columns:1fr}}
</style>