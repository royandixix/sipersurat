<script lang="ts">
	import{browser}from'$app/environment';
	import{onMount}from'svelte';
	import{fly}from'svelte/transition';
	import*as Card from'$lib/components/ui/card/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import Settings from'@lucide/svelte/icons/settings';
	import Building2 from'@lucide/svelte/icons/building-2';
	import Hash from'@lucide/svelte/icons/hash';
	import Bell from'@lucide/svelte/icons/bell';
	import ShieldCheck from'@lucide/svelte/icons/shield-check';
	import DatabaseBackup from'@lucide/svelte/icons/database-backup';
	import Save from'@lucide/svelte/icons/save';
	import RotateCcw from'@lucide/svelte/icons/rotate-ccw';
	import CircleCheck from'@lucide/svelte/icons/circle-check';
	import TriangleAlert from'@lucide/svelte/icons/triangle-alert';
	import X from'@lucide/svelte/icons/x';
	import Trash2 from'@lucide/svelte/icons/trash-2';

	import InstitutionSettings from'$lib/features/pengaturan/components/InstitutionSettings.svelte';
	import NumberingSettings from'$lib/features/pengaturan/components/NumberingSettings.svelte';
	import NotificationSettings from'$lib/features/pengaturan/components/NotificationSettings.svelte';
	import SecuritySettings from'$lib/features/pengaturan/components/SecuritySettings.svelte';
	import SystemDataSettings from'$lib/features/pengaturan/components/SystemDataSettings.svelte';

	import type{SystemSettings}from'$lib/features/pengaturan/types';
	import{
		SETTINGS_STORAGE_KEY,
		cloneDefaultSystemSettings
	}from'$lib/features/pengaturan/types';

	type Tab=
		|'institution'
		|'numbering'
		|'notifications'
		|'security'
		|'data';

	const DATA_KEYS=[
		'sipersurat-incoming-mail',
		'sipersurat-outgoing-mail',
		'sipersurat-dispositions',
		'sipersurat-master-data',
		'sipersurat-users'
	];

	const BACKUP_KEYS=[
		SETTINGS_STORAGE_KEY,
		...DATA_KEYS
	];

	const tabs=[
		{
			id:'institution',
			label:'Profil Instansi',
			description:'Identitas organisasi',
			icon:Building2
		},
		{
			id:'numbering',
			label:'Penomoran & Agenda',
			description:'Format nomor dan periode',
			icon:Hash
		},
		{
			id:'notifications',
			label:'Notifikasi',
			description:'Pemberitahuan sistem',
			icon:Bell
		},
		{
			id:'security',
			label:'Keamanan',
			description:'Sesi dan password',
			icon:ShieldCheck
		},
		{
			id:'data',
			label:'Data Sistem',
			description:'Backup dan pemulihan',
			icon:DatabaseBackup
		}
	]as const;

	let settings=$state<SystemSettings>(
		cloneDefaultSystemSettings()
	);

	let activeTab=$state<Tab>('institution');
	let initialized=$state(false);
	let hasChanges=$state(false);

	let confirmAction=$state<
		'SETTINGS'|'DATA'|null
	>(null);

	let notification=$state<{
		type:'success'|'error'|'info';
		title:string;
		message:string;
	}|null>(null);

	let notificationTimer:
		ReturnType<typeof setTimeout>|undefined;

	onMount(()=>{
		if(!browser)return;

		const saved=localStorage.getItem(
			SETTINGS_STORAGE_KEY
		);

		if(saved){
			try{
				settings=normalizeSettings(
					JSON.parse(saved)
				);
			}catch{
				settings=cloneDefaultSystemSettings();
			}
		}

		initialized=true;
	});

	function normalizeSettings(
		input:unknown
	):SystemSettings{
		const defaults=
			cloneDefaultSystemSettings();

		if(
			!input||
			typeof input!=='object'
		){
			return defaults;
		}

		const source=
			input as Partial<SystemSettings>;

		return{
			institution:{
				...defaults.institution,
				...(source.institution??{})
			},
			numbering:{
				...defaults.numbering,
				...(source.numbering??{})
			},
			agenda:{
				...defaults.agenda,
				...(source.agenda??{})
			},
			notifications:{
				...defaults.notifications,
				...(source.notifications??{})
			},
			security:{
				...defaults.security,
				...(source.security??{})
			},
			appearance:{
				...defaults.appearance,
				...(source.appearance??{})
			},
			updatedAt:
				source.updatedAt??''
		};
	}

	function markChanged(){
		hasChanges=true;
	}

	function showNotification(
		title:string,
		message:string,
		type:'success'|'error'|'info'='success'
	){
		notification={
			type,
			title,
			message
		};

		if(notificationTimer){
			clearTimeout(notificationTimer);
		}

		notificationTimer=setTimeout(()=>{
			notification=null;
		},3500);
	}

	function saveSettings(){
		if(!browser)return;

		if(!settings.institution.name.trim()){
			showNotification(
				'Data belum lengkap',
				'Nama instansi wajib diisi.',
				'error'
			);

			activeTab='institution';
			return;
		}

		settings={
			...settings,
			institution:{
				...settings.institution,
				name:
					settings.institution.name.trim()
			},
			updatedAt:new Date().toISOString()
		};

		try{
			localStorage.setItem(
				SETTINGS_STORAGE_KEY,
				JSON.stringify(settings)
			);

			hasChanges=false;

			showNotification(
				'Pengaturan tersimpan',
				'Perubahan konfigurasi SiPersurat berhasil disimpan.'
			);
		}catch{
			showNotification(
				'Gagal menyimpan',
				'Penyimpanan browser tidak dapat menyimpan konfigurasi. Ukuran logo mungkin terlalu besar.',
				'error'
			);
		}
	}

	function resetSettings(){
		settings=
			cloneDefaultSystemSettings();

		if(browser){
			localStorage.setItem(
				SETTINGS_STORAGE_KEY,
				JSON.stringify(settings)
			);
		}

		hasChanges=false;
		confirmAction=null;

		showNotification(
			'Pengaturan dipulihkan',
			'Seluruh konfigurasi dikembalikan ke nilai default.'
		);
	}

	function resetOperationalData(){
		if(!browser)return;

		for(const key of DATA_KEYS){
			localStorage.removeItem(key);
		}

		confirmAction=null;

		showNotification(
			'Data berhasil direset',
			'Data operasional lokal telah dihapus. Pengaturan sistem tetap dipertahankan.'
		);
	}

	function exportBackup(){
		if(!browser)return;

		const data:
			Record<string,unknown>={};

		for(const key of BACKUP_KEYS){
			const raw=
				localStorage.getItem(key);

			if(!raw){
				data[key]=null;
				continue;
			}

			try{
				data[key]=JSON.parse(raw);
			}catch{
				data[key]=raw;
			}
		}

		const backup={
			application:'SiPersurat',
			version:1,
			exportedAt:
				new Date().toISOString(),
			data
		};

		const blob=new Blob(
			[
				JSON.stringify(
					backup,
					null,
					2
				)
			],
			{
				type:
					'application/json;charset=utf-8'
			}
		);

		const url=
			URL.createObjectURL(blob);

		const link=
			document.createElement('a');

		link.href=url;

		link.download=
			`backup-sipersurat-${
				new Date()
					.toISOString()
					.slice(0,10)
			}.json`;

		document.body.appendChild(link);

		link.click();

		link.remove();

		URL.revokeObjectURL(url);

		showNotification(
			'Backup berhasil dibuat',
			'File backup SiPersurat berhasil diekspor.'
		);
	}

	async function importBackup(
		file:File
	){
		if(!browser)return;

		try{
			const content=
				await file.text();

			const parsed=
				JSON.parse(content);

			if(
				!parsed||
				parsed.application!=='SiPersurat'||
				!parsed.data||
				typeof parsed.data!=='object'
			){
				throw new Error(
					'invalid-backup'
				);
			}

			const importedData=
				parsed.data as Record<
					string,
					unknown
				>;

			for(const key of BACKUP_KEYS){
				if(!(key in importedData)){
					continue;
				}

				const value=
					importedData[key];

				if(value===null){
					localStorage.removeItem(key);
					continue;
				}

				if(typeof value==='string'){
					localStorage.setItem(
						key,
						value
					);
				}else{
					localStorage.setItem(
						key,
						JSON.stringify(value)
					);
				}
			}

			const importedSettings=
				importedData[
					SETTINGS_STORAGE_KEY
				];

			if(importedSettings){
				if(
					typeof importedSettings===
					'string'
				){
					try{
						settings=
							normalizeSettings(
								JSON.parse(
									importedSettings
								)
							);
					}catch{
						settings=
							cloneDefaultSystemSettings();
					}
				}else{
					settings=
						normalizeSettings(
							importedSettings
						);
				}
			}

			hasChanges=false;

			showNotification(
				'Backup berhasil dipulihkan',
				'Data dari file backup berhasil dimasukkan ke SiPersurat.'
			);
		}catch{
			showNotification(
				'Import gagal',
				'File yang dipilih bukan backup SiPersurat yang valid.',
				'error'
			);
		}
	}
