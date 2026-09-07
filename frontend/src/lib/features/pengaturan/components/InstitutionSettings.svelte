<script lang="ts">
	import{Input}from'$lib/components/ui/input/index.js';
	import Building2 from'@lucide/svelte/icons/building-2';
	import UploadCloud from'@lucide/svelte/icons/upload-cloud';
	import Trash2 from'@lucide/svelte/icons/trash-2';
	import ImageIcon from'@lucide/svelte/icons/image';
	import type{InstitutionConfig}from'$lib/features/pengaturan/types';

	type Props={
		value:InstitutionConfig;
		onChange:(value:InstitutionConfig)=>void;
	};

	let{value,onChange}:Props=$props();

	let fileInput=$state<HTMLInputElement|null>(null);

	function update(
		field:keyof InstitutionConfig,
		next:string
	){
		onChange({
			...value,
			[field]:next
		});
	}

	function input(
		field:keyof InstitutionConfig,
		event:Event
	){
		update(
			field,
			(event.currentTarget as HTMLInputElement).value
		);
	}

	function handleLogo(event:Event){
		const target=event.currentTarget as HTMLInputElement;
		const file=target.files?.[0];

		if(!file)return;

		if(!file.type.startsWith('image/')){
			target.value='';
			return;
		}

		if(file.size>1024*1024){
			target.value='';
			return;
		}

		const reader=new FileReader();

		reader.onload=()=>{
			onChange({
				...value,
				logoName:file.name,
				logoData:
					typeof reader.result==='string'
						?reader.result
						:''
			});
		};

		reader.readAsDataURL(file);
	}

	function removeLogo(){
		onChange({
			...value,
			logoName:'',
			logoData:''
		});

		if(fileInput){
			fileInput.value='';
		}
	}
</script>

