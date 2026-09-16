<script lang="ts">
	import { Button } from '$lib/components/ui/button/index.js';
	import { Input } from '$lib/components/ui/input/index.js';

	import X from '@lucide/svelte/icons/x';
	import UserPlus from '@lucide/svelte/icons/user-plus';
	import Pencil from '@lucide/svelte/icons/pencil';
	import Save from '@lucide/svelte/icons/save';
	import Eye from '@lucide/svelte/icons/eye';
	import EyeOff from '@lucide/svelte/icons/eye-off';
	import Check from '@lucide/svelte/icons/check';
	import CircleAlert from '@lucide/svelte/icons/circle-alert';

	import type {
		UserFormPayload,
		UserRecord,
		UserStatus
	} from '$lib/features/pengguna/types';

	import {
		DEPARTMENT_OPTIONS,
		ROLE_OPTIONS
	} from '$lib/features/pengguna/types';

	import type { UserRole } from '$lib/types/auth';

	type Props = {
		open: boolean;
		mode: 'create' | 'edit';
		user?: UserRecord | null;
		onSave: (payload: UserFormPayload) => void;
		onOpenChange: (value: boolean) => void;
	};

	let {
		open,
		mode,
		user = null,
		onSave,
		onOpenChange
	}: Props = $props();

	let dialogElement = $state<HTMLDialogElement | null>(null);
	let showPassword = $state(false);
	let lastSignature = '';

	function emptyForm(): UserFormPayload {
		return {
			name: '',
			email: '',
			role: 'STAFF',
			department: '',
			status: 'ACTIVE',
			password: ''
		};
	}

	let form = $state<UserFormPayload>(emptyForm());

	let errors = $state<{
		name?: string;
		email?: string;
		department?: string;
		password?: string;
	}>({});

	$effect(() => {
		if (!dialogElement) return;

		if (open && !dialogElement.open) {
			dialogElement.showModal();
		}

		if (!open && dialogElement.open) {
			dialogElement.close();
		}
	});

	$effect(() => {
		if (!open) {
			lastSignature = '';
			return;
		}

		const signature = `${mode}-${user?.id ?? 'new'}`;

		if (signature === lastSignature) return;

		if (mode === 'edit' && user) {
			form = {
				name: user.name,
				email: user.email,
				role: user.role,
				department: user.department,
				status: user.status,
				password: ''
			};
		} else {
			form = emptyForm();
		}

		errors = {};
		showPassword = false;
		lastSignature = signature;
	});

	function validateEmail(value: string) {
		return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
	}

	function validate() {
		const next: typeof errors = {};

		if (!form.name.trim()) {
			next.name = 'Nama lengkap wajib diisi.';
		}

		if (!form.email.trim()) {
			next.email = 'Email wajib diisi.';
		} else if (!validateEmail(form.email.trim())) {
			next.email = 'Format email tidak valid.';
		}

		if (!form.department) {
			next.department = 'Unit kerja wajib dipilih.';
		}

		if (
			mode === 'create' &&
			(!form.password || form.password.length < 6)
		) {
			next.password = 'Password minimal 6 karakter.';
		}

		if (
			mode === 'edit' &&
			form.password &&
			form.password.length < 6
		) {
			next.password = 'Password minimal 6 karakter.';
		}

		errors = next;

		return Object.keys(next).length === 0;
	}

	function submitForm() {
		if (!validate()) return;

		onSave({
			name: form.name.trim(),
			email: form.email.trim().toLowerCase(),
			role: form.role,
			department: form.department,
			status: form.status,
			password: form.password || undefined
		});
	}

	function closeModal() {
		onOpenChange(false);
	}

	function handleCancel(event: Event) {
		event.preventDefault();
		closeModal();
	}

	function handleBackdrop(event: MouseEvent) {
		if (event.target === dialogElement) {
			closeModal();
		}
	}

	function selectRole(role: UserRole) {
		form.role = role;
	}

	function selectStatus(status: UserStatus) {
		form.status = status;
	}
