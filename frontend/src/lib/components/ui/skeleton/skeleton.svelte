<script lang="ts">
    import type { HTMLAttributes } from 'svelte/elements';
    let {
        class: className = '',
        ...restProps
    }: HTMLAttributes<HTMLDivElement> = $props();
</script>
<div
    {...restProps}
    data-slot="skeleton"
    class={`animate-pulse rounded-md bg-accent ${className}`}
></div>