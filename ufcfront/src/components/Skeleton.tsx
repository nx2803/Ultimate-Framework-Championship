'use client';

import { cn } from '@/lib/utils';

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
        <header className="space-y-6">
          <div className="flex items-center gap-4">
            <Skeleton className="h-3 w-24" />
            <div className="h-px flex-1 bg-foreground/5" />
            <Skeleton className="h-3 w-32" />
          </div>
          <div className="flex flex-col md:flex-row justify-between gap-6">
            <div className="space-y-4 flex-1">
              <Skeleton className="h-16 md:h-24 w-3/4" />
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-2/3" />
            </div>
            <Skeleton className="w-60 h-60 rounded-sm" />
          </div>
        </header>

        {/* Stats Grid Skeleton */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-px bg-foreground/5 border border-foreground/5 rounded-sm overflow-hidden">
          <Skeleton className="h-32 w-full" />
          <Skeleton className="h-32 w-full" />
        </div>

        {/* Chart Skeleton */}
        <div className="flex-1 min-h-100 border border-foreground/5 p-6 space-y-6 rounded-sm">
          <div className="flex justify-between items-center">
            <div className="flex gap-4">
              <Skeleton className="h-4 w-32" />
              <Skeleton className="h-8 w-40" />
            </div>
            <Skeleton className="h-8 w-32" />
          </div>
          <Skeleton className="w-full h-[calc(100%-80px)]" />
        </div>
      </div>
    </div>
  );
}
