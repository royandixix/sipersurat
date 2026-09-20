<script lang="ts">
	import DatabaseBackup from'@lucide/svelte/icons/database-backup';
	import Download from'@lucide/svelte/icons/download';
	import Upload from'@lucide/svelte/icons/upload';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import HardDrive from'@lucide/svelte/icons/hard-drive';

	type Props={
		onExport:()=>void;
		onImport:(file:File)=>void|Promise<void>;
		onReset:()=>void;
	};

	let{
		onExport,
		onImport,
		onReset
	}:Props=$props();

	let fileInput=$state<HTMLInputElement|null>(null);

	function importFile(event:Event){
		const input=event.currentTarget as HTMLInputElement;
		const file=input.files?.[0];

		if(!file)return;

		void onImport(file);

		input.value='';
	}
</script>

<div>
	<div class="flex items-start gap-3">
		<div class="flex size-9 shrink-0 items-center justify-center rounded-lg bg-muted text-muted-foreground">
			<DatabaseBackup class="size-4"/>
		</div>

		<div>
			<h2 class="text-sm font-semibold">
				Data Sistem
			</h2>

			<p class="mt-1 text-xs leading-5 text-muted-foreground">
				Kelola backup, pemulihan, dan data database aplikasi SiPersurat.
			</p>
		</div>
	</div>

	<div class="mt-6 grid gap-4 lg:grid-cols-2">
		<div class="rounded-xl border p-5">
			<div class="flex size-9 items-center justify-center rounded-lg bg-blue-50 text-blue-700">
				<Download class="size-4"/>
			</div>

			<h3 class="mt-4 text-sm font-semibold">
				Backup Data
			</h3>

			<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
				Export data Surat Masuk, Surat Keluar, Disposisi, Master Data, Pengguna, dan Pengaturan ke satu file JSON.
			</p>

			<button
				type="button"
				class="mt-5 inline-flex h-10 items-center gap-2 rounded-lg bg-foreground px-4 text-[11px] font-semibold text-background transition hover:opacity-90"
				onclick={onExport}
			>
				<Download class="size-4"/>
				Export Backup
			</button>
		</div>

		<div class="rounded-xl border p-5">
			<div class="flex size-9 items-center justify-center rounded-lg bg-violet-50 text-violet-700">
				<Upload class="size-4"/>
			</div>

			<h3 class="mt-4 text-sm font-semibold">
				Import Backup
			</h3>

			<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
				Pulihkan data dari file backup JSON SiPersurat yang sebelumnya telah diekspor.
			</p>

			<input
				id="settings-import-file"
				bind:this={fileInput}
				type="file"
				class="hidden"
				accept=".json,application/json"
				onchange={importFile}
			/>

			<button
				type="button"
				class="mt-5 inline-flex h-10 items-center gap-2 rounded-lg border bg-background px-4 text-[11px] font-semibold transition hover:bg-muted"
				onclick={()=>fileInput?.click()}
			>
				<Upload class="size-4"/>
				Pilih File Backup
			</button>
		</div>
	</div>

	<div class="mt-5 rounded-xl border p-5">
		<div class="flex flex-col justify-between gap-5 sm:flex-row sm:items-center">
			<div class="flex min-w-0 items-start gap-3">
				<div class="flex size-9 shrink-0 items-center justify-center rounded-lg bg-muted text-muted-foreground">
					<HardDrive class="size-4"/>
				</div>

				<div>
					<h3 class="text-sm font-semibold">
						Penyimpanan Database
					</h3>

					<p class="mt-1 max-w-xl text-[10px] leading-5 text-muted-foreground">
						Data utama aplikasi tersimpan pada PostgreSQL Supabase dan diakses melalui backend API Spring Boot.
					</p>
				</div>
			</div>

			<span class="w-fit rounded-md bg-emerald-50 px-2.5 py-1.5 text-[10px] font-semibold text-emerald-700">
				Backend Terintegrasi
			</span>
		</div>
	</div>

	<div class="mt-5 overflow-hidden rounded-xl border border-red-200">
		<div class="bg-red-50/50 p-5">
			<div class="flex flex-col justify-between gap-5 sm:flex-row sm:items-center">
				<div>
					<h3 class="text-sm font-semibold text-red-700">
						Reset Data Operasional
					</h3>

					<p class="mt-1 max-w-xl text-[10px] leading-5 text-red-700/70">
						Menghapus Surat Masuk, Surat Keluar, Disposisi, dan Arsip dari database. Master Data, akun Pengguna, dan Pengaturan Sistem tetap dipertahankan.
					</p>
				</div>

				<button
					type="button"
					class="inline-flex h-10 shrink-0 items-center justify-center gap-2 rounded-lg bg-red-600 px-4 text-[11px] font-semibold text-white transition hover:bg-red-700"
					onclick={onReset}
				>
					<Trash2 class="size-4"/>
					Reset Data
				</button>
			</div>
		</div>
	</div>
</div>