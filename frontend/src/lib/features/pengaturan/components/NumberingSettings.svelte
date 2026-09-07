<script lang="ts">
	import{Input}from'$lib/components/ui/input/index.js';
	import Hash from'@lucide/svelte/icons/hash';
	import CalendarDays from'@lucide/svelte/icons/calendar-days';
	import type{
		AgendaConfig,
		DateFormat,
		NumberingConfig,
		NumberSeparator
	}from'$lib/features/pengaturan/types';

	type Props={
		numbering:NumberingConfig;
		agenda:AgendaConfig;
		onNumberingChange:(value:NumberingConfig)=>void;
		onAgendaChange:(value:AgendaConfig)=>void;
	};

	let{
		numbering,
		agenda,
		onNumberingChange,
		onAgendaChange
	}:Props=$props();

	const incomingPreview=$derived(
		preview(numbering.incomingPrefix)
	);

	const outgoingPreview=$derived(
		preview(numbering.outgoingPrefix)
	);

	const dispositionPreview=$derived(
		preview(numbering.dispositionPrefix)
	);

	function preview(prefix:string){
		const number=String(1).padStart(
			numbering.padding,
			'0'
		);

		return[
			prefix,
			String(numbering.activeYear),
			number
		].join(numbering.separator);
	}

	function updateNumbering<K extends keyof NumberingConfig>(
		field:K,
		value:NumberingConfig[K]
	){
		onNumberingChange({
			...numbering,
			[field]:value
		});
	}

	function updateAgenda<K extends keyof AgendaConfig>(
		field:K,
		value:AgendaConfig[K]
	){
		onAgendaChange({
			...agenda,
			[field]:value
		});
	}
</script>

