<script lang="ts">
	import{browser}from'$app/environment';
	import{onMount}from'svelte';
	import{fade,fly,scale}from'svelte/transition';
	import*as Card from'$lib/components/ui/card/index.js';
	import*as DropdownMenu from'$lib/components/ui/dropdown-menu/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import{Input}from'$lib/components/ui/input/index.js';

	import Search from'@lucide/svelte/icons/search';
	import Plus from'@lucide/svelte/icons/plus';
	import Users from'@lucide/svelte/icons/users';
	import UserCheck from'@lucide/svelte/icons/user-check';
	import UserX from'@lucide/svelte/icons/user-x';
	import Shield from'@lucide/svelte/icons/shield';
	import X from'@lucide/svelte/icons/x';
	import CircleCheck from'@lucide/svelte/icons/circle-check';
	import TriangleAlert from'@lucide/svelte/icons/triangle-alert';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import MoreHorizontal from'@lucide/svelte/icons/more-horizontal';
	import Pencil from'@lucide/svelte/icons/pencil';
	import KeyRound from'@lucide/svelte/icons/key-round';
	import Power from'@lucide/svelte/icons/power';
	import ChevronLeft from'@lucide/svelte/icons/chevron-left';
	import ChevronRight from'@lucide/svelte/icons/chevron-right';
	import RotateCcw from'@lucide/svelte/icons/rotate-ccw';
	import SlidersHorizontal from'@lucide/svelte/icons/sliders-horizontal';
	import Sparkles from'@lucide/svelte/icons/sparkles';

	import UserFormSheet from'$lib/features/pengguna/components/UserFormSheet.svelte';

	import type{
		UserFormPayload,
		UserRecord,
		UserStatus
	}from'$lib/features/pengguna/types';

	import{
		ROLE_LABELS,
		ROLE_OPTIONS,
		getInitials
	}from'$lib/features/pengguna/types';

	import type{UserRole}from'$lib/types/auth';

	const STORAGE_KEY='sipersurat-users';
	const perPage=6;

	const defaultUsers:UserRecord[]=[
		{
			id:1,
			name:'Super Administrator',
			email:'admin@sipersurat.id',
			role:'SUPER_ADMIN',
			department:'Administrator',
			status:'ACTIVE',
			createdAt:'01 Agu 2026',
			lastLogin:'Hari ini, 10:42'
		},
		{
			id:2,
			name:'Ahmad Ramadhan',
			email:'ahmad@sipersurat.id',
			role:'ADMIN_PERSURATAN',
			department:'Tata Usaha',
			status:'ACTIVE',
			createdAt:'02 Agu 2026',
			lastLogin:'Hari ini, 09:15'
		},
		{
			id:3,
			name:'Budi Santoso',
			email:'budi@sipersurat.id',
			role:'PIMPINAN',
			department:'Pimpinan',
			status:'ACTIVE',
			createdAt:'02 Agu 2026',
			lastLogin:'Kemarin, 17:20'
		},
		{
			id:4,
			name:'Cindy Maharani',
			email:'cindy@sipersurat.id',
			role:'STAFF',
			department:'Keuangan',
			status:'ACTIVE',
			createdAt:'03 Agu 2026',
			lastLogin:'Kemarin, 16:44'
		},
		{
			id:5,
			name:'Dimas Pratama',
			email:'dimas@sipersurat.id',
			role:'STAFF',
			department:'Sekretariat',
			status:'INACTIVE',
			createdAt:'04 Agu 2026',
			lastLogin:'08 Agu 2026'
		},
		{
			id:6,
			name:'Andi Saputra',
			email:'andi@sipersurat.id',
			role:'ADMIN_PERSURATAN',
			department:'Tata Usaha',
			status:'ACTIVE',
			createdAt:'05 Agu 2026',
			lastLogin:'Hari ini, 08:50'
		},
		{
			id:7,
			name:'Nur Aisyah',
			email:'aisyah@sipersurat.id',
			role:'STAFF',
			department:'Kepegawaian',
			status:'ACTIVE',
			createdAt:'06 Agu 2026',
			lastLogin:'12 Agu 2026'
		},
		{
			id:8,
			name:'Muhammad Akbar',
			email:'akbar@sipersurat.id',
			role:'PIMPINAN',
			department:'Pimpinan',
			status:'ACTIVE',
			createdAt:'07 Agu 2026',
			lastLogin:'Hari ini, 08:10'
		},
		{
			id:9,
			name:'Siti Rahma',
			email:'rahma@sipersurat.id',
			role:'STAFF',
			department:'Umum',
			status:'INACTIVE',
			createdAt:'08 Agu 2026',
			lastLogin:'08 Agu 2026'
		}
	];

	let users=$state<UserRecord[]>([
		...defaultUsers
	]);

	let initialized=$state(false);

	let search=$state('');

	let roleFilter=$state<
		'ALL'|UserRole
	>('ALL');

	let statusFilter=$state<
		'ALL'|UserStatus
	>('ALL');

	let currentPage=$state(1);

	let createOpen=$state(false);
	let editOpen=$state(false);

	let selectedUser=$state<
		UserRecord|null
	>(null);

	let deleteUserTarget=$state<
		UserRecord|null
	>(null);

	let notification=$state<{
		type:'success'|'info';
		message:string;
	}|null>(null);

	let notificationTimer:
		ReturnType<typeof setTimeout>|undefined;

	onMount(()=>{
		if(!browser)return;

		const saved=
			localStorage.getItem(
				STORAGE_KEY
			);

		if(saved){
			try{
				const parsed=
					JSON.parse(saved);

				if(Array.isArray(parsed)){
					users=parsed;
				}
			}catch{
				users=[
					...defaultUsers
				];
			}
		}

		requestAnimationFrame(()=>{
			initialized=true;
		});
	});

	$effect(()=>{
		if(
			!browser||
			!initialized
		){
			return;
		}

		localStorage.setItem(
			STORAGE_KEY,
			JSON.stringify(users)
		);
	});

	const filteredUsers=$derived(
		users.filter((user)=>{
			const keyword=
				search
					.trim()
					.toLowerCase();

			const name=
				(user.name??'')
					.toLowerCase();

			const email=
				(user.email??'')
					.toLowerCase();

			const department=
				(user.department??'')
					.toLowerCase();

			const matchesSearch=
				!keyword||
				name.includes(keyword)||
				email.includes(keyword)||
				department.includes(keyword);

			const matchesRole=
				roleFilter==='ALL'||
				user.role===roleFilter;

			const matchesStatus=
				statusFilter==='ALL'||
				user.status===statusFilter;

			return(
				matchesSearch&&
				matchesRole&&
				matchesStatus
			);
		})
	);

	const totalPages=$derived(
		Math.max(
			1,
			Math.ceil(
				filteredUsers.length/
				perPage
			)
		)
	);

	const paginatedUsers=$derived(
		filteredUsers.slice(
			(currentPage-1)*perPage,
			currentPage*perPage
		)
	);

	const activeCount=$derived(
		users.filter(
			(user)=>
				user.status==='ACTIVE'
		).length
	);

	const inactiveCount=$derived(
		users.filter(
			(user)=>
				user.status==='INACTIVE'
		).length
	);

	const superAdminCount=$derived(
		users.filter(
			(user)=>
				user.role==='SUPER_ADMIN'
		).length
	);

	const hasFilter=$derived(
		search.trim()!==''||
		roleFilter!=='ALL'||
		statusFilter!=='ALL'
	);

	const firstItem=$derived(
		filteredUsers.length===0
			?0
			:(currentPage-1)*perPage+1
	);

	const lastItem=$derived(
		Math.min(
			currentPage*perPage,
			filteredUsers.length
		)
	);

	$effect(()=>{
		if(
			currentPage>
			totalPages
		){
			currentPage=
				totalPages;
		}
	});

	function notify(
		message:string,
		type:'success'|'info'='success'
	){
		notification={
			type,
			message
		};

		if(notificationTimer){
			clearTimeout(
				notificationTimer
			);
		}

		notificationTimer=
			setTimeout(()=>{
				notification=null;
			},3000);
	}

	function formattedToday(){
		return new Intl.DateTimeFormat(
			'id-ID',
			{
				day:'2-digit',
				month:'short',
				year:'numeric'
			}
		).format(
			new Date()
		);
	}

	function createUser(
		payload:UserFormPayload
	){
		const duplicate=
			users.some(
				(user)=>
					(user.email??'')
						.toLowerCase()===
					payload.email
						.toLowerCase()
			);

		if(duplicate){
			notify(
				'Email tersebut sudah digunakan.',
				'info'
			);

			return;
		}

		const nextId=
			Math.max(
				0,
				...users.map(
					(user)=>user.id
				)
			)+1;

		users=[
			{
				id:nextId,
				name:payload.name,
				email:payload.email,
				role:payload.role,
				department:
					payload.department,
				status:payload.status,
				createdAt:
					formattedToday(),
				lastLogin:
					'Belum pernah login'
			},
			...users
		];

		createOpen=false;
		currentPage=1;

		notify(
			`${payload.name} berhasil ditambahkan.`
		);
	}

	function openEdit(
		user:UserRecord
	){
		selectedUser=user;
		editOpen=true;
	}

	function updateUser(
		payload:UserFormPayload
	){
		if(!selectedUser)return;

		const duplicate=
			users.some(
				(user)=>
					user.id!==
						selectedUser?.id&&
					(user.email??'')
						.toLowerCase()===
					payload.email
						.toLowerCase()
			);

		if(duplicate){
			notify(
				'Email tersebut sudah digunakan.',
				'info'
			);

			return;
		}

		users=
			users.map((user)=>
				user.id===
				selectedUser?.id
					?{
						...user,
						name:payload.name,
						email:payload.email,
						role:payload.role,
						department:
							payload.department,
						status:
							payload.status
					}
					:user
			);

		editOpen=false;
		selectedUser=null;

		notify(
			`${payload.name} berhasil diperbarui.`
		);
	}

	function toggleStatus(
		user:UserRecord
	){
		if(user.id===1)return;

		const nextStatus:
			UserStatus=
				user.status==='ACTIVE'
					?'INACTIVE'
					:'ACTIVE';

		users=
			users.map((item)=>
				item.id===user.id
					?{
						...item,
						status:
							nextStatus
					}
					:item
			);

		notify(
			nextStatus==='ACTIVE'
				?`${user.name} berhasil diaktifkan.`
				:`${user.name} berhasil dinonaktifkan.`
		);
	}

	function resetPassword(
		user:UserRecord
	){
		notify(
			`Reset password ${user.email} berhasil disiapkan.`,
			'info'
		);
	}

	function confirmDelete(){
		if(
			!deleteUserTarget||
			deleteUserTarget.id===1
		){
			return;
		}

		const name=
			deleteUserTarget.name;

		users=
			users.filter(
				(user)=>
					user.id!==
						deleteUserTarget?.id
			);

		deleteUserTarget=null;

		notify(
			`${name} berhasil dihapus.`
		);
	}

	function resetFilters(){
		search='';
		roleFilter='ALL';
		statusFilter='ALL';
		currentPage=1;
	}

	function showAllUsers(){
		resetFilters();
	}

	function filterActive(){
		search='';
		roleFilter='ALL';
		statusFilter='ACTIVE';
		currentPage=1;
	}

	function filterInactive(){
		search='';
		roleFilter='ALL';
		statusFilter='INACTIVE';
		currentPage=1;
	}

	function filterSuperAdmin(){
		search='';
		statusFilter='ALL';
		roleFilter='SUPER_ADMIN';
		currentPage=1;
	}

	function roleClass(
		role:UserRole
	){
		switch(role){
			case'SUPER_ADMIN':
				return'border-violet-200 bg-violet-50 text-violet-700';

			case'ADMIN_PERSURATAN':
				return'border-blue-200 bg-blue-50 text-blue-700';

			case'PIMPINAN':
				return'border-amber-200 bg-amber-50 text-amber-700';

			default:
				return'border-border bg-muted/40 text-foreground';
		}
	}
