<script lang="ts">
	import{Button}from '$lib/components/ui/button/index.js';
	import X from '@lucide/svelte/icons/x';
	import FileText from '@lucide/svelte/icons/file-text';
	import Download from '@lucide/svelte/icons/download';
	import Pencil from '@lucide/svelte/icons/pencil';
	import Archive from '@lucide/svelte/icons/archive';
	import CheckCircle2 from '@lucide/svelte/icons/circle-check-big';
	import Mail from '@lucide/svelte/icons/mail';
	import CalendarDays from '@lucide/svelte/icons/calendar-days';
	import Building2 from '@lucide/svelte/icons/building-2';
	import Tag from '@lucide/svelte/icons/tag';
	import ShieldAlert from '@lucide/svelte/icons/shield-alert';
	import type{IncomingMailRecord,IncomingMailStatus}from '$lib/features/surat-masuk/types';
	import{formatDate,formatFileSize,INCOMING_MAIL_STATUS_LABELS}from '$lib/features/surat-masuk/types';

	type Props={
		open:boolean;
		record:IncomingMailRecord|null;
		onClose:()=>void;
		onEdit:(record:IncomingMailRecord)=>void;
		onStatusChange:(record:IncomingMailRecord,status:IncomingMailStatus)=>void;
	};

	let{open,record,onClose,onEdit,onStatusChange}:Props=$props();

	function handleKeydown(event:KeyboardEvent){
		if(open&&event.key==='Escape')onClose();
	}

	function handleBackdrop(event:MouseEvent){
		if(event.target===event.currentTarget)onClose();
	}

	function statusClass(status:IncomingMailStatus){
		return`status-${status.toLowerCase().replaceAll('_','-')}`;
	}
</script>

<svelte:window onkeydown={handleKeydown}/>