<div>
	<div class="flex items-start gap-3">
		<div class="flex size-9 shrink-0 items-center justify-center rounded-lg bg-muted text-muted-foreground">
			<Hash class="size-4"/>
		</div>

		<div>
			<h2 class="text-sm font-semibold">
				Penomoran Surat
			</h2>

			<p class="mt-1 text-xs leading-5 text-muted-foreground">
				Atur prefix, tahun aktif, separator, dan pola nomor agenda otomatis.
			</p>
		</div>
	</div>

	<div class="mt-6 grid gap-4 sm:grid-cols-2">
		<div>
			<label
				for="incoming-prefix"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Prefix Surat Masuk
			</label>

			<Input
				id="incoming-prefix"
				value={numbering.incomingPrefix}
				class="h-11 rounded-lg font-mono text-xs uppercase"
				oninput={(event)=>
					updateNumbering(
						'incomingPrefix',
						(event.currentTarget as HTMLInputElement).value
							.toUpperCase()
							.trim()
					)}
			/>
		</div>

		<div>
			<label
				for="outgoing-prefix"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Prefix Surat Keluar
			</label>

			<Input
				id="outgoing-prefix"
				value={numbering.outgoingPrefix}
				class="h-11 rounded-lg font-mono text-xs uppercase"
				oninput={(event)=>
					updateNumbering(
						'outgoingPrefix',
						(event.currentTarget as HTMLInputElement).value
							.toUpperCase()
							.trim()
					)}
			/>
		</div>

		<div>
			<label
				for="disposition-prefix"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Prefix Disposisi
			</label>

			<Input
				id="disposition-prefix"
				value={numbering.dispositionPrefix}
				class="h-11 rounded-lg font-mono text-xs uppercase"
				oninput={(event)=>
					updateNumbering(
						'dispositionPrefix',
						(event.currentTarget as HTMLInputElement).value
							.toUpperCase()
							.trim()
					)}
			/>
		</div>

		<div>
			<label
				for="archive-prefix"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Prefix Arsip
			</label>

			<Input
				id="archive-prefix"
				value={numbering.archivePrefix}
				class="h-11 rounded-lg font-mono text-xs uppercase"
				oninput={(event)=>
					updateNumbering(
						'archivePrefix',
						(event.currentTarget as HTMLInputElement).value
							.toUpperCase()
							.trim()
					)}
			/>
		</div>
	</div>

	<div class="mt-5 grid gap-4 sm:grid-cols-3">
		<div>
			<label
				for="active-year"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Tahun Aktif
			</label>

			<Input
				id="active-year"
				type="number"
				min="2000"
				max="2100"
				value={numbering.activeYear}
				class="h-11 rounded-lg text-xs"
				oninput={(event)=>
					updateNumbering(
						'activeYear',
						Number(
							(event.currentTarget as HTMLInputElement).value
						)||new Date().getFullYear()
					)}
			/>
		</div>

		<div>
			<label
				for="number-separator"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Separator
			</label>

			<select
				id="number-separator"
				value={numbering.separator}
				class="h-11 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
				onchange={(event)=>
					updateNumbering(
						'separator',
						(event.currentTarget as HTMLSelectElement).value as NumberSeparator
					)}
			>
				<option value="-">
					Tanda hubung (-)
				</option>

				<option value="/">
					Garis miring (/)
				</option>

				<option value=".">
					Titik (.)
				</option>
			</select>
		</div>

		<div>
			<label
				for="number-padding"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Digit Nomor
			</label>

			<select
				id="number-padding"
				value={numbering.padding}
				class="h-11 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
				onchange={(event)=>
					updateNumbering(
						'padding',
						Number(
							(event.currentTarget as HTMLSelectElement).value
						)
					)}
			>
				<option value="3">3 digit</option>
				<option value="4">4 digit</option>
				<option value="5">5 digit</option>
				<option value="6">6 digit</option>
			</select>
		</div>
	</div>

	<div class="mt-6 rounded-xl border bg-muted/25 p-4">
		<p class="text-[10px] font-semibold uppercase tracking-wider text-muted-foreground">
			Preview Penomoran
		</p>

		<div class="mt-4 grid gap-3 sm:grid-cols-3">
			<div class="rounded-lg border bg-background p-3">
				<p class="text-[9px] text-muted-foreground">
					Surat Masuk
				</p>

				<p class="mt-2 font-mono text-xs font-bold text-blue-600">
					{incomingPreview}
				</p>
			</div>

			<div class="rounded-lg border bg-background p-3">
				<p class="text-[9px] text-muted-foreground">
					Surat Keluar
				</p>

				<p class="mt-2 font-mono text-xs font-bold text-violet-600">
					{outgoingPreview}
				</p>
			</div>

			<div class="rounded-lg border bg-background p-3">
				<p class="text-[9px] text-muted-foreground">
					Disposisi
				</p>

				<p class="mt-2 font-mono text-xs font-bold text-amber-600">
					{dispositionPreview}
				</p>
			</div>
		</div>
	</div>

	<div class="mt-6 space-y-3">
		<button
			type="button"
			class="flex w-full items-center justify-between gap-4 rounded-xl border p-4 text-left transition hover:bg-muted/30"
			aria-pressed={numbering.autoAgenda}
			onclick={()=>
				updateNumbering(
					'autoAgenda',
					!numbering.autoAgenda
				)}
		>
			<div>
				<p class="text-xs font-semibold">
					Nomor Agenda Otomatis
				</p>

				<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
					Sistem membuat nomor agenda secara otomatis ketika data baru ditambahkan.
				</p>
			</div>

			<span
				class={[
					'relative h-6 w-11 shrink-0 rounded-full transition',
					numbering.autoAgenda
						?'bg-foreground'
						:'bg-muted'
				]}
			>
				<span
					class={[
						'absolute top-1 size-4 rounded-full bg-background shadow transition-all',
						numbering.autoAgenda
							?'left-6'
							:'left-1'
					]}
				></span>
			</span>
		</button>

		<button
			type="button"
			class="flex w-full items-center justify-between gap-4 rounded-xl border p-4 text-left transition hover:bg-muted/30"
			aria-pressed={numbering.resetYearly}
			onclick={()=>
				updateNumbering(
					'resetYearly',
					!numbering.resetYearly
				)}
		>
			<div>
				<p class="text-xs font-semibold">
					Reset Nomor Setiap Tahun
				</p>

				<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
					Urutan agenda kembali ke nomor awal ketika memasuki tahun kerja baru.
				</p>
			</div>

			<span
				class={[
					'relative h-6 w-11 shrink-0 rounded-full transition',
					numbering.resetYearly
						?'bg-foreground'
						:'bg-muted'
				]}
			>
				<span
					class={[
						'absolute top-1 size-4 rounded-full bg-background shadow transition-all',
						numbering.resetYearly
							?'left-6'
							:'left-1'
					]}
				></span>
			</span>
		</button>
	</div>

	<div class="mt-8 border-t pt-6">
		<div class="flex items-start gap-3">
			<div class="flex size-9 shrink-0 items-center justify-center rounded-lg bg-muted text-muted-foreground">
				<CalendarDays class="size-4"/>
			</div>

			<div>
				<h3 class="text-sm font-semibold">
					Pengaturan Agenda
				</h3>

				<p class="mt-1 text-xs leading-5 text-muted-foreground">
					Atur periode kerja, format tanggal, zona waktu, dan nomor awal agenda.
				</p>
			</div>
		</div>

		<div class="mt-5 grid gap-4 sm:grid-cols-2">
			<div>
				<label
					for="work-year"
					class="mb-1.5 block text-[11px] font-semibold"
				>
					Tahun Kerja
				</label>

				<Input
					id="work-year"
					type="number"
					value={agenda.workYear}
					class="h-11 rounded-lg text-xs"
					oninput={(event)=>
						updateAgenda(
							'workYear',
							Number(
								(event.currentTarget as HTMLInputElement).value
							)||new Date().getFullYear()
						)}
				/>
			</div>

			<div>
				<label
					for="date-format"
					class="mb-1.5 block text-[11px] font-semibold"
				>
					Format Tanggal
				</label>

				<select
					id="date-format"
					value={agenda.dateFormat}
					class="h-11 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
					onchange={(event)=>
						updateAgenda(
							'dateFormat',
							(event.currentTarget as HTMLSelectElement).value as DateFormat
						)}
				>
					<option value="DD/MM/YYYY">
						DD/MM/YYYY
					</option>

					<option value="DD-MM-YYYY">
						DD-MM-YYYY
					</option>

					<option value="YYYY-MM-DD">
						YYYY-MM-DD
					</option>
				</select>
			</div>

			<div class="sm:col-span-2">
				<label
					for="timezone"
					class="mb-1.5 block text-[11px] font-semibold"
				>
					Zona Waktu
				</label>

				<select
					id="timezone"
					value={agenda.timezone}
					class="h-11 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
					onchange={(event)=>
						updateAgenda(
							'timezone',
							(event.currentTarget as HTMLSelectElement).value
						)}
				>
					<option value="Asia/Jakarta">
						WIB — Asia/Jakarta
					</option>

					<option value="Asia/Makassar">
						WITA — Asia/Makassar
					</option>

					<option value="Asia/Jayapura">
						WIT — Asia/Jayapura
					</option>
				</select>
			</div>
		</div>

		<div class="mt-4 grid gap-4 sm:grid-cols-3">
			<div>
				<label
					for="incoming-start"
					class="mb-1.5 block text-[11px] font-semibold"
				>
					Awal Surat Masuk
				</label>

				<Input
					id="incoming-start"
					type="number"
					min="1"
					value={agenda.incomingStartNumber}
					class="h-11 rounded-lg text-xs"
					oninput={(event)=>
						updateAgenda(
							'incomingStartNumber',
							Math.max(
								1,
								Number(
									(event.currentTarget as HTMLInputElement).value
								)||1
							)
						)}
				/>
			</div>

			<div>
				<label
					for="outgoing-start"
					class="mb-1.5 block text-[11px] font-semibold"
				>
					Awal Surat Keluar
				</label>

				<Input
					id="outgoing-start"
					type="number"
					min="1"
					value={agenda.outgoingStartNumber}
					class="h-11 rounded-lg text-xs"
					oninput={(event)=>
						updateAgenda(
							'outgoingStartNumber',
							Math.max(
								1,
								Number(
									(event.currentTarget as HTMLInputElement).value
								)||1
							)
						)}
				/>
			</div>

			<div>
				<label
					for="disposition-start"
					class="mb-1.5 block text-[11px] font-semibold"
				>
					Awal Disposisi
				</label>

				<Input
					id="disposition-start"
					type="number"
					min="1"
					value={agenda.dispositionStartNumber}
					class="h-11 rounded-lg text-xs"
					oninput={(event)=>
						updateAgenda(
							'dispositionStartNumber',
							Math.max(
								1,
								Number(
									(event.currentTarget as HTMLInputElement).value
								)||1
							)
						)}
				/>
			</div>
		</div>
	</div>
</div>