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
import type{UserRole}from'$lib/types/auth';

const allRoles:UserRole[]=['SUPER_ADMIN','ADMIN_PERSURATAN','PIMPINAN','STAFF'];
const superAdmin:UserRole[]=['SUPER_ADMIN'];

export const navigationGroups=[
	{
		label:'General',
		items:[
			{label:'Dashboard',href:'/dashboard',icon:LayoutDashboard,roles:allRoles},
			{label:'Surat Masuk',href:'/surat-masuk',icon:Inbox,roles:allRoles},
			{label:'Surat Keluar',href:'/surat-keluar',icon:Send,roles:allRoles},
			{label:'Disposisi',href:'/disposisi',icon:Forward,roles:allRoles},
			{label:'Arsip',href:'/arsip',icon:Archive,roles:allRoles},
			{label:'Pengguna',href:'/pengguna',icon:UsersRound,roles:superAdmin}
		]
	},
	{
		label:'Pages',
		items:[
			{label:'Master Data',href:'/master-data',icon:Boxes,roles:superAdmin},
			{label:'Laporan',href:'/laporan',icon:ChartNoAxesCombined,roles:allRoles}
		]
	},
	{
		label:'Other',
		items:[
			{label:'Pengaturan',href:'/pengaturan',icon:Settings2,roles:superAdmin},
			{label:'Pusat Bantuan',href:'/bantuan',icon:CircleHelp,roles:allRoles}
		]
	}
];
