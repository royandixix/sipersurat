<script lang="ts">
	import{browser}from'$app/environment';
	import{onMount}from'svelte';
	import{fly}from'svelte/transition';
	import*as Card from'$lib/components/ui/card/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import{Input}from'$lib/components/ui/input/index.js';
	import BarChart3 from'@lucide/svelte/icons/bar-chart-3';
	import Inbox from'@lucide/svelte/icons/inbox';
	import Send from'@lucide/svelte/icons/send';
	import Forward from'@lucide/svelte/icons/forward';
	import Archive from'@lucide/svelte/icons/archive';
	import Search from'@lucide/svelte/icons/search';
	import SlidersHorizontal from'@lucide/svelte/icons/sliders-horizontal';
	import RotateCcw from'@lucide/svelte/icons/rotate-ccw';
	import FileDown from'@lucide/svelte/icons/file-down';
	import Printer from'@lucide/svelte/icons/printer';
	import CalendarDays from'@lucide/svelte/icons/calendar-days';
	import type{IncomingMailRecord}from'$lib/features/surat-masuk/types';
	import type{OutgoingMailRecord}from'$lib/features/surat-keluar/types';
	import type{DispositionRecord}from'$lib/features/disposisi/types';
	import{
		DISPOSITION_STATUS_LABELS
	}from'$lib/features/disposisi/types';
	import{
		OUTGOING_MAIL_STATUS_LABELS
	}from'$lib/features/surat-keluar/types';
	import ReportTable from'$lib/features/laporan/components/ReportTable.svelte';
	import type{
		MonthlyReport,
		ReportRecord,
		ReportSource
	}from'$lib/features/laporan/types';
	import{
		formatReportDate,
		REPORT_SOURCE_LABELS
	}from'$lib/features/laporan/types';

	const INCOMING_STORAGE_KEY='sipersurat-incoming-mail';
	const OUTGOING_STORAGE_KEY='sipersurat-outgoing-mail';
	const DISPOSITION_STORAGE_KEY='sipersurat-dispositions';
	const perPage=8;

	const incomingStatusLabels:Record<string,string>={
		RECEIVED:'Diterima',
		PENDING_DISPOSITION:'Menunggu Disposisi',
		DISPOSITIONED:'Didisposisikan',
		IN_PROGRESS:'Diproses',
		COMPLETED:'Selesai',
		ARCHIVED:'Diarsipkan'
	};

	const monthLabels=[
		'Jan',
		'Feb',
		'Mar',
		'Apr',
		'Mei',
		'Jun',
		'Jul',
		'Agu',
		'Sep',
		'Okt',
		'Nov',
		'Des'
	];

	let incomingMails=$state<IncomingMailRecord[]>([]);
	let outgoingMails=$state<OutgoingMailRecord[]>([]);
	let dispositions=$state<DispositionRecord[]>([]);
	let initialized=$state(false);

	let search=$state('');
	let sourceFilter=$state<'ALL'|ReportSource>('ALL');
	let categoryFilter=$state('ALL');
	let unitFilter=$state('ALL');
	let statusFilter=$state('ALL');
	let startDate=$state('');
	let endDate=$state('');
	let currentPage=$state(1);
	let selectedYear=$state(String(new Date().getFullYear()));

	onMount(()=>{
		if(!browser)return;

		incomingMails=readStorage<IncomingMailRecord>(
			INCOMING_STORAGE_KEY
		);

		outgoingMails=readStorage<OutgoingMailRecord>(
			OUTGOING_STORAGE_KEY
		);

		dispositions=readStorage<DispositionRecord>(
			DISPOSITION_STORAGE_KEY
		);

		initialized=true;
	});

	function readStorage<T>(key:string){
		if(!browser)return[]as T[];

		const saved=localStorage.getItem(key);

		if(!saved)return[]as T[];

		try{
			const parsed=JSON.parse(saved);

			return Array.isArray(parsed)
				?parsed as T[]
				:[]as T[];
		}catch{
			return[]as T[];
		}
	}

	const reportRecords=$derived.by(()=>{
		const incoming:ReportRecord[]=incomingMails.map((record)=>({
			id:`INCOMING-${record.id}`,
			source:'INCOMING',
			date:normalizeDate(
				record.receivedDate||
				record.letterDate||
				record.createdAt
			),
			agendaNumber:record.agendaNumber,
			letterNumber:record.letterNumber,
			correspondent:record.sender,
			subject:record.subject,
			category:record.category||'-',
			unit:record.targetUnit||'-',
			status:incomingStatusLabels[record.status]??record.status
		}));

		const outgoing:ReportRecord[]=outgoingMails.map((record)=>({
			id:`OUTGOING-${record.id}`,
			source:'OUTGOING',
			date:normalizeDate(
				record.sentDate||
				record.letterDate||
				record.createdAt
			),
			agendaNumber:record.agendaNumber,
			letterNumber:record.letterNumber,
			correspondent:record.recipient,
			subject:record.subject,
			category:record.category||'-',
			unit:record.sourceUnit||'-',
			status:OUTGOING_MAIL_STATUS_LABELS[record.status]??record.status
		}));

		const dispositionRows:ReportRecord[]=dispositions.map((record)=>({
			id:`DISPOSITION-${record.id}`,
			source:'DISPOSITION',
			date:normalizeDate(
				record.createdAt||
				record.updatedAt
			),
			agendaNumber:record.agendaNumber,
			letterNumber:record.letterNumber,
			correspondent:record.targetName,
			subject:record.subject,
			category:'Disposisi',
			unit:record.targetName||'-',
			status:DISPOSITION_STATUS_LABELS[record.status]??record.status
		}));

		const incomingArchive:ReportRecord[]=incomingMails
			.filter((record)=>record.status==='ARCHIVED')
			.map((record)=>({
				id:`ARCHIVE-INCOMING-${record.id}`,
				source:'ARCHIVE',
				date:normalizeDate(
					record.updatedAt||
						record.receivedDate||
						record.letterDate
				),
				agendaNumber:record.agendaNumber,
				letterNumber:record.letterNumber,
				correspondent:record.sender,
				subject:record.subject,
				category:record.category||'-',
				unit:record.targetUnit||'-',
				status:'Diarsipkan'
			}));

		const outgoingArchive:ReportRecord[]=outgoingMails
			.filter((record)=>record.status==='ARCHIVED')
			.map((record)=>({
				id:`ARCHIVE-OUTGOING-${record.id}`,
				source:'ARCHIVE',
				date:normalizeDate(
					record.updatedAt||
						record.sentDate||
						record.letterDate
				),
				agendaNumber:record.agendaNumber,
				letterNumber:record.letterNumber,
				correspondent:record.recipient,
				subject:record.subject,
				category:record.category||'-',
				unit:record.sourceUnit||'-',
				status:'Diarsipkan'
			}));

		return[
			...incoming,
			...outgoing,
			...dispositionRows,
			...incomingArchive,
			...outgoingArchive
		].sort((a,b)=>
			b.date.localeCompare(a.date)
		);
	});

	const categories=$derived.by(()=>{
		return uniqueValues(
			reportRecords.map((record)=>record.category)
		);
	});

	const units=$derived.by(()=>{
		return uniqueValues(
			reportRecords.map((record)=>record.unit)
		);
	});

	const statuses=$derived.by(()=>{
		return uniqueValues(
			reportRecords.map((record)=>record.status)
		);
	});

	const availableYears=$derived.by(()=>{
		const years=reportRecords
			.map((record)=>record.date.slice(0,4))
			.filter((year)=>/^\d{4}$/.test(year));

		years.push(
			String(new Date().getFullYear())
		);

		return[
			...new Set(years)
		].sort((a,b)=>Number(b)-Number(a));
	});

	const filteredRecords=$derived(
		reportRecords.filter((record)=>{
			const keyword=search.trim().toLowerCase();

			const matchesSearch=
				!keyword||
				record.agendaNumber.toLowerCase().includes(keyword)||
				record.letterNumber.toLowerCase().includes(keyword)||
				record.correspondent.toLowerCase().includes(keyword)||
				record.subject.toLowerCase().includes(keyword)||
				record.category.toLowerCase().includes(keyword)||
				record.unit.toLowerCase().includes(keyword)||
				record.status.toLowerCase().includes(keyword);

			const matchesSource=
				sourceFilter==='ALL'||
				record.source===sourceFilter;

			const matchesCategory=
				categoryFilter==='ALL'||
				record.category===categoryFilter;

			const matchesUnit=
				unitFilter==='ALL'||
				record.unit===unitFilter;

			const matchesStatus=
				statusFilter==='ALL'||
				record.status===statusFilter;

			const matchesStart=
				!startDate||
				record.date>=startDate;

			const matchesEnd=
				!endDate||
				record.date<=endDate;

			return(
				matchesSearch&&
				matchesSource&&
				matchesCategory&&
				matchesUnit&&
				matchesStatus&&
				matchesStart&&
				matchesEnd
			);
		})
	);

	const totalPages=$derived(
		Math.max(
			1,
			Math.ceil(
				filteredRecords.length/perPage
			)
		)
	);

	const paginatedRecords=$derived(
		filteredRecords.slice(
			(currentPage-1)*perPage,
			currentPage*perPage
		)
	);

	const incomingCount=$derived(
		filteredRecords.filter(
			(record)=>record.source==='INCOMING'
		).length
	);

	const outgoingCount=$derived(
		filteredRecords.filter(
			(record)=>record.source==='OUTGOING'
		).length
	);

	const dispositionCount=$derived(
		filteredRecords.filter(
			(record)=>record.source==='DISPOSITION'
		).length
	);

	const archiveCount=$derived(
		filteredRecords.filter(
			(record)=>record.source==='ARCHIVE'
		).length
	);

	const monthlyData=$derived.by(()=>{
		const result:MonthlyReport[]=monthLabels.map(
			(label,index)=>({
				month:index,
				label,
				incoming:0,
				outgoing:0,
				disposition:0,
				archive:0,
				total:0
			})
		);

		reportRecords.forEach((record)=>{
			if(
				record.date.slice(0,4)!==
				selectedYear
			){
				return;
			}

			const month=Number(
				record.date.slice(5,7)
			)-1;

			if(month<0||month>11)return;

			if(record.source==='INCOMING'){
				result[month].incoming++;
			}else if(record.source==='OUTGOING'){
				result[month].outgoing++;
			}else if(record.source==='DISPOSITION'){
				result[month].disposition++;
			}else if(record.source==='ARCHIVE'){
				result[month].archive++;
			}

			result[month].total++;
		});

		return result;
	});

	const maxMonthlySeries=$derived.by(()=>{
		const values=monthlyData.flatMap(
			(item)=>[
				item.incoming,
				item.outgoing,
				item.disposition,
				item.archive
			]
		);

		return Math.max(
			1,
			...values
		);
	});

	const annualIncoming=$derived(
		monthlyData.reduce(
			(total,item)=>
				total+item.incoming,
			0
		)
	);

	const annualOutgoing=$derived(
		monthlyData.reduce(
			(total,item)=>
				total+item.outgoing,
			0
		)
	);

	const annualDisposition=$derived(
		monthlyData.reduce(
			(total,item)=>
				total+item.disposition,
			0
		)
	);

	const annualArchive=$derived(
		monthlyData.reduce(
			(total,item)=>
				total+item.archive,
			0
		)
	);

	const annualTotal=$derived(
		annualIncoming+
		annualOutgoing+
		annualDisposition+
		annualArchive
	);

	const hasFilter=$derived(
		search.trim()!==''||
		sourceFilter!=='ALL'||
		categoryFilter!=='ALL'||
		unitFilter!=='ALL'||
		statusFilter!=='ALL'||
		startDate!==''||
		endDate!==''
	);

	$effect(()=>{
		if(currentPage>totalPages){
			currentPage=totalPages;
		}
	});

	function normalizeDate(value:string){
		if(
			value&&
			/^\d{4}-\d{2}-\d{2}/.test(value)
		){
			return value.slice(0,10);
		}

		return new Date()
			.toISOString()
			.slice(0,10);
	}

	function uniqueValues(values:string[]){
		return[
			...new Set(
				values.filter(
					(value)=>
						value&&value!=='-'
				)
			)
		].sort((a,b)=>
			a.localeCompare(b,'id')
		);
	}

	function resetFilter(){
		search='';
		sourceFilter='ALL';
		categoryFilter='ALL';
		unitFilter='ALL';
		statusFilter='ALL';
		startDate='';
		endDate='';
		currentPage=1;
	}

	function filterSource(source:ReportSource){
		sourceFilter=source;
		currentPage=1;
	}

	function csvValue(value:string|number){
		const text=String(value??'')
			.replace(/"/g,'""');

		return`"${text}"`;
	}

	function exportCsv(){
		if(!browser)return;

		const header=[
			'Tanggal',
			'Jenis',
			'Nomor Agenda',
			'Nomor Surat',
			'Pengirim/Penerima',
			'Perihal',
			'Kategori',
			'Unit Kerja',
			'Status'
		];

		const rows=filteredRecords.map(
			(record)=>[
				record.date,
				REPORT_SOURCE_LABELS[record.source],
				record.agendaNumber,
				record.letterNumber,
				record.correspondent,
				record.subject,
				record.category,
				record.unit,
				record.status
			]
		);

		const csv=[
			header,
			...rows
		]
			.map((row)=>
				row
					.map(csvValue)
					.join(';')
			)
			.join('\n');

		const blob=new Blob(
			[
				'\uFEFF',
				csv
			],
			{
				type:'text/csv;charset=utf-8;'
			}
		);

		const url=URL.createObjectURL(blob);
		const link=document.createElement('a');

		link.href=url;
		link.download=`laporan-sipersurat-${new Date().toISOString().slice(0,10)}.csv`;

		document.body.appendChild(link);
		link.click();
		link.remove();

		URL.revokeObjectURL(url);
	}

	function escapeHtml(value:string){
		return value
			.replace(/&/g,'&amp;')
			.replace(/</g,'&lt;')
			.replace(/>/g,'&gt;')
			.replace(/"/g,'&quot;')
			.replace(/'/g,'&#039;');
	}

	function printReport(){
		if(!browser)return;

		const popup=window.open(
			'',
			'_blank',
			'width=1100,height=800'
		);

		if(!popup)return;

		const tableRows=filteredRecords
			.map((record)=>`
				<tr>
					<td>${escapeHtml(formatReportDate(record.date))}</td>
					<td>${escapeHtml(REPORT_SOURCE_LABELS[record.source])}</td>
					<td>${escapeHtml(record.agendaNumber)}</td>
					<td>${escapeHtml(record.letterNumber)}</td>
					<td>${escapeHtml(record.correspondent)}</td>
					<td>${escapeHtml(record.subject)}</td>
					<td>${escapeHtml(record.unit)}</td>
					<td>${escapeHtml(record.status)}</td>
				</tr>
			`)
			.join('');

		const periodStart=startDate
			?formatReportDate(startDate)
			:'Semua';

		const periodEnd=endDate
			?formatReportDate(endDate)
			:'Semua';

		popup.document.write(`
			<!doctype html>
			<html lang="id">
			<head>
				<meta charset="utf-8">
				<title>Laporan SiPersurat</title>
				<style>
					*{box-sizing:border-box}
					body{margin:0;padding:32px;font-family:Arial,sans-serif;color:#18181b;font-size:12px}
					h1{margin:0;font-size:22px}
					.subtitle{margin-top:6px;color:#71717a;font-size:11px}
					.meta{display:flex;gap:32px;margin-top:20px;padding:12px 0;border-top:1px solid #e4e4e7;border-bottom:1px solid #e4e4e7}
					.meta div{display:flex;flex-direction:column;gap:4px}
					.meta span{color:#71717a;font-size:10px}
					.meta strong{font-size:12px}
					.stats{display:grid;grid-template-columns:repeat(4,1fr);gap:10px;margin-top:20px}
					.stat{border:1px solid #e4e4e7;border-radius:8px;padding:12px}
					.stat span{display:block;color:#71717a;font-size:10px}
					.stat strong{display:block;margin-top:8px;font-size:20px}
					table{width:100%;border-collapse:collapse;margin-top:22px}
					th{padding:9px 7px;border:1px solid #d4d4d8;background:#f4f4f5;text-align:left;font-size:9px}
					td{padding:8px 7px;border:1px solid #e4e4e7;vertical-align:top;font-size:9px;line-height:1.4}
					.footer{margin-top:18px;color:#71717a;font-size:9px}
					@media print{
						body{padding:0}
						@page{size:A4 landscape;margin:12mm}
					}
				</style>
			</head>
			<body>
				<h1>Laporan Sistem Persuratan</h1>
				<div class="subtitle">SiPersurat · Dicetak ${escapeHtml(new Intl.DateTimeFormat('id-ID',{
					day:'2-digit',
					month:'long',
					year:'numeric',
					hour:'2-digit',
					minute:'2-digit'
				}).format(new Date()))}</div>

				<div class="meta">
					<div>
						<span>Periode Mulai</span>
						<strong>${escapeHtml(periodStart)}</strong>
					</div>

					<div>
						<span>Periode Akhir</span>
						<strong>${escapeHtml(periodEnd)}</strong>
					</div>

					<div>
						<span>Jumlah Data</span>
						<strong>${filteredRecords.length}</strong>
					</div>
				</div>

				<div class="stats">
					<div class="stat">
						<span>Surat Masuk</span>
						<strong>${incomingCount}</strong>
					</div>

					<div class="stat">
						<span>Surat Keluar</span>
						<strong>${outgoingCount}</strong>
					</div>

					<div class="stat">
						<span>Disposisi</span>
						<strong>${dispositionCount}</strong>
					</div>

					<div class="stat">
						<span>Arsip</span>
						<strong>${archiveCount}</strong>
					</div>
				</div>

				<table>
					<thead>
						<tr>
							<th>Tanggal</th>
							<th>Jenis</th>
							<th>Agenda</th>
							<th>Nomor Surat</th>
							<th>Pengirim/Penerima</th>
							<th>Perihal</th>
							<th>Unit</th>
							<th>Status</th>
						</tr>
					</thead>

					<tbody>
						${tableRows||`
							<tr>
								<td colspan="8">Tidak ada data laporan.</td>
							</tr>
						`}
					</tbody>
				</table>

				<div class="footer">
					Dokumen dihasilkan dari Sistem Informasi Persuratan.
				</div>

				<script>
					window.onload=()=>{
						window.print();
					};
				<\/script>
			</body>
			</html>
		`);

		popup.document.close();
	}
</script>

<svelte:head>
	<title>Laporan | SiPersurat</title>
</svelte:head>

<div class="w-full min-w-0 overflow-x-hidden">
	<div class="mx-auto w-full min-w-0 max-w-[1500px] px-5 py-7 md:px-8 lg:px-10">
		<div class="flex flex-col justify-between gap-6 lg:flex-row lg:items-start">
			<div
				class="min-w-0"
				in:fly={{y:18,duration:450}}
			>
				<h1 class="text-2xl font-bold tracking-tight md:text-[28px]">
					Laporan
				</h1>

				<p class="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
					Pantau dan analisis aktivitas persuratan secara terpusat. Laporan menggabungkan data surat masuk, surat keluar, disposisi, dan arsip untuk membantu proses monitoring, evaluasi, serta penyusunan rekap administrasi.
				</p>
			</div>

			<div
				class="flex shrink-0 flex-col gap-2 sm:flex-row"
				in:fly={{y:14,duration:420,delay:100}}
			>
				<Button
					type="button"
					variant="outline"
					class="h-10 rounded-lg px-4 text-xs font-semibold"
					onclick={exportCsv}
				>
					<FileDown class="size-4"/>
					Export Excel
				</Button>

				<Button
					type="button"
					class="h-10 rounded-lg px-4 text-xs font-semibold"
					onclick={printReport}
				>
					<Printer class="size-4"/>
					Cetak / PDF
				</Button>
			</div>
		</div>

		<section class="mt-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
			<div in:fly={{y:22,duration:450,delay:100}}>
				<button
					type="button"
					class="group h-full w-full text-left"
					onclick={()=>filterSource('INCOMING')}
				>
					<Card.Root class="h-full rounded-xl border shadow-none transition-all duration-200 group-hover:-translate-y-1 group-hover:border-blue-200 group-hover:shadow-md">
						<Card.Content class="p-5">
							<div class="flex items-center justify-between">
								<p class="text-xs font-semibold">
									Surat Masuk
								</p>

								<div class="flex size-8 items-center justify-center rounded-lg bg-blue-50 text-blue-700 transition group-hover:scale-110">
									<Inbox class="size-4"/>
								</div>
							</div>

							<p class="mt-7 text-[28px] font-bold leading-none">
								{incomingCount}
							</p>

							<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
								Surat masuk sesuai filter laporan saat ini.
							</p>
						</Card.Content>
					</Card.Root>
				</button>
			</div>

			<div in:fly={{y:22,duration:450,delay:170}}>
				<button
					type="button"
					class="group h-full w-full text-left"
					onclick={()=>filterSource('OUTGOING')}
				>
					<Card.Root class="h-full rounded-xl border shadow-none transition-all duration-200 group-hover:-translate-y-1 group-hover:border-violet-200 group-hover:shadow-md">
						<Card.Content class="p-5">
							<div class="flex items-center justify-between">
								<p class="text-xs font-semibold">
									Surat Keluar
								</p>

								<div class="flex size-8 items-center justify-center rounded-lg bg-violet-50 text-violet-700 transition group-hover:scale-110">
									<Send class="size-4"/>
								</div>
							</div>

							<p class="mt-7 text-[28px] font-bold leading-none">
								{outgoingCount}
							</p>

							<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
								Surat keluar sesuai filter laporan saat ini.
							</p>
						</Card.Content>
					</Card.Root>
				</button>
			</div>

			<div in:fly={{y:22,duration:450,delay:240}}>
				<button
					type="button"
					class="group h-full w-full text-left"
					onclick={()=>filterSource('DISPOSITION')}
				>
					<Card.Root class="h-full rounded-xl border shadow-none transition-all duration-200 group-hover:-translate-y-1 group-hover:border-amber-200 group-hover:shadow-md">
						<Card.Content class="p-5">
							<div class="flex items-center justify-between">
								<p class="text-xs font-semibold">
									Disposisi
								</p>

								<div class="flex size-8 items-center justify-center rounded-lg bg-amber-50 text-amber-700 transition group-hover:scale-110">
									<Forward class="size-4"/>
								</div>
							</div>

							<p class="mt-7 text-[28px] font-bold leading-none">
								{dispositionCount}
							</p>

							<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
								Disposisi surat berdasarkan filter yang aktif.
							</p>
						</Card.Content>
					</Card.Root>
				</button>
			</div>

			<div in:fly={{y:22,duration:450,delay:310}}>
				<button
					type="button"
					class="group h-full w-full text-left"
					onclick={()=>filterSource('ARCHIVE')}
				>
					<Card.Root class="h-full rounded-xl border shadow-none transition-all duration-200 group-hover:-translate-y-1 group-hover:border-emerald-200 group-hover:shadow-md">
						<Card.Content class="p-5">
							<div class="flex items-center justify-between">
								<p class="text-xs font-semibold">
									Arsip
								</p>

								<div class="flex size-8 items-center justify-center rounded-lg bg-emerald-50 text-emerald-700 transition group-hover:scale-110">
									<Archive class="size-4"/>
								</div>
							</div>

							<p class="mt-7 text-[28px] font-bold leading-none">
								{archiveCount}
							</p>

							<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
								Dokumen arsip sesuai filter laporan saat ini.
							</p>
						</Card.Content>
					</Card.Root>
				</button>
			</div>
		</section>

		<div in:fly={{y:20,duration:450,delay:370}}>
			<Card.Root class="mt-6 rounded-xl border shadow-none">
				<Card.Header class="border-b px-5 py-5 sm:px-6">
					<div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
						<div>
							<Card.Title class="flex items-center gap-2 text-base font-semibold">
								<BarChart3 class="size-4"/>
								Aktivitas Persuratan Bulanan
							</Card.Title>

							<Card.Description class="mt-1.5 text-xs leading-5">
								Perbandingan aktivitas surat masuk, surat keluar, disposisi, dan arsip setiap bulan.
							</Card.Description>
						</div>

						<select
							bind:value={selectedYear}
							class="h-9 rounded-lg border border-input bg-background px-3 text-xs font-semibold outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
						>
							{#each availableYears as year}
								<option value={year}>
									{year}
								</option>
							{/each}
						</select>
					</div>
				</Card.Header>

				<Card.Content class="p-5 sm:p-6">
					<div class="grid gap-6 xl:grid-cols-[minmax(0,1fr)_250px]">
						<div class="min-w-0 overflow-x-auto">
							<div class="min-w-[720px]">
								<div class="flex h-[250px] items-end gap-3 border-b border-l pl-3">
									{#each monthlyData as item}
										<div class="flex h-full min-w-0 flex-1 flex-col justify-end">
											<div class="flex h-[210px] items-end justify-center gap-1">
												<div
													class="w-[18%] min-w-[5px] rounded-t bg-blue-500 transition-all duration-500"
													style={`height:${item.incoming===0?0:Math.max(5,(item.incoming/maxMonthlySeries)*100)}%`}
													title={`Surat Masuk ${item.label}: ${item.incoming}`}
												></div>

												<div
													class="w-[18%] min-w-[5px] rounded-t bg-violet-500 transition-all duration-500"
													style={`height:${item.outgoing===0?0:Math.max(5,(item.outgoing/maxMonthlySeries)*100)}%`}
													title={`Surat Keluar ${item.label}: ${item.outgoing}`}
												></div>

												<div
													class="w-[18%] min-w-[5px] rounded-t bg-amber-500 transition-all duration-500"
													style={`height:${item.disposition===0?0:Math.max(5,(item.disposition/maxMonthlySeries)*100)}%`}
													title={`Disposisi ${item.label}: ${item.disposition}`}
												></div>

												<div
													class="w-[18%] min-w-[5px] rounded-t bg-emerald-500 transition-all duration-500"
													style={`height:${item.archive===0?0:Math.max(5,(item.archive/maxMonthlySeries)*100)}%`}
													title={`Arsip ${item.label}: ${item.archive}`}
												></div>
											</div>

											<div class="mt-3 text-center">
												<p class="text-[10px] font-semibold">
													{item.label}
												</p>

												<p class="mt-0.5 text-[9px] text-muted-foreground">
													{item.total}
												</p>
											</div>
										</div>
									{/each}
								</div>

								<div class="mt-5 flex flex-wrap items-center justify-center gap-x-5 gap-y-2">
									<div class="flex items-center gap-2">
										<span class="size-2 rounded-full bg-blue-500"></span>
										<span class="text-[10px] text-muted-foreground">
											Surat Masuk
										</span>
									</div>

									<div class="flex items-center gap-2">
										<span class="size-2 rounded-full bg-violet-500"></span>
										<span class="text-[10px] text-muted-foreground">
											Surat Keluar
										</span>
									</div>

									<div class="flex items-center gap-2">
										<span class="size-2 rounded-full bg-amber-500"></span>
										<span class="text-[10px] text-muted-foreground">
											Disposisi
										</span>
									</div>

									<div class="flex items-center gap-2">
										<span class="size-2 rounded-full bg-emerald-500"></span>
										<span class="text-[10px] text-muted-foreground">
											Arsip
										</span>
									</div>
								</div>
							</div>
						</div>

						<div class="rounded-xl border bg-muted/20 p-4">
							<div class="flex items-center justify-between">
								<div>
									<p class="text-xs font-semibold">
										Ringkasan {selectedYear}
									</p>

									<p class="mt-1 text-[10px] text-muted-foreground">
										Total aktivitas tahunan
									</p>
								</div>

								<div class="flex size-9 items-center justify-center rounded-lg bg-background shadow-sm">
									<CalendarDays class="size-4 text-muted-foreground"/>
								</div>
							</div>

							<p class="mt-6 text-3xl font-bold">
								{annualTotal}
							</p>

							<div class="mt-5 space-y-3">
								<div class="flex items-center justify-between">
									<div class="flex items-center gap-2">
										<span class="size-2 rounded-full bg-blue-500"></span>
										<span class="text-[11px] text-muted-foreground">
											Surat Masuk
										</span>
									</div>

									<strong class="text-xs">
										{annualIncoming}
									</strong>
								</div>

								<div class="flex items-center justify-between">
									<div class="flex items-center gap-2">
										<span class="size-2 rounded-full bg-violet-500"></span>
										<span class="text-[11px] text-muted-foreground">
											Surat Keluar
										</span>
									</div>

									<strong class="text-xs">
										{annualOutgoing}
									</strong>
								</div>

								<div class="flex items-center justify-between">
									<div class="flex items-center gap-2">
										<span class="size-2 rounded-full bg-amber-500"></span>
										<span class="text-[11px] text-muted-foreground">
											Disposisi
										</span>
									</div>

									<strong class="text-xs">
										{annualDisposition}
									</strong>
								</div>

								<div class="flex items-center justify-between">
									<div class="flex items-center gap-2">
										<span class="size-2 rounded-full bg-emerald-500"></span>
										<span class="text-[11px] text-muted-foreground">
											Arsip
										</span>
									</div>

									<strong class="text-xs">
										{annualArchive}
									</strong>
								</div>
							</div>
						</div>
					</div>
				</Card.Content>
			</Card.Root>
		</div>

		<div in:fly={{y:20,duration:450,delay:430}}>
			<Card.Root class="mt-6 rounded-xl border shadow-none">
				<Card.Content class="p-5 sm:p-6">
					<div>
						<h2 class="text-sm font-semibold">
							Filter Laporan
						</h2>

						<p class="mt-1 text-xs leading-5 text-muted-foreground">
							Atur periode dan kriteria data yang ingin ditampilkan maupun diekspor.
						</p>
					</div>

					<div class="relative mt-4">
						<Search class="pointer-events-none absolute left-3.5 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

						<Input
							bind:value={search}
							placeholder="Cari nomor surat, agenda, pengirim, penerima, perihal, atau unit..."
							class="h-11 rounded-lg pl-10 text-sm"
							oninput={()=>currentPage=1}
						/>
					</div>

					<div class="mt-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
						<div class="relative">
							<SlidersHorizontal class="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

							<select
								bind:value={sourceFilter}
								onchange={()=>currentPage=1}
								class="h-10 w-full rounded-lg border border-input bg-background pl-10 pr-3 text-xs font-medium outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
							>
								<option value="ALL">
									Semua Jenis
								</option>

								<option value="INCOMING">
									Surat Masuk
								</option>

								<option value="OUTGOING">
									Surat Keluar
								</option>

								<option value="DISPOSITION">
									Disposisi
								</option>

								<option value="ARCHIVE">
									Arsip
								</option>
							</select>
						</div>

						<select
							bind:value={categoryFilter}
							onchange={()=>currentPage=1}
							class="h-10 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
						>
							<option value="ALL">
								Semua Kategori
							</option>

							{#each categories as category}
								<option value={category}>
									{category}
								</option>
							{/each}
						</select>

						<select
							bind:value={unitFilter}
							onchange={()=>currentPage=1}
							class="h-10 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
						>
							<option value="ALL">
								Semua Unit
							</option>

							{#each units as unit}
								<option value={unit}>
									{unit}
								</option>
							{/each}
						</select>

						<select
							bind:value={statusFilter}
							onchange={()=>currentPage=1}
							class="h-10 w-full rounded-lg border border-input bg-background px-3 text-xs font-medium outline-none focus:border-ring focus:ring-2 focus:ring-ring/20"
						>
							<option value="ALL">
								Semua Status
							</option>

							{#each statuses as status}
								<option value={status}>
									{status}
								</option>
							{/each}
						</select>
					</div>

					<div class="mt-3 grid gap-3 sm:grid-cols-2 xl:grid-cols-[minmax(180px,1fr)_minmax(180px,1fr)_auto]">
						<div>
							<label
								for="report-start-date"
								class="mb-1.5 block text-[10px] font-semibold text-muted-foreground"
							>
								Dari Tanggal
							</label>

							<Input
								id="report-start-date"
								type="date"
								bind:value={startDate}
								onchange={()=>currentPage=1}
								class="h-10 rounded-lg text-xs"
							/>
						</div>

						<div>
							<label
								for="report-end-date"
								class="mb-1.5 block text-[10px] font-semibold text-muted-foreground"
							>
								Sampai Tanggal
							</label>

							<Input
								id="report-end-date"
								type="date"
								bind:value={endDate}
								onchange={()=>currentPage=1}
								class="h-10 rounded-lg text-xs"
							/>
						</div>

						<div class="flex items-end">
							{#if hasFilter}
								<Button
									type="button"
									variant="outline"
									class="h-10 w-full rounded-lg px-4 text-xs xl:w-auto"
									onclick={resetFilter}
								>
									<RotateCcw class="size-4"/>
									Reset Filter
								</Button>
							{/if}
						</div>
					</div>
				</Card.Content>
			</Card.Root>
		</div>

		<div in:fly={{y:24,duration:500,delay:500}}>
			<Card.Root class="mt-6 min-w-0 overflow-hidden rounded-xl border shadow-none">
				<Card.Header class="border-b px-5 py-5 sm:px-6">
					<div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
						<div>
							<Card.Title class="text-base font-semibold">
								Rekap Data Persuratan
							</Card.Title>

							<Card.Description class="mt-1.5 max-w-2xl text-xs leading-5">
								Data yang ditampilkan mengikuti seluruh filter laporan yang sedang aktif dan menjadi sumber data untuk export Excel maupun PDF.
							</Card.Description>
						</div>

						<div class="rounded-md bg-muted px-3 py-1.5 text-[11px] font-medium text-muted-foreground">
							{filteredRecords.length} data
						</div>
					</div>
				</Card.Header>

				<ReportTable
					records={paginatedRecords}
					{currentPage}
					{totalPages}
					totalItems={filteredRecords.length}
					{perPage}
					onPageChange={(page)=>currentPage=page}
				/>
			</Card.Root>
		</div>

		<div
			class="mt-6 flex items-center gap-2 rounded-xl border bg-muted/20 px-4 py-3"
			in:fly={{y:18,duration:450,delay:560}}
		>
			<BarChart3 class="size-4 shrink-0 text-muted-foreground"/>

			<p class="text-[11px] leading-5 text-muted-foreground">
				Laporan saat ini mengambil data langsung dari penyimpanan frontend SiPersurat. Setelah backend terintegrasi, sumber data ini akan diganti ke API dan database tanpa perlu mengubah struktur utama halaman laporan.
			</p>
		</div>
	</div>
</div>