</script>

<svelte:head>
	<title>Pengaturan | SiPersurat</title>
</svelte:head>

<div class="w-full min-w-0 overflow-x-hidden">
	<div class="mx-auto w-full min-w-0 max-w-[1500px] px-5 py-7 md:px-8 lg:px-10">
		<div class="flex flex-col justify-between gap-6 lg:flex-row lg:items-start">
			<div
				class="min-w-0"
				in:fly={{
					y:18,
					duration:450
				}}
			>
				<div class="flex items-center gap-2">
					<div class="flex size-8 items-center justify-center rounded-lg bg-muted text-muted-foreground">
						<Settings class="size-4"/>
					</div>

					<h1 class="text-2xl font-bold tracking-tight md:text-[28px]">
						Pengaturan Sistem
					</h1>
				</div>

				<p class="mt-3 max-w-3xl text-sm leading-6 text-muted-foreground">
					Kelola profil instansi, penomoran surat, agenda, notifikasi, keamanan, serta data sistem SiPersurat dari satu halaman konfigurasi.
				</p>
			</div>

			<div
				class="flex shrink-0 flex-col gap-2 sm:flex-row"
				in:fly={{
					y:14,
					duration:420,
					delay:100
				}}
			>
				<Button
					type="button"
					variant="outline"
					class="h-10 rounded-lg px-4 text-xs font-semibold"
					onclick={()=>
						confirmAction='SETTINGS'
					}
				>
					<RotateCcw class="size-4"/>
					Default
				</Button>

				<Button
					type="button"
					class="h-10 rounded-lg px-4 text-xs font-semibold transition-all hover:-translate-y-0.5 hover:shadow-md"
					onclick={saveSettings}
				>
					<Save class="size-4"/>

					Simpan Pengaturan

					{#if hasChanges}
						<span class="ml-1 size-1.5 rounded-full bg-amber-300"></span>
					{/if}
				</Button>
			</div>
		</div>

		{#if initialized}
			<div class="mt-8 grid min-w-0 gap-5 lg:grid-cols-[250px_minmax(0,1fr)]">
				<div
					class="min-w-0"
					in:fly={{
						x:-18,
						duration:450,
						delay:120
					}}
				>
					<Card.Root class="overflow-hidden rounded-xl border shadow-none">
						<Card.Content class="p-2">
							<div class="flex gap-2 overflow-x-auto lg:flex-col lg:overflow-visible">
								{#each tabs as tab}
									{@const Icon=tab.icon}

									<button
										type="button"
										class={[
											'flex min-w-[190px] items-center gap-3 rounded-lg px-3 py-3 text-left transition lg:min-w-0',
											activeTab===tab.id
												?'bg-foreground text-background'
												:'text-muted-foreground hover:bg-muted hover:text-foreground'
										]}
										onclick={()=>
											activeTab=
												tab.id as Tab
										}
									>
										<Icon class="size-4 shrink-0"/>

										<div class="min-w-0">
											<p class="text-xs font-semibold">
												{tab.label}
											</p>

											<p
												class={[
													'mt-0.5 truncate text-[9px]',
													activeTab===tab.id
														?'text-background/65'
														:'text-muted-foreground'
												]}
											>
												{tab.description}
											</p>
										</div>
									</button>
								{/each}
							</div>
						</Card.Content>
					</Card.Root>

					<div class="mt-4 hidden rounded-xl border bg-muted/20 p-4 lg:block">
						<p class="text-[10px] font-semibold text-muted-foreground">
							Status Konfigurasi
						</p>

						<div class="mt-3 flex items-center gap-2">
							<span
								class={[
									'size-2 rounded-full',
									hasChanges
										?'bg-amber-500'
										:'bg-emerald-500'
								]}
							></span>

							<p class="text-[11px] font-semibold">
								{hasChanges
									?'Ada perubahan belum disimpan'
									:'Pengaturan tersimpan'}
							</p>
						</div>
					</div>
				</div>

				<div
					class="min-w-0"
					in:fly={{
						x:20,
						duration:450,
						delay:190
					}}
				>
					<Card.Root class="min-w-0 rounded-xl border shadow-none">
						<Card.Content class="p-5 sm:p-6 lg:p-7">
							{#if activeTab==='institution'}
								<InstitutionSettings
									value={settings.institution}
									onChange={(value)=>{
										settings={
											...settings,
											institution:value
										};

										markChanged();
									}}
								/>
							{:else if activeTab==='numbering'}
								<NumberingSettings
									numbering={settings.numbering}
									agenda={settings.agenda}
									onNumberingChange={(value)=>{
										settings={
											...settings,
											numbering:value
										};

										markChanged();
									}}
									onAgendaChange={(value)=>{
										settings={
											...settings,
											agenda:value
										};

										markChanged();
									}}
								/>
							{:else if activeTab==='notifications'}
								<NotificationSettings
									value={settings.notifications}
									onChange={(value)=>{
										settings={
											...settings,
											notifications:value
										};

										markChanged();
									}}
								/>
							{:else if activeTab==='security'}
								<SecuritySettings
									value={settings.security}
									onChange={(value)=>{
										settings={
											...settings,
											security:value
										};

										markChanged();
									}}
								/>
							{:else}
								<SystemDataSettings
									onExport={exportBackup}
									onImport={importBackup}
									onReset={()=>
										confirmAction='DATA'
									}
								/>
							{/if}
						</Card.Content>
					</Card.Root>
				</div>
			</div>
		{:else}
			<div class="mt-8 grid gap-5 lg:grid-cols-[250px_minmax(0,1fr)]">
				<div class="h-[330px] animate-pulse rounded-xl border bg-muted/30"></div>

				<div class="h-[560px] animate-pulse rounded-xl border bg-muted/30"></div>
			</div>
		{/if}
	</div>
</div>

{#if confirmAction}
	<div
		class="fixed inset-0 z-[300] flex items-center justify-center bg-black/35 p-4 backdrop-blur-md"
		role="presentation"
	>
		<div
			class="w-full max-w-md overflow-hidden rounded-xl border bg-background shadow-2xl"
			in:fly={{
				y:15,
				duration:220
			}}
		>
			<div class="p-5">
				<div
					class={[
						'flex size-10 items-center justify-center rounded-lg',
						confirmAction==='DATA'
							?'bg-red-50 text-red-600'
							:'bg-amber-50 text-amber-700'
					]}
				>
					{#if confirmAction==='DATA'}
						<Trash2 class="size-5"/>
					{:else}
						<RotateCcw class="size-5"/>
					{/if}
				</div>

				<h2 class="mt-4 text-lg font-bold">
					{confirmAction==='DATA'
						?'Reset data operasional?'
						:'Kembalikan pengaturan default?'}
				</h2>

				<p class="mt-2 text-sm leading-6 text-muted-foreground">
					{confirmAction==='DATA'
						?'Surat Masuk, Surat Keluar, Disposisi, Master Data, dan data Pengguna pada browser akan dihapus. Tindakan ini tidak dapat dibatalkan.'
						:'Seluruh konfigurasi Pengaturan Sistem akan kembali ke nilai awal. Data Surat Masuk, Surat Keluar, Disposisi, dan Arsip tidak akan dihapus.'}
				</p>

				{#if confirmAction==='DATA'}
					<div class="mt-4 rounded-lg border border-red-200 bg-red-50/60 p-3">
						<p class="text-[10px] font-semibold leading-5 text-red-700">
							Sebaiknya lakukan Export Backup sebelum melakukan reset data.
						</p>
					</div>
				{/if}
			</div>

			<div class="flex justify-end gap-2 border-t p-4">
				<Button
					type="button"
					variant="outline"
					class="rounded-lg"
					onclick={()=>
						confirmAction=null
					}
				>
					Batal
				</Button>

				{#if confirmAction==='DATA'}
					<Button
						type="button"
						variant="destructive"
						class="rounded-lg"
						onclick={resetOperationalData}
					>
						<Trash2 class="size-4"/>
						Reset Data
					</Button>
				{:else}
					<Button
						type="button"
						class="rounded-lg"
						onclick={resetSettings}
					>
						<RotateCcw class="size-4"/>
						Kembalikan Default
					</Button>
				{/if}
			</div>
		</div>
	</div>
{/if}

{#if notification}
	<div
		class="fixed bottom-5 right-5 z-[350] flex w-[calc(100%-40px)] max-w-sm items-start gap-3 rounded-xl border bg-background p-4 shadow-xl"
		in:fly={{
			x:30,
			duration:250
		}}
	>
		<div
			class={[
				'flex size-9 shrink-0 items-center justify-center rounded-full',
				notification.type==='success'
					?'bg-emerald-50 text-emerald-600'
					:notification.type==='error'
						?'bg-red-50 text-red-600'
						:'bg-blue-50 text-blue-600'
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
				{notification.title}
			</p>

			<p class="mt-1 text-[11px] leading-5 text-muted-foreground">
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