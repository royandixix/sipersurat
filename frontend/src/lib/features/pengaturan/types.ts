export type DateFormat='DD/MM/YYYY'|'DD-MM-YYYY'|'YYYY-MM-DD';
export type NumberSeparator='-'|'/'|'.';

export interface InstitutionConfig{
	name:string;
	shortName:string;
	address:string;
	city:string;
	province:string;
	postalCode:string;
	phone:string;
	email:string;
	website:string;
	headName:string;
	headPosition:string;
	logoName:string;
	logoData:string;
}

export interface NumberingConfig{
	incomingPrefix:string;
	outgoingPrefix:string;
	dispositionPrefix:string;
	archivePrefix:string;
	activeYear:number;
	separator:NumberSeparator;
	padding:number;
	resetYearly:boolean;
	autoAgenda:boolean;
}

export interface AgendaConfig{
	dateFormat:DateFormat;
	timezone:string;
	workYear:number;
	incomingStartNumber:number;
	outgoingStartNumber:number;
	dispositionStartNumber:number;
}

export interface NotificationConfig{
	incomingMail:boolean;
	disposition:boolean;
	outgoingApproval:boolean;
	outgoingApproved:boolean;
	dispositionDeadline:boolean;
	archive:boolean;
	system:boolean;
	deadlineReminderDays:number;
}

export interface SecurityConfig{
	sessionMinutes:number;
	autoLogout:boolean;
	minPasswordLength:number;
	requireUppercase:boolean;
	requireLowercase:boolean;
	requireNumber:boolean;
	requireSymbol:boolean;
	maxLoginAttempts:number;
	twoFactorEnabled:boolean;
}

export interface AppearanceConfig{
	applicationName:string;
	sidebarName:string;
	footerText:string;
	compactSidebar:boolean;
	showInstitutionName:boolean;
}

export interface SystemSettings{
	institution:InstitutionConfig;
	numbering:NumberingConfig;
	agenda:AgendaConfig;
	notifications:NotificationConfig;
	security:SecurityConfig;
	appearance:AppearanceConfig;
	updatedAt:string;
}

export const SETTINGS_STORAGE_KEY='sipersurat-settings';

export const DEFAULT_SYSTEM_SETTINGS:SystemSettings={
	institution:{
		name:'SiPersurat',
		shortName:'SiPersurat',
		address:'',
		city:'',
		province:'',
		postalCode:'',
		phone:'',
		email:'',
		website:'',
		headName:'',
		headPosition:'',
		logoName:'',
		logoData:''
	},
	numbering:{
		incomingPrefix:'SM',
		outgoingPrefix:'SK',
		dispositionPrefix:'DSP',
		archivePrefix:'ARS',
		activeYear:new Date().getFullYear(),
		separator:'-',
		padding:4,
		resetYearly:true,
		autoAgenda:true
	},
	agenda:{
		dateFormat:'DD/MM/YYYY',
		timezone:'Asia/Makassar',
		workYear:new Date().getFullYear(),
		incomingStartNumber:1,
		outgoingStartNumber:1,
		dispositionStartNumber:1
	},
	notifications:{
		incomingMail:true,
		disposition:true,
		outgoingApproval:true,
		outgoingApproved:true,
		dispositionDeadline:true,
		archive:false,
		system:true,
		deadlineReminderDays:1
	},
	security:{
		sessionMinutes:120,
		autoLogout:true,
		minPasswordLength:8,
		requireUppercase:true,
		requireLowercase:true,
		requireNumber:true,
		requireSymbol:false,
		maxLoginAttempts:5,
		twoFactorEnabled:false
	},
	appearance:{
		applicationName:'SiPersurat',
		sidebarName:'SiPersurat',
		footerText:'Sistem Informasi Persuratan',
		compactSidebar:false,
		showInstitutionName:true
	},
	updatedAt:''
};

export function cloneDefaultSystemSettings():SystemSettings{
	return JSON.parse(
		JSON.stringify(DEFAULT_SYSTEM_SETTINGS)
	)as SystemSettings;
}