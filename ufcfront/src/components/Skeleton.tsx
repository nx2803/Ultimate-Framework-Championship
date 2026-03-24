'use client';

import { cn } from '@/lib/utils';
import { Sparkles } from 'lucide-react';

const CornerMarkers = () => (
  <>
    <div className="corner-top-left" />
    <div className="corner-top-right" />
    <div className="corner-bottom-left" />
    <div className="corner-bottom-right" />
  </>
);

interface SkeletonProps {
  className?: string;
}

export function Skeleton({ className }: SkeletonProps) {
  return (
    <div
      className={cn(
        "animate-pulse bg-foreground/5 rounded-sm",
        className
      )}
    />
  );
}

export function DashboardSkeleton() {
  return (
    <div className="flex w-full h-dvh bg-background overflow-hidden relative p-4 md:p-8 flex-col lg:flex-row gap-6">
      {/* Sidebar Skeleton */}
      <div className="hidden lg:flex w-72 h-full flex-col gap-8 shrink-0">
        <div className="flex items-center gap-3">
          <Skeleton className="w-8 h-8 rounded-full" />
          <Skeleton className="h-4 w-32" />
        </div>
        <div className="space-y-4">
          <Skeleton className="h-3 w-20" />
          <div className="space-y-2">
            {[1, 2, 3, 4, 5].map((i) => (
              <Skeleton key={i} className="h-10 w-full" />
            ))}
          </div>
        </div>
      </div>

      <div className="flex-1 flex flex-col gap-6 max-w-7xl mx-auto w-full">
        {/* Header Skeleton */}
        <header className="flex flex-col space-y-2 shrink-0">
          <div className="flex items-center gap-4 text-muted-foreground mb-4">
            <Skeleton className="h-3 w-24" />
            <div className="h-px flex-1 bg-border" />
            <Skeleton className="h-3 w-32" />
          </div>
          <div className="flex flex-col md:flex-row md:items-start justify-between gap-6">
            <div className="flex-1 min-h-45 md:min-h-60 flex flex-col justify-start space-y-4">
              <Skeleton className="h-16 md:h-24 w-3/4" />
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-2/3" />
            </div>
            {/* AI Commentary Skeleton */}
            <div className="flex-1 max-w-2xl min-h-[240px] relative shrink-0 bg-background/20 p-6 rounded-sm corner-frame flex flex-col gap-4">
              <CornerMarkers />
              <div className="flex justify-between items-center mb-4 relative z-10">
                <h4 className="text-[10px] font-medium uppercase tracking-[0.3em] text-muted-foreground flex items-center gap-2">
                  <Sparkles className="w-3 h-3 text-[#22c55e] animate-pulse" /> Analyzed Insight
                </h4>
              </div>
              <div className="flex-1 space-y-2">
                <div className="h-3 w-full bg-foreground/5 animate-pulse rounded" />
                <div className="h-3 w-full bg-foreground/5 animate-pulse rounded" />
                <div className="h-3 w-2/3 bg-foreground/5 animate-pulse rounded" />
              </div>
            </div>
          </div>
        </header>

        {/* Stats Grid Skeleton */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 md:gap-6 shrink-0 z-10">
          {[1, 2].map((i) => (
            <div key={i} className="bg-background/20 p-6 flex flex-col justify-between relative corner-frame min-h-[140px]">
              <CornerMarkers />
              <span className="text-[10px] font-medium uppercase tracking-[0.3em] text-muted-foreground mb-4">
                {i === 1 ? 'Dominant Tech' : 'Total Sector Scale'}
              </span>
              <Skeleton className="h-10 w-40" />
            </div>
          ))}
        </div>

        {/* Chart Skeleton */}
        <div className="bg-background/20 p-6 flex-1 min-h-[400px] flex flex-col gap-6 relative rounded-sm corner-frame z-10">
          <CornerMarkers />
          <div className="flex justify-between items-center relative z-10">
            <h3 className="text-[10px] font-medium uppercase tracking-[0.4em] text-muted-foreground flex items-center gap-3">
              <span className="w-8 h-px bg-green-500/50" />
              Dynamic Analysis
            </h3>
            <Skeleton className="h-8 w-32" />
          </div>
          <Skeleton className="w-full h-full opacity-50 relative z-10" />
        </div>
      </div>
    </div>
  );
}
