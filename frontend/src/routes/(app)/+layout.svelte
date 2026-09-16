<script lang="ts">
	import AppSidebar from '$lib/components/layout/AppSidebar.svelte';
	import AppHeader from '$lib/components/layout/AppHeader.svelte';
	import type{AuthUser}from'$lib/types/auth';

	let{children,data}=$props();
	let sidebarOpen=$state(false);
	const currentUser=$derived({
		id:data.currentUser.id,
		name:data.currentUser.name,
		email:data.currentUser.email,
		role:data.currentUser.role,
		initials:data.currentUser.name.trim().split(/\s+/).slice(0,2).map((word:string)=>word[0]?.toUpperCase()??'').join('')
	} as AuthUser);
</script>

<div class="app-shell">
	<AppSidebar user={currentUser} open={sidebarOpen} onClose={()=>sidebarOpen=false}/>
	<AppHeader user={currentUser} onMenuClick={()=>sidebarOpen=true}/>
	<main class="app-content">{@render children()}</main>
</div>

<style>
	.app-shell{width:100%;min-height:100dvh;background:#fff}
	.app-content{width:calc(100% - 240px);min-height:100dvh;margin-left:240px;padding-top:64px;background:#fff;overflow-x:hidden}
	@media(max-width:900px){.app-content{width:100%;margin-left:0}}
</style>
