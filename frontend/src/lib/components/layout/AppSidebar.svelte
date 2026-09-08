<script lang="ts">
	import{page}from'$app/state';
	import{onMount}from'svelte';
	import{navigationGroups}from'$lib/config/navigation';
	import{getIncomingMails}from'$lib/features/surat-masuk/api';
	import Mail from'@lucide/svelte/icons/mail';
	import ChevronDown from'@lucide/svelte/icons/chevron-down';
	import X from'@lucide/svelte/icons/x';

	type Props={
		open?:boolean;
		onClose?:()=>void;
	};

	let{open=false,onClose=()=>{}}:Props=$props();
	let incomingMailCount=$state<number|null>(null);

	onMount(()=>{
		void loadIncomingMailCount();
	});

	async function loadIncomingMailCount(){
		try{
			const records=await getIncomingMails();
			incomingMailCount=records.length;
		}catch(error){
			console.error('Gagal memuat jumlah surat masuk:',error);
			incomingMailCount=null;
		}
	}

	function isActive(href:string){
		return page.url.pathname===href||page.url.pathname.startsWith(`${href}/`);
	}
</script>

{#if open}
	<button
		type="button"
		class="sidebar-backdrop"
		aria-label="Tutup sidebar"
		onclick={onClose}
	></button>
{/if}

<aside class:mobile-open={open} class="sidebar">
	<div class="brand">
		<div class="brand-logo">
			<Mail class="size-[17px]"/>
		</div>
		<div class="brand-copy">
			<strong>SiPersurat</strong>
			<span>Sistem Persuratan</span>
		</div>
		<button type="button" class="brand-action">
			<ChevronDown class="size-3.5"/>
		</button>
		<button type="button" class="mobile-close" onclick={onClose}>
			<X class="size-4"/>
		</button>
	</div>

	<nav class="navigation">
		{#each navigationGroups as group}
			<div class="nav-group">
				<p class="group-title">{group.label}</p>

				<div class="nav-items">
					{#each group.items as item}
						<a
							href={item.href}
							class:active={isActive(item.href)}
							class="nav-item"
							onclick={onClose}
						>
							<item.icon class="nav-icon"/>
							<span class="nav-label">{item.label}</span>

							{#if item.href==='/surat-masuk'}
								{#if incomingMailCount!==null}
									<span class="nav-badge">
										{incomingMailCount}
									</span>
								{/if}
							{:else if 'badge' in item&&item.badge}
								<span class="nav-badge">
									{item.badge}
								</span>
							{/if}
						</a>
					{/each}
				</div>
			</div>
		{/each}
	</nav>

	<div class="sidebar-user">
		<div class="user-avatar">SA</div>

		<div class="user-copy">
			<strong>Super Administrator</strong>
			<span>admin@sipersurat.id</span>
		</div>

		<ChevronDown class="size-3.5 text-[#737373]"/>
	</div>
</aside>

<style>
	.sidebar{
		position:fixed;
		top:0;
		bottom:0;
		left:0;
		z-index:50;
		display:flex;
		width:240px;
		flex-direction:column;
		border-right:1px solid #e5e5e5;
		background:#fff;
		color:#171717;
	}
	.brand{
		display:flex;
		height:64px;
		flex-shrink:0;
		align-items:center;
		gap:10px;
		padding:0 16px;
		border-bottom:1px solid #e5e5e5;
	}
	.brand-logo{
		display:flex;
		width:36px;
		height:36px;
		flex-shrink:0;
		align-items:center;
		justify-content:center;
		border-radius:10px;
		background:#171717;
		color:#fff;
	}
	.brand-copy{
		min-width:0;
		flex:1;
	}
	.brand-copy strong{
		display:block;
		font-size:14px;
		font-weight:700;
		line-height:1.2;
	}
	.brand-copy span{
		display:block;
		margin-top:3px;
		font-size:10px;
		font-weight:500;
		color:#737373;
	}
	.brand-action{
		display:flex;
		width:28px;
		height:28px;
		align-items:center;
		justify-content:center;
		border-radius:7px;
		color:#525252;
	}
	.brand-action:hover{
		background:#f5f5f5;
	}
	.mobile-close{
		display:none;
		width:30px;
		height:30px;
		align-items:center;
		justify-content:center;
	}
	.navigation{
		flex:1;
		overflow-y:auto;
		padding:18px 12px;
	}
	.nav-group+.nav-group{
		margin-top:24px;
	}
	.group-title{
		margin:0 0 8px 9px;
		font-size:10px;
		font-weight:600;
		color:#737373;
	}
	.nav-items{
		display:grid;
		gap:3px;
	}
	.nav-item{
		display:flex;
		height:41px;
		align-items:center;
		gap:10px;
		border-radius:9px;
		padding:0 10px;
		font-size:13px;
		font-weight:500;
		color:#262626;
		transition:.13s ease;
	}
	.nav-item:hover{
		background:#f5f5f5;
	}
	.nav-item.active{
		background:#f4f4f5;
		font-weight:600;
		color:#171717;
	}
	.nav-item :global(svg){
		width:17px;
		height:17px;
		flex-shrink:0;
		stroke-width:1.9;
	}
	.nav-label{
		min-width:0;
		flex:1;
	}
	.nav-badge{
		display:flex;
		min-width:21px;
		height:21px;
		align-items:center;
		justify-content:center;
		border-radius:7px;
		background:#171717;
		padding:0 6px;
		font-size:9px;
		font-weight:700;
		color:#fff;
	}
	.sidebar-user{
		display:grid;
		grid-template-columns:auto 1fr auto;
		align-items:center;
		gap:10px;
		min-height:68px;
		padding:11px 16px;
		border-top:1px solid #e5e5e5;
	}
	.user-avatar{
		display:flex;
		width:32px;
		height:32px;
		align-items:center;
		justify-content:center;
		border:1px solid #e5e5e5;
		border-radius:50%;
		background:#f5f5f5;
		font-size:10px;
		font-weight:600;
		color:#525252;
	}
	.user-copy{
		min-width:0;
	}
	.user-copy strong,.user-copy span{
		display:block;
		overflow:hidden;
		text-overflow:ellipsis;
		white-space:nowrap;
	}
	.user-copy strong{
		font-size:11px;
		font-weight:600;
	}
	.user-copy span{
		margin-top:3px;
		font-size:9px;
		font-weight:500;
		color:#737373;
	}
	.sidebar-backdrop{
		display:none;
	}
	@media(max-width:900px){
		.sidebar{
			z-index:100;
			transform:translateX(-100%);
			box-shadow:8px 0 30px rgb(0 0 0/.12);
			transition:transform .18s ease;
		}
		.sidebar.mobile-open{
			transform:translateX(0);
		}
		.sidebar-backdrop{
			position:fixed;
			inset:0;
			z-index:90;
			display:block;
			background:rgb(0 0 0/.4);
			backdrop-filter:blur(2px);
		}
		.brand-action{
			display:none;
		}
		.mobile-close{
			display:flex;
		}
	}
</style>