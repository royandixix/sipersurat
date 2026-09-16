<script lang="ts">
    import { apiFetch } from '$lib/api';
    import { onMount, onDestroy } from 'svelte';
    import { fly, fade } from 'svelte/transition';
    import * as Card from '$lib/components/ui/card/index.js';
    import * as Avatar from '$lib/components/ui/avatar/index.js';
    import { Button } from '$lib/components/ui/button/index.js';
    import { Badge } from '$lib/components/ui/badge/index.js';
    import Inbox from '@lucide/svelte/icons/inbox';
    import Send from '@lucide/svelte/icons/send';
    import Forward from '@lucide/svelte/icons/forward';
    import Archive from '@lucide/svelte/icons/archive';
    import Plus from '@lucide/svelte/icons/plus';
    import FileText from '@lucide/svelte/icons/file-text';
    import ArrowUpRight from '@lucide/svelte/icons/arrow-up-right';
    import Bell from '@lucide/svelte/icons/bell';
    import Download from '@lucide/svelte/icons/download';
    import RefreshCw from '@lucide/svelte/icons/refresh-cw';

    type StatisticApi = {
        key: string;
        title: string;
        value: number;
        subtitle: string;
    };
    type ChartApi = {
        month: string;
        value: number;
    };
    type ActivityApi = {
        initials: string;
        name: string;
        description: string;
        value: string | null;
        createdAt: string;
    };
    type NotificationApi = {
        type: string;
        title: string;
        description: string;
        count: number;
    };
    type DashboardData = {
        year: number;
        statistics: StatisticApi[];
        chartData: ChartApi[];
        recentActivity: ActivityApi[];
        notifications: NotificationApi[];
    };
    type DashboardResponse = {
        success: boolean;
        message: string;
        data: DashboardData;
    };
    type StatisticView = StatisticApi & {
        icon: typeof Inbox;
    };
    type ActivityView = ActivityApi & {
        color: string;
    };

    let activeTab = $state('Overview');
    let selectedStat = $state('incoming');
    let hoveredMonth = $state<string | null>(null);
    let selectedMonth = $state('');
    let chartReady = $state(false);
    let loading = $state(true);
    let refreshing = $state(false);
    let error = $state('');
    let sessionExpired = $state(false);
    let dashboardYear = $state(new Date().getFullYear());
    let statistics = $state<StatisticView[]>([]);
    let chartData = $state<ChartApi[]>([]);
    let recentActivity = $state<ActivityView[]>([]);
    let notifications = $state<NotificationApi[]>([]);

    let requestInProgress = false;
    let disposed = false;
    let chartTimer: ReturnType<typeof setTimeout> | undefined;
    let requestController: AbortController | undefined;

    const tabs = ['Overview', 'Analytics', 'Reports', 'Notifications'];
    const iconMap: Record<string, typeof Inbox> = {
        incoming: Inbox,
        outgoing: Send,
        disposition: Forward,
        archive: Archive
    };
    const activityColors = [
        'bg-blue-50 text-blue-700',
        'bg-amber-50 text-amber-700',
        'bg-emerald-50 text-emerald-700',
        'bg-violet-50 text-violet-700',
        'bg-zinc-100 text-zinc-700'
    ];

    let maxChartValue = $derived(
        Math.max(1, ...chartData.map((item) => item.value))
    );
    let axisMax = $derived(calculateAxisMax(maxChartValue));
    let axisValues = $derived([
        axisMax,
        Math.round(axisMax * 0.75),
        Math.round(axisMax * 0.5),
        Math.round(axisMax * 0.25),
        0
    ]);
    let analytics = $derived(
        statistics.map((item) => {
            const max = Math.max(1, ...statistics.map((stat) => stat.value));
            return {
                name: item.title,
                value: item.value,
                percent: Math.round((item.value / max) * 100)
            };
        })
    );
    let reports = $derived([
        {
            title: 'Laporan Surat Masuk',
            description: 'Rekap surat masuk berdasarkan periode',
            total: `${getStatisticValue('incoming')} dokumen`
        },
        {
            title: 'Laporan Surat Keluar',
            description: 'Rekap seluruh surat yang diterbitkan',
            total: `${getStatisticValue('outgoing')} dokumen`
        },
        {
            title: 'Laporan Disposisi',
            description: 'Rekap status dan tindak lanjut disposisi',
            total: `${getStatisticValue('disposition')} disposisi`
        }
    ]);
    let archiveRatio = $derived(calculateArchiveRatio());

    onMount(() => {
        void loadDashboard();
    });

    onDestroy(() => {
        disposed = true;
        requestController?.abort();
        if (chartTimer !== undefined) {
            clearTimeout(chartTimer);
        }
    });

    function animateChart() {
        if (chartTimer !== undefined) {
            clearTimeout(chartTimer);
        }
        chartReady = false;
        chartTimer = setTimeout(() => {
            if (!disposed) chartReady = true;
        }, 100);
    }

    async function loadDashboard(isRefresh = false) {
        if (requestInProgress || disposed) return;
        requestInProgress = true;
        if (isRefresh) refreshing = true;
        else loading = true;
        error = '';
        sessionExpired = false;
        requestController = new AbortController();
        try {
            const response = await apiFetch(
                `/api/v1/dashboard?year=${encodeURIComponent(dashboardYear)}`,
                {
                    method: 'GET',
                    headers: {
                        Accept: 'application/json'
                    },
                    signal: requestController.signal
                }
            );
            if (disposed) return;
            if (response.status === 401) {
                sessionExpired = true;
                throw new Error('Sesi tidak tersedia atau sudah berakhir. Silakan masuk kembali.');
            }
            if (response.status === 403) {
                throw new Error('Akun kamu tidak memiliki izin untuk membuka data dashboard.');
            }
            if (!response.ok) {
                throw new Error(`Dashboard gagal dimuat. HTTP ${response.status}`);
            }
            const result: DashboardResponse = await response.json();
            if (disposed) return;
            if (!result.success || !result.data) {
                throw new Error(result.message || 'Dashboard gagal dimuat.');
            }
            const data = result.data;
            if (
                !Array.isArray(data.statistics) ||
                !Array.isArray(data.chartData) ||
                !Array.isArray(data.recentActivity) ||
                !Array.isArray(data.notifications)
            ) {
                throw new Error('Format respons dashboard tidak sesuai.');
            }
            dashboardYear = data.year;
            statistics = data.statistics.map((item) => ({
                ...item,
                icon: iconMap[item.key] ?? FileText
            }));
            chartData = data.chartData;
            recentActivity = data.recentActivity.map((item, index) => ({
                ...item,
                color: activityColors[index % activityColors.length]
            }));
            notifications = data.notifications;
            if (!chartData.some((item) => item.month === selectedMonth)) {
                selectedMonth = chartData[0]?.month ?? '';
            }
            animateChart();
        } catch (err) {
            if (disposed || requestController?.signal.aborted) return;
            error = err instanceof Error
                ? err.message
                : 'Tidak dapat mengambil data dashboard.';
        } finally {
            requestInProgress = false;
            if (!disposed) {
                loading = false;
                refreshing = false;
            }
        }
    }

    function formatNumber(value: number) {
        return new Intl.NumberFormat('id-ID').format(value);
    }

    function formatDate(value: string) {
        if (!value) return '';
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) return '';
        return new Intl.DateTimeFormat('id-ID', {
            day: '2-digit',
            month: 'short',
            hour: '2-digit',
            minute: '2-digit'
        }).format(date);
    }

    function selectTab(tab: string) {
        activeTab = tab;
        if (tab === 'Overview') animateChart();
    }

    function calculateAxisMax(value: number) {
        if (value <= 10) return 10;
        const magnitude = Math.pow(10, Math.floor(Math.log10(value)));
        return Math.ceil(value / magnitude) * magnitude;
    }

    function getStatisticValue(key: string) {
        return statistics.find((item) => item.key === key)?.value ?? 0;
    }

    function calculateArchiveRatio() {
        const total = getStatisticValue('incoming') + getStatisticValue('outgoing');
        if (total <= 0) return 0;
        return Math.min(
            100,
            Math.round((getStatisticValue('archive') / total) * 100)
        );
    }