{#if open&&record}
	<div class="modal-layer" role="presentation" onclick={handleBackdrop}>
		<div class="detail-shell" role="dialog" aria-modal="true" aria-labelledby="incoming-detail-title">
			<header class="detail-header">
				<div>
					<div class="title-row">
						<span class="mail-icon">
							<Mail class="size-4"/>
						</span>
						<div>
							<p>{record.agendaNumber}</p>
							<h2 id="incoming-detail-title">{record.subject}</h2>
						</div>
					</div>
					<span class={`status-badge ${statusClass(record.status)}`}>
						<span></span>
						{INCOMING_MAIL_STATUS_LABELS[record.status]}
					</span>
				</div>
				<button type="button" class="close-button" onclick={onClose}>
					<X class="size-4"/>
				</button>
			</header>

			<div class="detail-body">
				<section>
					<h3>Informasi Surat</h3>
					<div class="info-grid">
						<div class="info-item">
							<span><FileText class="size-4"/></span>
							<div>
								<p>Nomor Surat</p>
								<strong>{record.letterNumber}</strong>
							</div>
						</div>

						<div class="info-item">
							<span><CalendarDays class="size-4"/></span>
							<div>
								<p>Tanggal Surat</p>
								<strong>{formatDate(record.letterDate)}</strong>
							</div>
						</div>

						<div class="info-item">
							<span><CalendarDays class="size-4"/></span>
							<div>
								<p>Tanggal Diterima</p>
								<strong>{formatDate(record.receivedDate)}</strong>
							</div>
						</div>

						<div class="info-item">
							<span><Building2 class="size-4"/></span>
							<div>
								<p>Pengirim</p>
								<strong>{record.sender}</strong>
							</div>
						</div>

						<div class="info-item">
							<span><Tag class="size-4"/></span>
							<div>
								<p>Kategori</p>
								<strong>{record.category}</strong>
							</div>
						</div>

						<div class="info-item">
							<span><ShieldAlert class="size-4"/></span>
							<div>
								<p>Sifat Surat</p>
								<strong>{record.priority}</strong>
							</div>
						</div>
					</div>
				</section>

				<section>
					<h3>Tujuan & Catatan</h3>
					<div class="wide-info">
						<div>
							<p>Unit Tujuan</p>
							<strong>{record.targetUnit}</strong>
						</div>
						<div>
							<p>Catatan</p>
							<strong>{record.notes||'Tidak ada catatan.'}</strong>
						</div>
					</div>
				</section>

				<section>
					<h3>Dokumen</h3>
					{#if record.fileName}
						<div class="document-card">
							<div class="document-icon">
								<FileText class="size-5"/>
							</div>
							<div>
								<strong>{record.fileName}</strong>
								<p>{formatFileSize(record.fileSize)}</p>
							</div>
							<Button type="button" variant="outline" size="sm" class="ml-auto h-9 rounded-lg text-[11px]" disabled>
								<Download class="size-3.5"/>
								Download
							</Button>
						</div>
						<p class="document-note">Dokumen akan tersedia untuk diunduh setelah penyimpanan backend dihubungkan.</p>
					{:else}
						<div class="empty-document">
							<FileText class="size-5"/>
							<p>Belum ada dokumen yang dilampirkan.</p>
						</div>
					{/if}
				</section>
			</div>

			<footer class="detail-footer">
				<div class="left-actions">
					{#if record.status!=='COMPLETED'&&record.status!=='ARCHIVED'}
						<Button type="button" variant="outline" class="h-10 rounded-lg text-[11px] font-semibold" onclick={()=>onStatusChange(record,'COMPLETED')}>
							<CheckCircle2 class="size-4"/>
							Tandai Selesai
						</Button>
					{/if}

					{#if record.status!=='ARCHIVED'}
						<Button type="button" variant="outline" class="h-10 rounded-lg text-[11px] font-semibold" onclick={()=>onStatusChange(record,'ARCHIVED')}>
							<Archive class="size-4"/>
							Arsipkan
						</Button>
					{/if}
				</div>

				<Button type="button" class="h-10 rounded-lg px-5 text-[11px] font-semibold" onclick={()=>onEdit(record)}>
					<Pencil class="size-4"/>
					Edit Surat
				</Button>
			</footer>
		</div>
	</div>
{/if}

<style>
	.modal-layer{position:fixed;inset:0;z-index:210;display:flex;align-items:center;justify-content:center;padding:20px;background:rgb(15 23 42/.45);backdrop-filter:blur(4px)}
	.detail-shell{display:flex;width:min(760px,100%);max-height:calc(100dvh - 40px);flex-direction:column;overflow:hidden;border:1px solid var(--border);border-radius:18px;background:var(--background);box-shadow:0 24px 70px rgb(0 0 0/.25);animation:detail-in .16s ease-out}
	.detail-header{display:flex;align-items:flex-start;justify-content:space-between;gap:16px;padding:18px;border-bottom:1px solid var(--border)}
	.detail-header>div:first-child{min-width:0}
	.title-row{display:flex;min-width:0;align-items:flex-start;gap:11px}
	.mail-icon{display:flex;width:38px;height:38px;flex-shrink:0;align-items:center;justify-content:center;border-radius:10px;background:#f4f4f4;color:#525252}
	.title-row p{font-family:ui-monospace,SFMono-Regular,Menlo,monospace;font-size:9px;font-weight:700;color:var(--muted-foreground)}
	.title-row h2{margin-top:2px;font-size:16px;font-weight:700;line-height:1.35;letter-spacing:-.02em}
	.close-button{display:flex;width:34px;height:34px;flex-shrink:0;align-items:center;justify-content:center;border-radius:9px;color:var(--muted-foreground)}
	.close-button:hover{background:var(--muted);color:var(--foreground)}
	.status-badge{display:inline-flex;align-items:center;gap:6px;margin-top:10px;border-radius:999px;padding:5px 9px;font-size:9px;font-weight:650}
	.status-badge>span{width:6px;height:6px;border-radius:50%}
	.status-received{background:#edf5ff;color:#0043ce}.status-received>span{background:#0f62fe}
	.status-pending-disposition{background:#fff8e1;color:#8a3800}.status-pending-disposition>span{background:#f1c21b}
	.status-dispositioned{background:#f6f2ff;color:#6929c4}.status-dispositioned>span{background:#8a3ffc}
	.status-in-progress{background:#e5f6ff;color:#00539a}.status-in-progress>span{background:#1192e8}
	.status-completed{background:#defbe6;color:#0e6027}.status-completed>span{background:#24a148}
	.status-archived{background:#f4f4f4;color:#525252}.status-archived>span{background:#8d8d8d}
	.detail-body{min-height:0;overflow-y:auto;padding:20px}
	.detail-body section+section{margin-top:22px;padding-top:22px;border-top:1px solid var(--border)}
	.detail-body h3{margin-bottom:12px;font-size:12px;font-weight:700}
	.info-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:10px}
	.info-item{display:flex;min-width:0;align-items:flex-start;gap:10px;border:1px solid var(--border);border-radius:11px;padding:11px}
	.info-item>span{display:flex;width:30px;height:30px;flex-shrink:0;align-items:center;justify-content:center;border-radius:8px;background:#f4f4f4;color:#525252}
	.info-item div{min-width:0}
	.info-item p,.wide-info p{font-size:9px;color:var(--muted-foreground)}
	.info-item strong,.wide-info strong{display:block;margin-top:3px;font-size:11px;font-weight:650;line-height:1.45}
	.wide-info{display:grid;grid-template-columns:1fr 2fr;gap:10px}
	.wide-info>div{border:1px solid var(--border);border-radius:11px;padding:11px}
	.document-card{display:flex;align-items:center;gap:10px;border:1px solid var(--border);border-radius:11px;padding:11px}
	.document-icon{display:flex;width:38px;height:38px;flex-shrink:0;align-items:center;justify-content:center;border-radius:9px;background:#f4f4f4;color:#525252}
	.document-card>div:nth-child(2){min-width:0}
	.document-card strong{display:block;overflow:hidden;font-size:11px;font-weight:700;text-overflow:ellipsis;white-space:nowrap}
	.document-card p{margin-top:2px;font-size:9px;color:var(--muted-foreground)}
	.document-note{margin-top:7px;font-size:9px;color:var(--muted-foreground)}
	.empty-document{display:flex;min-height:90px;flex-direction:column;align-items:center;justify-content:center;border:1px dashed var(--border);border-radius:11px;color:var(--muted-foreground)}
	.empty-document p{margin-top:6px;font-size:10px;font-weight:500}
	.detail-footer{display:flex;align-items:center;justify-content:space-between;gap:10px;padding:13px 18px;border-top:1px solid var(--border)}
	.left-actions{display:flex;gap:8px}
	@keyframes detail-in{from{opacity:0;transform:translateY(8px) scale(.985)}to{opacity:1;transform:translateY(0) scale(1)}}
	@media(max-width:640px){.modal-layer{align-items:flex-end;padding:8px}.detail-shell{max-height:calc(100dvh - 16px);border-radius:18px 18px 12px 12px}.detail-body{padding:16px}.info-grid,.wide-info{grid-template-columns:1fr}.document-card :global(button){display:none}.detail-footer{align-items:stretch;flex-direction:column}.left-actions{display:grid;grid-template-columns:repeat(2,minmax(0,1fr))}.detail-footer>:global(button){width:100%}}
</style>