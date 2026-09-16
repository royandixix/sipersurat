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
	import type{OutgoingMailPayload,OutgoingMailRecord,OutgoingMailStatus}from'$lib/features/surat-keluar/types';
	import{OUTGOING_MAIL_STATUS_OPTIONS}from'$lib/features/surat-keluar/types';
	import{createOutgoingMail,deleteOutgoingMail,getOutgoingMails,updateOutgoingMail,updateOutgoingMailStatus}from'$lib/features/surat-keluar/api';
	import{getMasterData}from'$lib/features/master-data/api';

	const perPage=6;
	const fallbackCategories=['Undangan','Permohonan','Pemberitahuan','Internal','Eksternal'];
	const fallbackPriorities=['Biasa','Penting','Rahasia','Sangat Rahasia'];
	const fallbackUnits=['Administrator','Tata Usaha','Pimpinan','Sekretariat','Keuangan','Kepegawaian','Umum'];

	let records=$state<OutgoingMailRecord[]>([]);
	let masterData=$state<MasterDataRecord[]>([]);
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
	let notification=$state<{type:'success'|'info';message:string}|null>(null);
	let notificationTimer:ReturnType<typeof setTimeout>|undefined;

	onMount(async()=>{
		if(!browser)return;
		await Promise.all([loadMasterData(),loadRecords()]);
	});

	const categories=$derived.by(()=>{
		const items=masterData.filter((item)=>item.type==='KATEGORI_SURAT'&&item.status==='ACTIVE').map((item)=>item.name);
		return(items.length?items:fallbackCategories).map((value)=>({value,label:value}));
	});

	const priorities=$derived.by(()=>{
		const items=masterData.filter((item)=>item.type==='SIFAT_SURAT'&&item.status==='ACTIVE').map((item)=>item.name);
		return(items.length?items:fallbackPriorities).map((value)=>({value,label:value}));
	});

	const units=$derived.by(()=>{
		const items=masterData.filter((item)=>item.type==='UNIT_KERJA'&&item.status==='ACTIVE').map((item)=>item.name);
		return(items.length?items:fallbackUnits).map((value)=>({value,label:value}));
	});

	const filteredRecords=$derived(records.filter((record)=>{
		const keyword=search.trim().toLowerCase();
		const matchesSearch=!keyword||
			record.agendaNumber.toLowerCase().includes(keyword)||
			record.letterNumber.toLowerCase().includes(keyword)||
			record.recipient.toLowerCase().includes(keyword)||
			record.subject.toLowerCase().includes(keyword)||
			record.sourceUnit.toLowerCase().includes(keyword)||
			record.signer.toLowerCase().includes(keyword);
		const matchesCategory=categoryFilter==='ALL'||record.category===categoryFilter;
		const matchesPriority=priorityFilter==='ALL'||record.priority===priorityFilter;
		const matchesStatus=statusFilter==='ALL'||record.status===statusFilter;
		const matchesDate=!dateFilter||record.letterDate===dateFilter;
		return matchesSearch&&matchesCategory&&matchesPriority&&matchesStatus&&matchesDate;
	}));

	const totalPages=$derived(Math.max(1,Math.ceil(filteredRecords.length/perPage)));
	const paginatedRecords=$derived(filteredRecords.slice((currentPage-1)*perPage,currentPage*perPage));
	const draftCount=$derived(records.filter((record)=>record.status==='DRAFT').length);
	const pendingCount=$derived(records.filter((record)=>record.status==='PENDING_APPROVAL').length);
	const sentCount=$derived(records.filter((record)=>record.status==='SENT'||record.status==='ARCHIVED').length);
	const hasFilter=$derived(search.trim()!==''||categoryFilter!=='ALL'||priorityFilter!=='ALL'||statusFilter!=='ALL'||dateFilter!=='');
	const nextAgendaNumber=$derived(generateAgendaNumber(records));

	$effect(()=>{
		if(currentPage>totalPages)currentPage=totalPages;
	});

	async function loadMasterData(){
		try{masterData=await getMasterData();}
		catch{masterData=[];}
	}

	async function loadRecords(){
		try{
			records=await getOutgoingMails();
		}catch(error){
			records=[];
			notify(getErrorMessage(error,'Data surat keluar gagal dimuat.'),'info');
		}
	}

	function generateAgendaNumber(items:OutgoingMailRecord[]){
		const year=new Date().getFullYear();
		const max=items.reduce((highest,item)=>{
			const match=item.agendaNumber.match(/^SK-(\d{4})-(\d+)$/);
			if(!match||Number(match[1])!==year)return highest;
			return Math.max(highest,Number(match[2]));
		},0);
		return`SK-${year}-${String(max+1).padStart(4,'0')}`;
	}

	function getErrorMessage(error:unknown,fallback:string){
		if(error instanceof Error&&error.message)return error.message;
		return fallback;
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

	async function saveRecord(payload:OutgoingMailPayload){
		try{
			if(formMode==='create'){
				const saved=await createOutgoingMail(payload);
				records=[saved,...records];
				notify('Surat keluar berhasil ditambahkan.');
			}else if(selectedRecord){
				const saved=await updateOutgoingMail(selectedRecord.id,payload);
				records=records.map((record)=>record.id===saved.id?saved:record);
				if(detailRecord?.id===saved.id)detailRecord=saved;
				notify('Surat keluar berhasil diperbarui.');
			}
			formOpen=false;
			selectedRecord=null;
			currentPage=1;
		}catch(error){
			notify(getErrorMessage(error,'Surat keluar gagal disimpan.'),'info');
		}
	}

	async function changeStatus(record:OutgoingMailRecord,status:OutgoingMailStatus){
		if(record.status===status)return;
		try{
			const saved=await updateOutgoingMailStatus(record.id,status);
			records=records.map((item)=>item.id===saved.id?saved:item);
			if(detailRecord?.id===saved.id)detailRecord=saved;
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
			}else if(status==='REJECTED'){
				notify('Surat ditolak.');
			}else{
				notify('Status surat berhasil diperbarui.');
			}
		}catch(error){
			notify(getErrorMessage(error,'Status surat gagal diperbarui.'),'info');
		}
	}

	async function confirmDelete(){
		if(!deleteTarget)return;
		const target=deleteTarget;
		try{
			await deleteOutgoingMail(target.id);
			records=records.filter((record)=>record.id!==target.id);
			if(detailRecord?.id===target.id){
				detailOpen=false;
				detailRecord=null;
			}
			deleteTarget=null;
			notify('Surat keluar berhasil dihapus.');
		}catch(error){
			notify(getErrorMessage(error,'Surat keluar gagal dihapus.'),'info');
		}
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