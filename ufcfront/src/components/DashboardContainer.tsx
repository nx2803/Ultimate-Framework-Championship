'use client';

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { TechList, TechStats, Period } from '../types';
import { techApi } from '../lib/api';
import TechSidebar from './TechSidebar';
import { cn } from '../lib/utils';
import { useTechData, parseDate, MetricType } from '../hooks/useTechData';
import { useTheme } from 'next-themes';
import { getLogoUrl, getThemeColor } from '../lib/logoUtils';
import { TypewriterText } from './TypewriterText';
import { Skeleton, DashboardSkeleton } from './Skeleton';
import { AICommentary } from './AICommentary';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
  ArcElement,
  ChartOptions
} from 'chart.js';
import { TrendingUp, GitFork, AlertCircle, Calendar, ExternalLink, Menu, X, ChevronRight, BarChart3, PieChart as PieIcon, Activity, Trophy } from 'lucide-react';
import { format } from 'date-fns';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
  ArcElement
);

import { Line, Doughnut } from 'react-chartjs-2';

const CornerMarkers = () => (
  <>
    <div className="corner-top-left" />
    <div className="corner-top-right" />
    <div className="corner-bottom-left" />
    <div className="corner-bottom-right" />
  </>
);

export default function DashboardContainer() {
  const [selectedCategory, setSelectedCategory] = useState<string>('LANGUAGE');
  const [period, setPeriod] = useState<Period>(30);
  const [metric, setMetric] = useState<MetricType>('marketShare');
  const [hoveredTech, setHoveredTech] = useState<string | null>(null);
  const [selectedTechNames, setSelectedTechNames] = useState<string[]>([]);
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [chartType, setChartType] = useState<'line' | 'pie'>('pie');
  const { theme } = useTheme();

  const {
    techs,
    stats,
    statsByTech,
    techRankings,
    risingStars,
    labels,
    isTechsLoading,
    isStatsLoading
  } = useTechData(selectedCategory, period, metric);

  const onToggleTech = (name: string) => {
    setSelectedTechNames(prev =>
      prev.includes(name)
        ? prev.filter(t => t !== name)
        : [...prev, name]
    );
  };

  // 카테고리 변경 시 선택된 기술 초기화
  React.useEffect(() => {
    setSelectedTechNames([]);
  }, [selectedCategory]);

  const colors = [
    '#000000', '#666666', '#999999', '#CCCCCC',
    '#333333', '#4D4D4D', '#B3B3B3', '#E6E6E6'
  ];

  const chartData = {
    labels,
    datasets: Object.keys(statsByTech).length > 0
      ? Object.keys(statsByTech)
        .filter(name => selectedTechNames.length === 0 || selectedTechNames.includes(name))
        .map((techName, index) => {
          const techInfo = techs.find(t => t.name === techName);
          const rawColor = techInfo?.color || colors[index % colors.length];
          const brandColor = getThemeColor(rawColor, techName, theme);
          const isHovered = hoveredTech === techName;
          const hasHover = hoveredTech !== null;

          return {
            label: techName,
            data: labels.map(label => {
              const stat = statsByTech[techName].find(s => format(parseDate(s.collectedAt), 'MM/dd HH:mm') === label);
              return stat ? (stat[metric] as number) : null;
            }),
            borderColor: hasHover ? (isHovered ? brandColor : `${brandColor}20`) : brandColor,
            backgroundColor: `${brandColor}10`,
            borderWidth: isHovered ? 4 : 2,
            pointRadius: 0,
            pointHoverRadius: 6,
            tension: 0.3,
            fill: false,
          };
        })
      : []
  };

  const chartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    animation: {
      duration: 800,
      easing: 'easeOutQuart'
    },
    interaction: {
      mode: 'index',
      intersect: false,
    },
    plugins: {
      legend: {
        display: true,
        position: 'top',
        align: 'end',
        labels: {
          boxWidth: 8,
          boxHeight: 8,
          usePointStyle: true,
          font: { family: "var(--font-geologica)", size: 10, weight: 600 },
          padding: 20
        }
      },
      tooltip: {
        backgroundColor: '#1a1a1a',
        titleColor: '#ffffff',
        bodyColor: '#ffffff',
        titleFont: { family: "var(--font-geologica)", size: 12, weight: 600 },
        bodyFont: { family: "var(--font-geologica)", size: 11, weight: 400 },
        padding: 16,
        cornerRadius: 0,
        displayColors: true,
        callbacks: {
          label: (item: any) => `${item.dataset.label}: ${item.formattedValue}${metric === 'marketShare' ? '%' : ''}`,
        }
      }
    },
    scales: {
      x: {
        grid: { display: false },
        ticks: {
          color: 'rgba(128, 128, 128, 0.5)',
          font: { family: "var(--font-geologica)", size: 9, weight: 400 },
          maxRotation: 0,
          autoSkip: true,
          maxTicksLimit: 7,
          padding: 10
        }
      },
      y: {
        grid: { color: 'rgba(128, 128, 128, 0.05)', drawTicks: false },
        border: { display: false },
        ticks: {
          color: 'rgba(128, 128, 128, 0.5)',
          font: { family: "'Outfit', sans-serif", size: 9, weight: 400 },
          padding: 10,
          callback: (val: any) => `${val}%`
        }
      }
    }
  };

  const currentTechs = Object.keys(techRankings)
    .filter(name => selectedTechNames.length === 0 || selectedTechNames.includes(name))
    .sort((a, b) => techRankings[b].share - techRankings[a].share);

  const pieData = {
    labels: currentTechs,
    datasets: [
      {
        data: currentTechs.map(name => techRankings[name].share),
        backgroundColor: currentTechs.map((name, i) => {
          const techInfo = techs.find(t => t.name === name);
          const color = getThemeColor(techInfo?.color || colors[i % colors.length], name, theme);
          if (hoveredTech && hoveredTech !== name) {
            return `${color}22`; // 흐릿하게
          }
          return `${color}DD`; // 선명하게
        }),
        borderWidth: hoveredTech ? currentTechs.map(name => name === hoveredTech ? 2 : 0) : 0,
        borderColor: currentTechs.map((name, i) => {
          const techInfo = techs.find(t => t.name === name);
          return getThemeColor(techInfo?.color || colors[i % colors.length], name, theme);
        }),
        hoverOffset: 0,
        spacing: 0,
        borderRadius: 0,
        cutout: '94%'
      }
    ]
  };

  const pieOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '90%',
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: '#0a0a0a',
        padding: 16,
        borderWidth: 1,
        borderColor: 'rgba(255, 255, 255, 0.1)',
        cornerRadius: 0,
        callbacks: {
          label: (item: any) => ` ${item.label}: ${item.formattedValue}${metric === 'marketShare' ? '%' : ''}`
        }
      }
    }
  };

  // 초기 데이터가 아예 없을 때만 전체 스켈레톤 노출
  if (isTechsLoading || (isStatsLoading && techs.length === 0)) {
    return <DashboardSkeleton />;
  }

  return (
    <div className="flex w-full h-dvh bg-background font-sans overflow-hidden relative">
      <TechSidebar
        techs={techs}
        selectedCategory={selectedCategory}
        onSelectCategory={(cat) => { setSelectedCategory(cat); setIsSidebarOpen(false); }}
        techRankings={techRankings}
        hoveredTech={hoveredTech}
        onHoverTech={setHoveredTech}
        selectedTechNames={selectedTechNames}
        onToggleTech={onToggleTech}
        isMobileOpen={isSidebarOpen}
        onMobileClose={() => setIsSidebarOpen(false)}
      />

      {/* Mobile Overlay */}
      <AnimatePresence>
        {isSidebarOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setIsSidebarOpen(false)}
            className="fixed inset-0 bg-background/80 backdrop-blur-sm z-40 lg:hidden"
          />
        )}
      </AnimatePresence>

      <AnimatePresence mode="wait">
        <motion.main
          key={selectedCategory}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.25, ease: "easeInOut" }}
          className="flex-1 p-3 md:p-8 flex flex-col gap-4 md:gap-6 max-w-7xl mx-auto w-full overflow-y-auto lg:h-full lg:overflow-hidden"
        >
          {/* Mobile Header Toggle */}
          <div className="lg:hidden flex items-center justify-between mb-2">
            <button
              onClick={() => setIsSidebarOpen(true)}
              className="p-2 rounded-sm bg-background text-foreground"
            >
              <Menu className="w-5 h-5" />
            </button>
            <div className="text-[10px] font-black tracking-widest uppercase">UFC.</div>
          </div>
          {/* Main Header / Title Section */}
          <header className="flex flex-col space-y-2 shrink-0">
            <div className="flex flex-col md:flex-row md:items-center gap-1 md:gap-4 text-muted-foreground mb-4">
              <div className="flex items-center gap-4 flex-1">
                <span className="text-[10px] uppercase tracking-[0.3em] font-bold text-foreground shrink-0">C:\UFC\SYSTEM\ANALYSIS_MODULE</span>
                <div className="hidden md:block h-px flex-1 bg-border" />
              </div>
              <span className="text-[10px] font-mono shrink-0 md:opacity-100 opacity-60">{format(new Date(), 'yyyy-MM-dd HH:mm:ss')}</span>
            </div>

            <div className="flex flex-col md:flex-row md:items-start justify-between gap-4 md:gap-6">
              <div className="flex-1 md:min-h-60 flex flex-col justify-start">
                <div className="mb-2 md:mb-6 flex items-baseline gap-2 md:gap-4">
                  <TypewriterText
                    key={selectedCategory}
                    text={selectedCategory}
                    className="text-5xl md:text-8xl font-light tracking-tighter text-foreground uppercase"
                    speed={80}
                  />
                </div>

                <p className="hidden md:block text-muted-foreground text-xs md:text-sm tracking-wide max-w-xl font-light leading-relaxed">
                  Comparative analysis of technological dominance within the {selectedCategory} ecosystem. Real-time market share mapping and relative adoption trajectories.
                </p>
              </div>

              {/* AI Analyst Commentary Widget — Expanded to utilize left space */}
              <div className="flex-1 max-w-2xl md:min-h-70 relative shrink-0">
                <AICommentary category={selectedCategory} />
              </div>
            </div>
          </header>

          {/* Highlight Stats */}
          <div className="grid grid-cols-2 lg:grid-cols-2 gap-2 md:gap-6 shrink-0 overflow-visible">
            <div className="bg-background p-4 md:p-6 flex flex-col justify-between group transition-all duration-300 relative corner-frame min-h-32 md:min-h-0">
              <CornerMarkers />
              <span className="text-[8px] md:text-[10px] font-medium uppercase tracking-[0.2em] md:tracking-[0.3em] text-muted-foreground mb-3 md:mb-4">Dominant Tech</span>
              <div className="flex flex-col md:flex-row md:items-end justify-between gap-1">
                <div className="flex items-center gap-2 md:gap-4 group-hover:translate-x-1 transition-transform duration-500">
                  {isStatsLoading ? (
                    <Skeleton className="h-10 w-32" />
                  ) : (() => {
                    const topTechEntry = Object.entries(techRankings).find(([_, info]) => info.rank === 1);
                    const topTechName = topTechEntry?.[0] || '';
                    const topTech = techs.find(t => t.name === topTechName);
                    return (
                      <>
                        {topTech?.logoUrl && (
                          <div className="w-8 h-8 md:w-12 md:h-12 flex items-center justify-center overflow-hidden shrink-0">
                            <img src={getLogoUrl(topTech.logoUrl, topTechName, theme)} alt={topTechName} className="w-full h-full object-contain transition-all duration-500" />
                          </div>
                        )}
                        <TypewriterText
                          key={`dominant-${topTechName}`}
                          text={topTechName || '---'}
                          className="text-2xl md:text-5xl font-medium tracking-tighter"
                          speed={100}
                          delay={1.2}
                        />
                      </>
                    );
                  })()}
                </div>
                <div className="text-[8px] md:text-[10px] font-mono text-muted-foreground flex items-center gap-1 md:gap-2 mb-0.5 md:mb-1">
                  <Trophy className="w-2 md:w-3 h-2 md:h-3 text-foreground" /> <div className="text-[8px] md:text-[10px] font-mono text-muted-foreground flex items-center gap-1 md:gap-2 mb-0.5 md:mb-1">
                    CHAMP
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-background p-4 md:p-6 flex flex-col justify-between group transition-all duration-300 relative corner-frame min-h-32 md:min-h-0">
              <CornerMarkers />
              <span className="text-[8px] md:text-[10px] font-medium uppercase tracking-[0.2em] md:tracking-[0.3em] text-muted-foreground mb-3 md:mb-4">Total Sector Scale</span>
              <div className="flex flex-col md:flex-row md:items-end justify-between gap-1">
                {isStatsLoading ? (
                  <Skeleton className="h-6 md:h-10 w-24 md:w-40" />
                ) : (
                  <TypewriterText
                    key={`scale-${selectedCategory}`}
                    text={Array.from(new Set(stats.map(s => s.repoCount))).reduce((a, b) => a + b, 0).toLocaleString()}
                    className="text-2xl md:text-5xl font-medium tracking-tighter group-hover:translate-x-1 transition-transform duration-500"
                    speed={50}
                    delay={1.5}
                  />
                )}
                <div className="text-[8px] md:text-[10px] font-mono text-muted-foreground flex items-center gap-1 md:gap-2 mb-0.5 md:mb-1">
                  ACTIVE
                </div>
              </div>
            </div>
          </div>

          {/* Chart Section */}
          <div className="bg-background p-4 md:p-6 flex-none lg:flex-1 min-h-95 md:min-h-100 flex flex-col gap-4 md:gap-6 relative group/chart rounded-sm corner-frame w-full">
            <CornerMarkers />

            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 relative z-10 shrink-0">
              <div className="flex flex-col sm:flex-row sm:items-center gap-4 sm:gap-6">
                <h3 className="text-[10px] font-medium uppercase tracking-[0.4em] text-foreground flex items-center gap-3">
                  <span className="w-8 h-px bg-green-500" />
                  Dynamic Analysis
                </h3>
                <div className="flex items-center border border-border p-0.5 bg-muted/10 rounded-sm self-start overflow-hidden">
                  <span className="px-3 text-[7px] font-mono select-none uppercase border-r border-border/50 mr-0.5 mt-0.5">[SELECT_METRIC]:</span>
                  {(['marketShare', 'starCount', 'forkCount'] as MetricType[]).map((m) => (
                    <button
                      key={m}
                      onClick={() => setMetric(m)}
                      className={cn(
                        "relative px-3 md:px-4 py-1 text-[8px] font-medium uppercase tracking-widest transition-all duration-300 rounded-xs",
                        metric === m ? "text-background" : "text-muted-foreground hover:bg-muted/50"
                      )}
                    >
                      {metric === m && (
                        <motion.div
                          layoutId="metric-bg"
                          className="absolute inset-0 bg-foreground rounded-xs"
                          transition={{ type: "spring", bounce: 0.15, duration: 0.6 }}
                        />
                      )}
                      <span className="relative z-10">
                        {m === 'marketShare' ? 'Repos' : m === 'starCount' ? 'Stars' : 'Forks'}
                      </span>
                    </button>
                  ))}
                </div>
              </div>
              <div className="flex gap-2">
                {chartType === 'line' && (
                  <div className="hidden sm:flex border border-border p-0.5 bg-muted/20 rounded-sm">
                    {[7, 30, 90].map((p) => (
                      <button
                        key={p}
                        onClick={() => setPeriod(p as Period)}
                        className={cn(
                          "relative px-4 md:px-8 py-1.5 md:py-2 text-[9px] font-medium uppercase tracking-[0.2em] transition-all duration-300 rounded-sm",
                          period === p ? "text-background" : "bg-transparent text-muted-foreground hover:text-foreground hover:bg-muted/30"
                        )}
                      >
                        {period === p && (
                          <motion.div
                            layoutId="period-bg"
                            className="absolute inset-0 bg-foreground rounded-sm shadow-lg"
                            transition={{ type: "spring", bounce: 0.15, duration: 0.6 }}
                          />
                        )}
                        <span className="relative z-10">{p}D</span>
                      </button>
                    ))}
                  </div>
                )}
                <div className="flex border border-border p-0.5 bg-muted/20 rounded-sm shrink-0">
                  <button
                    onClick={() => setChartType('pie')}
                    className={cn(
                      "relative p-1.5 transition-all duration-300 rounded-sm",
                      chartType === 'pie' ? "text-background" : "text-muted-foreground hover:bg-muted/30"
                    )}
                    title="Pie Chart"
                  >
                    {chartType === 'pie' && (
                      <motion.div
                        layoutId="chart-type-bg"
                        className="absolute inset-0 bg-foreground rounded-sm"
                        transition={{ type: "spring", bounce: 0.15, duration: 0.6 }}
                      />
                    )}
                    <PieIcon className={cn("relative z-10 w-4 h-4", chartType === 'pie' ? "opacity-100" : "opacity-40")} />
                  </button>
                  <button
                    onClick={() => setChartType('line')}
                    className={cn(
                      "relative p-1.5 transition-all duration-300 rounded-sm",
                      chartType === 'line' ? "text-background" : "text-muted-foreground hover:bg-muted/30"
                    )}
                    title="Line Chart"
                  >
                    {chartType === 'line' && (
                      <motion.div
                        layoutId="chart-type-bg"
                        className="absolute inset-0 bg-foreground rounded-sm"
                        transition={{ type: "spring", bounce: 0.15, duration: 0.6 }}
                      />
                    )}
                    <Activity className={cn("relative z-10 w-4 h-4", chartType === 'line' ? "opacity-100" : "opacity-40")} />
                  </button>
                </div>
              </div>
            </div>

            <div className={cn("relative z-10 py-4 w-full", chartType === 'line' ? "flex-1 min-h-0" : "flex-none")}>
              {isStatsLoading ? (
                <div className="w-full h-full flex items-center justify-center">
                  <Skeleton className="w-full h-full" />
                </div>
              ) : stats.length > 0 ? (
                <AnimatePresence mode="wait">
                  <motion.div
                    key={chartType}
                    initial={{ opacity: 0, scale: 0.98 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 1.02 }}
                    transition={{ duration: 0.4 }}
                    className={cn("w-full", chartType === 'line' ? "h-full" : "h-auto md:h-full")}
                  >
                    {chartType === 'line' ? (
                      <Line data={chartData} options={chartOptions} />
                    ) : (
                      <div className="w-full h-auto md:h-full flex flex-col md:flex-row items-center justify-between gap-8 md:gap-10 py-4 px-2 md:px-6 relative">
                        {/* Far Left Detail Panel */}
                        <div className="hidden lg:flex flex-col gap-4 self-stretch justify-center transition-opacity duration-500 select-none pointer-events-none">
                          <div className="flex flex-col gap-1">
                            <div className="w-12 h-0.5 bg-foreground/20" />
                            <span className="text-[6px] font-mono tracking-[0.2em]">NODE_ALPHA</span>
                          </div>
                          <div className="flex flex-col gap-1">
                            <div className="w-8 h-0.5 bg-foreground/20" />
                            <span className="text-[6px] font-mono tracking-[0.2em]">LATENCY: 12ms</span>
                          </div>
                          <div className="flex flex-col gap-1">
                            <div className="w-16 h-0.5 bg-foreground/20" />
                            <span className="text-[6px] font-mono tracking-[0.2em]">PKT_LOSS: 0.00%</span>
                          </div>
                        </div>
                        {/* Thin Doughnut Container (Pushed Left) */}
                        <div className="relative w-full max-w-65 md:max-w-104 aspect-square flex items-center justify-center p-2 md:p-14 group/core mx-auto">
                          {/* Corner Metadata (Technical Polish) */}
                          <div className="absolute top-0 left-0 text-[6px] font-mono flex flex-col gap-1 uppercase select-none">
                            <span>System_ID: 0x88AF</span>
                            <span>Buffer_Load: Nominal</span>
                          </div>
                          <div className="absolute bottom-4 right-0 text-[6px] font-mono flex flex-col items-end gap-1 uppercase select-none">
                            <span>Sync_Active: 100%</span>
                            <span>Frame_Rate: 60Hz</span>
                          </div>

                          <div className="w-full h-full relative z-10 drop-shadow-[0_0_40px_rgba(255,255,255,0.06)] group-hover/core:scale-[1.02] transition-transform duration-700">
                            <Doughnut data={pieData} options={pieOptions} />
                          </div>

                          {/* Center Technical Readout — fixed layout, text-only changes */}
                          <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none gap-1">
                            {/* 기술 이름 — 항상 같은 높이 차지 */}
                            <span className="text-[9px] font-mono uppercase tracking-[0.3em] text-muted-foreground select-none h-4 flex items-center">
                              {hoveredTech && techRankings[hoveredTech] ? hoveredTech : 'TOTAL'}
                            </span>
                            {/* 숫자 — 항상 고정 */}
                            <div className="flex items-baseline gap-1">
                              <span className="text-4xl font-black tracking-tighter tabular-nums leading-none select-none">
                                {hoveredTech && techRankings[hoveredTech]
                                  ? (metric === 'marketShare'
                                    ? techRankings[hoveredTech].share.toFixed(1)
                                    : techRankings[hoveredTech].share.toLocaleString())
                                  : (metric === 'marketShare'
                                    ? '100'
                                    : currentTechs.reduce((sum, name) => sum + techRankings[name].share, 0).toLocaleString())}
                              </span>
                              <span className="text-[10px] font-bold mt-auto select-none">
                                {metric === 'marketShare' ? '%' : ''}
                              </span>
                            </div>
                            {/* 하단 레이블 — 항상 같은 높이 차지 */}
                            <span className="text-[5px] font-mono uppercase tracking-[0.8em] leading-none select-none text-muted-foreground h-3 flex items-center">
                              {hoveredTech && techRankings[hoveredTech]
                                ? (metric === 'marketShare' ? 'Market_Share' : metric === 'starCount' ? 'Star_Count' : 'Fork_Count')
                                : (metric === 'marketShare' ? 'Total_Share' : metric === 'starCount' ? 'Total_Stars' : 'Total_Forks')}
                            </span>
                          </div>
                        </div>

                        {/* Custom Technical Legend (Vertical Stream - Pushed Right) */}
                        <div className="flex-1 w-full max-w-md flex flex-col gap-4 border-t md:border-t-0 md:border-l border-border/20 pt-6 md:pt-0 pl-0 md:pl-12 overflow-y-auto max-h-80 pr-2 custom-scrollbar">
                          <div className="flex items-center gap-3 mb-2">
                            <div className="w-1 h-3 bg-green-500" />
                            <span className="text-[8px] font-mono font-bold tracking-[0.4em] uppercase">Sector_Analysis_Stream</span>
                          </div>
                          {currentTechs.map((name, i) => {
                            const techInfo = techs.find(t => t.name === name);
                            const color = getThemeColor(techInfo?.color || colors[i % colors.length], name, theme);
                            const val = techRankings[name].share;
                            return (
                              <div key={name} className="flex flex-col gap-1.5 group/item cursor-crosshair">
                                <div className="flex items-center justify-between">
                                  <div className="flex items-center gap-3">
                                    <span className="text-[7px] font-mono">{String(i + 1).padStart(2, '0')}</span>
                                    <span className="text-[11px] font-bold tracking-tight text-foreground transition-all group-hover/item:translate-x-1">{name}</span>
                                  </div>
                                  <div className="flex items-center gap-4">
                                    <span className="text-[10px] font-mono font-black" style={{ color }}>{val.toFixed(2)}{metric === 'marketShare' ? '%' : ''}</span>
                                    <div className="w-1 h-1 rounded-full bg-foreground/10 group-hover/item:bg-white animate-pulse" />
                                  </div>
                                </div>
                                <div className="w-full h-0.5 bg-muted/10 rounded-full overflow-hidden relative">
                                  <motion.div
                                    initial={{ width: 0 }}
                                    animate={{ width: `${(val / (metric === 'marketShare' ? 100 : Math.max(...currentTechs.map(n => techRankings[n].share)))) * 100}%` }}
                                    className="h-full relative z-10"
                                    style={{ backgroundColor: color }}
                                  />
                                  <div className="absolute inset-0 bg-linear-to-r from-transparent via-white/5 to-transparent animate-[shimmer_2s_infinite]" />
                                </div>
                              </div>
                            );
                          })}
                        </div>

                        {/* Far Right Detail Panel */}
                        <div className="hidden lg:flex flex-col gap-4 self-stretch justify-center items-end transition-opacity duration-500 select-none pointer-events-none">
                          <div className="flex flex-col items-end gap-1">
                            <span className="text-[6px] font-mono tracking-[0.2em]">STREAM_SYNC</span>
                            <div className="w-12 h-0.5 bg-foreground/20" />
                          </div>
                          <div className="flex flex-col items-end gap-1">
                            <span className="text-[6px] font-mono tracking-[0.2em]">ENCRYPT: AES-256</span>
                            <div className="w-16 h-0.5 bg-foreground/20" />
                          </div>
                          <div className="flex flex-col items-end gap-1">
                            <span className="text-[6px] font-mono tracking-[0.2em]">UPLINK: ACTIVE</span>
                            <div className="w-10 h-0.5 bg-foreground/20" />
                          </div>
                        </div>
                      </div>
                    )}
                  </motion.div>
                </AnimatePresence>
              ) : (
                <div className="w-full h-full flex items-center justify-center text-[10px] font-bold text-muted-foreground uppercase tracking-[0.4em] opacity-50">
                  Aggregating Global Stream...
                </div>
              )}
            </div>

            <div className="flex items-center justify-between text-[8px] font-mono text-muted-foreground tracking-[0.3em] uppercase border-t border-border pt-4 shrink-0">
              <span className="flex items-center gap-2"><div className="w-1 h-1 bg-green-500" /> Dynamic Data Analytics / Scale_Linear</span>
              <span className="flex items-center gap-4">
                <span className="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse shadow-[0_0_8px_rgba(74,222,128,0.8)]" /> Live Feed
              </span>
            </div>
          </div>
        </motion.main>
      </AnimatePresence>
    </div>
  );
}

function StatCard({ title, value, icon, growth }: { title: string; value: number | string; icon: React.ReactNode; growth?: string }) {
  return (
    <div className="border border-border p-8 transition-all hover:bg-secondary/50 group">
      <div className="flex items-start justify-between mb-8 opacity-40 group-hover:opacity-100 transition-opacity">
        <div className="p-1 border border-foreground/20">{icon}</div>
        {growth && <div className="text-[9px] font-medium tracking-widest text-foreground uppercase">{growth}</div>}
      </div>
      <p className="text-[9px] font-medium text-muted-foreground uppercase tracking-[0.2em] leading-none mb-3">{title}</p>
      <div className="text-4xl font-light text-foreground tabular-nums tracking-tighter">
        {typeof value === 'number' ? value.toLocaleString() : value}
      </div>
    </div>
  );
}
