<script lang="ts">
	import ShieldCheck from'@lucide/svelte/icons/shield-check';
	import Clock3 from'@lucide/svelte/icons/clock-3';
	import KeyRound from'@lucide/svelte/icons/key-round';
	import type{SecurityConfig}from'$lib/features/pengaturan/types';

	type Props={
		value:SecurityConfig;
		onChange:(value:SecurityConfig)=>void;
	};

	let{value,onChange}:Props=$props();

	function update<K extends keyof SecurityConfig>(
		field:K,
		next:SecurityConfig[K]
	){
		onChange({
			...value,
			[field]:next
		});
	}

	function toggle(field:keyof SecurityConfig){
		const current=value[field];

		if(typeof current!=='boolean')return;

		update(
			field,
			!current as SecurityConfig[typeof field]
		);
	}
</script>

<div>
	<div class="flex items-start gap-3">
		<div class="flex size-9 shrink-0 items-center justify-center rounded-lg bg-muted text-muted-foreground">
			<ShieldCheck class="size-4"/>
		</div>

		<div>
			<h2 class="text-sm font-semibold">
				Keamanan
			</h2>

			<p class="mt-1 text-xs leading-5 text-muted-foreground">
				Atur sesi pengguna, autentikasi, batas percobaan login, dan kebijakan kata sandi.
			</p>
		</div>
	</div>

	<div class="mt-6 rounded-xl border p-4">
		<div class="flex items-start gap-3">
			<div class="flex size-8 shrink-0 items-center justify-center rounded-lg bg-blue-50 text-blue-700">
				<Clock3 class="size-4"/>
			</div>

			<div class="min-w-0 flex-1">
				<h3 class="text-xs font-semibold">
					Sesi Pengguna
				</h3>

				<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
					Atur durasi sesi sebelum pengguna harus melakukan autentikasi kembali.
				</p>

				<div class="mt-4 grid gap-4 sm:grid-cols-2">
					<div>
						<label
							for="session-duration"
							class="mb-1.5 block text-[11px] font-semibold"
						>
							Durasi Sesi
						</label>

						<select
							id="session-duration"
							value={value.sessionMinutes}
							class="h-11 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
							onchange={(event)=>
								update(
									'sessionMinutes',
									Number(
										(event.currentTarget as HTMLSelectElement).value
									)
								)}
						>
							<option value="30">30 menit</option>
							<option value="60">1 jam</option>
							<option value="120">2 jam</option>
							<option value="240">4 jam</option>
							<option value="480">8 jam</option>
						</select>
					</div>

					<div>
						<label
							for="login-attempts"
							class="mb-1.5 block text-[11px] font-semibold"
						>
							Maksimum Percobaan Login
						</label>

						<input
							id="login-attempts"
							type="number"
							min="3"
							max="10"
							value={value.maxLoginAttempts}
							class="h-11 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
							oninput={(event)=>
								update(
									'maxLoginAttempts',
									Math.min(
										10,
										Math.max(
											3,
											Number(
												(event.currentTarget as HTMLInputElement).value
											)||5
										)
									)
								)}
						/>
					</div>
				</div>

				<button
					type="button"
					class="mt-4 flex w-full items-center justify-between gap-4 rounded-lg bg-muted/30 p-3 text-left transition hover:bg-muted/50"
					aria-pressed={value.autoLogout}
					onclick={()=>toggle('autoLogout')}
				>
					<div>
						<p class="text-[11px] font-semibold">
							Logout Otomatis
						</p>

						<p class="mt-1 text-[9px] leading-4 text-muted-foreground">
							Keluarkan pengguna setelah sesi tidak aktif melewati batas waktu.
						</p>
					</div>

					<span
						class={[
							'relative h-6 w-11 shrink-0 rounded-full transition',
							value.autoLogout
								?'bg-foreground'
								:'bg-muted'
						]}
					>
						<span
							class={[
								'absolute top-1 size-4 rounded-full bg-background shadow transition-all',
								value.autoLogout
									?'left-6'
									:'left-1'
							]}
						></span>
					</span>
				</button>
			</div>
		</div>
	</div>

	<div class="mt-5 rounded-xl border p-4">
		<div class="flex items-start gap-3">
			<div class="flex size-8 shrink-0 items-center justify-center rounded-lg bg-violet-50 text-violet-700">
				<KeyRound class="size-4"/>
			</div>

			<div class="min-w-0 flex-1">
				<h3 class="text-xs font-semibold">
					Kebijakan Kata Sandi
				</h3>

				<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
					Tentukan persyaratan minimal untuk kata sandi akun pengguna.
				</p>

				<div class="mt-4">
					<label
						for="minimum-password"
						class="mb-1.5 block text-[11px] font-semibold"
					>
						Minimal Karakter
					</label>

					<input
						id="minimum-password"
						type="number"
						min="6"
						max="32"
						value={value.minPasswordLength}
						class="h-11 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none sm:w-40"
						oninput={(event)=>
							update(
								'minPasswordLength',
								Math.min(
									32,
									Math.max(
										6,
										Number(
											(event.currentTarget as HTMLInputElement).value
										)||8
									)
								)
							)}
					/>
				</div>

				<div class="mt-4 grid gap-2 sm:grid-cols-2">
					{#each [
						['requireUppercase','Wajib huruf besar'],
						['requireLowercase','Wajib huruf kecil'],
						['requireNumber','Wajib angka'],
						['requireSymbol','Wajib simbol']
					] as option}
						<button
							type="button"
							class={[
								'flex items-center gap-3 rounded-lg border p-3 text-left transition',
								value[option[0] as keyof SecurityConfig]
									?'border-foreground bg-muted/40'
									:'hover:bg-muted/20'
							]}
							onclick={()=>
								toggle(
									option[0] as keyof SecurityConfig
								)}
						>
							<span
								class={[
									'flex size-4 items-center justify-center rounded border',
									value[option[0] as keyof SecurityConfig]
										?'border-foreground bg-foreground'
										:'bg-background'
								]}
							>
								{#if value[option[0] as keyof SecurityConfig]}
									<span class="size-1.5 rounded-full bg-background"></span>
								{/if}
							</span>

							<span class="text-[11px] font-semibold">
								{option[1]}
							</span>
						</button>
					{/each}
				</div>
			</div>
		</div>
	</div>

	<div class="mt-5 rounded-xl border p-4">
		<button
			type="button"
			class="flex w-full items-center justify-between gap-4 text-left"
			aria-pressed={value.twoFactorEnabled}
			onclick={()=>toggle('twoFactorEnabled')}
		>
			<div>
				<p class="text-xs font-semibold">
					Two-Factor Authentication
				</p>

				<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
					Persiapkan autentikasi dua langkah. Fitur backend akan dihubungkan pada tahap integrasi.
				</p>
			</div>

			<span
				class={[
					'relative h-6 w-11 shrink-0 rounded-full transition',
					value.twoFactorEnabled
						?'bg-foreground'
						:'bg-muted'
				]}
			>
				<span
					class={[
						'absolute top-1 size-4 rounded-full bg-background shadow transition-all',
						value.twoFactorEnabled
							?'left-6'
							:'left-1'
					]}
				></span>
			</span>
		</button>
	</div>
</div>