<script lang="ts">
	import * as DropdownMenu from '$lib/components/ui/dropdown-menu/index.js';
	import { Button } from '$lib/components/ui/button/index.js';

	import MoreHorizontal from '@lucide/svelte/icons/more-horizontal';
	import Pencil from '@lucide/svelte/icons/pencil';
	import KeyRound from '@lucide/svelte/icons/key-round';
	import Power from '@lucide/svelte/icons/power';
	import Trash2 from '@lucide/svelte/icons/trash-2';
	import ChevronLeft from '@lucide/svelte/icons/chevron-left';
	import ChevronRight from '@lucide/svelte/icons/chevron-right';
	import Users from '@lucide/svelte/icons/users';

	import type { UserRecord } from '$lib/features/pengguna/types';

	import {
		getInitials,
		ROLE_LABELS
	} from '$lib/features/pengguna/types';

	type Props = {
		users: UserRecord[];
		currentPage: number;
		totalPages: number;
		totalItems: number;
		perPage: number;
		onPageChange: (page: number) => void;
		onEdit: (user: UserRecord) => void;
		onDelete: (user: UserRecord) => void;
		onToggleStatus: (user: UserRecord) => void;
		onResetPassword: (user: UserRecord) => void;
	};

	let {
		users,
		currentPage,
		totalPages,
		totalItems,
		perPage,
		onPageChange,
		onEdit,
		onDelete,
		onToggleStatus,
		onResetPassword
	}: Props = $props();

	const firstItem = $derived(
		totalItems === 0
			? 0
			: (currentPage - 1) * perPage + 1
	);

	const lastItem = $derived(
		Math.min(currentPage * perPage, totalItems)
	);

	function roleClass(role: UserRecord['role']) {
		switch (role) {
			case 'SUPER_ADMIN':
				return 'role-super';

			case 'ADMIN_PERSURATAN':
				return 'role-admin';

			case 'PIMPINAN':
				return 'role-leader';

			default:
				return 'role-staff';
		}
	}
</script>

