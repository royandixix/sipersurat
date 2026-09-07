<script lang="ts">
	import Bell from'@lucide/svelte/icons/bell';
	import Clock3 from'@lucide/svelte/icons/clock-3';
	import type{NotificationConfig}from'$lib/features/pengaturan/types';

	type Props={
		value:NotificationConfig;
		onChange:(value:NotificationConfig)=>void;
	};

	let{value,onChange}:Props=$props();

	const options=[
		{
			key:'incomingMail',
			title:'Surat Masuk Baru',
			description:'Tampilkan notifikasi ketika surat masuk baru berhasil dicatat.'
		},
		{
			key:'disposition',
			title:'Disposisi Baru',
			description:'Tampilkan pemberitahuan ketika ada disposisi baru yang harus ditindaklanjuti.'
		},
		{
			key:'outgoingApproval',
			title:'Menunggu Persetujuan',
			description:'Notifikasi ketika surat keluar diajukan untuk proses persetujuan.'
		},
		{
			key:'outgoingApproved',
			title:'Surat Disetujui',
			description:'Notifikasi ketika surat keluar telah mendapatkan persetujuan.'
		},
		{
			key:'dispositionDeadline',
			title:'Batas Waktu Disposisi',
			description:'Berikan pengingat untuk disposisi yang mendekati batas waktu.'
		},
		{
			key:'archive',
			title:'Aktivitas Arsip',
			description:'Notifikasi ketika dokumen dipindahkan atau dipulihkan dari arsip.'
		},
		{
			key:'system',
			title:'Notifikasi Sistem',
			description:'Informasi terkait perubahan konfigurasi dan aktivitas sistem.'
		}
	]as const;

	function toggle(
		key:keyof NotificationConfig
	){
		const current=value[key];

		if(typeof current!=='boolean')return;

		onChange({
			...value,
			[key]:!current
		});
	}
</script>

<div>
	<div class="flex items-start gap-3">
		<div class="flex size-9 shrink-0 items-center justify-center rounded-lg bg-muted text-muted-foreground">
			<Bell class="size-4"/>
		</div>

		<div>
			<h2 class="text-sm font-semibold">
				Notifikasi
			</h2>

			<p class="mt-1 text-xs leading-5 text-muted-foreground">
				Tentukan aktivitas apa saja yang perlu menampilkan pemberitahuan kepada pengguna.
			</p>
		</div>
	</div>

	<div class="mt-6 divide-y overflow-hidden rounded-xl border">
		{#each options as option}
			<button
				type="button"
				class="flex w-full items-center justify-between gap-5 bg-background p-4 text-left transition hover:bg-muted/30"
				aria-pressed={Boolean(value[option.key])}
				onclick={()=>toggle(option.key)}
			>
				<div class="min-w-0">
					<p class="text-xs font-semibold">
						{option.title}
					</p>

					<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
						{option.description}
					</p>
				</div>

				<span
					class={[
						'relative h-6 w-11 shrink-0 rounded-full transition',
						value[option.key]
							?'bg-foreground'
							:'bg-muted'
					]}
				>
					<span
						class={[
							'absolute top-1 size-4 rounded-full bg-background shadow transition-all',
							value[option.key]
								?'left-6'
								:'left-1'
						]}
					></span>
				</span>
			</button>
		{/each}
	</div>

	<div class="mt-6 rounded-xl border p-4">
		<div class="flex items-start gap-3">
			<div class="flex size-8 shrink-0 items-center justify-center rounded-lg bg-amber-50 text-amber-700">
				<Clock3 class="size-4"/>
			</div>

			<div class="min-w-0 flex-1">
				<label
					for="deadline-reminder"
					class="text-xs font-semibold"
				>
					Pengingat Batas Waktu
				</label>

				<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
					Tentukan berapa hari sebelum jatuh tempo sistem mulai memberikan pengingat disposisi.
				</p>

				<div class="mt-3 flex items-center gap-2">
					<input
						id="deadline-reminder"
						type="number"
						min="1"
						max="30"
						value={value.deadlineReminderDays}
						class="h-10 w-24 rounded-lg border border-input bg-background px-3 text-xs font-semibold outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
						oninput={(event)=>
							onChange({
								...value,
								deadlineReminderDays:Math.min(
									30,
									Math.max(
										1,
										Number(
											(event.currentTarget as HTMLInputElement).value
										)||1
									)
								)
							})}
					/>

					<span class="text-xs text-muted-foreground">
						hari sebelumnya
					</span>
				</div>
			</div>
		</div>
	</div>
</div>