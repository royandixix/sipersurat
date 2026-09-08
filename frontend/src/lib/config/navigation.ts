import LayoutDashboard from'@lucide/svelte/icons/layout-dashboard';
import Inbox from'@lucide/svelte/icons/inbox';
import Send from'@lucide/svelte/icons/send';
import Forward from'@lucide/svelte/icons/forward';
import Archive from'@lucide/svelte/icons/archive';
import UsersRound from'@lucide/svelte/icons/users-round';
import Boxes from'@lucide/svelte/icons/boxes';
import ChartNoAxesCombined from'@lucide/svelte/icons/chart-no-axes-combined';
import Settings2 from'@lucide/svelte/icons/settings-2';
import CircleHelp from'@lucide/svelte/icons/circle-help';

export const navigationGroups=[
	{
		label:'General',
		items:[
			{
				label:'Dashboard',
				href:'/dashboard',
				icon:LayoutDashboard
			},
			{
				label:'Surat Masuk',
				href:'/surat-masuk',
				icon:Inbox
			},
			{
				label:'Surat Keluar',
				href:'/surat-keluar',
				icon:Send
			},
			{
				label:'Disposisi',
				href:'/disposisi',
				icon:Forward,
				badge:5
			},
			{
				label:'Arsip',
				href:'/arsip',
				icon:Archive
			},
			{
				label:'Pengguna',
				href:'/pengguna',
				icon:UsersRound
			}
		]
	},
	{
		label:'Pages',
		items:[
			{
				label:'Master Data',
				href:'/master-data',
				icon:Boxes
			},
			{
				label:'Laporan',
				href:'/laporan',
				icon:ChartNoAxesCombined
			}
		]
	},
	{
		label:'Other',
		items:[
			{
				label:'Pengaturan',
				href:'/pengaturan',
				icon:Settings2
			},
			{
				label:'Pusat Bantuan',
				href:'/bantuan',
				icon:CircleHelp
			}
		]
	}
];