</script>

<dialog
	bind:this={dialogElement}
	class="user-dialog"
	oncancel={handleCancel}
	onclick={handleBackdrop}
>
	<div class="dialog-container">
		<!-- HEADER -->
		<header class="dialog-header">
			<div class="flex min-w-0 items-center gap-3">
				<div class="flex size-10 shrink-0 items-center justify-center rounded-xl bg-foreground text-background">
					{#if mode === 'create'}
						<UserPlus class="size-5" />
					{:else}
						<Pencil class="size-5" />
					{/if}
				</div>

				<div class="min-w-0">
					<h2 class="text-base font-bold tracking-tight sm:text-lg">
						{mode === 'create'
							? 'Tambah Pengguna'
							: 'Edit Pengguna'}
					</h2>

					<p class="mt-0.5 text-xs text-muted-foreground">
						{mode === 'create'
							? 'Tambahkan akun baru ke SiPersurat.'
							: 'Perbarui informasi pengguna.'}
					</p>
				</div>
			</div>

			<button
				type="button"
				class="close-button"
				onclick={closeModal}
			>
				<X class="size-4" />
			</button>
		</header>

		<!-- BODY -->
		<div class="dialog-body">
			<div class="grid gap-4 sm:grid-cols-2">
				<!-- NAMA -->
				<div class="field">
					<label for={`${mode}-name`}>
						Nama Lengkap
						<span>*</span>
					</label>

					<Input
						id={`${mode}-name`}
						bind:value={form.name}
						placeholder="Contoh: Ahmad Ramadhan"
						class="h-10 rounded-lg"
					/>

					{#if errors.name}
						<p class="error">
							<CircleAlert class="size-3" />
							{errors.name}
						</p>
					{/if}
				</div>

				<!-- EMAIL -->
				<div class="field">
					<label for={`${mode}-email`}>
						Email
						<span>*</span>
					</label>

					<Input
						id={`${mode}-email`}
						type="email"
						bind:value={form.email}
						placeholder="nama@sipersurat.id"
						class="h-10 rounded-lg"
					/>

					{#if errors.email}
						<p class="error">
							<CircleAlert class="size-3" />
							{errors.email}
						</p>
					{/if}
				</div>
			</div>

			<!-- ROLE -->
			<div class="field mt-5">
				<label for={`${mode}-role-SUPER_ADMIN`}>
					Role
					<span>*</span>
				</label>

				<div class="role-grid">
					{#each ROLE_OPTIONS as role}
						<button
							id={`${mode}-role-${role.value}`}
							type="button"
							class:role-active={form.role === role.value}
							class="role-button"
							onclick={() => selectRole(role.value)}
						>
							<span>{role.label}</span>

							{#if form.role === role.value}
								<Check class="size-3.5" />
							{/if}
						</button>
					{/each}
				</div>
			</div>

			<div class="mt-5 grid gap-4 sm:grid-cols-2">
				<!-- UNIT -->
				<div class="field">
					<label for={`${mode}-department`}>
						Unit Kerja
						<span>*</span>
					</label>

					<select
						id={`${mode}-department`}
						bind:value={form.department}
						class="select-input"
					>
						<option value="">
							Pilih unit kerja
						</option>

						{#each DEPARTMENT_OPTIONS as department}
							<option value={department}>
								{department}
							</option>
						{/each}
					</select>

					{#if errors.department}
						<p class="error">
							<CircleAlert class="size-3" />
							{errors.department}
						</p>
					{/if}
				</div>

				<!-- STATUS -->
				<div class="field">
					<label for={`${mode}-status-active`}>
						Status Akun
						<span>*</span>
					</label>

					<div class="status-grid">
						<button
							id={`${mode}-status-active`}
							type="button"
							class:status-active={form.status === 'ACTIVE'}
							class="status-button"
							onclick={() => selectStatus('ACTIVE')}
						>
							<span class="status-dot active-dot"></span>
							<span>Aktif</span>

							{#if form.status === 'ACTIVE'}
								<Check class="ml-auto size-3.5" />
							{/if}
						</button>

						<button
							type="button"
							class:status-active={form.status === 'INACTIVE'}
							class="status-button"
							onclick={() => selectStatus('INACTIVE')}
						>
							<span class="status-dot inactive-dot"></span>
							<span>Nonaktif</span>

							{#if form.status === 'INACTIVE'}
								<Check class="ml-auto size-3.5" />
							{/if}
						</button>
					</div>
				</div>
			</div>

			<!-- PASSWORD -->
			<div class="field mt-5">
				<label for={`${mode}-password`}>
					{mode === 'create'
						? 'Password'
						: 'Password Baru'}

					{#if mode === 'create'}
						<span>*</span>
					{/if}
				</label>

				<div class="relative">
					<Input
						id={`${mode}-password`}
						bind:value={form.password}
						type={showPassword ? 'text' : 'password'}
						placeholder={mode === 'create'
							? 'Minimal 6 karakter'
							: 'Kosongkan jika tidak diubah'}
						class="h-10 rounded-lg pr-11"
					/>

					<button
						type="button"
						class="password-toggle"
						onclick={() => (showPassword = !showPassword)}
					>
						{#if showPassword}
							<EyeOff class="size-4" />
						{:else}
							<Eye class="size-4" />
						{/if}
					</button>
				</div>

				{#if errors.password}
					<p class="error">
						<CircleAlert class="size-3" />
						{errors.password}
					</p>
				{:else}
					<p class="helper">
						{mode === 'create'
							? 'Gunakan minimal 6 karakter.'
							: 'Kosongkan jika password tidak diubah.'}
					</p>
				{/if}
			</div>
		</div>

		<!-- FOOTER -->
		<footer class="dialog-footer">
			<p class="hidden text-[10px] text-muted-foreground sm:block">
				<span class="text-red-500">*</span>
				Wajib diisi
			</p>

			<div class="flex w-full gap-2 sm:w-auto">
				<Button
					type="button"
					variant="outline"
					class="h-10 flex-1 rounded-lg sm:min-w-24 sm:flex-none"
					onclick={closeModal}
				>
					Batal
				</Button>

				<Button
					type="button"
					class="h-10 flex-1 rounded-lg sm:min-w-40 sm:flex-none"
					onclick={submitForm}
				>
					<Save class="size-4" />

					{mode === 'create'
						? 'Simpan Pengguna'
						: 'Simpan Perubahan'}
				</Button>
			</div>
		</footer>
	</div>
</dialog>

<style>
	.user-dialog {
		width: min(720px, calc(100vw - 32px));
		max-width: 720px;
		max-height: calc(100dvh - 40px);
		margin: auto;
		padding: 0;
		border: 0;
		border-radius: 18px;
		background: transparent;
		color: var(--foreground);
		overflow: visible;
	}

	.user-dialog::backdrop {
		background: rgb(15 23 42 / 0.42);
		backdrop-filter: blur(5px);
		-webkit-backdrop-filter: blur(5px);
	}

	.user-dialog[open] {
		animation: modal-in 160ms ease-out;
	}

	.dialog-container {
		display: flex;
		max-height: calc(100dvh - 40px);
		flex-direction: column;
		overflow: hidden;
		border: 1px solid var(--border);
		border-radius: 18px;
		background: var(--background);
		box-shadow: 0 24px 70px rgb(0 0 0 / 0.22);
	}

	.dialog-header {
		display: flex;
		flex-shrink: 0;
		align-items: center;
		justify-content: space-between;
		gap: 16px;
		padding: 16px 18px;
		border-bottom: 1px solid var(--border);
	}

	.dialog-body {
		min-height: 0;
		flex: 1;
		overflow-y: auto;
		padding: 20px;
	}

	.dialog-footer {
		display: flex;
		flex-shrink: 0;
		align-items: center;
		justify-content: space-between;
		gap: 12px;
		padding: 13px 18px;
		border-top: 1px solid var(--border);
		background: var(--background);
	}

	.close-button {
		display: flex;
		width: 34px;
		height: 34px;
		flex-shrink: 0;
		align-items: center;
		justify-content: center;
		border-radius: 9px;
		color: var(--muted-foreground);
		transition: 150ms ease;
	}

	.close-button:hover {
		background: var(--muted);
		color: var(--foreground);
	}

	.field label {
		display: block;
		margin-bottom: 7px;
		font-size: 11px;
		font-weight: 600;
	}

	.field label span {
		color: #ef4444;
	}

	.select-input {
		width: 100%;
		height: 40px;
		border: 1px solid var(--input);
		border-radius: 8px;
		background: var(--background);
		padding: 0 12px;
		font-size: 12px;
		outline: none;
		transition: 150ms ease;
	}

	.select-input:focus {
		border-color: var(--ring);
		box-shadow: 0 0 0 3px color-mix(in oklab, var(--ring) 15%, transparent);
	}

	.role-grid {
		display: grid;
		grid-template-columns: repeat(4, minmax(0, 1fr));
		gap: 8px;
	}

	.role-button {
		display: flex;
		min-width: 0;
		height: 38px;
		align-items: center;
		justify-content: center;
		gap: 6px;
		border: 1px solid var(--border);
		border-radius: 9px;
		background: var(--background);
		padding: 0 10px;
		font-size: 10px;
		font-weight: 500;
		transition: 150ms ease;
	}

	.role-button:hover {
		background: var(--muted);
	}

	.role-button.role-active {
		border-color: var(--foreground);
		background: var(--foreground);
		color: var(--background);
	}

	.status-grid {
		display: grid;
		grid-template-columns: repeat(2, minmax(0, 1fr));
		gap: 8px;
	}

	.status-button {
		display: flex;
		height: 40px;
		align-items: center;
		gap: 7px;
		border: 1px solid var(--border);
		border-radius: 8px;
		background: var(--background);
		padding: 0 11px;
		font-size: 10px;
		font-weight: 500;
		transition: 150ms ease;
	}

	.status-button:hover {
		background: var(--muted);
	}

	.status-button.status-active {
		border-color: var(--foreground);
		background: var(--muted);
	}

	.status-dot {
		width: 7px;
		height: 7px;
		flex-shrink: 0;
		border-radius: 50%;
	}

	.active-dot {
		background: #10b981;
	}

	.inactive-dot {
		background: #a1a1aa;
	}

	.password-toggle {
		position: absolute;
		top: 50%;
		right: 12px;
		display: flex;
		transform: translateY(-50%);
		color: var(--muted-foreground);
		transition: 150ms ease;
	}

	.password-toggle:hover {
		color: var(--foreground);
	}

	.error {
		display: flex;
		align-items: center;
		gap: 4px;
		margin-top: 5px;
		font-size: 10px;
		color: #ef4444;
	}

	.helper {
		margin-top: 5px;
		font-size: 10px;
		color: var(--muted-foreground);
	}

	@keyframes modal-in {
		from {
			opacity: 0;
			transform: translateY(8px) scale(0.98);
		}

		to {
			opacity: 1;
			transform: translateY(0) scale(1);
		}
	}

	@media (max-width: 700px) {
		.user-dialog {
			width: calc(100vw - 16px);
			max-height: calc(100dvh - 16px);
		}

		.dialog-container {
			max-height: calc(100dvh - 16px);
			border-radius: 15px;
		}

		.role-grid {
			grid-template-columns: repeat(2, minmax(0, 1fr));
		}

		.dialog-body {
			padding: 16px;
		}

		.dialog-header {
			padding: 14px 16px;
		}

		.dialog-footer {
			padding: 12px 16px;
		}
	}

	@media (max-width: 480px) {
		.role-grid,
		.status-grid {
			grid-template-columns: 1fr;
		}
	}
</style>