<div>
	<div class="flex items-start gap-3">
		<div class="flex size-9 shrink-0 items-center justify-center rounded-lg bg-muted text-muted-foreground">
			<Building2 class="size-4"/>
		</div>

		<div>
			<h2 class="text-sm font-semibold">
				Profil Instansi
			</h2>

			<p class="mt-1 text-xs leading-5 text-muted-foreground">
				Informasi identitas organisasi yang digunakan pada sistem persuratan dan dokumen laporan.
			</p>
		</div>
	</div>

	<div class="mt-6">
		<p class="text-xs font-semibold">
			Logo Instansi
		</p>

		<div class="mt-3 flex flex-col gap-4 rounded-xl border bg-muted/20 p-4 sm:flex-row sm:items-center">
			<div class="flex size-20 shrink-0 items-center justify-center overflow-hidden rounded-xl border bg-background">
				{#if value.logoData}
					<img
						src={value.logoData}
						alt="Logo instansi"
						class="h-full w-full object-contain p-2"
					/>
				{:else}
					<ImageIcon class="size-7 text-muted-foreground"/>
				{/if}
			</div>

			<div class="min-w-0 flex-1">
				<p class="text-xs font-semibold">
					{value.logoName||'Belum ada logo'}
				</p>

				<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
					Gunakan PNG, JPG, JPEG, atau WEBP dengan ukuran maksimum 1 MB.
				</p>

				<input
					id="institution-logo"
					bind:this={fileInput}
					type="file"
					class="hidden"
					accept="image/png,image/jpeg,image/webp"
					onchange={handleLogo}
				/>

				<div class="mt-3 flex flex-wrap gap-2">
					<button
						type="button"
						class="inline-flex h-9 items-center gap-2 rounded-lg border bg-background px-3 text-[11px] font-semibold transition hover:bg-muted"
						onclick={()=>fileInput?.click()}
					>
						<UploadCloud class="size-3.5"/>
						Pilih Logo
					</button>

					{#if value.logoData}
						<button
							type="button"
							class="inline-flex h-9 items-center gap-2 rounded-lg border px-3 text-[11px] font-semibold text-red-600 transition hover:bg-red-50"
							onclick={removeLogo}
						>
							<Trash2 class="size-3.5"/>
							Hapus
						</button>
					{/if}
				</div>
			</div>
		</div>
	</div>

	<div class="mt-6 grid gap-4 sm:grid-cols-2">
		<div>
			<label
				for="institution-name"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Nama Instansi
				<span class="text-red-500">*</span>
			</label>

			<Input
				id="institution-name"
				value={value.name}
				placeholder="Nama instansi"
				class="h-11 rounded-lg text-xs"
				oninput={(event)=>input('name',event)}
			/>
		</div>

		<div>
			<label
				for="institution-short-name"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Nama Singkat
			</label>

			<Input
				id="institution-short-name"
				value={value.shortName}
				placeholder="Contoh: DISKOMINFO"
				class="h-11 rounded-lg text-xs"
				oninput={(event)=>input('shortName',event)}
			/>
		</div>

		<div class="sm:col-span-2">
			<label
				for="institution-address"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Alamat
			</label>

			<textarea
				id="institution-address"
				value={value.address}
				rows="3"
				placeholder="Alamat lengkap instansi"
				class="w-full resize-none rounded-lg border border-input bg-background px-3 py-2.5 text-xs leading-5 outline-none transition focus:border-ring focus:ring-2 focus:ring-ring/20"
				oninput={(event)=>
					update(
						'address',
						(event.currentTarget as HTMLTextAreaElement).value
					)}
			></textarea>
		</div>

		<div>
			<label
				for="institution-city"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Kabupaten / Kota
			</label>

			<Input
				id="institution-city"
				value={value.city}
				placeholder="Nama kabupaten/kota"
				class="h-11 rounded-lg text-xs"
				oninput={(event)=>input('city',event)}
			/>
		</div>

		<div>
			<label
				for="institution-province"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Provinsi
			</label>

			<Input
				id="institution-province"
				value={value.province}
				placeholder="Provinsi"
				class="h-11 rounded-lg text-xs"
				oninput={(event)=>input('province',event)}
			/>
		</div>

		<div>
			<label
				for="institution-postal"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Kode Pos
			</label>

			<Input
				id="institution-postal"
				value={value.postalCode}
				placeholder="Kode pos"
				class="h-11 rounded-lg text-xs"
				oninput={(event)=>input('postalCode',event)}
			/>
		</div>

		<div>
			<label
				for="institution-phone"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Telepon
			</label>

			<Input
				id="institution-phone"
				value={value.phone}
				placeholder="Nomor telepon"
				class="h-11 rounded-lg text-xs"
				oninput={(event)=>input('phone',event)}
			/>
		</div>

		<div>
			<label
				for="institution-email"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Email
			</label>

			<Input
				id="institution-email"
				type="email"
				value={value.email}
				placeholder="email@instansi.go.id"
				class="h-11 rounded-lg text-xs"
				oninput={(event)=>input('email',event)}
			/>
		</div>

		<div>
			<label
				for="institution-website"
				class="mb-1.5 block text-[11px] font-semibold"
			>
				Website
			</label>

			<Input
				id="institution-website"
				value={value.website}
				placeholder="www.instansi.go.id"
				class="h-11 rounded-lg text-xs"
				oninput={(event)=>input('website',event)}
			/>
		</div>
	</div>

	<div class="mt-7 border-t pt-6">
		<h3 class="text-xs font-semibold">
			Pimpinan Instansi
		</h3>

		<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
			Data ini dapat digunakan pada laporan dan dokumen resmi.
		</p>

		<div class="mt-4 grid gap-4 sm:grid-cols-2">
			<div>
				<label
					for="institution-head"
					class="mb-1.5 block text-[11px] font-semibold"
				>
					Nama Pimpinan
				</label>

				<Input
					id="institution-head"
					value={value.headName}
					placeholder="Nama lengkap"
					class="h-11 rounded-lg text-xs"
					oninput={(event)=>input('headName',event)}
				/>
			</div>

			<div>
				<label
					for="institution-position"
					class="mb-1.5 block text-[11px] font-semibold"
				>
					Jabatan
				</label>

				<Input
					id="institution-position"
					value={value.headPosition}
					placeholder="Contoh: Kepala Dinas"
					class="h-11 rounded-lg text-xs"
					oninput={(event)=>input('headPosition',event)}
				/>
			</div>
		</div>
	</div>
</div>