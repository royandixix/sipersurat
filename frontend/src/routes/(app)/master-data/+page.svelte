<script lang="ts">
	import{browser}from '$app/environment';
	import{onMount}from 'svelte';
	import * as Card from '$lib/components/ui/card/index.js';
	import * as DropdownMenu from '$lib/components/ui/dropdown-menu/index.js';
	import{Button}from '$lib/components/ui/button/index.js';
	import{Input}from '$lib/components/ui/input/index.js';
	import Search from '@lucide/svelte/icons/search';
	import Plus from '@lucide/svelte/icons/plus';
	import BadgeCheck from '@lucide/svelte/icons/badge-check';
	import Landmark from '@lucide/svelte/icons/landmark';
	import FolderTree from '@lucide/svelte/icons/folder-tree';
	import LockKeyhole from '@lucide/svelte/icons/lock-keyhole';
	import Boxes from '@lucide/svelte/icons/boxes';
	import CircleCheck from '@lucide/svelte/icons/circle-check';
	import CircleX from '@lucide/svelte/icons/circle-x';
	import Link2 from '@lucide/svelte/icons/link-2';
	import MoreHorizontal from '@lucide/svelte/icons/more-horizontal';
	import Pencil from '@lucide/svelte/icons/pencil';
	import Power from '@lucide/svelte/icons/power';
	import Trash2 from '@lucide/svelte/icons/trash-2';
	import ChevronLeft from '@lucide/svelte/icons/chevron-left';
	import ChevronRight from '@lucide/svelte/icons/chevron-right';
	import RotateCcw from '@lucide/svelte/icons/rotate-ccw';
	import TriangleAlert from '@lucide/svelte/icons/triangle-alert';
	import X from '@lucide/svelte/icons/x';
	import MasterDataModal from '$lib/features/master-data/components/MasterDataModal.svelte';
	import type{MasterDataPayload,MasterDataRecord,MasterDataStatus,MasterDataType}from '$lib/features/master-data/types';
	import{MASTER_DATA_CONFIG}from '$lib/features/master-data/types';

	const STORAGE_KEY='sipersurat-master-data';
	const perPage=6;

	const defaultData:MasterDataRecord[]=[
		{id:1,type:'ROLE',name:'Super Administrator',code:'SUPER_ADMIN',description:'Akses penuh ke seluruh sistem.',status:'ACTIVE',usageCount:1,createdAt:'01 Agu 2026'},
		{id:2,type:'ROLE',name:'Admin Persuratan',code:'ADMIN_PERSURATAN',description:'Mengelola proses administrasi persuratan.',status:'ACTIVE',usageCount:2,createdAt:'01 Agu 2026'},
		{id:3,type:'ROLE',name:'Pimpinan',code:'PIMPINAN',description:'Menerima dan memberikan disposisi.',status:'ACTIVE',usageCount:2,createdAt:'01 Agu 2026'},
		{id:4,type:'ROLE',name:'Staff',code:'STAFF',description:'Pengguna operasional SiPersurat.',status:'ACTIVE',usageCount:4,createdAt:'01 Agu 2026'},
		{id:5,type:'UNIT_KERJA',name:'Administrator',code:'ADMINISTRATOR',description:'Unit administrasi sistem.',status:'ACTIVE',usageCount:1,createdAt:'01 Agu 2026'},
		{id:6,type:'UNIT_KERJA',name:'Tata Usaha',code:'TATA_USAHA',description:'Unit pengelolaan administrasi persuratan.',status:'ACTIVE',usageCount:2,createdAt:'01 Agu 2026'},
		{id:7,type:'UNIT_KERJA',name:'Pimpinan',code:'PIMPINAN',description:'Unit pimpinan instansi.',status:'ACTIVE',usageCount:2,createdAt:'01 Agu 2026'},
		{id:8,type:'UNIT_KERJA',name:'Keuangan',code:'KEUANGAN',description:'Unit pengelolaan keuangan.',status:'ACTIVE',usageCount:1,createdAt:'01 Agu 2026'},
		{id:9,type:'UNIT_KERJA',name:'Sekretariat',code:'SEKRETARIAT',description:'Unit sekretariat.',status:'ACTIVE',usageCount:1,createdAt:'01 Agu 2026'},
		{id:10,type:'UNIT_KERJA',name:'Kepegawaian',code:'KEPEGAWAIAN',description:'Unit administrasi kepegawaian.',status:'ACTIVE',usageCount:1,createdAt:'01 Agu 2026'},
		{id:11,type:'UNIT_KERJA',name:'Umum',code:'UMUM',description:'Unit pelayanan umum.',status:'ACTIVE',usageCount:1,createdAt:'01 Agu 2026'},
		{id:12,type:'KATEGORI_SURAT',name:'Undangan',code:'UNDANGAN',description:'Surat yang berisi undangan kegiatan atau rapat.',status:'ACTIVE',usageCount:18,createdAt:'01 Agu 2026'},
		{id:13,type:'KATEGORI_SURAT',name:'Permohonan',code:'PERMOHONAN',description:'Surat permohonan resmi.',status:'ACTIVE',usageCount:24,createdAt:'01 Agu 2026'},
		{id:14,type:'KATEGORI_SURAT',name:'Pemberitahuan',code:'PEMBERITAHUAN',description:'Surat pemberitahuan informasi resmi.',status:'ACTIVE',usageCount:31,createdAt:'01 Agu 2026'},
		{id:15,type:'KATEGORI_SURAT',name:'Internal',code:'INTERNAL',description:'Surat untuk kebutuhan internal instansi.',status:'ACTIVE',usageCount:15,createdAt:'01 Agu 2026'},
		{id:16,type:'KATEGORI_SURAT',name:'Eksternal',code:'EKSTERNAL',description:'Surat untuk pihak di luar instansi.',status:'ACTIVE',usageCount:22,createdAt:'01 Agu 2026'},
		{id:17,type:'SIFAT_SURAT',name:'Biasa',code:'BIASA',description:'Surat dengan tingkat prioritas normal.',status:'ACTIVE',usageCount:72,createdAt:'01 Agu 2026'},
		{id:18,type:'SIFAT_SURAT',name:'Penting',code:'PENTING',description:'Surat yang membutuhkan perhatian khusus.',status:'ACTIVE',usageCount:26,createdAt:'01 Agu 2026'},
		{id:19,type:'SIFAT_SURAT',name:'Rahasia',code:'RAHASIA',description:'Surat dengan akses terbatas.',status:'ACTIVE',usageCount:9,createdAt:'01 Agu 2026'},
		{id:20,type:'SIFAT_SURAT',name:'Sangat Rahasia',code:'SANGAT_RAHASIA',description:'Surat dengan tingkat kerahasiaan tertinggi.',status:'ACTIVE',usageCount:3,createdAt:'01 Agu 2026'}
	];

	let data=$state<MasterDataRecord[]>([...defaultData]);
	let initialized=$state(false);
	let activeType=$state<MasterDataType>('ROLE');
	let search=$state('');
	let statusFilter=$state<'ALL'|MasterDataStatus>('ALL');
	let currentPage=$state(1);
	let formOpen=$state(false);
	let formMode=$state<'create'|'edit'>('create');
	let selectedRecord=$state<MasterDataRecord|null>(null);
	let deleteTarget=$state<MasterDataRecord|null>(null);
	let notification=$state<string|null>(null);
	let notificationTimer:ReturnType<typeof setTimeout>|undefined;

	onMount(()=>{
		if(!browser)return;
		const saved=localStorage.getItem(STORAGE_KEY);
		if(saved){
			try{
				const parsed=JSON.parse(saved);
				if(Array.isArray(parsed))data=parsed;
			}catch{
				data=[...defaultData];
			}
		}
		initialized=true;
	});

	$effect(()=>{
		if(!browser||!initialized)return;
		localStorage.setItem(STORAGE_KEY,JSON.stringify(data));
	});

	const config=$derived(MASTER_DATA_CONFIG[activeType]);
	const typeData=$derived(data.filter((item)=>item.type===activeType));
	const filteredData=$derived(typeData.filter((item)=>{
		const keyword=search.trim().toLowerCase();
		const matchesSearch=!keyword||item.name.toLowerCase().includes(keyword)||item.code.toLowerCase().includes(keyword)||item.description.toLowerCase().includes(keyword);
		const matchesStatus=statusFilter==='ALL'||item.status===statusFilter;
		return matchesSearch&&matchesStatus;
	}));
	const activeCount=$derived(typeData.filter((item)=>item.status==='ACTIVE').length);
	const inactiveCount=$derived(typeData.filter((item)=>item.status==='INACTIVE').length);
	const usageCount=$derived(typeData.reduce((total,item)=>total+item.usageCount,0));
	const totalPages=$derived(Math.max(1,Math.ceil(filteredData.length/perPage)));
	const paginatedData=$derived(filteredData.slice((currentPage-1)*perPage,currentPage*perPage));
	const firstItem=$derived(filteredData.length===0?0:(currentPage-1)*perPage+1);
	const lastItem=$derived(Math.min(currentPage*perPage,filteredData.length));
	const hasFilter=$derived(search.trim()!==''||statusFilter!=='ALL');

	$effect(()=>{
		if(currentPage>totalPages)currentPage=totalPages;
	});

	function changeTab(type:MasterDataType){
		activeType=type;
		search='';
		statusFilter='ALL';
		currentPage=1;
	}

	function openCreate(){
		formMode='create';
		selectedRecord=null;
		formOpen=true;
	}

	function openEdit(record:MasterDataRecord){
		formMode='edit';
		selectedRecord=record;
		formOpen=true;
	}

	function showNotification(message:string){
		notification=message;
		if(notificationTimer)clearTimeout(notificationTimer);
		notificationTimer=setTimeout(()=>notification=null,3000);
	}

	function today(){
		return new Intl.DateTimeFormat('id-ID',{day:'2-digit',month:'short',year:'numeric'}).format(new Date());
	}

	function saveRecord(payload:MasterDataPayload){
		const duplicate=data.some((item)=>item.type===activeType&&item.id!==selectedRecord?.id&&item.code.toLowerCase()===payload.code.toLowerCase());
		if(duplicate){
			showNotification(`Kode ${payload.code} sudah digunakan.`);
			return;
		}
		if(formMode==='create'){
			const nextId=Math.max(0,...data.map((item)=>item.id))+1;
			data=[...data,{
				id:nextId,
				type:activeType,
				name:payload.name,
				code:payload.code,
				description:payload.description,
				status:payload.status,
				usageCount:0,
				createdAt:today()
			}];
			showNotification(`${config.singular} berhasil ditambahkan.`);
		}else if(selectedRecord){
			data=data.map((item)=>item.id===selectedRecord?.id?{
				...item,
				name:payload.name,
				code:payload.code,
				description:payload.description,
				status:payload.status
			}:item);
			showNotification(`${config.singular} berhasil diperbarui.`);
		}
		formOpen=false;
		selectedRecord=null;
	}

	function toggleStatus(record:MasterDataRecord){
		if(record.type==='ROLE'&&record.code==='SUPER_ADMIN')return;
		const nextStatus:MasterDataStatus=record.status==='ACTIVE'?'INACTIVE':'ACTIVE';
		data=data.map((item)=>item.id===record.id?{...item,status:nextStatus}:item);
		showNotification(`${record.name} berhasil ${nextStatus==='ACTIVE'?'diaktifkan':'dinonaktifkan'}.`);
	}

	function confirmDelete(){
		if(!deleteTarget)return;
		if(deleteTarget.type==='ROLE'&&deleteTarget.code==='SUPER_ADMIN')return;
		if(deleteTarget.usageCount>0){
			showNotification(`${deleteTarget.name} masih digunakan dan belum dapat dihapus.`);
			deleteTarget=null;
			return;
		}
		const name=deleteTarget.name;
		data=data.filter((item)=>item.id!==deleteTarget?.id);
		deleteTarget=null;
		showNotification(`${name} berhasil dihapus.`);
	}

	function resetFilter(){
		search='';
		statusFilter='ALL';
		currentPage=1;
	}
