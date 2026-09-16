<script lang="ts">
	import AppSidebar from '$lib/components/layout/AppSidebar.svelte';
	import AppHeader from '$lib/components/layout/AppHeader.svelte';

	let{children}=$props();
	let sidebarOpen=$state(false);
</script>

<div class="app-shell">
	<AppSidebar
		open={sidebarOpen}
		onClose={()=>sidebarOpen=false}
	/>
	<AppHeader
		onMenuClick={()=>sidebarOpen=true}
	/>
	<main class="app-content">
		{@render children()}
	</main>
</div>

<style>
	.app-shell{
		width:100%;
		min-height:100dvh;
		background:#fff;
	}
	.app-content{
		width:calc(100% - 240px);
		min-height:100dvh;
		margin-left:240px;
		padding-top:64px;
		background:#fff;
		overflow-x:hidden;
	}
	@media(max-width:900px){
		.app-content{
			width:100%;
			margin-left:0;
		}
	}
</style>