</script>

<svelte:head>
    <title>Dashboard | SiPersurat</title>
</svelte:head>

<div class="mx-auto w-full max-w-[1500px] px-5 py-7 md:px-8 lg:px-10">
    <div class="flex flex-col justify-between gap-5 md:flex-row md:items-start">
        <div in:fly={{ y: 18, duration: 450 }}>
            <h1 class="text-2xl font-bold tracking-tight md:text-[28px]">
                Dashboard
            </h1>
            <p class="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
                Pantau ringkasan aktivitas persuratan, proses disposisi,
                perkembangan dokumen, arsip, serta aktivitas pengguna
                Sistem Informasi Persuratan dalam satu halaman.
            </p>
        </div>
        <div class="flex shrink-0 gap-2" in:fly={{ y: 14, duration: 420, delay: 100 }}>
            <Button
                variant="outline"
                onclick={() => loadDashboard(true)}
                disabled={loading || refreshing}
            >
                <RefreshCw class={refreshing ? 'size-4 animate-spin' : 'size-4'} />
                {refreshing ? 'Memuat...' : 'Refresh'}
            </Button>
            <Button class="rounded-lg transition-all duration-200 hover:-translate-y-0.5 hover:shadow-md">
                <Plus class="size-4" />
                Tambah Surat
            </Button>
        </div>
    </div>

    <div class="mt-6" in:fly={{ y: 15, duration: 420, delay: 160 }}>
        <div class="inline-flex max-w-full flex-wrap rounded-lg bg-muted p-1">
            {#each tabs as tab}
                <button
                    type="button"
                    onclick={() => selectTab(tab)}
                    aria-pressed={activeTab === tab}
                    class={[
                        'rounded-md px-3 py-1.5 text-xs font-medium transition-all duration-200',
                        activeTab === tab
                            ? 'bg-background text-foreground shadow-sm'
                            : 'text-muted-foreground hover:bg-background/50 hover:text-foreground'
                    ]}
                >
                    {tab}
                </button>
            {/each}
        </div>
    </div>

    {#if loading}
        <div class="mt-7 grid gap-4 sm:grid-cols-2 xl:grid-cols-4" aria-busy="true">
            {#each [1, 2, 3, 4] as item (item)}
                <Card.Root class="h-[145px] animate-pulse shadow-none">
                    <Card.Content class="h-full p-5">
                        <div class="h-3 w-24 rounded bg-muted"></div>
                        <div class="mt-8 h-8 w-20 rounded bg-muted"></div>
                        <div class="mt-3 h-3 w-32 rounded bg-muted"></div>
                    </Card.Content>
                </Card.Root>
            {/each}
            <span class="sr-only">Memuat dashboard...</span>
        </div>
    {:else if error}
        <Card.Root class="mt-7 border-destructive/30 shadow-none">
            <Card.Content class="flex flex-col items-center justify-center py-12 text-center">
                <p class="font-semibold">Dashboard tidak dapat dimuat</p>
                <p class="mt-2 text-sm text-muted-foreground" role="alert">
                    {error}
                </p>
                {#if sessionExpired}
                    <a
                        href="/"
                        class="mt-5 inline-flex items-center justify-center rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground"
                    >
                        Masuk Kembali
                    </a>
                {:else}
                    <Button
                        class="mt-5"
                        onclick={() => loadDashboard(true)}
                        disabled={refreshing}
                    >
                        <RefreshCw class={refreshing ? 'size-4 animate-spin' : 'size-4'} />
                        {refreshing ? 'Memuat...' : 'Coba Lagi'}
                    </Button>
                {/if}
            </Card.Content>
        </Card.Root>
    {:else if activeTab === 'Overview'}
        <section class="mt-7 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {#each statistics as item, index (item.key)}
                <div in:fly={{ y: 22, duration: 450, delay: 200 + index * 70 }}>
                    <button
                        type="button"
                        onclick={() => selectedStat = item.key}
                        aria-pressed={selectedStat === item.key}
                        class={[
                            'group h-full w-full rounded-xl border bg-card p-5 text-left transition-all duration-200',
                            'hover:-translate-y-1 hover:shadow-md',
                            selectedStat === item.key
                                ? 'border-foreground/30 shadow-sm'
                                : 'border-border'
                        ]}
                    >
                        <div class="flex items-center justify-between gap-4">
                            <p class="text-xs font-semibold">{item.title}</p>
                            <div
                                class={[
                                    'flex size-8 shrink-0 items-center justify-center rounded-lg transition-all duration-200',
                                    selectedStat === item.key
                                        ? 'bg-foreground text-background'
                                        : 'bg-muted text-muted-foreground group-hover:bg-foreground group-hover:text-background'
                                ]}
                            >
                                <item.icon class="size-4 transition-transform duration-200 group-hover:scale-110" />
                            </div>
                        </div>
                        <p class="mt-7 text-[28px] leading-none font-bold tracking-tight">
                            {formatNumber(item.value)}
                        </p>
                        <p class="mt-2 text-[11px] text-muted-foreground">
                            {item.subtitle}
                        </p>
                    </button>
                </div>
            {/each}
        </section>

        <section class="mt-4 grid gap-4 xl:grid-cols-[minmax(0,1.7fr)_minmax(340px,.8fr)]">
            <div class="min-w-0" in:fly={{ y: 25, duration: 500, delay: 500 }}>
                <Card.Root class="h-full overflow-hidden shadow-none transition-shadow duration-200 hover:shadow-sm">
                    <Card.Header>
                        <div class="flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
                            <div>
                                <Card.Title class="text-base">Aktivitas Persuratan</Card.Title>
                                <Card.Description class="mt-1">
                                    Perkembangan jumlah aktivitas dokumen sepanjang tahun berjalan.
                                </Card.Description>
                            </div>
                            <Badge variant="outline" class="w-fit">
                                {dashboardYear}
                            </Badge>
                        </div>
                    </Card.Header>
                    <Card.Content>
                        {#if chartData.length === 0}
                            <div class="flex h-[330px] items-center justify-center text-sm text-muted-foreground">
                                Belum ada data grafik.
                            </div>
                        {:else}
                            <div class="relative h-[330px]">
                                <div class="absolute top-0 bottom-8 left-0 flex w-12 flex-col justify-between text-right text-[10px] text-muted-foreground">
                                    {#each axisValues as value}
                                        <span>{formatNumber(value)}</span>
                                    {/each}
                                </div>
                                <div class="absolute top-0 right-0 bottom-8 left-16 flex flex-col justify-between">
                                    {#each [1, 2, 3, 4, 5] as line (line)}
                                        <div class="border-t border-dashed"></div>
                                    {/each}
                                </div>
                                <div class="absolute top-0 right-0 bottom-0 left-16 grid grid-cols-12 gap-1 sm:gap-2">
                                    {#each chartData as item, index (item.month)}
                                        <button
                                            type="button"
                                            onmouseenter={() => hoveredMonth = item.month}
                                            onmouseleave={() => hoveredMonth = null}
                                            onfocus={() => hoveredMonth = item.month}
                                            onblur={() => hoveredMonth = null}
                                            onclick={() => selectedMonth = item.month}
                                            aria-label={`${item.month}: ${formatNumber(item.value)} aktivitas`}
                                            aria-pressed={selectedMonth === item.month}
                                            class="group relative flex h-full min-w-0 flex-col justify-end"
                                        >
                                            <div class="relative flex h-[calc(100%-32px)] items-end justify-center">
                                                {#if hoveredMonth === item.month || selectedMonth === item.month}
                                                    <div
                                                        class="absolute z-20 mb-2 rounded-lg bg-foreground px-2.5 py-1.5 text-[10px] font-medium whitespace-nowrap text-background shadow-lg"
                                                        style={`bottom:${(item.value / axisMax) * 100}%`}
                                                        in:fade={{ duration: 120 }}
                                                    >
                                                        {item.month}: {formatNumber(item.value)}
                                                    </div>
                                                {/if}
                                                <div
                                                    class={[
                                                        'w-full max-w-10 origin-bottom rounded-t-md transition-all ease-out',
                                                        selectedMonth === item.month
                                                            ? 'bg-foreground'
                                                            : 'bg-foreground/75 group-hover:bg-foreground'
                                                    ]}
                                                    style={`height:${chartReady ? (item.value / axisMax) * 100 : 0}%;transition-duration:${650 + index * 55}ms;transition-delay:${index * 35}ms`}
                                                ></div>
                                            </div>
                                            <div
                                                class={[
                                                    'mt-2 text-center text-[10px] transition-all duration-200',
                                                    selectedMonth === item.month
                                                        ? 'font-bold text-foreground'
                                                        : 'text-muted-foreground group-hover:text-foreground'
                                                ]}
                                            >
                                                {item.month}
                                            </div>
                                        </button>
                                    {/each}
                                </div>
                            </div>
                        {/if}
                    </Card.Content>
                </Card.Root>
            </div>

            <div class="min-w-0" in:fly={{ y: 25, duration: 500, delay: 570 }}>
                <Card.Root class="h-full shadow-none transition-shadow duration-200 hover:shadow-sm">
                    <Card.Header>
                        <Card.Title class="text-base">Aktivitas Terbaru</Card.Title>
                        <Card.Description>
                            Aktivitas terbaru yang dilakukan pengguna pada sistem.
                        </Card.Description>
                    </Card.Header>
                    <Card.Content class="space-y-1">
                        {#if recentActivity.length === 0}
                            <div class="flex min-h-56 flex-col items-center justify-center text-center">
                                <FileText class="size-8 text-muted-foreground/50" />
                                <p class="mt-3 text-xs font-medium">Belum ada aktivitas</p>
                                <p class="mt-1 text-[10px] text-muted-foreground">
                                    Aktivitas pengguna akan tampil di sini.
                                </p>
                            </div>
                        {:else}
                            {#each recentActivity as activity, index}
                                <div
                                    class="group flex items-center gap-3 rounded-lg px-2 py-2.5 transition-all duration-200 hover:translate-x-1 hover:bg-muted/50"
                                    in:fly={{ x: 18, duration: 350, delay: 620 + index * 60 }}
                                >
                                    <Avatar.Root class="size-9 shrink-0">
                                        <Avatar.Fallback class={`text-[10px] font-semibold ${activity.color}`}>
                                            {activity.initials}
                                        </Avatar.Fallback>
                                    </Avatar.Root>
                                    <div class="min-w-0 flex-1">
                                        <p class="truncate text-xs font-semibold">{activity.name}</p>
                                        <p class="truncate text-[10px] text-muted-foreground">
                                            {activity.description}
                                        </p>
                                        <p class="mt-0.5 text-[9px] text-muted-foreground/70">
                                            {formatDate(activity.createdAt)}
                                        </p>
                                    </div>
                                    <p class="max-w-24 shrink-0 truncate text-[11px] font-semibold">
                                        {activity.value ?? '-'}
                                    </p>
                                </div>
                            {/each}
                        {/if}
                    </Card.Content>
                </Card.Root>
            </div>
        </section>
    {:else if activeTab === 'Analytics'}
        <section class="mt-7 grid gap-4 lg:grid-cols-3" in:fly={{ y: 22, duration: 420 }}>
            <div class="min-w-0 lg:col-span-2">
                <Card.Root class="h-full shadow-none">
                    <Card.Header>
                        <Card.Title>Analitik Persuratan</Card.Title>
                        <Card.Description>
                            Perbandingan aktivitas berdasarkan data yang tercatat pada sistem.
                        </Card.Description>
                    </Card.Header>
                    <Card.Content class="space-y-6">
                        {#each analytics as item, index}
                            <div in:fly={{ x: -15, duration: 350, delay: index * 70 }}>
                                <div class="mb-2 flex items-center justify-between">
                                    <p class="text-xs font-medium">{item.name}</p>
                                    <p class="text-xs font-semibold">{formatNumber(item.value)}</p>
                                </div>
                                <div class="h-2 overflow-hidden rounded-full bg-muted">
                                    <div
                                        class="h-full origin-left rounded-full bg-foreground transition-all duration-700"
                                        style={`width:${item.percent}%`}
                                    ></div>
                                </div>
                            </div>
                        {/each}
                    </Card.Content>
                </Card.Root>
            </div>
            <div class="min-w-0" in:fly={{ x: 20, duration: 420, delay: 100 }}>
                <Card.Root class="h-full shadow-none">
                    <Card.Header>
                        <Card.Title>Rasio Pengarsipan</Card.Title>
                        <Card.Description>
                            Persentase dokumen masuk dan keluar yang telah tercatat sebagai arsip.
                        </Card.Description>
                    </Card.Header>
                    <Card.Content>
                        <div class="flex min-h-52 flex-col items-center justify-center">
                            <div class="group flex size-36 items-center justify-center rounded-full border-[14px] border-muted transition-all duration-300 hover:scale-105 hover:border-foreground/20">
                                <div class="text-center">
                                    <p class="text-3xl font-bold">{archiveRatio}%</p>
                                    <p class="mt-1 text-[10px] text-muted-foreground">Terarsip</p>
                                </div>
                            </div>
                        </div>
                    </Card.Content>
                </Card.Root>
            </div>
        </section>
    {:else if activeTab === 'Reports'}
        <section class="mt-7 grid gap-4 md:grid-cols-3">
            {#each reports as report, index}
                <div in:fly={{ y: 22, duration: 400, delay: index * 80 }}>
                    <Card.Root class="group h-full shadow-none transition-all duration-200 hover:-translate-y-1 hover:shadow-md">
                        <Card.Header>
                            <div class="mb-3 flex size-10 items-center justify-center rounded-lg bg-muted transition-all duration-200 group-hover:bg-foreground group-hover:text-background">
                                <FileText class="size-5" />
                            </div>
                            <Card.Title class="text-base">{report.title}</Card.Title>
                            <Card.Description>{report.description}</Card.Description>
                        </Card.Header>
                        <Card.Content>
                            <p class="text-2xl font-bold">{report.total}</p>
                        </Card.Content>
                        <Card.Footer>
                            <Button
                                variant="outline"
                                class="w-full transition-all duration-200 group-hover:bg-foreground group-hover:text-background"
                            >
                                <Download class="size-4" />
                                Download Laporan
                            </Button>
                        </Card.Footer>
                    </Card.Root>
                </div>
            {/each}
        </section>
    {:else if activeTab === 'Notifications'}
        <div class="mt-7 max-w-3xl" in:fly={{ y: 22, duration: 420 }}>
            <Card.Root class="shadow-none">
                <Card.Header>
                    <Card.Title>Notifikasi</Card.Title>
                    <Card.Description>
                        Informasi terbaru pada sistem yang membutuhkan perhatian atau tindak lanjut.
                    </Card.Description>
                </Card.Header>
                <Card.Content class="divide-y p-0">
                    {#if notifications.length === 0}
                        <div class="flex min-h-56 flex-col items-center justify-center p-6 text-center">
                            <Bell class="size-8 text-muted-foreground/50" />
                            <p class="mt-3 text-sm font-semibold">Tidak ada notifikasi</p>
                        </div>
                    {:else}
                        {#each notifications as item, index}
                            <button
                                type="button"
                                class="group flex w-full items-start gap-4 px-6 py-5 text-left transition-all duration-200 hover:bg-muted/40"
                                in:fly={{ x: -18, duration: 350, delay: index * 70 }}
                            >
                                <div class="mt-0.5 flex size-9 shrink-0 items-center justify-center rounded-full bg-muted transition-all duration-200 group-hover:bg-foreground group-hover:text-background">
                                    <Bell class="size-4" />
                                </div>
                                <div class="min-w-0 flex-1">
                                    <div class="flex items-center gap-2">
                                        <p class="text-sm font-semibold">{item.title}</p>
                                        {#if item.count > 0}
                                            <Badge variant="secondary">{item.count}</Badge>
                                        {/if}
                                    </div>
                                    <p class="mt-1 text-xs leading-5 text-muted-foreground">
                                        {item.description}
                                    </p>
                                </div>
                                <ArrowUpRight class="size-4 shrink-0 text-muted-foreground transition-transform duration-200 group-hover:translate-x-0.5 group-hover:-translate-y-0.5 group-hover:text-foreground" />
                            </button>
                        {/each}
                    {/if}
                </Card.Content>
            </Card.Root>
        </div>
    {/if}
</div>