</script>

<svelte:head>
	<title>Master Data | SiPersurat</title>
</svelte:head>

<div class="mx-auto w-full max-w-[1600px] px-5 py-7 md:px-8 lg:px-9">
	<div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
		<div>
			<h1 class="text-[26px] font-bold tracking-[-0.03em] md:text-[30px]">Master Data</h1>
			<p class="mt-1.5 text-[13px] font-medium text-muted-foreground">Kelola data referensi yang digunakan pada Sistem Informasi Persuratan.</p>
		</div>
		<Button type="button" class="h-10 rounded-lg px-4 text-[12px] font-semibold" onclick={openCreate}>
			<Plus class="size-4"/>
			Tambah {config.singular}
		</Button>
	</div>

	<div class="master-tabs">
		<button type="button" class:tab-active={activeType==='ROLE'} class="tab-button" onclick={()=>changeTab('ROLE')}>
			<span class="tab-icon tab-purple"><BadgeCheck/></span>
			<span>Role</span>
		</button>
		<button type="button" class:tab-active={activeType==='UNIT_KERJA'} class="tab-button" onclick={()=>changeTab('UNIT_KERJA')}>
			<span class="tab-icon tab-blue"><Landmark/></span>
			<span>Unit Kerja</span>
		</button>
		<button type="button" class:tab-active={activeType==='KATEGORI_SURAT'} class="tab-button" onclick={()=>changeTab('KATEGORI_SURAT')}>
			<span class="tab-icon tab-amber"><FolderTree/></span>
			<span>Kategori Surat</span>
		</button>
		<button type="button" class:tab-active={activeType==='SIFAT_SURAT'} class="tab-button" onclick={()=>changeTab('SIFAT_SURAT')}>
			<span class="tab-icon tab-red"><LockKeyhole/></span>
			<span>Sifat Surat</span>
		</button>
	</div>

	<section class="mt-5 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
		<Card.Root class="shadow-none transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md">
			<Card.Content class="p-6">
				<div class="flex items-start justify-between">
					<p class="text-[13px] font-semibold">Total Data</p>
					<div class="flex size-10 items-center justify-center rounded-xl bg-slate-100 text-slate-700"><Boxes class="size-[18px]"/></div>
				</div>
				<p class="mt-5 text-[32px] font-bold tracking-[-0.04em]">{typeData.length}</p>
				<p class="mt-1 text-[12px] font-medium text-muted-foreground">Total {config.label.toLowerCase()}</p>
			</Card.Content>
		</Card.Root>

		<Card.Root class="shadow-none transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md">
			<Card.Content class="p-6">
				<div class="flex items-start justify-between">
					<p class="text-[13px] font-semibold">Aktif</p>
					<div class="flex size-10 items-center justify-center rounded-xl bg-emerald-50 text-emerald-600"><CircleCheck class="size-[18px]"/></div>
				</div>
				<p class="mt-5 text-[32px] font-bold tracking-[-0.04em]">{activeCount}</p>
				<p class="mt-1 text-[12px] font-medium text-muted-foreground">Dapat digunakan sistem</p>
			</Card.Content>
		</Card.Root>

		<Card.Root class="shadow-none transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md">
			<Card.Content class="p-6">
				<div class="flex items-start justify-between">
					<p class="text-[13px] font-semibold">Nonaktif</p>
					<div class="flex size-10 items-center justify-center rounded-xl bg-zinc-100 text-zinc-500"><CircleX class="size-[18px]"/></div>
				</div>
				<p class="mt-5 text-[32px] font-bold tracking-[-0.04em]">{inactiveCount}</p>
				<p class="mt-1 text-[12px] font-medium text-muted-foreground">Tidak dapat digunakan</p>
			</Card.Content>
		</Card.Root>

		<Card.Root class="shadow-none transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md">
			<Card.Content class="p-6">
				<div class="flex items-start justify-between">
					<p class="text-[13px] font-semibold">Digunakan</p>
					<div class="flex size-10 items-center justify-center rounded-xl bg-blue-50 text-blue-600"><Link2 class="size-[18px]"/></div>
				</div>
				<p class="mt-5 text-[32px] font-bold tracking-[-0.04em]">{usageCount}</p>
				<p class="mt-1 text-[12px] font-medium text-muted-foreground">{config.usageLabel} terhubung</p>
			</Card.Content>
		</Card.Root>
	</section>

	<Card.Root class="mt-5 shadow-none">
		<Card.Content class="p-4">
			<div class="flex flex-col gap-3 md:flex-row">
				<div class="relative flex-1">
					<Search class="pointer-events-none absolute left-3.5 top-1/2 size-[17px] -translate-y-1/2 text-muted-foreground"/>
					<Input bind:value={search} oninput={()=>currentPage=1} placeholder={config.searchPlaceholder} class="h-11 rounded-lg pl-10 pr-9 text-[13px] font-medium"/>
					{#if search}
						<button type="button" class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground" onclick={()=>{search='';currentPage=1;}}>
							<X class="size-4"/>
						</button>
					{/if}
				</div>
				<select bind:value={statusFilter} onchange={()=>currentPage=1} class="h-11 rounded-lg border border-input bg-background px-3 text-[13px] font-medium outline-none md:w-[180px]">
					<option value="ALL">Semua Status</option>
					<option value="ACTIVE">Aktif</option>
					<option value="INACTIVE">Nonaktif</option>
				</select>
				{#if hasFilter}
					<Button type="button" variant="outline" class="h-11 rounded-lg text-[12px] font-semibold" onclick={resetFilter}>
						<RotateCcw class="size-4"/>
						Reset
					</Button>
				{/if}
			</div>
		</Card.Content>
	</Card.Root>

	<Card.Root class="mt-4 overflow-hidden shadow-none">
		<Card.Header class="flex-row items-center justify-between border-b px-5 py-4">
			<div>
				<Card.Title class="text-[15px] font-bold">{config.title}</Card.Title>
				<Card.Description class="mt-1 text-[12px] font-medium">{config.description}</Card.Description>
			</div>
			<span class="hidden rounded-full bg-muted px-3 py-1 text-[10px] font-semibold text-muted-foreground sm:inline-flex">{filteredData.length} data</span>
		</Card.Header>

		<div class="hidden overflow-x-auto md:block">
			<table class="w-full border-collapse">
				<thead>
					<tr class="border-b bg-muted/35">
						<th class="h-12 px-5 text-left text-[12px] font-bold">Nama</th>
						<th class="h-12 px-4 text-left text-[12px] font-bold">Kode</th>
						<th class="h-12 px-4 text-left text-[12px] font-bold">Deskripsi</th>
						<th class="h-12 px-4 text-left text-[12px] font-bold">Digunakan</th>
						<th class="h-12 px-4 text-left text-[12px] font-bold">Status</th>
						<th class="h-12 w-16 px-4 text-right text-[12px] font-bold">Aksi</th>
					</tr>
				</thead>
				<tbody>
					{#if paginatedData.length===0}
						<tr>
							<td colspan="6">
								<div class="flex min-h-[250px] flex-col items-center justify-center text-center">
									<div class="flex size-12 items-center justify-center rounded-xl bg-muted"><Boxes class="size-5 text-muted-foreground"/></div>
									<p class="mt-4 text-[14px] font-bold">Data tidak ditemukan</p>
									<p class="mt-1 text-[12px] font-medium text-muted-foreground">Coba ubah pencarian atau filter.</p>
								</div>
							</td>
						</tr>
					{:else}
						{#each paginatedData as item(item.id)}
							<tr class="border-b transition-colors last:border-b-0 hover:bg-muted/25">
								<td class="px-5 py-4">
									<p class="text-[13px] font-bold">{item.name}</p>
									<p class="mt-1 text-[10px] font-medium text-muted-foreground">Dibuat {item.createdAt}</p>
								</td>
								<td class="px-4 py-4">
									<span class="rounded-md bg-muted px-2.5 py-1.5 font-mono text-[10px] font-semibold">{item.code}</span>
								</td>
								<td class="max-w-[360px] px-4 py-4">
									<p class="line-clamp-2 text-[12px] font-medium leading-5 text-muted-foreground">{item.description||'-'}</p>
								</td>
								<td class="px-4 py-4">
									<span class="text-[12px] font-semibold">{item.usageCount}</span>
									<span class="text-[10px] text-muted-foreground">{config.usageLabel}</span>
								</td>
								<td class="px-4 py-4">
									{#if item.status==='ACTIVE'}
										<span class="inline-flex items-center gap-1.5 rounded-full border border-emerald-200 bg-emerald-50 px-3 py-1.5 text-[10px] font-semibold text-emerald-700">
											<span class="size-1.5 rounded-full bg-emerald-500"></span>
											Aktif
										</span>
									{:else}
										<span class="inline-flex items-center gap-1.5 rounded-full border bg-muted/40 px-3 py-1.5 text-[10px] font-semibold">
											<span class="size-1.5 rounded-full bg-zinc-400"></span>
											Nonaktif
										</span>
									{/if}
								</td>
								<td class="px-4 py-4 text-right">
									<DropdownMenu.Root>
										<DropdownMenu.Trigger>
											{#snippet child({props})}
												<button {...props} type="button" class="inline-flex size-9 items-center justify-center rounded-lg text-muted-foreground hover:bg-muted hover:text-foreground">
													<MoreHorizontal class="size-[18px]"/>
												</button>
												
											{/snippet}
										</DropdownMenu.Trigger>
										<DropdownMenu.Content align="end" class="w-52">
											<DropdownMenu.Label>Aksi Data</DropdownMenu.Label>
											<DropdownMenu.Separator/>
											<DropdownMenu.Item onclick={()=>openEdit(item)}>
												<Pencil/>
												Edit
											</DropdownMenu.Item>
											<DropdownMenu.Item disabled={item.type==='ROLE'&&item.code==='SUPER_ADMIN'} onclick={()=>toggleStatus(item)}>
												<Power/>
												{item.status==='ACTIVE'?'Nonaktifkan':'Aktifkan'}
											</DropdownMenu.Item>
											<DropdownMenu.Separator/>
											<DropdownMenu.Item disabled={item.type==='ROLE'&&item.code==='SUPER_ADMIN'} class="text-red-600 focus:text-red-600" onclick={()=>deleteTarget=item}>
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

		<div class="divide-y md:hidden">
			{#each paginatedData as item(item.id)}
				<div class="p-4">
					<div class="flex items-start justify-between gap-3">
						<div>
							<p class="text-[13px] font-bold">{item.name}</p>
							<p class="mt-1 font-mono text-[10px] font-semibold text-muted-foreground">{item.code}</p>
						</div>
						<Button type="button" variant="ghost" size="icon" class="size-9" onclick={()=>openEdit(item)}>
							<Pencil class="size-4"/>
						</Button>
					</div>
					<p class="mt-3 text-[11px] font-medium leading-5 text-muted-foreground">{item.description||'Tidak ada deskripsi.'}</p>
					<div class="mt-4 grid grid-cols-2 gap-3 rounded-xl bg-muted/30 p-3">
						<div>
							<p class="text-[10px] text-muted-foreground">Digunakan</p>
							<p class="mt-1 text-[11px] font-bold">{item.usageCount} {config.usageLabel}</p>
						</div>
						<div>
							<p class="text-[10px] text-muted-foreground">Status</p>
							<p class="mt-1 text-[11px] font-bold">{item.status==='ACTIVE'?'Aktif':'Nonaktif'}</p>
						</div>
					</div>
				</div>
			{/each}
		</div>

		<div class="flex flex-col gap-3 border-t px-5 py-4 sm:flex-row sm:items-center sm:justify-between">
			<p class="text-[11px] font-medium text-muted-foreground">
				Menampilkan <span class="font-bold text-foreground">{firstItem}–{lastItem}</span> dari <span class="font-bold text-foreground">{filteredData.length}</span> data
			</p>
			<div class="flex items-center gap-1">
				<Button type="button" variant="outline" size="icon" class="size-9 rounded-lg" disabled={currentPage<=1} onclick={()=>currentPage-=1}>
					<ChevronLeft class="size-4"/>
				</Button>
				<div class="flex h-9 min-w-14 items-center justify-center rounded-lg border px-3 text-[11px] font-bold">{currentPage} / {totalPages}</div>
				<Button type="button" variant="outline" size="icon" class="size-9 rounded-lg" disabled={currentPage>=totalPages} onclick={()=>currentPage+=1}>
					<ChevronRight class="size-4"/>
				</Button>
			</div>
		</div>
	</Card.Root>
</div>

<MasterDataModal open={formOpen} mode={formMode} type={activeType} record={selectedRecord} onSave={saveRecord} onOpenChange={(value)=>{formOpen=value;if(!value)selectedRecord=null;}}/>

{#if deleteTarget}
	<div class="fixed inset-0 z-[200] flex items-center justify-center bg-black/45 p-4 backdrop-blur-[3px]">
		<div class="w-full max-w-md overflow-hidden rounded-2xl border bg-background shadow-2xl">
			<div class="p-5">
				<div class="flex size-11 items-center justify-center rounded-xl bg-red-50 text-red-600">
					<TriangleAlert class="size-5"/>
				</div>
				<h2 class="mt-4 text-lg font-bold">Hapus {MASTER_DATA_CONFIG[deleteTarget.type].singular}?</h2>
				<p class="mt-1.5 text-[13px] font-medium leading-6 text-muted-foreground">
					{deleteTarget.usageCount>0?'Data ini masih digunakan. Sistem akan mencegah penghapusan sampai tidak lagi terhubung.':'Data yang dihapus tidak akan tampil pada pilihan sistem.'}
				</p>
				<div class="mt-4 rounded-xl border bg-muted/30 p-3">
					<p class="text-[13px] font-bold">{deleteTarget.name}</p>
					<p class="mt-1 font-mono text-[10px] font-semibold text-muted-foreground">{deleteTarget.code}</p>
				</div>
			</div>
			<div class="flex justify-end gap-2 border-t bg-muted/20 px-5 py-3">
				<Button type="button" variant="outline" size="sm" onclick={()=>deleteTarget=null}>Batal</Button>
				<Button type="button" variant="destructive" size="sm" onclick={confirmDelete}>
					<Trash2 class="size-3.5"/>
					Hapus
				</Button>
			</div>
		</div>
	</div>
{/if}

{#if notification}
	<div class="fixed bottom-5 right-5 z-[300] w-[calc(100%-40px)] max-w-sm rounded-xl border bg-background p-4 shadow-xl">
		<div class="flex items-start gap-3">
			<div class="flex size-9 shrink-0 items-center justify-center rounded-full bg-emerald-50 text-emerald-600">
				<CircleCheck class="size-4"/>
			</div>
			<div class="flex-1">
				<p class="text-[12px] font-bold">Informasi</p>
				<p class="mt-1 text-[11px] font-medium leading-5 text-muted-foreground">{notification}</p>
			</div>
			<button type="button" onclick={()=>notification=null}>
				<X class="size-4"/>
			</button>
		</div>
	</div>
{/if}

<style>
	.master-tabs{
		display:grid;
		grid-template-columns:repeat(4,minmax(0,1fr));
		gap:8px;
		margin-top:24px;
		padding:5px;
		border:1px solid var(--border);
		border-radius:14px;
		background:var(--muted);
	}
	.tab-button{
		display:flex;
		min-width:0;
		height:48px;
		align-items:center;
		justify-content:center;
		gap:9px;
		border-radius:10px;
		padding:0 14px;
		font-size:12px;
		font-weight:600;
		color:var(--muted-foreground);
		transition:.15s ease;
	}
	.tab-button:hover{
		background:rgb(255 255 255/.65);
		color:var(--foreground);
	}
	.tab-button.tab-active{
		background:var(--background);
		color:var(--foreground);
		box-shadow:0 1px 4px rgb(0 0 0/.08);
	}
	.tab-icon{
		display:flex;
		width:30px;
		height:30px;
		flex-shrink:0;
		align-items:center;
		justify-content:center;
		border-radius:8px;
	}
	.tab-icon :global(svg){
		width:16px;
		height:16px;
		stroke-width:1.9;
	}
	.tab-purple{
		background:#f5f3ff;
		color:#7c3aed;
	}
	.tab-blue{
		background:#eff6ff;
		color:#2563eb;
	}
	.tab-amber{
		background:#fffbeb;
		color:#d97706;
	}
	.tab-red{
		background:#fff1f2;
		color:#e11d48;
	}
	@media(max-width:800px){
		.master-tabs{
			grid-template-columns:repeat(2,minmax(0,1fr));
		}
	}
	@media(max-width:480px){
		.master-tabs{
			grid-template-columns:1fr;
		}
		.tab-button{
			justify-content:flex-start;
		}
	}
</style>