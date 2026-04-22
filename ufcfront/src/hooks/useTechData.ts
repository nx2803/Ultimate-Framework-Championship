'use client';

import React, { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { techApi } from '../lib/api';
import { Period, TechStats, TechList } from '../types';
import { format } from 'date-fns';

// Helper to parse potential date array or string
export const parseDate = (dateVal: any) => {
  if (!dateVal) return new Date(0); // Failsafe for undefined
  if (Array.isArray(dateVal)) {
    return new Date(dateVal[0], dateVal[1] - 1, dateVal[2], dateVal[3] || 0, dateVal[4] || 0);
  }
  const date = new Date(dateVal);
  return isNaN(date.getTime()) ? new Date(0) : date;
};

export type MetricType = 'marketShare' | 'starCount' | 'forkCount';

export function useTechData(selectedCategory: string, period: Period, metric: MetricType = 'marketShare') {
  // 1. 전체 기술 목록 조회 (전역 캐싱)
  const { data: techs = [], isLoading: isTechsLoading } = useQuery({
    queryKey: ['techs'],
    queryFn: techApi.getAllTechs,
    staleTime: 1000 * 60 * 60 * 4, // 4시간 캐싱
  });

  // 2. 카테고리별 통계 데이터 조회
  const { data: stats = [], isLoading: isStatsLoading } = useQuery({
    queryKey: ['stats', selectedCategory, period],
    queryFn: () => techApi.getCategoryStats(selectedCategory, period),
    enabled: !!selectedCategory,
    staleTime: 1000 * 60 * 60 * 4, // 4시간 캐싱 (자주 바뀌지 않으므로)
    gcTime: 1000 * 60 * 60 * 24, // 가비지 컬렉션 타임 24시간
  });

  // 3. 데이터 가공 로직 (메모이제이션 적용)
  const processedData = useMemo(() => {
    if (!stats.length) {
      return {
        statsByTech: {} as Record<string, TechStats[]>,
        techRankings: {} as Record<string, { rank: number; share: number; rankDiff: number }>,
        risingStars: [] as { name: string; growth: number; current: number }[],
        labels: [] as string[]
      };
    }

    const statsByTech = stats.reduce((acc, curr) => {
      if (!curr.techName) return acc;
      if (!acc[curr.techName]) acc[curr.techName] = [];
      acc[curr.techName].push(curr);
      return acc;
    }, {} as Record<string, TechStats[]>);

    // 타임스탬프 목록 (중복 제거 및 정렬)
    const timestamps = Array.from(new Set(stats.map(s => s.collectedAt.toString()))).sort();
    const latestTs = timestamps[timestamps.length - 1];
    const previousTs = timestamps[timestamps.length - 2];

    // 최신 순위 계산 (선택된 metric 기준)
    const getRankingsAt = (ts: string) => {
      return Object.keys(statsByTech)
        .map(name => statsByTech[name].find(s => s.collectedAt.toString() === ts))
        .filter((s): s is TechStats => !!s)
        .sort((a, b) => (b[metric] as number) - (a[metric] as number))
        .reduce((acc, stat, index) => {
          acc[stat.techName] = { rank: index + 1, value: stat[metric] as number };
          return acc;
        }, {} as Record<string, { rank: number; value: number }>);
    };

    const latestRankings = latestTs ? getRankingsAt(latestTs) : {};
    const previousRankings = previousTs ? getRankingsAt(previousTs) : {};

    // 7일 전 데이터 기준 Rising Stars 계산 (데이터가 충분할 경우)
    let risingStars: { name: string; growth: number; current: number }[] = [];
    if (latestTs) {
      const sevenDaysAgo = format(new Date(parseDate(latestTs).getTime() - 7 * 24 * 60 * 60 * 1000), 'yyyy-MM-dd HH:mm');
      const oldTs = timestamps.find(ts => format(parseDate(ts), 'yyyy-MM-dd HH:mm') >= sevenDaysAgo) || timestamps[0];
      const oldRankings = oldTs ? getRankingsAt(oldTs) : {};

      risingStars = Object.keys(latestRankings)
        .map(name => {
          const cur = latestRankings[name];
          const old = oldRankings[name];
          const growth = old ? cur.value - old.value : 0;
          return { name, growth, current: cur.value };
        })
        .sort((a, b) => b.growth - a.growth)
        .slice(0, 5);
    }

    // 랭킹 변동 정보 포함한 최종 랭킹 데이터
    const techRankings = Object.keys(latestRankings).reduce((acc, name) => {
      const cur = latestRankings[name];
      const prev = previousRankings[name];
      acc[name] = {
        rank: cur.rank,
        share: cur.value,
        rankDiff: prev ? prev.rank - cur.rank : 0
      };
      return acc;
    }, {} as Record<string, { rank: number; share: number; rankDiff: number }>);

    const labels = Array.from(new Set(stats.map(s => format(parseDate(s.collectedAt), 'MM/dd HH:mm')))).sort();

    return { statsByTech, techRankings, risingStars, labels };
  }, [stats, metric]);

  return {
    techs,
    stats,
    ...processedData,
    isTechsLoading,
    isStatsLoading
  };
}
