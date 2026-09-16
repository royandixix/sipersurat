<script lang="ts">
	import Menu from '@lucide/svelte/icons/menu';
	import Search from '@lucide/svelte/icons/search';
	import Bell from '@lucide/svelte/icons/bell';
	import Settings from '@lucide/svelte/icons/settings';
	import PanelLeft from '@lucide/svelte/icons/panel-left';

	type Props={
		onMenuClick?:()=>void;
	};

	let{onMenuClick=()=>{}}:Props=$props();
</script>

<header class="app-header">
	<div class="header-left">
		<button type="button" class="mobile-menu" onclick={onMenuClick}>
			<Menu class="size-[18px]"/>
		</button>
		<button type="button" class="panel-button">
			<PanelLeft class="size-[18px]"/>
		</button>
		<div class="divider"></div>
		<nav class="top-nav">
			<a href="/dashboard">Overview</a>
			<a href="/surat-masuk">Surat Masuk</a>
			<a href="/surat-keluar">Surat Keluar</a>
			<a href="/pengaturan">Pengaturan</a>
		</nav>
	</div>
	<div class="header-right">
		<button type="button" class="search-box">
			<Search class="size-[17px]"/>
			<span>Cari...</span>
			<kbd>⌘ K</kbd>
		</button>
		<button type="button" class="icon-button">
			<Settings class="size-[17px]"/>
		</button>
		<button type="button" class="icon-button notification">
			<Bell class="size-[17px]"/>
			<span></span>
		</button>
		<button type="button" class="profile">SA</button>
	</div>
</header>

<style>
	.app-header{
		position:fixed;
		top:0;
		right:0;
		left:240px;
		z-index:40;
		display:flex;
		height:64px;
		align-items:center;
		justify-content:space-between;
		gap:20px;
		border-bottom:1px solid #e5e5e5;
		background:rgba(255,255,255,.98);
		padding:0 20px;
		backdrop-filter:blur(10px);
	}
	.header-left,.header-right{
		display:flex;
		align-items:center;
	}
	.header-left{
		min-width:0;
		gap:12px;
	}
	.header-right{
		gap:4px;
	}
	.panel-button,.mobile-menu,.icon-button{
		display:flex;
		width:36px;
		height:36px;
		flex-shrink:0;
		align-items:center;
		justify-content:center;
		border-radius:9px;
		color:#262626;
		transition:.15s ease;
	}
	.panel-button:hover,.mobile-menu:hover,.icon-button:hover{
		background:#f5f5f5;
	}
	.mobile-menu{
		display:none;
	}
	.divider{
		width:1px;
		height:25px;
		background:#e5e5e5;
	}
	.top-nav{
		display:flex;
		align-items:center;
		gap:2px;
	}
	.top-nav a{
		display:flex;
		height:36px;
		align-items:center;
		border-radius:8px;
		padding:0 14px;
		font-size:12px;
		font-weight:600;
		color:#525252;
		transition:.15s ease;
	}
	.top-nav a:hover{
		background:#f5f5f5;
		color:#171717;
	}
	.search-box{
		display:grid;
		width:245px;
		height:38px;
		grid-template-columns:auto 1fr auto;
		align-items:center;
		gap:9px;
		margin-right:6px;
		border:1px solid #e5e5e5;
		border-radius:10px;
		background:#fff;
		padding:0 8px 0 12px;
		color:#737373;
		transition:.15s ease;
	}
	.search-box:hover{
		border-color:#c5c5c5;
	}
	.search-box span{
		text-align:left;
		font-size:12px;
		font-weight:500;
	}
	.search-box kbd{
		display:flex;
		height:22px;
		align-items:center;
		border:1px solid #e5e5e5;
		border-radius:5px;
		background:#f5f5f5;
		padding:0 7px;
		font-family:inherit;
		font-size:9px;
		font-weight:600;
		color:#737373;
	}
	.notification{
		position:relative;
	}
	.notification>span{
		position:absolute;
		top:7px;
		right:7px;
		width:6px;
		height:6px;
		border:1.5px solid #fff;
		border-radius:50%;
		background:#ef4444;
	}
	.profile{
		display:flex;
		width:36px;
		height:36px;
		align-items:center;
		justify-content:center;
		margin-left:4px;
		border:1px solid #e5e5e5;
		border-radius:50%;
		background:#f5f5f5;
		font-size:10px;
		font-weight:600;
		color:#525252;
	}
	@media(max-width:1050px){
		.search-box{
			width:190px;
		}
		.top-nav a{
			padding:0 9px;
			font-size:11px;
		}
	}
	@media(max-width:900px){
		.app-header{
			left:0;
			padding:0 14px;
		}
		.mobile-menu{
			display:flex;
		}
		.panel-button,.divider,.top-nav{
			display:none;
		}
	}
	@media(max-width:640px){
		.search-box{
			width:38px;
			grid-template-columns:1fr;
			justify-items:center;
			padding:0;
		}
		.search-box span,.search-box kbd{
			display:none;
		}
		.header-right .icon-button:first-of-type{
			display:none;
		}
	}
</style>