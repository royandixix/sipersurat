<script lang="ts">
	import{browser}from'$app/environment';
	import{onMount}from'svelte';
	import{fly}from'svelte/transition';
	import*as Card from'$lib/components/ui/card/index.js';
	import{Button}from'$lib/components/ui/button/index.js';
	import{Input}from'$lib/components/ui/input/index.js';

	import LifeBuoy from'@lucide/svelte/icons/life-buoy';
	import Search from'@lucide/svelte/icons/search';
	import BookOpen from'@lucide/svelte/icons/book-open';
	import Inbox from'@lucide/svelte/icons/inbox';
	import Forward from'@lucide/svelte/icons/forward';
	import Send from'@lucide/svelte/icons/send';
	import Archive from'@lucide/svelte/icons/archive';
	import BarChart3 from'@lucide/svelte/icons/bar-chart-3';
	import Settings from'@lucide/svelte/icons/settings';
	import Users from'@lucide/svelte/icons/users';
	import Database from'@lucide/svelte/icons/database';
	import ChevronRight from'@lucide/svelte/icons/chevron-right';
	import ChevronDown from'@lucide/svelte/icons/chevron-down';
	import CircleHelp from'@lucide/svelte/icons/circle-help';
	import Mail from'@lucide/svelte/icons/mail';
	import Phone from'@lucide/svelte/icons/phone';
	import ShieldCheck from'@lucide/svelte/icons/shield-check';
	import FileText from'@lucide/svelte/icons/file-text';
	import RotateCcw from'@lucide/svelte/icons/rotate-ccw';
	import CheckCircle2 from'@lucide/svelte/icons/check-circle-2';

	type GuideCategory=
		|'ALL'
		|'LETTER'
		|'ADMIN'
		|'REPORT'
		|'SYSTEM';

	type Guide={
		id:string;
		title:string;
		description:string;
		category:Exclude<GuideCategory,'ALL'>;
		href:string;
		icon:typeof Inbox;
	};

	type Faq={
		id:string;
		question:string;
		answer:string;
		category:Exclude<GuideCategory,'ALL'>;
	};

	const SETTINGS_STORAGE_KEY='sipersurat-settings';

	const categories:[
		{
			value:GuideCategory;
			label:string;
		}
	]=[
		{
			value:'ALL',
			label:'Semua'
		}
	] as never;

	const categoryOptions:{
		value:GuideCategory;
		label:string;
	}[]=[
		{
			value:'ALL',
			label:'Semua'
		},
		{
			value:'LETTER',
			label:'Persuratan'
		},
		{
			value:'REPORT',
			label:'Laporan'
		},
		{
			value:'ADMIN',
			label:'Administrasi'
		},
		{
			value:'SYSTEM',
			label:'Sistem'
		}
	];

	const guides:Guide[]=[
		{
			id:'incoming',
			title:'Surat Masuk',
			description:'Panduan mencatat, mengubah, memeriksa, dan memproses surat yang diterima.',
			category:'LETTER',
			href:'/surat-masuk',
			icon:Inbox
		},
		{
			id:'disposition',
			title:'Disposisi',
			description:'Pelajari cara meneruskan surat, menentukan penerima, instruksi, dan tindak lanjut.',
			category:'LETTER',
			href:'/disposisi',
			icon:Forward
		},
		{
			id:'outgoing',
			title:'Surat Keluar',
			description:'Panduan membuat draft, persetujuan, pengiriman, dan pengarsipan surat keluar.',
			category:'LETTER',
			href:'/surat-keluar',
			icon:Send
		},
		{
			id:'archive',
			title:'Arsip',
			description:'Cari, lihat, pulihkan, atau kelola dokumen yang telah diarsipkan.',
			category:'LETTER',
			href:'/arsip',
			icon:Archive
		},
		{
			id:'report',
			title:'Laporan',
			description:'Gunakan filter, grafik, rekap data, Export Excel, dan Cetak PDF.',
			category:'REPORT',
			href:'/laporan',
			icon:BarChart3
		},
		{
			id:'users',
			title:'Pengguna',
			description:'Panduan pengelolaan akun pengguna, role, status akun, dan akses sistem.',
			category:'ADMIN',
			href:'/pengguna',
			icon:Users
		},
		{
			id:'master',
			title:'Master Data',
			description:'Kelola kategori surat, sifat surat, unit kerja, dan data referensi lainnya.',
			category:'ADMIN',
			href:'/master-data',
			icon:Database
		},
		{
			id:'settings',
			title:'Pengaturan Sistem',
			description:'Konfigurasi instansi, nomor agenda, notifikasi, keamanan, serta backup data.',
			category:'SYSTEM',
			href:'/pengaturan',
			icon:Settings
		}
	];

	const faqs:Faq[]=[
		{
			id:'faq-1',
			question:'Bagaimana cara menambahkan Surat Masuk?',
			answer:'Buka menu Surat Masuk kemudian pilih tombol Tambah Surat Masuk. Isi nomor surat, tanggal surat, tanggal diterima, pengirim, perihal, kategori, sifat surat, unit tujuan, dan lampiran bila tersedia. Setelah data lengkap, pilih Simpan Surat.',
			category:'LETTER'
		},
		{
			id:'faq-2',
			question:'Apa fungsi nomor agenda pada Surat Masuk?',
			answer:'Nomor agenda digunakan sebagai identitas internal surat di SiPersurat. Nomor ini berbeda dengan nomor surat dari pengirim dan dapat dibuat secara otomatis oleh sistem.',
			category:'LETTER'
		},
		{
			id:'faq-3',
			question:'Bagaimana cara melakukan disposisi surat?',
			answer:'Buka halaman Disposisi kemudian pilih Buat Disposisi. Pilih surat masuk yang akan diteruskan, tentukan pengguna atau unit kerja tujuan, isi instruksi, prioritas, batas waktu, dan status disposisi lalu simpan.',
			category:'LETTER'
		},
		{
			id:'faq-4',
			question:'Apa perbedaan Surat Masuk dan Disposisi?',
			answer:'Surat Masuk merupakan dokumen yang diterima oleh instansi. Disposisi merupakan arahan atau proses penerusan dari surat tersebut kepada pengguna atau unit kerja tertentu untuk ditindaklanjuti.',
			category:'LETTER'
		},
		{
			id:'faq-5',
			question:'Bagaimana alur Surat Keluar?',
			answer:'Surat Keluar dapat dimulai dari Draft, kemudian diajukan menjadi Menunggu Persetujuan, setelah disetujui berubah menjadi Disetujui, lalu dapat ditandai Dikirim dan akhirnya Diarsipkan.',
			category:'LETTER'
		},
		{
			id:'faq-6',
			question:'Bagaimana surat masuk ke halaman Arsip?',
			answer:'Dokumen akan tampil pada halaman Arsip ketika status surat berubah menjadi Diarsipkan atau ARCHIVED. Arsip membaca data dari Surat Masuk dan Surat Keluar.',
			category:'LETTER'
		},
		{
			id:'faq-7',
			question:'Apakah arsip bisa dipulihkan?',
			answer:'Bisa. Pilih dokumen pada halaman Arsip kemudian gunakan aksi Pulihkan dari Arsip. Surat Masuk akan dikembalikan ke status Selesai, sedangkan Surat Keluar akan dikembalikan ke status Dikirim.',
			category:'LETTER'
		},
		{
			id:'faq-8',
			question:'Apa fungsi Hapus Permanen pada Arsip?',
			answer:'Hapus Permanen menghapus data dokumen dari sumber datanya, bukan sekadar menghilangkannya dari halaman Arsip. Gunakan fitur ini dengan hati-hati.',
			category:'LETTER'
		},
		{
			id:'faq-9',
			question:'Bagaimana membuat laporan berdasarkan periode?',
			answer:'Buka menu Laporan lalu tentukan Dari Tanggal dan Sampai Tanggal. Kamu juga dapat memfilter jenis aktivitas, kategori, unit kerja, dan status sebelum mencetak atau mengekspor laporan.',
			category:'REPORT'
		},
		{
			id:'faq-10',
			question:'Bagaimana cara Export laporan ke Excel?',
			answer:'Pada halaman Laporan pilih Export Excel. Dalam frontend saat ini file yang dihasilkan menggunakan format CSV yang dapat langsung dibuka menggunakan Microsoft Excel atau WPS Office.',
			category:'REPORT'
		},
		{
			id:'faq-11',
			question:'Bagaimana menyimpan laporan menjadi PDF?',
			answer:'Pilih tombol Cetak / PDF pada halaman Laporan. Sistem akan membuka tampilan laporan khusus cetak. Pada dialog printer browser pilih Save as PDF.',
			category:'REPORT'
		},
		{
			id:'faq-12',
			question:'Apa fungsi Master Data?',
			answer:'Master Data menyimpan data referensi yang digunakan oleh modul lain, seperti kategori surat, sifat surat, dan unit kerja sehingga pilihan data dapat digunakan secara konsisten di seluruh sistem.',
			category:'ADMIN'
		},
		{
			id:'faq-13',
			question:'Apa fungsi halaman Pengguna?',
			answer:'Halaman Pengguna digunakan untuk mengelola akun yang dapat mengakses SiPersurat, termasuk informasi pengguna, role, unit kerja, dan status akun.',
			category:'ADMIN'
		},
		{
			id:'faq-14',
			question:'Di mana mengubah format nomor agenda?',
			answer:'Buka Pengaturan Sistem kemudian pilih Penomoran & Agenda. Di sana tersedia prefix Surat Masuk, Surat Keluar, Disposisi, separator, tahun aktif, jumlah digit, dan nomor awal agenda.',
			category:'SYSTEM'
		},
		{
			id:'faq-15',
			question:'Bagaimana melakukan backup data SiPersurat?',
			answer:'Buka Pengaturan Sistem lalu masuk ke Data Sistem dan pilih Export Backup. Data frontend akan disimpan ke file JSON yang dapat digunakan kembali melalui fitur Import Backup.',
			category:'SYSTEM'
		},
		{
			id:'faq-16',
			question:'Di mana data aplikasi disimpan saat ini?',
			answer:'Pada tahap frontend saat ini data SiPersurat masih disimpan menggunakan localStorage browser. Setelah integrasi backend selesai, data akan dipindahkan ke API dan database server.',
			category:'SYSTEM'
		}
	];

	let search=$state('');
	let selectedCategory=$state<GuideCategory>('ALL');
	let openFaq=$state<string|null>(null);

	let adminEmail=$state('');
	let adminPhone=$state('');
	let institutionName=$state('SiPersurat');

	onMount(()=>{
		if(!browser)return;

		const saved=localStorage.getItem(
			SETTINGS_STORAGE_KEY
		);

		if(!saved)return;

		try{
			const parsed=JSON.parse(saved);

			institutionName=
				parsed?.institution?.name||
				'SiPersurat';

			adminEmail=
				parsed?.institution?.email||
				'';

			adminPhone=
				parsed?.institution?.phone||
				'';
		}catch{
			institutionName='SiPersurat';
			adminEmail='';
			adminPhone='';
		}
	});

	const filteredGuides=$derived.by(()=>{
		const keyword=
			search.trim().toLowerCase();

		return guides.filter((guide)=>{
			const categoryMatch=
				selectedCategory==='ALL'||
				guide.category===selectedCategory;

			const searchMatch=
				!keyword||
				guide.title
					.toLowerCase()
					.includes(keyword)||
				guide.description
					.toLowerCase()
					.includes(keyword);

			return categoryMatch&&searchMatch;
		});
	});

	const filteredFaqs=$derived.by(()=>{
		const keyword=
			search.trim().toLowerCase();

		return faqs.filter((faq)=>{
			const categoryMatch=
				selectedCategory==='ALL'||
				faq.category===selectedCategory;

			const searchMatch=
				!keyword||
				faq.question
					.toLowerCase()
					.includes(keyword)||
				faq.answer
					.toLowerCase()
					.includes(keyword);

			return categoryMatch&&searchMatch;
		});
	});

	const resultCount=$derived(
		filteredGuides.length+
		filteredFaqs.length
	);

	function toggleFaq(id:string){
		openFaq=
			openFaq===id
				?null
				:id;
	}

	function resetSearch(){
		search='';
		selectedCategory='ALL';
		openFaq=null;
	}