<!-- DESKTOP -->
<div class="desktop-table">
	<table>
		<thead>
			<tr>
				<th>Pengguna</th>
				<th>Role</th>
				<th>Unit Kerja</th>
				<th>Status</th>
				<th>Login Terakhir</th>
				<th class="action-heading">Aksi</th>
			</tr>
		</thead>

		<tbody>
			{#if users.length === 0}
				<tr>
					<td colspan="6">
						<div class="empty-state">
							<div>
								<Users class="size-5" />
							</div>

							<strong>Pengguna tidak ditemukan</strong>

							<p>
								Coba ubah kata pencarian atau filter yang digunakan.
							</p>
						</div>
					</td>
				</tr>
			{:else}
				{#each users as user (user.id)}
					<tr>
						<td>
							<div class="user-cell">
								<div class="avatar">
									{getInitials(user.name)}
								</div>

								<div class="user-info">
									<div>
										<strong>{user.name}</strong>

										{#if user.id === 1}
											<span class="you">
												ANDA
											</span>
										{/if}
									</div>

									<p>{user.email}</p>
								</div>
							</div>
						</td>

						<td>
							<span
								class={`role ${roleClass(user.role)}`}
							>
								{ROLE_LABELS[user.role]}
							</span>
						</td>

						<td>
							<span class="department">
								{user.department}
							</span>
						</td>

						<td>
							<div
								class:status-active={user.status === 'ACTIVE'}
								class="status"
							>
								<span></span>

								{user.status === 'ACTIVE'
									? 'Aktif'
									: 'Nonaktif'}
							</div>
						</td>

						<td>
							<div class="login">
								<strong>{user.lastLogin}</strong>
								<span>Dibuat {user.createdAt}</span>
							</div>
						</td>

						<td class="actions">
							<DropdownMenu.Root>
								<DropdownMenu.Trigger>
									{#snippet child({ props })}
										<button
											{...props}
											type="button"
											class="action-button"
										>
											<MoreHorizontal class="size-4" />
										</button>
									{/snippet}
								</DropdownMenu.Trigger>

								<DropdownMenu.Content
									align="end"
									class="w-52"
								>
									<DropdownMenu.Label>
										Aksi pengguna
									</DropdownMenu.Label>

									<DropdownMenu.Separator />

									<DropdownMenu.Item
										onclick={() => onEdit(user)}
									>
										<Pencil />
										Edit Pengguna
									</DropdownMenu.Item>

									<DropdownMenu.Item
										onclick={() =>
											onResetPassword(user)}
									>
										<KeyRound />
										Reset Password
									</DropdownMenu.Item>

									<DropdownMenu.Item
										disabled={user.id === 1}
										onclick={() =>
											onToggleStatus(user)}
									>
										<Power />

										{user.status === 'ACTIVE'
											? 'Nonaktifkan'
											: 'Aktifkan'}
									</DropdownMenu.Item>

									<DropdownMenu.Separator />

									<DropdownMenu.Item
										disabled={user.id === 1}
										class="text-red-600 focus:text-red-600"
										onclick={() =>
											onDelete(user)}
									>
										<Trash2 />
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

<!-- MOBILE -->
<div class="mobile-list">
	{#if users.length === 0}
		<div class="empty-state mobile-empty">
			<div>
				<Users class="size-5" />
			</div>

			<strong>Pengguna tidak ditemukan</strong>

			<p>
				Coba ubah pencarian atau filter.
			</p>
		</div>
	{:else}
		{#each users as user (user.id)}
			<div class="mobile-user">
				<div class="mobile-user-top">
					<div class="user-cell">
						<div class="avatar">
							{getInitials(user.name)}
						</div>

						<div class="user-info">
							<div>
								<strong>{user.name}</strong>

								{#if user.id === 1}
									<span class="you">ANDA</span>
								{/if}
							</div>

							<p>{user.email}</p>
						</div>
					</div>

					<DropdownMenu.Root>
						<DropdownMenu.Trigger>
							{#snippet child({ props })}
								<button
									{...props}
									type="button"
									class="action-button"
								>
									<MoreHorizontal class="size-4" />
								</button>
							{/snippet}
						</DropdownMenu.Trigger>

						<DropdownMenu.Content
							align="end"
							class="w-52"
						>
							<DropdownMenu.Item
								onclick={() => onEdit(user)}
							>
								<Pencil />
								Edit
							</DropdownMenu.Item>

							<DropdownMenu.Item
								onclick={() =>
									onResetPassword(user)}
							>
								<KeyRound />
								Reset Password
							</DropdownMenu.Item>

							<DropdownMenu.Item
								disabled={user.id === 1}
								onclick={() =>
									onToggleStatus(user)}
							>
								<Power />
								{user.status === 'ACTIVE'
									? 'Nonaktifkan'
									: 'Aktifkan'}
							</DropdownMenu.Item>

							<DropdownMenu.Separator />

							<DropdownMenu.Item
								disabled={user.id === 1}
								class="text-red-600"
								onclick={() => onDelete(user)}
							>
								<Trash2 />
								Hapus
							</DropdownMenu.Item>
						</DropdownMenu.Content>
					</DropdownMenu.Root>
				</div>

				<div class="mobile-meta">
					<div>
						<span>Role</span>
						<strong>{ROLE_LABELS[user.role]}</strong>
					</div>

					<div>
						<span>Unit Kerja</span>
						<strong>{user.department}</strong>
					</div>

					<div>
						<span>Status</span>

						<div
							class:status-active={user.status === 'ACTIVE'}
							class="status"
						>
							<span></span>

							{user.status === 'ACTIVE'
								? 'Aktif'
								: 'Nonaktif'}
						</div>
					</div>

					<div>
						<span>Login</span>
						<strong>{user.lastLogin}</strong>
					</div>
				</div>
			</div>
		{/each}
	{/if}
</div>

<div class="pagination">
	<p>
		Menampilkan
		<strong>{firstItem}–{lastItem}</strong>
		dari
		<strong>{totalItems}</strong>
		pengguna
	</p>

	<div>
		<Button
			variant="ghost"
			size="icon"
			class="page-button"
			disabled={currentPage <= 1}
			onclick={() =>
				onPageChange(currentPage - 1)}
		>
			<ChevronLeft class="size-4" />
		</Button>

		<span class="page-number">
			{currentPage}
		</span>

		<span class="page-total">
			dari {totalPages}
		</span>

		<Button
			variant="ghost"
			size="icon"
			class="page-button"
			disabled={currentPage >= totalPages}
			onclick={() =>
				onPageChange(currentPage + 1)}
		>
			<ChevronRight class="size-4" />
		</Button>
	</div>
</div>

<style>
	.desktop-table {
		display: block;
		width: 100%;
		overflow-x: auto;
	}

	table {
		width: 100%;
		border-collapse: collapse;
		background: #ffffff;
	}

	thead {
		background: #e0e0e0;
	}

	th {
		height: 42px;
		padding: 0 14px;
		text-align: left;
		font-size: 10px;
		font-weight: 600;
		color: #161616;
		white-space: nowrap;
	}

	td {
		height: 58px;
		padding: 7px 14px;
		border-bottom: 1px solid #e0e0e0;
		font-size: 11px;
		color: #161616;
		vertical-align: middle;
	}

	tbody tr {
		transition: background 120ms ease;
	}

	tbody tr:hover {
		background: #f4f4f4;
	}

	.action-heading,
	.actions {
		width: 64px;
		text-align: right;
	}

	.user-cell {
		display: flex;
		min-width: 220px;
		align-items: center;
		gap: 10px;
	}

	.avatar {
		display: flex;
		width: 34px;
		height: 34px;
		flex-shrink: 0;
		align-items: center;
		justify-content: center;
		border: 1px solid #e0e0e0;
		border-radius: 50%;
		background: #f4f4f4;
		font-size: 9px;
		font-weight: 600;
		color: #525252;
	}

	.user-info {
		min-width: 0;
	}

	.user-info > div {
		display: flex;
		align-items: center;
		gap: 6px;
	}

	.user-info strong {
		overflow: hidden;
		max-width: 200px;
		font-size: 11px;
		font-weight: 600;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.user-info p {
		overflow: hidden;
		max-width: 220px;
		margin-top: 2px;
		font-size: 9px;
		color: #6f6f6f;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.you {
		border-radius: 2px;
		background: #e0e0e0;
		padding: 2px 4px;
		font-size: 7px;
		font-weight: 600;
		color: #525252;
	}

	.role {
		display: inline-flex;
		align-items: center;
		min-height: 22px;
		padding: 0 7px;
		border-radius: 2px;
		font-size: 9px;
		font-weight: 500;
		white-space: nowrap;
	}

	.role-super {
		background: #f6f2ff;
		color: #6929c4;
	}

	.role-admin {
		background: #edf5ff;
		color: #0043ce;
	}

	.role-leader {
		background: #fff8e1;
		color: #8a3800;
	}

	.role-staff {
		background: #f4f4f4;
		color: #525252;
	}

	.department {
		white-space: nowrap;
	}

	.status {
		display: inline-flex;
		align-items: center;
		gap: 5px;
		font-size: 9px;
		font-weight: 500;
		color: #525252;
		white-space: nowrap;
	}

	.status > span {
		width: 7px;
		height: 7px;
		border-radius: 50%;
		background: #8d8d8d;
	}

	.status.status-active {
		color: #0e6027;
	}

	.status.status-active > span {
		background: #24a148;
	}

	.login strong {
		display: block;
		font-size: 10px;
		font-weight: 500;
		white-space: nowrap;
	}

	.login span {
		display: block;
		margin-top: 2px;
		font-size: 8px;
		color: #8d8d8d;
		white-space: nowrap;
	}

	.action-button {
		display: inline-flex;
		width: 32px;
		height: 32px;
		align-items: center;
		justify-content: center;
		color: #161616;
		transition: background 120ms ease;
	}

	.action-button:hover {
		background: #e0e0e0;
	}

	.empty-state {
		display: flex;
		min-height: 220px;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		text-align: center;
	}

	.empty-state > div {
		display: flex;
		width: 40px;
		height: 40px;
		align-items: center;
		justify-content: center;
		background: #f4f4f4;
	}

	.empty-state strong {
		margin-top: 12px;
		font-size: 11px;
	}

	.empty-state p {
		margin-top: 3px;
		font-size: 9px;
		color: #6f6f6f;
	}

	.pagination {
		display: flex;
		min-height: 46px;
		align-items: center;
		justify-content: space-between;
		gap: 16px;
		padding-left: 14px;
		border-top: 0;
		background: #ffffff;
	}

	.pagination > p {
		font-size: 9px;
		color: #6f6f6f;
	}

	.pagination > p strong {
		color: #161616;
		font-weight: 600;
	}

	.pagination > div {
		display: flex;
		height: 46px;
		align-items: center;
	}

	.page-button {
		width: 46px;
		height: 46px;
		border-left: 1px solid #e0e0e0;
		border-radius: 0;
	}

	.page-button:hover {
		background: #e0e0e0;
	}

	.page-number {
		display: flex;
		height: 46px;
		min-width: 42px;
		align-items: center;
		justify-content: center;
		border-left: 1px solid #e0e0e0;
		font-size: 10px;
		font-weight: 600;
	}

	.page-total {
		padding: 0 10px;
		font-size: 9px;
		color: #6f6f6f;
	}

	.mobile-list {
		display: none;
	}

	@media (max-width: 780px) {
		.desktop-table {
			display: none;
		}

		.mobile-list {
			display: block;
		}

		.mobile-user {
			padding: 14px;
			border-bottom: 1px solid #e0e0e0;
			background: #ffffff;
		}

		.mobile-user-top {
			display: flex;
			align-items: flex-start;
			justify-content: space-between;
			gap: 12px;
		}

		.mobile-meta {
			display: grid;
			grid-template-columns: repeat(2, minmax(0, 1fr));
			gap: 12px;
			margin-top: 14px;
			padding-top: 12px;
			border-top: 1px solid #e0e0e0;
		}

		.mobile-meta > div > span {
			display: block;
			font-size: 8px;
			color: #8d8d8d;
		}

		.mobile-meta > div > strong {
			display: block;
			margin-top: 3px;
			font-size: 10px;
			font-weight: 500;
		}

		.mobile-meta .status {
			margin-top: 4px;
		}

		.mobile-empty {
			min-height: 200px;
		}

		.pagination {
			align-items: flex-start;
			flex-direction: column;
			gap: 0;
			padding: 10px 0 0 14px;
		}

		.pagination > div {
			align-self: flex-end;
		}
	}
</style>