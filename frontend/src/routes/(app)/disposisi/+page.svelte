<script lang="ts">
	import{browser}from'$app/environment';
	import{onMount}from'svelte';
	import{fly}from'svelte/transition';
	import*as Card from'$lib/components/ui/card/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import{Input}from'$lib/components/ui/input/index.js';
	import Plus from'@lucide/svelte/icons/plus';
	import Search from'@lucide/svelte/icons/search';
	import Forward from'@lucide/svelte/icons/forward';
	import Clock from'@lucide/svelte/icons/clock';
	import Play from'@lucide/svelte/icons/play';
	import CircleCheck from'@lucide/svelte/icons/circle-check';
	import SlidersHorizontal from'@lucide/svelte/icons/sliders-horizontal';
	import RotateCcw from'@lucide/svelte/icons/rotate-ccw';
	import TriangleAlert from'@lucide/svelte/icons/triangle-alert';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import X from'@lucide/svelte/icons/x';
	import DispositionFormModal from'$lib/features/disposisi/components/DispositionFormModal.svelte';
	import DispositionDetailModal from'$lib/features/disposisi/components/DispositionDetailModal.svelte';
	import DispositionTable from'$lib/features/disposisi/components/DispositionTable.svelte';
	import type{MasterDataRecord}from'$lib/features/master-data/types';
	import type{IncomingMailRecord,IncomingMailStatus}from'$lib/features/surat-masuk/types';
	import type{
		DispositionMailOption,
		DispositionOption,
		DispositionPayload,
		DispositionPriority,
		DispositionRecord,
		DispositionStatus,
		DispositionTargetType
	}from'$lib/features/disposisi/types';
	import{
		DISPOSITION_PRIORITY_OPTIONS,
		DISPOSITION_STATUS_OPTIONS
	}from'$lib/features/disposisi/types';
	import{getDispositions,createDisposition,updateDisposition,updateDispositionStatus,deleteDisposition}from'$lib/features/disposisi/api';
	import{getIncomingMails}from'$lib/features/surat-masuk/api';
	import{getMasterData}from'$lib/features/master-data/api';
	import{getUsers}from'$lib/features/pengguna/api';

	type StoredUser={
		id?:number|string;
		name?:string;
		fullName?:string;
		nama?:string;
		email?:string;
		unit?:string;
		unitKerja?:string;
		workUnit?:string;
		status?:string;
	};

	const perPage=6;

	const fallbackUnits=[
		'Administrator',
		'Tata Usaha',
		'Pimpinan',
		'Sekretariat',
		'Keuangan',
		'Kepegawaian',
		'Umum'
	];

	const fallbackUsers:StoredUser[]=[
		{id:1,name:'Admin Persuratan',email:'admin@sipersurat.id',unit:'Tata Usaha',status:'ACTIVE'},
		{id:2,name:'Kepala Pimpinan',email:'pimpinan@sipersurat.id',unit:'Pimpinan',status:'ACTIVE'},
		{id:3,name:'Staff Administrasi',email:'staff@sipersurat.id',unit:'Tata Usaha',status:'ACTIVE'},
		{id:4,name:'Staff Keuangan',email:'keuangan@sipersurat.id',unit:'Keuangan',status:'ACTIVE'},
		{id:5,name:'Staff Kepegawaian',email:'kepegawaian@sipersurat.id',unit:'Kepegawaian',status:'ACTIVE'}
	];

	const fallbackIncomingMails:IncomingMailRecord[]=[
		{id:1,agendaNumber:'SM-2026-0001',letterNumber:'005/UND/VIII/2026',letterDate:'2026-08-12',receivedDate:'2026-08-13',sender:'Dinas Pendidikan Kabupaten',subject:'Undangan Rapat Koordinasi Program Kerja Tahun 2026',category:'Undangan',priority:'Penting',targetUnit:'Tata Usaha',notes:'Mohon diteruskan kepada pimpinan untuk disposisi.',status:'PENDING_DISPOSITION',fileName:'undangan-rapat-koordinasi.pdf',fileType:'application/pdf',fileSize:428000,createdAt:'13 Agu 2026, 08:35',updatedAt:'13 Agu 2026, 08:35'},
		{id:2,agendaNumber:'SM-2026-0002',letterNumber:'117/DPK/VIII/2026',letterDate:'2026-08-11',receivedDate:'2026-08-13',sender:'Dinas Pekerjaan Umum',subject:'Permohonan Data Pendukung Kegiatan Infrastruktur',category:'Permohonan',priority:'Biasa',targetUnit:'Sekretariat',notes:'',status:'RECEIVED',fileName:'permohonan-data.pdf',fileType:'application/pdf',fileSize:312000,createdAt:'13 Agu 2026, 09:05',updatedAt:'13 Agu 2026, 09:05'},
		{id:3,agendaNumber:'SM-2026-0003',letterNumber:'021/BPKAD/VIII/2026',letterDate:'2026-08-10',receivedDate:'2026-08-12',sender:'BPKAD',subject:'Pemberitahuan Rekonsiliasi Laporan Keuangan Semester I',category:'Pemberitahuan',priority:'Penting',targetUnit:'Keuangan',notes:'Jadwal rekonsiliasi terlampir.',status:'DISPOSITIONED',fileName:'rekonsiliasi-keuangan.pdf',fileType:'application/pdf',fileSize:516000,createdAt:'12 Agu 2026, 10:20',updatedAt:'12 Agu 2026, 13:10'},
		{id:4,agendaNumber:'SM-2026-0004',letterNumber:'045/SET/VIII/2026',letterDate:'2026-08-09',receivedDate:'2026-08-12',sender:'Sekretariat Daerah',subject:'Penyampaian Jadwal Evaluasi Kinerja Perangkat Daerah',category:'Pemberitahuan',priority:'Biasa',targetUnit:'Pimpinan',notes:'',status:'IN_PROGRESS',fileName:'jadwal-evaluasi.pdf',fileType:'application/pdf',fileSize:287000,createdAt:'12 Agu 2026, 11:40',updatedAt:'13 Agu 2026, 08:10'},
		{id:5,agendaNumber:'SM-2026-0005',letterNumber:'088/INS/VIII/2026',letterDate:'2026-08-08',receivedDate:'2026-08-11',sender:'Inspektorat Daerah',subject:'Permintaan Dokumen Tindak Lanjut Hasil Pemeriksaan',category:'Permohonan',priority:'Rahasia',targetUnit:'Pimpinan',notes:'Dokumen bersifat terbatas.',status:'COMPLETED',fileName:'permintaan-dokumen.pdf',fileType:'application/pdf',fileSize:624000,createdAt:'11 Agu 2026, 09:15',updatedAt:'12 Agu 2026, 16:30'}
	];

	const defaultRecords:DispositionRecord[]=[
		{
			id:1,
			code:'DSP-2026-0001',
			incomingMailId:3,
			agendaNumber:'SM-2026-0003',
			letterNumber:'021/BPKAD/VIII/2026',
			sender:'BPKAD',
			subject:'Pemberitahuan Rekonsiliasi Laporan Keuangan Semester I',
			targetType:'UNIT',
			targetId:'Keuangan',
			targetName:'Keuangan',
			instruction:'Mohon ditelaah dan siapkan data pendukung untuk proses rekonsiliasi.',
			priority:'IMPORTANT',
			dueDate:'2026-08-15',
			notes:'Koordinasikan dengan Tata Usaha jika terdapat dokumen yang belum lengkap.',
			status:'RECEIVED',
			createdBy:'Admin Persuratan',
			createdAt:'2026-08-12T13:10:00',
			updatedAt:'2026-08-13T08:30:00',
			history:[
				{id:1,status:'WAITING',note:'Disposisi dibuat dan diteruskan ke Unit Keuangan.',at:'2026-08-12T13:10:00'},
				{id:2,status:'RECEIVED',note:'Disposisi telah diterima oleh Unit Keuangan.',at:'2026-08-13T08:30:00'}
			]
		},
		{
			id:2,
			code:'DSP-2026-0002',
			incomingMailId:4,
			agendaNumber:'SM-2026-0004',
			letterNumber:'045/SET/VIII/2026',
			sender:'Sekretariat Daerah',
			subject:'Penyampaian Jadwal Evaluasi Kinerja Perangkat Daerah',
			targetType:'USER',
			targetId:'2',
			targetName:'Kepala Pimpinan',
			instruction:'Mohon dipelajari dan siapkan arahan untuk pelaksanaan evaluasi kinerja.',
			priority:'NORMAL',
			dueDate:'2026-08-14',
			notes:'',
			status:'IN_PROGRESS',
			createdBy:'Admin Persuratan',
			createdAt:'2026-08-12T14:00:00',
			updatedAt:'2026-08-13T09:15:00',
			history:[
				{id:1,status:'WAITING',note:'Disposisi dibuat dan diteruskan kepada Kepala Pimpinan.',at:'2026-08-12T14:00:00'},
				{id:2,status:'RECEIVED',note:'Disposisi telah diterima.',at:'2026-08-12T15:20:00'},
				{id:3,status:'IN_PROGRESS',note:'Tindak lanjut disposisi mulai diproses.',at:'2026-08-13T09:15:00'}
			]
		},
		{
			id:3,
			code:'DSP-2026-0003',
			incomingMailId:5,
			agendaNumber:'SM-2026-0005',
			letterNumber:'088/INS/VIII/2026',
			sender:'Inspektorat Daerah',
			subject:'Permintaan Dokumen Tindak Lanjut Hasil Pemeriksaan',
			targetType:'UNIT',
			targetId:'Pimpinan',
			targetName:'Pimpinan',
			instruction:'Lengkapi dokumen tindak lanjut dan koordinasikan penyampaiannya kepada Inspektorat.',
			priority:'URGENT',
			dueDate:'2026-08-12',
			notes:'Dokumen bersifat terbatas.',
			status:'COMPLETED',
			createdBy:'Admin Persuratan',
			createdAt:'2026-08-11T10:00:00',
			updatedAt:'2026-08-12T16:30:00',
			history:[
				{id:1,status:'WAITING',note:'Disposisi dibuat dan diteruskan ke Pimpinan.',at:'2026-08-11T10:00:00'},
				{id:2,status:'RECEIVED',note:'Disposisi telah diterima.',at:'2026-08-11T11:15:00'},
				{id:3,status:'IN_PROGRESS',note:'Dokumen tindak lanjut sedang disiapkan.',at:'2026-08-12T08:10:00'},
				{id:4,status:'COMPLETED',note:'Tindak lanjut disposisi telah diselesaikan.',at:'2026-08-12T16:30:00'}
			]
		}
	];

	let records=$state<DispositionRecord[]>([...defaultRecords]);
	let incomingMails=$state<IncomingMailRecord[]>([...fallbackIncomingMails]);
	let masterData=$state<MasterDataRecord[]>([]);
	let storedUsers=$state<StoredUser[]>([]);
	let initialized=$state(false);
	let search=$state('');
	let statusFilter=$state<'ALL'|DispositionStatus>('ALL');
	let priorityFilter=$state<'ALL'|DispositionPriority>('ALL');
	let targetFilter=$state<'ALL'|DispositionTargetType>('ALL');
	let dueDateFilter=$state('');
	let currentPage=$state(1);
	let formOpen=$state(false);
	let formMode=$state<'create'|'edit'>('create');
	let selectedRecord=$state<DispositionRecord|null>(null);
	let detailOpen=$state(false);
	let detailRecord=$state<DispositionRecord|null>(null);
	let deleteTarget=$state<DispositionRecord|null>(null);
	let notification=$state<{type:'success'|'info';message:string}|null>(null);
	let notificationTimer:ReturnType<typeof setTimeout>|undefined;
	let selectedStat=$state('total');

	onMount(async()=>{
		if(!browser)return;
		try{
			const[result,mails,master,userRecords]=await Promise.all([getDispositions(),getIncomingMails(),getMasterData(),getUsers()]);
			records=result;incomingMails=mails;masterData=master;storedUsers=userRecords;
		}catch(error){records=[];incomingMails=[];masterData=[];storedUsers=[];notify(error instanceof Error?error.message:'Data disposisi gagal dimuat.','info');}
		initialized=true;
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

	const users=$derived.by(()=>{
		const source=storedUsers.length?storedUsers:fallbackUsers;

		return source
			.filter((user)=>String(user.status??'ACTIVE').toUpperCase()!=='INACTIVE')
			.map((user,index)=>{
				const name=user.fullName||user.name||user.nama||user.email||`Pengguna ${index+1}`;
				const unit=user.unitKerja||user.unit||user.workUnit||'';
				const value=String(user.id??user.email??name);

				return{
					value,
					label:unit?`${name} · ${unit}`:name
				};
			});
	});

	const mailOptions=$derived.by(()=>{
		return incomingMails
			.filter((mail)=>
				mail.status==='RECEIVED'||
				mail.status==='PENDING_DISPOSITION'||
				selectedRecord?.incomingMailId===mail.id
			)
			.map((mail):DispositionMailOption=>({
				id:mail.id,
				agendaNumber:mail.agendaNumber,
				letterNumber:mail.letterNumber,
				sender:mail.sender,
				subject:mail.subject
			}));
	});

	const filteredRecords=$derived(records.filter((record)=>{
		const keyword=search.trim().toLowerCase();

		const matchesSearch=
			!keyword||
			record.code.toLowerCase().includes(keyword)||
			record.agendaNumber.toLowerCase().includes(keyword)||
			record.letterNumber.toLowerCase().includes(keyword)||
			record.sender.toLowerCase().includes(keyword)||
			record.subject.toLowerCase().includes(keyword)||
			record.targetName.toLowerCase().includes(keyword)||
			record.instruction.toLowerCase().includes(keyword);

		const matchesStatus=statusFilter==='ALL'||record.status===statusFilter;
		const matchesPriority=priorityFilter==='ALL'||record.priority===priorityFilter;
		const matchesTarget=targetFilter==='ALL'||record.targetType===targetFilter;
		const matchesDueDate=!dueDateFilter||record.dueDate===dueDateFilter;

		return matchesSearch&&matchesStatus&&matchesPriority&&matchesTarget&&matchesDueDate;
	}));

	const totalPages=$derived(
		Math.max(1,Math.ceil(filteredRecords.length/perPage))
	);

	const paginatedRecords=$derived(
		filteredRecords.slice((currentPage-1)*perPage,currentPage*perPage)
	);

	const waitingCount=$derived(
		records.filter((record)=>record.status==='WAITING').length
	);

	const progressCount=$derived(
		records.filter((record)=>record.status==='IN_PROGRESS').length
	);

	const completedCount=$derived(
		records.filter((record)=>record.status==='COMPLETED').length
	);

	const hasFilter=$derived(
		search.trim()!==''||
		statusFilter!=='ALL'||
		priorityFilter!=='ALL'||
		targetFilter!=='ALL'||
		dueDateFilter!==''
	);

	function generateCode(items:DispositionRecord[]):string{
		const year=new Date().getFullYear();
		const firstCode=items.find((item)=>item.code)?.code??'';
		const prefix=firstCode.split(/[-/]/)[0]||'DSP';

		const highest=items.reduce((max,item)=>{
			const match=item.code.match(/(\d+)$/);

			if(!match)return max;

			const value=Number(match[1]);

			return Number.isFinite(value)
				?Math.max(max,value)
				:max;
		},0);

		return`${prefix}-${year}-${String(highest+1).padStart(4,'0')}`;
	}

	const nextCode=$derived(generateCode(records));

	$effect(()=>{
		if(currentPage>totalPages)currentPage=totalPages;
	});

	function notify(message:string,type:'success'|'info'='success'){
		notification={type,message};

		if(notificationTimer)clearTimeout(notificationTimer);

		notificationTimer=setTimeout(()=>{
			notification=null;
		},3000);
	}

	function openCreate(){
		selectedRecord=null;

		if(mailOptions.length===0){
			notify('Tidak ada surat yang sedang menunggu disposisi.','info');
			return;
		}

		formMode='create';
		formOpen=true;
	}

	function openEdit(record:DispositionRecord){
		detailOpen=false;
		detailRecord=null;
		selectedRecord=record;
		formMode='edit';
		formOpen=true;
	}

	function openDetail(record:DispositionRecord){
		detailRecord=record;
		detailOpen=true;
	}

	async function refreshIncoming(){
		try{incomingMails=await getIncomingMails();}catch{}
	}

	async function saveDisposition(payload:DispositionPayload){
		try{
			if(formMode==='create'){const saved=await createDisposition(payload);records=[saved,...records];notify('Disposisi berhasil dibuat.');}
			else if(selectedRecord){const saved=await updateDisposition(selectedRecord.id,payload);records=records.map((item)=>item.id===saved.id?saved:item);notify('Disposisi berhasil diperbarui.');}
			await refreshIncoming();formOpen=false;selectedRecord=null;currentPage=1;
		}catch(error){notify(error instanceof Error?error.message:'Disposisi gagal disimpan.','info');}
	}

	async function changeStatus(record:DispositionRecord,status:DispositionStatus){
		if(record.status===status)return;
		try{
			const saved=await updateDispositionStatus(record.id,status);records=records.map((item)=>item.id===saved.id?saved:item);detailRecord=saved;await refreshIncoming();
			if(status==='RECEIVED')notify('Disposisi ditandai telah diterima.');else if(status==='IN_PROGRESS')notify('Disposisi mulai diproses.');else if(status==='COMPLETED')notify('Disposisi berhasil diselesaikan.');else notify('Status disposisi berhasil diperbarui.');
		}catch(error){notify(error instanceof Error?error.message:'Status disposisi gagal diperbarui.','info');}
	}

	async function confirmDelete(){
		if(!deleteTarget)return;const target=deleteTarget;
		try{await deleteDisposition(target.id);records=records.filter((record)=>record.id!==target.id);await refreshIncoming();if(detailRecord?.id===target.id){detailOpen=false;detailRecord=null;}deleteTarget=null;notify('Disposisi berhasil dihapus.');}
		catch(error){deleteTarget=null;notify(error instanceof Error?error.message:'Disposisi gagal dihapus.','info');}
	}

	function resetFilter(){
		search='';
		statusFilter='ALL';
		priorityFilter='ALL';
		targetFilter='ALL';
		dueDateFilter='';
		selectedStat='total';
		currentPage=1;
	}

	function selectStat(value:string){
		selectedStat=value;
		currentPage=1;

		if(value==='total'){
			statusFilter='ALL';
		}else if(value==='waiting'){
			statusFilter='WAITING';
		}else if(value==='progress'){
			statusFilter='IN_PROGRESS';
		}else if(value==='completed'){
			statusFilter='COMPLETED';
		}
	}
</script>

<svelte:head>
	<title>Disposisi | SiPersurat</title>
</svelte:head>

<div class="w-full min-w-0 overflow-x-hidden">
	<div class="mx-auto w-full min-w-0 max-w-[1500px] px-5 py-7 md:px-8 lg:px-10">
		<div class="flex flex-col justify-between gap-6 md:flex-row md:items-start">
			<div
				class="min-w-0"
				in:fly={{y:18,duration:450}}
			>
				<h1 class="text-2xl font-bold tracking-tight md:text-[28px]">
					Disposisi
				</h1>

				<p class="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
					Kelola proses penerusan surat masuk kepada pengguna atau unit kerja yang bertanggung jawab. Pantau instruksi, prioritas, batas waktu, status penerimaan, proses tindak lanjut, hingga penyelesaian disposisi secara terpusat.
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
					Buat Disposisi
				</Button>
			</div>
		</div>

		<section class="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
			<div in:fly={{y:22,duration:450,delay:100}}>
				<button
					onclick={()=>selectStat('total')}
					class={[
						'group h-full w-full rounded-xl border bg-card p-5 text-left transition-all duration-200 hover:-translate-y-1 hover:shadow-md',
						selectedStat==='total'?'border-foreground/30 shadow-sm':'border-border'
					]}
				>
					<div class="flex items-center justify-between gap-4">
						<p class="text-xs font-semibold">Total Disposisi</p>

						<div class={selectedStat==='total'
							?'flex size-8 items-center justify-center rounded-lg bg-foreground text-background'
							:'flex size-8 items-center justify-center rounded-lg bg-muted text-muted-foreground transition group-hover:bg-foreground group-hover:text-background'}
						>
							<Forward class="size-4"/>
						</div>
					</div>

					<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
						{records.length}
					</p>

					<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
						Seluruh disposisi yang telah tercatat pada sistem.
					</p>
				</button>
			</div>

			<div in:fly={{y:22,duration:450,delay:170}}>
				<button
					onclick={()=>selectStat('waiting')}
					class={[
						'group h-full w-full rounded-xl border bg-card p-5 text-left transition-all duration-200 hover:-translate-y-1 hover:shadow-md',
						selectedStat==='waiting'?'border-amber-300 shadow-sm':'border-border'
					]}
				>
					<div class="flex items-center justify-between gap-4">
						<p class="text-xs font-semibold">Menunggu</p>

						<div class="flex size-8 items-center justify-center rounded-lg bg-amber-50 text-amber-700 transition group-hover:scale-110">
							<Clock class="size-4"/>
						</div>
					</div>

					<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
						{waitingCount}
					</p>

					<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
						Disposisi yang belum diterima oleh tujuan.
					</p>
				</button>
			</div>

			<div in:fly={{y:22,duration:450,delay:240}}>
				<button
					onclick={()=>selectStat('progress')}
					class={[
						'group h-full w-full rounded-xl border bg-card p-5 text-left transition-all duration-200 hover:-translate-y-1 hover:shadow-md',
						selectedStat==='progress'?'border-violet-300 shadow-sm':'border-border'
					]}
				>
					<div class="flex items-center justify-between gap-4">
						<p class="text-xs font-semibold">Diproses</p>

						<div class="flex size-8 items-center justify-center rounded-lg bg-violet-50 text-violet-700 transition group-hover:scale-110">
							<Play class="size-4"/>
						</div>
					</div>

					<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
						{progressCount}
					</p>

					<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
						Disposisi yang sedang dalam proses tindak lanjut.
					</p>
				</button>
			</div>

			<div in:fly={{y:22,duration:450,delay:310}}>
				<button
					onclick={()=>selectStat('completed')}
					class={[
						'group h-full w-full rounded-xl border bg-card p-5 text-left transition-all duration-200 hover:-translate-y-1 hover:shadow-md',
						selectedStat==='completed'?'border-emerald-300 shadow-sm':'border-border'
					]}
				>
					<div class="flex items-center justify-between gap-4">
						<p class="text-xs font-semibold">Selesai</p>

						<div class="flex size-8 items-center justify-center rounded-lg bg-emerald-50 text-emerald-700 transition group-hover:scale-110">
							<CircleCheck class="size-4"/>
						</div>
					</div>

					<p class="mt-7 text-[28px] font-bold leading-none tracking-tight">
						{completedCount}
					</p>

					<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
						Disposisi yang tindak lanjutnya telah diselesaikan.
					</p>
				</button>
			</div>
		</section>

		<div in:fly={{y:20,duration:450,delay:380}}>
			<Card.Root class="mt-6 rounded-xl border shadow-none">
				<Card.Content class="p-5">
					<div>
						<h2 class="text-sm font-semibold">
							Cari & Filter Disposisi
						</h2>

						<p class="mt-1 text-xs leading-5 text-muted-foreground">
							Temukan disposisi berdasarkan kode, nomor surat, pengirim, perihal, tujuan, instruksi, prioritas, dan status.
						</p>
					</div>

					<div class="relative mt-4">
						<Search class="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

						<Input
							bind:value={search}
							placeholder="Cari disposisi, surat, pengirim, tujuan, atau instruksi..."
							class="h-11 rounded-lg pl-10 text-sm"
							oninput={()=>{
								currentPage=1;
								selectedStat='custom';
							}}
						/>
					</div>

					<div class="mt-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-[minmax(170px,1fr)_minmax(150px,.8fr)_minmax(150px,.8fr)_minmax(170px,.9fr)_40px]">
						<div class="relative">
							<SlidersHorizontal class="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

							<select
								bind:value={statusFilter}
								onchange={()=>{
									currentPage=1;
									selectedStat='custom';
								}}
								class="h-10 w-full rounded-lg border border-input bg-background pl-10 pr-3 text-xs font-medium outline-none"
							>
								<option value="ALL">Semua Status</option>

								{#each DISPOSITION_STATUS_OPTIONS as option}
									<option value={option.value}>{option.label}</option>
								{/each}
							</select>
						</div>

						<select
							bind:value={priorityFilter}
							onchange={()=>{
								currentPage=1;
								selectedStat='custom';
							}}
							class="h-10 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none"
						>
							<option value="ALL">Semua Prioritas</option>

							{#each DISPOSITION_PRIORITY_OPTIONS as option}
								<option value={option.value}>{option.label}</option>
							{/each}
						</select>

						<select
							bind:value={targetFilter}
							onchange={()=>{
								currentPage=1;
								selectedStat='custom';
							}}
							class="h-10 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none"
						>
							<option value="ALL">Semua Tujuan</option>
							<option value="USER">Pengguna</option>
							<option value="UNIT">Unit Kerja</option>
						</select>

						<Input
							type="date"
							bind:value={dueDateFilter}
							onchange={()=>{
								currentPage=1;
								selectedStat='custom';
							}}
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
								Daftar Disposisi
							</Card.Title>

							<Card.Description class="mt-1.5 max-w-2xl text-xs leading-5">
								Pantau tujuan disposisi, prioritas, batas waktu, status penerimaan, dan progres tindak lanjut seluruh surat.
							</Card.Description>
						</div>

						<div class="w-fit rounded-md bg-muted px-3 py-1.5 text-[11px] font-medium text-muted-foreground">
							{filteredRecords.length} data
						</div>
					</div>
				</Card.Header>

				<DispositionTable
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

<DispositionFormModal
	open={formOpen}
	mode={formMode}
	record={selectedRecord}
	code={nextCode}
	mails={mailOptions}
	{users}
	{units}
	onSave={saveDisposition}
	onOpenChange={(value)=>{
		formOpen=value;
		if(!value)selectedRecord=null;
	}}
/>

<DispositionDetailModal
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
	<div class="fixed inset-0 z-[280] flex items-center justify-center bg-black/40 p-4 backdrop-blur-sm">
		<div
			class="w-full max-w-md overflow-hidden rounded-xl border bg-background shadow-2xl"
			in:fly={{y:15,duration:220}}
		>
			<div class="p-5">
				<div class="flex size-10 items-center justify-center rounded-lg bg-red-50 text-red-600">
					<TriangleAlert class="size-5"/>
				</div>

				<h2 class="mt-4 text-lg font-bold">
					Hapus disposisi?
				</h2>

				<p class="mt-1 text-sm leading-6 text-muted-foreground">
					Data disposisi akan dihapus. Status surat masuk akan disesuaikan kembali secara otomatis.
				</p>

				<div class="mt-4 rounded-lg border bg-muted/40 p-3.5">
					<p class="font-mono text-xs font-semibold text-blue-600">
						{deleteTarget.code}
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
		<div class={notification.type==='success'
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