</script>

<svelte:head>
	<title>Pusat Bantuan | SiPersurat</title>
</svelte:head>

<div class="w-full min-w-0 overflow-x-hidden">
	<div class="mx-auto w-full max-w-[1500px] px-5 py-7 md:px-8 lg:px-10">
		<section
			class="relative overflow-hidden rounded-2xl border bg-foreground px-5 py-8 text-background sm:px-8 sm:py-10"
			in:fly={{
				y:18,
				duration:450
			}}
		>
			<div class="pointer-events-none absolute -right-12 -top-20 size-64 rounded-full bg-background/5"></div>
			<div class="pointer-events-none absolute -bottom-28 right-28 size-56 rounded-full bg-background/5"></div>

			<div class="relative z-[1] mx-auto max-w-3xl text-center">
				<div class="mx-auto flex size-11 items-center justify-center rounded-xl bg-background/10">
					<LifeBuoy class="size-5"/>
				</div>

				<h1 class="mt-5 text-2xl font-bold tracking-tight sm:text-[30px]">
					Pusat Bantuan SiPersurat
				</h1>

				<p class="mx-auto mt-3 max-w-2xl text-xs leading-6 text-background/65 sm:text-sm">
					Temukan panduan penggunaan, alur persuratan, jawaban pertanyaan umum, dan informasi pengelolaan sistem.
				</p>

				<div class="relative mx-auto mt-7 max-w-2xl">
					<Search class="pointer-events-none absolute left-4 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"/>

					<Input
						bind:value={search}
						placeholder="Cari panduan, fitur, atau pertanyaan..."
						class="h-12 rounded-xl border-0 bg-background pl-11 pr-4 text-sm text-foreground shadow-lg"
					/>
				</div>

				<p class="mt-3 text-[10px] text-background/50">
					Contoh: disposisi, surat keluar, arsip, laporan, backup
				</p>
			</div>
		</section>

		<div
			class="mt-5 flex gap-2 overflow-x-auto pb-1"
			in:fly={{
				y:15,
				duration:420,
				delay:100
			}}
		>
			{#each categoryOptions as category}
				<button
					type="button"
					class={[
						'h-9 shrink-0 rounded-lg border px-3.5 text-[11px] font-semibold transition',
						selectedCategory===category.value
							?'border-foreground bg-foreground text-background'
							:'bg-background text-muted-foreground hover:bg-muted hover:text-foreground'
					]}
					onclick={()=>{
						selectedCategory=category.value;
						openFaq=null;
					}}
				>
					{category.label}
				</button>
			{/each}

			{#if search||selectedCategory!=='ALL'}
				<button
					type="button"
					class="inline-flex h-9 shrink-0 items-center gap-2 rounded-lg border px-3.5 text-[11px] font-semibold text-muted-foreground transition hover:bg-muted hover:text-foreground"
					onclick={resetSearch}
				>
					<RotateCcw class="size-3.5"/>
					Reset
				</button>
			{/if}
		</div>

		{#if search||selectedCategory!=='ALL'}
			<div class="mt-4 flex items-center gap-2 text-[11px] text-muted-foreground">
				<Search class="size-3.5"/>

				<span>
					Ditemukan
					<strong class="text-foreground">
						{resultCount}
					</strong>
					hasil bantuan
				</span>
			</div>
		{/if}

		<section
			class="mt-8"
			in:fly={{
				y:20,
				duration:450,
				delay:160
			}}
		>
			<div class="flex flex-col justify-between gap-3 sm:flex-row sm:items-end">
				<div>
					<div class="flex items-center gap-2">
						<BookOpen class="size-4 text-muted-foreground"/>

						<h2 class="text-lg font-bold tracking-tight">
							Panduan Cepat
						</h2>
					</div>

					<p class="mt-1.5 text-xs leading-5 text-muted-foreground">
						Pilih fitur untuk membuka halaman terkait dan mulai menggunakannya.
					</p>
				</div>

				<span class="w-fit rounded-md bg-muted px-2.5 py-1.5 text-[10px] font-medium text-muted-foreground">
					{filteredGuides.length} panduan
				</span>
			</div>

			{#if filteredGuides.length}
				<div class="mt-5 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
					{#each filteredGuides as guide,index}
						{@const Icon=guide.icon}

						<a
							href={guide.href}
							class="group block"
							in:fly={{
								y:18,
								duration:400,
								delay:Math.min(
									index*45,
									250
								)
							}}
						>
							<Card.Root class="h-full rounded-xl border shadow-none transition-all duration-200 group-hover:-translate-y-1 group-hover:shadow-md">
								<Card.Content class="p-5">
									<div class="flex items-start justify-between gap-4">
										<div class="flex size-10 items-center justify-center rounded-xl bg-muted text-muted-foreground transition group-hover:bg-foreground group-hover:text-background">
											<Icon class="size-4"/>
										</div>

										<ChevronRight class="size-4 text-muted-foreground transition-transform group-hover:translate-x-1 group-hover:text-foreground"/>
									</div>

									<h3 class="mt-5 text-sm font-bold">
										{guide.title}
									</h3>

									<p class="mt-2 text-[11px] leading-5 text-muted-foreground">
										{guide.description}
									</p>

									<div class="mt-5 flex items-center gap-2 text-[10px] font-semibold">
										Buka fitur

										<ChevronRight class="size-3"/>
									</div>
								</Card.Content>
							</Card.Root>
						</a>
					{/each}
				</div>
			{:else}
				<div class="mt-5 flex min-h-[220px] flex-col items-center justify-center rounded-xl border bg-muted/10 p-6 text-center">
					<div class="flex size-11 items-center justify-center rounded-lg bg-muted">
						<Search class="size-5 text-muted-foreground"/>
					</div>

					<p class="mt-4 text-sm font-semibold">
						Panduan tidak ditemukan
					</p>

					<p class="mt-1 max-w-sm text-xs leading-5 text-muted-foreground">
						Coba gunakan kata pencarian lain atau tampilkan semua kategori bantuan.
					</p>

					<Button
						type="button"
						variant="outline"
						class="mt-4 rounded-lg text-xs"
						onclick={resetSearch}
					>
						<RotateCcw class="size-4"/>
						Reset Pencarian
					</Button>
				</div>
			{/if}
		</section>

		<section
			class="mt-10 grid gap-5 xl:grid-cols-[minmax(0,1fr)_330px]"
			in:fly={{
				y:20,
				duration:450,
				delay:240
			}}
		>
			<Card.Root class="min-w-0 rounded-xl border shadow-none">
				<Card.Header class="border-b px-5 py-5 sm:px-6">
					<div class="flex items-start gap-3">
						<div class="flex size-9 shrink-0 items-center justify-center rounded-lg bg-muted text-muted-foreground">
							<CircleHelp class="size-4"/>
						</div>

						<div>
							<Card.Title class="text-base font-semibold">
								Pertanyaan Umum
							</Card.Title>

							<Card.Description class="mt-1.5 text-xs leading-5">
								Jawaban untuk pertanyaan yang paling sering muncul ketika menggunakan SiPersurat.
							</Card.Description>
						</div>
					</div>
				</Card.Header>

				<Card.Content class="p-0">
					{#if filteredFaqs.length}
						<div class="divide-y">
							{#each filteredFaqs as faq}
								<div>
									<button
										type="button"
										class="flex w-full items-center justify-between gap-5 px-5 py-4 text-left transition hover:bg-muted/30 sm:px-6"
										aria-expanded={openFaq===faq.id}
										onclick={()=>toggleFaq(faq.id)}
									>
										<div class="flex min-w-0 items-start gap-3">
											<div class="mt-0.5 flex size-6 shrink-0 items-center justify-center rounded-md bg-muted text-[10px] font-bold text-muted-foreground">
												?
											</div>

											<p class="text-xs font-semibold leading-5">
												{faq.question}
											</p>
										</div>

										<ChevronDown
											class={[
												'size-4 shrink-0 text-muted-foreground transition-transform duration-200',
												openFaq===faq.id
													?'rotate-180'
													:''
											]}
										/>
									</button>

									{#if openFaq===faq.id}
										<div class="px-5 pb-5 pl-[60px] sm:px-6 sm:pb-5 sm:pl-[68px]">
											<p class="max-w-3xl text-[11px] leading-6 text-muted-foreground">
												{faq.answer}
											</p>
										</div>
									{/if}
								</div>
							{/each}
						</div>
					{:else}
						<div class="flex min-h-[220px] flex-col items-center justify-center p-6 text-center">
							<CircleHelp class="size-6 text-muted-foreground"/>

							<p class="mt-4 text-sm font-semibold">
								Pertanyaan tidak ditemukan
							</p>

							<p class="mt-1 text-xs text-muted-foreground">
								Coba gunakan kata pencarian yang berbeda.
							</p>
						</div>
					{/if}
				</Card.Content>
			</Card.Root>

			<div class="space-y-5">
				<Card.Root class="rounded-xl border shadow-none">
					<Card.Content class="p-5">
						<div class="flex size-9 items-center justify-center rounded-lg bg-emerald-50 text-emerald-700">
							<CheckCircle2 class="size-4"/>
						</div>

						<h3 class="mt-4 text-sm font-bold">
							Alur Utama SiPersurat
						</h3>

						<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
							Alur dasar pengelolaan dokumen di dalam sistem.
						</p>

						<div class="mt-5 space-y-1">
							<a
								href="/surat-masuk"
								class="flex items-center gap-3 rounded-lg p-2.5 transition hover:bg-muted"
							>
								<span class="flex size-7 items-center justify-center rounded-md bg-blue-50 text-[10px] font-bold text-blue-700">
									1
								</span>

								<span class="text-[11px] font-semibold">
									Catat Surat Masuk
								</span>
							</a>

							<div class="ml-[25px] h-3 border-l"></div>

							<a
								href="/disposisi"
								class="flex items-center gap-3 rounded-lg p-2.5 transition hover:bg-muted"
							>
								<span class="flex size-7 items-center justify-center rounded-md bg-amber-50 text-[10px] font-bold text-amber-700">
									2
								</span>

								<span class="text-[11px] font-semibold">
									Proses Disposisi
								</span>
							</a>

							<div class="ml-[25px] h-3 border-l"></div>

							<a
								href="/surat-keluar"
								class="flex items-center gap-3 rounded-lg p-2.5 transition hover:bg-muted"
							>
								<span class="flex size-7 items-center justify-center rounded-md bg-violet-50 text-[10px] font-bold text-violet-700">
									3
								</span>

								<span class="text-[11px] font-semibold">
									Kelola Surat Keluar
								</span>
							</a>

							<div class="ml-[25px] h-3 border-l"></div>

							<a
								href="/arsip"
								class="flex items-center gap-3 rounded-lg p-2.5 transition hover:bg-muted"
							>
								<span class="flex size-7 items-center justify-center rounded-md bg-emerald-50 text-[10px] font-bold text-emerald-700">
									4
								</span>

								<span class="text-[11px] font-semibold">
									Arsipkan Dokumen
								</span>
							</a>

							<div class="ml-[25px] h-3 border-l"></div>

							<a
								href="/laporan"
								class="flex items-center gap-3 rounded-lg p-2.5 transition hover:bg-muted"
							>
								<span class="flex size-7 items-center justify-center rounded-md bg-zinc-100 text-[10px] font-bold text-zinc-700">
									5
								</span>

								<span class="text-[11px] font-semibold">
									Buat Laporan
								</span>
							</a>
						</div>
					</Card.Content>
				</Card.Root>

				<Card.Root class="rounded-xl border shadow-none">
					<Card.Content class="p-5">
						<div class="flex items-start gap-3">
							<div class="flex size-9 shrink-0 items-center justify-center rounded-lg bg-blue-50 text-blue-700">
								<ShieldCheck class="size-4"/>
							</div>

							<div>
								<h3 class="text-sm font-bold">
									Informasi Sistem
								</h3>

								<p class="mt-1 text-[10px] leading-5 text-muted-foreground">
									Mode aplikasi yang sedang digunakan.
								</p>
							</div>
						</div>

						<div class="mt-4 space-y-3">
							<div class="flex items-center justify-between rounded-lg bg-muted/40 px-3 py-2.5">
								<span class="text-[10px] text-muted-foreground">
									Aplikasi
								</span>

								<strong class="text-[10px]">
									SiPersurat
								</strong>
							</div>

							<div class="flex items-center justify-between rounded-lg bg-muted/40 px-3 py-2.5">
								<span class="text-[10px] text-muted-foreground">
									Penyimpanan
								</span>

								<strong class="text-[10px]">
									localStorage
								</strong>
							</div>

							<div class="flex items-center justify-between rounded-lg bg-muted/40 px-3 py-2.5">
								<span class="text-[10px] text-muted-foreground">
									Status
								</span>

								<span class="rounded-md bg-amber-50 px-2 py-1 text-[9px] font-semibold text-amber-700">
									Frontend Mode
								</span>
							</div>
						</div>
					</Card.Content>
				</Card.Root>
			</div>
		</section>

		<section
			class="mt-10"
			in:fly={{
				y:20,
				duration:450,
				delay:320
			}}
		>
			<Card.Root class="overflow-hidden rounded-2xl border shadow-none">
				<Card.Content class="p-0">
					<div class="grid lg:grid-cols-[minmax(0,1fr)_360px]">
						<div class="p-6 sm:p-8">
							<div class="flex size-10 items-center justify-center rounded-xl bg-foreground text-background">
								<LifeBuoy class="size-5"/>
							</div>

							<h2 class="mt-5 text-xl font-bold tracking-tight">
								Masih membutuhkan bantuan?
							</h2>

							<p class="mt-2 max-w-xl text-xs leading-6 text-muted-foreground">
								Jika panduan di atas belum menyelesaikan masalah, hubungi administrator sistem atau periksa kembali konfigurasi aplikasi.
							</p>

							<div class="mt-6 flex flex-wrap gap-2">
								<a href="/pengaturan">
									<Button
										type="button"
										class="rounded-lg text-xs"
									>
										<Settings class="size-4"/>
										Buka Pengaturan
									</Button>
								</a>

								<a href="/pengguna">
									<Button
										type="button"
										variant="outline"
										class="rounded-lg text-xs"
									>
										<Users class="size-4"/>
										Kelola Pengguna
									</Button>
								</a>
							</div>
						</div>

						<div class="border-t bg-muted/25 p-6 lg:border-l lg:border-t-0 sm:p-8">
							<div class="flex items-center gap-2">
								<FileText class="size-4 text-muted-foreground"/>

								<h3 class="text-sm font-bold">
									Administrator Sistem
								</h3>
							</div>

							<p class="mt-2 text-[10px] leading-5 text-muted-foreground">
								Informasi kontak mengikuti Profil Instansi pada halaman Pengaturan.
							</p>

							<div class="mt-5 space-y-3">
								<div class="rounded-lg border bg-background p-3">
									<p class="text-[9px] text-muted-foreground">
										Instansi
									</p>

									<p class="mt-1 text-[11px] font-semibold">
										{institutionName}
									</p>
								</div>

								<div class="flex items-center gap-3 rounded-lg border bg-background p-3">
									<div class="flex size-8 shrink-0 items-center justify-center rounded-lg bg-muted">
										<Mail class="size-3.5 text-muted-foreground"/>
									</div>

									<div class="min-w-0">
										<p class="text-[9px] text-muted-foreground">
											Email
										</p>

										<p class="mt-0.5 truncate text-[11px] font-semibold">
											{adminEmail||'Belum diatur'}
										</p>
									</div>
								</div>

								<div class="flex items-center gap-3 rounded-lg border bg-background p-3">
									<div class="flex size-8 shrink-0 items-center justify-center rounded-lg bg-muted">
										<Phone class="size-3.5 text-muted-foreground"/>
									</div>

									<div class="min-w-0">
										<p class="text-[9px] text-muted-foreground">
											Telepon
										</p>

										<p class="mt-0.5 truncate text-[11px] font-semibold">
											{adminPhone||'Belum diatur'}
										</p>
									</div>
								</div>
							</div>

							{#if !adminEmail&&!adminPhone}
								<a
									href="/pengaturan"
									class="mt-4 flex items-center justify-between rounded-lg border border-dashed p-3 text-[10px] font-semibold text-muted-foreground transition hover:bg-background hover:text-foreground"
								>
									Lengkapi kontak instansi

									<ChevronRight class="size-3.5"/>
								</a>
							{/if}
						</div>
					</div>
				</Card.Content>
			</Card.Root>
		</section>

		<footer class="mt-8 flex flex-col justify-between gap-2 border-t py-5 text-[10px] text-muted-foreground sm:flex-row sm:items-center">
			<p>
				Pusat Bantuan SiPersurat
			</p>

			<p>
				Panduan penggunaan Sistem Informasi Persuratan
			</p>
		</footer>
	</div>
</div>