</script>

<svelte:head>
	<title>Pengguna | SiPersurat</title>
</svelte:head>

<div class="w-full min-w-0 overflow-x-hidden">
	<div class="mx-auto w-full max-w-[1600px] px-5 py-7 md:px-8 lg:px-9">
		<div
			class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
			in:fly={{
				y:18,
				duration:450
			}}
		>
			<div>
				<div class="flex items-center gap-2">
					<h1 class="text-[26px] font-bold tracking-[-0.03em] md:text-[30px]">
						Pengguna
					</h1>

					<span class="hidden items-center gap-1.5 rounded-full bg-muted px-2.5 py-1 text-[9px] font-semibold text-muted-foreground sm:inline-flex">
						<Sparkles class="size-3"/>
						Manajemen Akun
					</span>
				</div>

				<p class="mt-1.5 text-[13px] font-medium text-muted-foreground">
					Kelola akun pengguna dan hak akses Sistem Informasi Persuratan.
				</p>
			</div>

			<Button
				type="button"
				class="h-10 rounded-lg px-4 text-[12px] font-semibold shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md"
				onclick={()=>
					createOpen=true
				}
			>
				<Plus class="size-4"/>
				Tambah Pengguna
			</Button>
		</div>

		{#if initialized}
			<section class="mt-7 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
				<div
					in:fly={{
						y:22,
						duration:450,
						delay:80
					}}
				>
					<Card.Root
						class={[
							'group h-full shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md',
							!hasFilter
								?'border-foreground/20'
								:''
						]}
					>
						<Card.Content class="p-6">
							<div class="flex items-start justify-between">
								<p class="text-[13px] font-semibold">
									Total Pengguna
								</p>

								<div class="flex size-10 items-center justify-center rounded-xl bg-muted transition-all duration-200 group-hover:scale-110 group-hover:bg-foreground group-hover:text-background">
									<Users class="size-[18px]"/>
								</div>
							</div>

							<p class="mt-5 text-[32px] font-bold tracking-[-0.04em]">
								{users.length}
							</p>

							<div class="mt-2 flex items-center justify-between gap-3">
								<p class="text-[12px] font-medium text-muted-foreground">
									Seluruh akun terdaftar
								</p>

								<button
									type="button"
									class="text-[10px] font-semibold text-muted-foreground transition hover:text-foreground"
									onclick={showAllUsers}
								>
									Lihat semua
								</button>
							</div>
						</Card.Content>
					</Card.Root>
				</div>

				<div
					in:fly={{
						y:22,
						duration:450,
						delay:140
					}}
				>
					<Card.Root
						class={[
							'group h-full shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md',
							statusFilter==='ACTIVE'
								?'border-emerald-300 ring-2 ring-emerald-100'
								:''
						]}
					>
						<Card.Content class="p-6">
							<div class="flex items-start justify-between">
								<p class="text-[13px] font-semibold">
									Pengguna Aktif
								</p>

								<div class="flex size-10 items-center justify-center rounded-xl bg-emerald-50 text-emerald-600 transition-all duration-200 group-hover:scale-110">
									<UserCheck class="size-[18px]"/>
								</div>
							</div>

							<p class="mt-5 text-[32px] font-bold tracking-[-0.04em]">
								{activeCount}
							</p>

							<div class="mt-2 flex items-center justify-between gap-3">
								<p class="text-[12px] font-medium text-muted-foreground">
									Dapat mengakses sistem
								</p>

								<button
									type="button"
									class="text-[10px] font-semibold text-emerald-600 transition hover:text-emerald-700"
									onclick={filterActive}
								>
									Filter
								</button>
							</div>
						</Card.Content>
					</Card.Root>
				</div>

				<div
					in:fly={{
						y:22,
						duration:450,
						delay:200
					}}
				>
					<Card.Root
						class={[
							'group h-full shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md',
							statusFilter==='INACTIVE'
								?'border-zinc-400 ring-2 ring-zinc-100'
								:''
						]}
					>
						<Card.Content class="p-6">
							<div class="flex items-start justify-between">
								<p class="text-[13px] font-semibold">
									Nonaktif
								</p>

								<div class="flex size-10 items-center justify-center rounded-xl bg-muted text-muted-foreground transition-all duration-200 group-hover:scale-110">
									<UserX class="size-[18px]"/>
								</div>
							</div>

							<p class="mt-5 text-[32px] font-bold tracking-[-0.04em]">
								{inactiveCount}
							</p>

							<div class="mt-2 flex items-center justify-between gap-3">
								<p class="text-[12px] font-medium text-muted-foreground">
									Akses dihentikan
								</p>

								<button
									type="button"
									class="text-[10px] font-semibold text-muted-foreground transition hover:text-foreground"
									onclick={filterInactive}
								>
									Filter
								</button>
							</div>
						</Card.Content>
					</Card.Root>
				</div>

				<div
					in:fly={{
						y:22,
						duration:450,
						delay:260
					}}
				>
					<Card.Root
						class={[
							'group h-full shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md',
							roleFilter==='SUPER_ADMIN'
								?'border-violet-300 ring-2 ring-violet-100'
								:''
						]}
					>
						<Card.Content class="p-6">
							<div class="flex items-start justify-between">
								<p class="text-[13px] font-semibold">
									Super Administrator
								</p>

								<div class="flex size-10 items-center justify-center rounded-xl bg-violet-50 text-violet-600 transition-all duration-200 group-hover:scale-110">
									<Shield class="size-[18px]"/>
								</div>
							</div>

							<p class="mt-5 text-[32px] font-bold tracking-[-0.04em]">
								{superAdminCount}
							</p>

							<div class="mt-2 flex items-center justify-between gap-3">
								<p class="text-[12px] font-medium text-muted-foreground">
									Memiliki akses penuh
								</p>

								<button
									type="button"
									class="text-[10px] font-semibold text-violet-600 transition hover:text-violet-700"
									onclick={filterSuperAdmin}
								>
									Filter
								</button>
							</div>
						</Card.Content>
					</Card.Root>
				</div>
			</section>

			<div
				in:fly={{
					y:20,
					duration:450,
					delay:320
				}}
			>
				<Card.Root class="mt-5 shadow-none transition-shadow duration-200 hover:shadow-sm">
					<Card.Content class="p-4">
						<div class="flex flex-col gap-3 xl:flex-row xl:items-center">
							<div class="relative min-w-0 flex-1">
								<Search class="pointer-events-none absolute left-3.5 top-1/2 size-[17px] -translate-y-1/2 text-muted-foreground"/>

								<Input
									bind:value={search}
									oninput={()=>
										currentPage=1
									}
									placeholder="Cari nama, email, atau unit kerja..."
									class="h-11 rounded-lg pl-10 pr-9 text-[13px] font-medium transition-shadow focus:shadow-sm"
								/>

								{#if search}
									<button
										type="button"
										class="absolute right-3 top-1/2 flex size-6 -translate-y-1/2 items-center justify-center rounded-md text-muted-foreground transition hover:bg-muted hover:text-foreground"
										aria-label="Hapus pencarian"
										onclick={()=>{
											search='';
											currentPage=1;
										}}
										in:scale={{
											duration:130,
											start:.75
										}}
									>
										<X class="size-3.5"/>
									</button>
								{/if}
							</div>

							<div class="flex flex-col gap-2 sm:flex-row">
								<div class="relative">
									<SlidersHorizontal class="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

									<select
										bind:value={roleFilter}
										onchange={()=>
											currentPage=1
										}
										class="h-11 w-full rounded-lg border border-input bg-background pl-9 pr-9 text-[13px] font-medium outline-none transition focus:border-ring focus:ring-2 focus:ring-ring/10 sm:w-[195px]"
									>
										<option value="ALL">
											Semua Role
										</option>

										{#each ROLE_OPTIONS as option}
											<option value={option.value}>
												{option.label}
											</option>
										{/each}
									</select>
								</div>

								<select
									bind:value={statusFilter}
									onchange={()=>
										currentPage=1
									}
									class="h-11 w-full rounded-lg border border-input bg-background px-3 text-[13px] font-medium outline-none transition focus:border-ring focus:ring-2 focus:ring-ring/10 sm:w-[170px]"
								>
									<option value="ALL">
										Semua Status
									</option>

									<option value="ACTIVE">
										Aktif
									</option>

									<option value="INACTIVE">
										Nonaktif
									</option>
								</select>

								{#if hasFilter}
									<div
										in:fly={{
											x:8,
											duration:180
										}}
									>
										<Button
											type="button"
											variant="outline"
											class="h-11 w-full rounded-lg text-[12px] font-semibold transition-all hover:-translate-y-0.5 sm:w-auto"
											onclick={resetFilters}
										>
											<RotateCcw class="size-4"/>
											Reset
										</Button>
									</div>
								{/if}
							</div>
						</div>

						{#if hasFilter}
							<div
								class="mt-3 flex flex-wrap items-center gap-2 border-t pt-3"
								in:fade={{
									duration:180
								}}
							>
								<span class="text-[10px] font-semibold text-muted-foreground">
									Filter aktif:
								</span>

								{#if search}
									<span class="rounded-md bg-muted px-2 py-1 text-[9px] font-semibold">
										"{search}"
									</span>
								{/if}

								{#if roleFilter!=='ALL'}
									<span class="rounded-md bg-blue-50 px-2 py-1 text-[9px] font-semibold text-blue-700">
										{ROLE_LABELS[roleFilter]}
									</span>
								{/if}

								{#if statusFilter!=='ALL'}
									<span
										class={[
											'rounded-md px-2 py-1 text-[9px] font-semibold',
											statusFilter==='ACTIVE'
												?'bg-emerald-50 text-emerald-700'
												:'bg-zinc-100 text-zinc-700'
										]}
									>
										{statusFilter==='ACTIVE'
											?'Aktif'
											:'Nonaktif'}
									</span>
								{/if}

								<span class="ml-auto text-[10px] font-medium text-muted-foreground">
									{filteredUsers.length} hasil
								</span>
							</div>
						{/if}
					</Card.Content>
				</Card.Root>
			</div>

			<div
				in:fly={{
					y:24,
					duration:480,
					delay:380
				}}
			>
				<Card.Root class="mt-4 overflow-hidden shadow-none transition-shadow duration-200 hover:shadow-sm">
					<Card.Header class="flex-row items-center justify-between border-b px-5 py-4">
						<div>
							<Card.Title class="text-[15px] font-bold">
								Daftar Pengguna
							</Card.Title>

							<Card.Description class="mt-1 text-[12px] font-medium">
								{filteredUsers.length} pengguna ditemukan pada sistem.
							</Card.Description>
						</div>

						<div class="hidden items-center gap-2 sm:flex">
							<span class="relative flex size-2">
								<span class="absolute inline-flex size-full animate-ping rounded-full bg-emerald-400 opacity-60"></span>
								<span class="relative inline-flex size-2 rounded-full bg-emerald-500"></span>
							</span>

							<span class="text-[11px] font-medium text-muted-foreground">
								Data pengguna
							</span>
						</div>
					</Card.Header>

					<div class="hidden overflow-x-auto md:block">
						<table class="w-full border-collapse">
							<thead>
								<tr class="border-b bg-muted/35">
									<th class="h-12 px-5 text-left text-[12px] font-bold">
										Pengguna
									</th>

									<th class="h-12 px-4 text-left text-[12px] font-bold">
										Role
									</th>

									<th class="h-12 px-4 text-left text-[12px] font-bold">
										Unit Kerja
									</th>

									<th class="h-12 px-4 text-left text-[12px] font-bold">
										Status
									</th>

									<th class="h-12 px-4 text-left text-[12px] font-bold">
										Login Terakhir
									</th>

									<th class="h-12 w-16 px-4 text-right text-[12px] font-bold">
										Aksi
									</th>
								</tr>
							</thead>

							<tbody>
								{#if paginatedUsers.length===0}
									<tr>
										<td colspan="6">
											<div
												class="flex min-h-[260px] flex-col items-center justify-center px-5 text-center"
												in:fade={{
													duration:220
												}}
											>
												<div class="flex size-12 items-center justify-center rounded-xl bg-muted">
													<Users class="size-5 text-muted-foreground"/>
												</div>

												<p class="mt-4 text-[14px] font-bold">
													Pengguna tidak ditemukan
												</p>

												<p class="mt-1 text-[12px] font-medium text-muted-foreground">
													Coba ubah kata pencarian atau filter yang digunakan.
												</p>

												{#if hasFilter}
													<Button
														type="button"
														variant="outline"
														size="sm"
														class="mt-4 rounded-lg"
														onclick={resetFilters}
													>
														<RotateCcw class="size-3.5"/>
														Reset Filter
													</Button>
												{/if}
											</div>
										</td>
									</tr>
								{:else}
									{#each paginatedUsers as user,index(user.id)}
										<tr
											class="group border-b transition-colors last:border-b-0 hover:bg-muted/25"
											in:fade={{
												duration:200,
												delay:
													Math.min(
														index*35,
														180
													)
											}}
										>
											<td class="px-5 py-3.5">
												<div class="flex min-w-[240px] items-center gap-3">
													<div class="flex size-10 shrink-0 items-center justify-center rounded-full border bg-muted/40 text-[11px] font-semibold text-muted-foreground transition-all duration-200 group-hover:scale-105 group-hover:border-foreground/20 group-hover:bg-background">
														{getInitials(user.name)}
													</div>

													<div class="min-w-0">
														<div class="flex items-center gap-2">
															<p class="truncate text-[13px] font-bold">
																{user.name}
															</p>

															{#if user.id===1}
																<span class="rounded bg-muted px-1.5 py-0.5 text-[9px] font-semibold text-muted-foreground">
																	ANDA
																</span>
															{/if}
														</div>

														<p class="mt-1 truncate text-[11px] font-medium text-muted-foreground">
															{user.email}
														</p>
													</div>
												</div>
											</td>

											<td class="px-4 py-3.5">
												<span
													class={`inline-flex rounded-full border px-3 py-1.5 text-[10px] font-semibold transition-transform duration-200 group-hover:scale-[1.03] ${roleClass(user.role)}`}
												>
													{ROLE_LABELS[user.role]}
												</span>
											</td>

											<td class="px-4 py-3.5">
												<p class="whitespace-nowrap text-[12px] font-medium">
													{user.department}
												</p>
											</td>

											<td class="px-4 py-3.5">
												{#if user.status==='ACTIVE'}
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

											<td class="px-4 py-3.5">
												<p class="whitespace-nowrap text-[12px] font-semibold">
													{user.lastLogin}
												</p>

												<p class="mt-1 whitespace-nowrap text-[10px] font-medium text-muted-foreground">
													Dibuat {user.createdAt}
												</p>
											</td>

											<td class="px-4 py-3.5 text-right">
												<DropdownMenu.Root>
													<DropdownMenu.Trigger>
														{#snippet child({props})}
															<button
																{...props}
																type="button"
																class="inline-flex size-9 items-center justify-center rounded-lg text-muted-foreground transition-all duration-150 hover:scale-105 hover:bg-muted hover:text-foreground active:scale-95"
																aria-label={`Aksi ${user.name}`}
															>
																<MoreHorizontal class="size-[18px]"/>
															</button>
														{/snippet}
													</DropdownMenu.Trigger>

													<DropdownMenu.Content
														align="end"
														class="w-52"
													>
														<DropdownMenu.Label>
															Aksi Pengguna
														</DropdownMenu.Label>

														<DropdownMenu.Separator/>

														<DropdownMenu.Item
															onclick={()=>
																openEdit(user)
															}
														>
															<Pencil/>
															Edit Pengguna
														</DropdownMenu.Item>

														<DropdownMenu.Item
															onclick={()=>
																resetPassword(user)
															}
														>
															<KeyRound/>
															Reset Password
														</DropdownMenu.Item>

														<DropdownMenu.Item
															disabled={user.id===1}
															onclick={()=>
																toggleStatus(user)
															}
														>
															<Power/>

															{user.status==='ACTIVE'
																?'Nonaktifkan'
																:'Aktifkan'}
														</DropdownMenu.Item>

														<DropdownMenu.Separator/>

														<DropdownMenu.Item
															disabled={user.id===1}
															class="text-red-600 focus:text-red-600"
															onclick={()=>
																deleteUserTarget=user
															}
														>
															<Trash2/>
															Hapus Pengguna
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
						{#if paginatedUsers.length===0}
							<div
								class="flex min-h-[220px] flex-col items-center justify-center p-6 text-center"
								in:fade={{
									duration:220
								}}
							>
								<Users class="size-6 text-muted-foreground"/>

								<p class="mt-3 text-[14px] font-bold">
									Pengguna tidak ditemukan
								</p>

								{#if hasFilter}
									<Button
										type="button"
										variant="outline"
										size="sm"
										class="mt-4"
										onclick={resetFilters}
									>
										<RotateCcw class="size-3.5"/>
										Reset
									</Button>
								{/if}
							</div>
						{:else}
							{#each paginatedUsers as user,index(user.id)}
								<div
									class="p-4 transition-colors hover:bg-muted/20"
									in:fly={{
										x:16,
										duration:240,
										delay:
											Math.min(
												index*45,
												200
											)
									}}
								>
									<div class="flex items-start justify-between gap-3">
										<div class="flex min-w-0 items-center gap-3">
											<div class="flex size-11 shrink-0 items-center justify-center rounded-full border bg-muted/40 text-[11px] font-semibold">
												{getInitials(user.name)}
											</div>

											<div class="min-w-0">
												<div class="flex items-center gap-2">
													<p class="truncate text-[13px] font-bold">
														{user.name}
													</p>

													{#if user.id===1}
														<span class="rounded bg-muted px-1.5 py-0.5 text-[8px] font-semibold text-muted-foreground">
															ANDA
														</span>
													{/if}
												</div>

												<p class="mt-1 truncate text-[11px] font-medium text-muted-foreground">
													{user.email}
												</p>
											</div>
										</div>

										<DropdownMenu.Root>
											<DropdownMenu.Trigger>
												{#snippet child({props})}
													<button
														{...props}
														type="button"
														class="flex size-9 shrink-0 items-center justify-center rounded-lg text-muted-foreground transition hover:bg-muted hover:text-foreground"
														aria-label={`Aksi ${user.name}`}
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
													onclick={()=>
														openEdit(user)
													}
												>
													<Pencil/>
													Edit
												</DropdownMenu.Item>

												<DropdownMenu.Item
													onclick={()=>
														resetPassword(user)
													}
												>
													<KeyRound/>
													Reset Password
												</DropdownMenu.Item>

												<DropdownMenu.Item
													disabled={user.id===1}
													onclick={()=>
														toggleStatus(user)
													}
												>
													<Power/>

													{user.status==='ACTIVE'
														?'Nonaktifkan'
														:'Aktifkan'}
												</DropdownMenu.Item>

												<DropdownMenu.Separator/>

												<DropdownMenu.Item
													disabled={user.id===1}
													class="text-red-600 focus:text-red-600"
													onclick={()=>
														deleteUserTarget=user
													}
												>
													<Trash2/>
													Hapus
												</DropdownMenu.Item>
											</DropdownMenu.Content>
										</DropdownMenu.Root>
									</div>

									<div class="mt-4 grid grid-cols-2 gap-3 rounded-xl bg-muted/30 p-3">
										<div>
											<p class="text-[10px] font-medium text-muted-foreground">
												Role
											</p>

											<p class="mt-1 text-[11px] font-semibold">
												{ROLE_LABELS[user.role]}
											</p>
										</div>

										<div>
											<p class="text-[10px] font-medium text-muted-foreground">
												Unit Kerja
											</p>

											<p class="mt-1 truncate text-[11px] font-semibold">
												{user.department}
											</p>
										</div>

										<div>
											<p class="text-[10px] font-medium text-muted-foreground">
												Status
											</p>

											<p
												class={[
													'mt-1 text-[11px] font-semibold',
													user.status==='ACTIVE'
														?'text-emerald-700'
														:'text-muted-foreground'
												]}
											>
												{user.status==='ACTIVE'
													?'Aktif'
													:'Nonaktif'}
											</p>
										</div>

										<div>
											<p class="text-[10px] font-medium text-muted-foreground">
												Login
											</p>

											<p class="mt-1 truncate text-[11px] font-semibold">
												{user.lastLogin}
											</p>
										</div>
									</div>
								</div>
							{/each}
						{/if}
					</div>

					<div class="flex flex-col gap-3 border-t px-5 py-4 sm:flex-row sm:items-center sm:justify-between">
						<p class="text-[11px] font-medium text-muted-foreground">
							Menampilkan
							<span class="font-bold text-foreground">
								{firstItem}–{lastItem}
							</span>
							dari
							<span class="font-bold text-foreground">
								{filteredUsers.length}
							</span>
							pengguna
						</p>

						<div class="flex items-center gap-1">
							<Button
								type="button"
								variant="outline"
								size="icon"
								class="size-9 rounded-lg transition-all hover:-translate-x-0.5"
								disabled={currentPage<=1}
								onclick={()=>
									currentPage-=1
								}
							>
								<ChevronLeft class="size-4"/>
							</Button>

							<div
								class="flex h-9 min-w-14 items-center justify-center rounded-lg border px-3 text-[11px] font-bold"
								in:fade={{
									duration:140
								}}
							>
								{currentPage} / {totalPages}
							</div>

							<Button
								type="button"
								variant="outline"
								size="icon"
								class="size-9 rounded-lg transition-all hover:translate-x-0.5"
								disabled={currentPage>=totalPages}
								onclick={()=>
									currentPage+=1
								}
							>
								<ChevronRight class="size-4"/>
							</Button>
						</div>
					</div>
				</Card.Root>
			</div>
		{:else}
			<div
				class="mt-7 space-y-5"
				in:fade={{
					duration:180
				}}
			>
				<div class="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
					{#each Array(4) as _}
						<div class="h-[174px] animate-pulse rounded-xl border bg-muted/25"></div>
					{/each}
				</div>

				<div class="h-[76px] animate-pulse rounded-xl border bg-muted/25"></div>

				<div class="h-[460px] animate-pulse rounded-xl border bg-muted/25"></div>
			</div>
		{/if}
	</div>
</div>

<UserFormSheet
	open={createOpen}
	mode="create"
	onSave={createUser}
	onOpenChange={(value)=>
		createOpen=value
	}
/>

<UserFormSheet
	open={editOpen}
	mode="edit"
	user={selectedUser}
	onSave={updateUser}
	onOpenChange={(value)=>{
		editOpen=value;

		if(!value){
			selectedUser=null;
		}
	}}
/>

{#if deleteUserTarget}
	<div
		class="fixed inset-0 z-[200] flex items-center justify-center bg-black/45 p-4 backdrop-blur-[4px]"
		role="presentation"
		in:fade={{
			duration:160
		}}
		out:fade={{
			duration:120
		}}
	>
		<div
			class="w-full max-w-md overflow-hidden rounded-2xl border bg-background shadow-2xl"
			in:scale={{
				duration:190,
				start:.96
			}}
		>
			<div class="p-5">
				<div class="flex size-11 items-center justify-center rounded-xl bg-red-50 text-red-600">
					<TriangleAlert class="size-5"/>
				</div>

				<h2 class="mt-4 text-lg font-bold">
					Hapus pengguna?
				</h2>

				<p class="mt-1.5 text-[13px] font-medium leading-6 text-muted-foreground">
					Akun pengguna ini akan dihapus dari SiPersurat.
				</p>

				<div class="mt-4 flex items-center gap-3 rounded-xl border bg-muted/30 p-3">
					<div class="flex size-10 shrink-0 items-center justify-center rounded-full border bg-background text-[11px] font-semibold">
						{getInitials(
							deleteUserTarget.name
						)}
					</div>

					<div class="min-w-0">
						<p class="truncate text-[13px] font-bold">
							{deleteUserTarget.name}
						</p>

						<p class="mt-1 truncate text-[11px] font-medium text-muted-foreground">
							{deleteUserTarget.email}
						</p>
					</div>
				</div>

				<div class="mt-4 rounded-lg border border-red-100 bg-red-50/50 px-3 py-2.5">
					<p class="text-[10px] font-medium leading-5 text-red-700">
						Data pengguna yang telah dihapus tidak dapat dikembalikan dari halaman ini.
					</p>
				</div>
			</div>

			<div class="flex justify-end gap-2 border-t bg-muted/20 px-5 py-3">
				<Button
					type="button"
					variant="outline"
					size="sm"
					onclick={()=>
						deleteUserTarget=null
					}
				>
					Batal
				</Button>

				<Button
					type="button"
					variant="destructive"
					size="sm"
					class="transition-transform active:scale-95"
					onclick={confirmDelete}
				>
					<Trash2 class="size-3.5"/>
					Hapus
				</Button>
			</div>
		</div>
	</div>
{/if}

{#if notification}
	<div
		class="fixed bottom-5 right-5 z-[300] w-[calc(100%-40px)] max-w-sm rounded-xl border bg-background p-4 shadow-xl"
		in:fly={{
			x:30,
			duration:240
		}}
		out:fade={{
			duration:150
		}}
	>
		<div class="flex gap-3">
			<div
				class={[
					'flex size-9 shrink-0 items-center justify-center rounded-full',
					notification.type==='success'
						?'bg-emerald-50 text-emerald-600'
						:'bg-blue-50 text-blue-600'
				]}
			>
				<CircleCheck class="size-4"/>
			</div>

			<div class="min-w-0 flex-1">
				<p class="text-[12px] font-bold">
					{notification.type==='success'
						?'Berhasil'
						:'Informasi'}
				</p>

				<p class="mt-1 text-[11px] font-medium leading-5 text-muted-foreground">
					{notification.message}
				</p>
			</div>

			<button
				type="button"
				class="flex size-7 shrink-0 items-center justify-center rounded-md text-muted-foreground transition hover:bg-muted hover:text-foreground"
				aria-label="Tutup notifikasi"
				onclick={()=>
					notification=null
				}
			>
				<X class="size-4"/>
			</button>
		</div>
	</div>
{/if}