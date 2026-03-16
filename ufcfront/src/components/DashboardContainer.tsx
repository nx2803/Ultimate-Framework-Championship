'use client';

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { TechList, TechStats, Period } from '../types';
import { techApi } from '../lib/api';
import TechSidebar from './TechSidebar';
import { cn } from '../lib/utils';
import { Line } from 'react-chartjs-2';
import { useTechData, parseDate, MetricType } from '../hooks/useTechData';
import { useTheme } from 'next-themes';
import { getLogoUrl, getThemeColor } from '../lib/logoUtils';
import { TypewriterText } from './TypewriterText';
import { Skeleton, DashboardSkeleton } from './Skeleton';
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
  ChartOptions
} from 'chart.js';
import { TrendingUp, GitFork, AlertCircle, Calendar, ExternalLink, Menu, X, ChevronRight } from 'lucide-react';
import { format } from 'date-fns';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

export default function DashboardContainer() {
  const [selectedCategory, setSelectedCategory] = useState<string>('LANGUAGE');
  const [period, setPeriod] = useState<Period>(30);
  const [metric, setMetric] = useState<MetricType>('marketShare');
  const [hoveredTech, setHoveredTech] = useState<string | null>(null);
  const [selectedTechNames, setSelectedTechNames] = useState<string[]>([]);
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
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
          font: { family: "'Outfit', sans-serif", size: 10, weight: 600 },
          padding: 20
        }
      },
      tooltip: {
        backgroundColor: '#1a1a1a',
        titleColor: '#ffffff',
        bodyColor: '#ffffff',
        titleFont: { family: "'Outfit', sans-serif", size: 12, weight: 600 },
        bodyFont: { family: "'Outfit', sans-serif", size: 11, weight: 400 },
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
          font: { family: "'Outfit', sans-serif", size: 9, weight: 400 },
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
          initial={{ opacity: 0, y: 5 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -5 }}
          transition={{ duration: 0.4, ease: "easeOut" }}
          className="flex-1 p-4 md:p-8 flex flex-col gap-6 max-w-7xl mx-auto w-full overflow-y-auto lg:h-full lg:overflow-hidden"
        >
          {/* Mobile Header Toggle */}
          <div className="lg:hidden flex items-center justify-between mb-2">
            <button
              onClick={() => setIsSidebarOpen(true)}
              className="p-2 border border-border rounded-sm bg-background text-foreground"
            >
              <Menu className="w-5 h-5" />
            </button>
            <div className="text-[10px] font-black tracking-widest uppercase opacity-40">UFC Census</div>
          </div>
          {/* Main Header / Title Section */}
          <header className="flex flex-col space-y-2 shrink-0">
            <div className="flex items-center gap-4 text-muted-foreground mb-4">
              <span className="text-[10px] uppercase tracking-[0.3em] font-bold text-foreground/40">Category Analysis</span>
              <div className="h-px flex-1 bg-green-400/30" />
              <span className="text-[10px] font-mono opacity-30">{format(new Date(), 'yyyy-MM-dd HH:mm:ss')}</span>
            </div>

            <div className="flex flex-col md:flex-row md:items-start justify-between gap-6">
              <div className="flex-1 min-h-45 md:min-h-60 flex flex-col justify-start">
                <div className="mb-4 md:mb-6">
                  <TypewriterText
                    key={selectedCategory}
                    text={selectedCategory}
                    className="text-5xl md:text-8xl font-medium tracking-tighter text-foreground uppercase"
                    speed={80}
                  />
                </div>
                <p className="text-muted-foreground text-xs md:text-sm tracking-wide max-w-xl font-light leading-relaxed">
                  Comparative analysis of technological dominance within the {selectedCategory} ecosystem. Real-time market share mapping and relative adoption trajectories.
                </p>
              </div>

              {/* Rising Stars Widget */}
              {risingStars && risingStars.length > 0 && (
                <div className="bg-accent/30 border border-border/50 p-4 md:p-5 rounded-sm min-w-60 min-h-55 md:min-h-70">
                  <h4 className="text-[10px] font-bold uppercase tracking-[0.2em] text-muted-foreground mb-3 flex items-center gap-2">
                    <TrendingUp className="w-3 h-3 text-green-400" /> Rising Stars (7D)
                  </h4>
                  <div className="space-y-3">
                    {risingStars.map((star, i) => {
                      const starTech = techs.find(t => t.name === star.name);
                      return (
                        <div key={star.name} className="flex items-center justify-between gap-4 group/star">
                          <div className="flex items-center gap-2.5">
                            <span className="text-[9px] font-bold text-muted-foreground/30 w-3">{i + 1}</span>
                            <div className="w-6 h-6 flex items-center justify-center overflow-hidden shrink-0">
                              {starTech?.logoUrl ? (
                                <img
                                  src={getLogoUrl(starTech.logoUrl, star.name, theme)}
                                  alt={star.name}
                                  className="w-full h-full object-contain transition-all duration-300"
                                />
                              ) : (
                                <div className="w-full h-full bg-foreground/5 rounded-full" />
                              )}
                            </div>
                            <TypewriterText
                              key={`star-${star.name}`}
                              text={star.name}
                              className="text-[11px] font-bold text-foreground/70 group-hover/star:text-foreground transition-colors tracking-tight"
                              speed={30}
                              delay={1.5 + (i * 0.1)}
                            />
                          </div>
                          <span className="text-green-500 font-mono font-bold text-[10px]">
                            +{star.growth.toFixed(1)}%
                          </span>
                        </div>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>
          </header>

          {/* Highlight Stats */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-px bg-border/40 shrink-0 overflow-hidden border border-border/50 rounded-sm">
            <div className="bg-background p-6 flex flex-col justify-between group transition-all duration-300">
              <span className="text-[10px] font-bold uppercase tracking-[0.3em] text-muted-foreground mb-4 opacity-50 group-hover:opacity-100 transition-opacity">Dominant Tech</span>
              <div className="flex items-end justify-between">
                <div className="flex items-center gap-4 group-hover:translate-x-1 transition-transform duration-500">
                  {isStatsLoading ? (
                    <Skeleton className="h-10 w-32" />
                  ) : (() => {
                    const topTechEntry = Object.entries(techRankings).find(([_, info]) => info.rank === 1);
                    const topTechName = topTechEntry?.[0] || '';
                    const topTech = techs.find(t => t.name === topTechName);
                    return (
                      <>
                        {topTech?.logoUrl && (
                          <div className="w-10 h-10 md:w-12 md:h-12 flex items-center justify-center overflow-hidden shrink-0">
                            <img src={getLogoUrl(topTech.logoUrl, topTechName, theme)} alt={topTechName} className="w-full h-full object-contain transition-all duration-500" />
                          </div>
                        )}
                        <TypewriterText
                          key={`dominant-${topTechName}`}
                          text={topTechName || '---'}
                          className="text-5xl font-medium tracking-tighter"
                          speed={100}
                          delay={1.2}
                        />
                      </>
                    );
                  })()}
                </div>
                <div className="text-[10px] font-mono text-muted-foreground flex items-center gap-3 mb-1">
                  <div className="w-1.5 h-1.5 rounded-full bg-green-400" /> LEADER
                </div>
              </div>
            </div>

            <div className="bg-background p-6 flex flex-col justify-between group transition-all duration-300">
              <span className="text-[10px] font-bold uppercase tracking-[0.3em] text-muted-foreground mb-4 opacity-50 group-hover:opacity-100 transition-opacity">Total Sector Scale</span>
              <div className="flex items-end justify-between">
                {isStatsLoading ? (
                  <Skeleton className="h-10 w-40" />
                ) : (
                  <TypewriterText
                    key={`scale-${selectedCategory}`}
                    text={Array.from(new Set(stats.map(s => s.repoCount))).reduce((a, b) => a + b, 0).toLocaleString()}
                    className="text-5xl font-medium tracking-tighter group-hover:translate-x-1 transition-transform duration-500"
                    speed={50}
                    delay={1.5}
                  />
                )}
                <div className="text-[10px] font-mono text-muted-foreground flex items-center gap-2 mb-1 opacity-20">
                  ACTIVE REPOS
                </div>
              </div>
            </div>
          </div>

          {/* Chart Section */}
          <div className="bg-background border border-border p-6 flex-1 min-h-100 flex flex-col gap-6 relative overflow-hidden group/chart rounded-sm">
            <div className="absolute -top-12 -right-12 p-12 pointer-events-none opacity-[0.06] group-hover/chart:opacity-[0.15] transition-opacity duration-1000">
              <div className="text-[12rem] font-black tracking-tighter uppercase leading-none select-none">{selectedCategory}</div>
            </div>

            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 relative z-10 shrink-0">
              <div className="flex flex-col sm:flex-row sm:items-center gap-4 sm:gap-6">
                <h3 className="text-[10px] font-bold uppercase tracking-[0.4em] text-foreground flex items-center gap-3">
                  <span className="w-8 h-px bg-green-400" />
                  Dynamic Analysis
                </h3>
                <div className="flex border border-border p-0.5 bg-muted/10 rounded-sm self-start">
                  {(['marketShare', 'starCount', 'forkCount'] as MetricType[]).map((m) => (
                    <button
                      key={m}
                      onClick={() => setMetric(m)}
                      className={cn(
                        "px-3 md:px-4 py-1 text-[8px] font-bold uppercase tracking-widest transition-all duration-300 rounded-xs",
                        metric === m ? "bg-foreground text-background" : "text-muted-foreground hover:bg-muted/50"
                      )}
                    >
                      {m === 'marketShare' ? 'Market' : m === 'starCount' ? 'Stars' : 'Forks'}
                    </button>
                  ))}
                </div>
              </div>
              <div className="flex border border-border p-0.5 bg-muted/20 rounded-sm self-start sm:self-auto">
                {[7, 30, 90].map((p) => (
                  <button
                    key={p}
                    onClick={() => setPeriod(p as Period)}
                    className={cn(
                      "px-4 md:px-8 py-1.5 md:py-2 text-[9px] font-bold uppercase tracking-[0.2em] transition-all duration-300 rounded-sm",
                      period === p ? "bg-foreground text-background shadow-lg" : "bg-transparent text-muted-foreground hover:text-foreground hover:bg-muted/30"
                    )}
                  >
                    {p}D
                  </button>
                ))}
              </div>
            </div>

            <div className="flex-1 min-h-0 relative z-10">
              {isStatsLoading ? (
                <div className="w-full h-full flex items-center justify-center">
                  <Skeleton className="w-full h-full" />
                </div>
              ) : stats.length > 0 ? (
                <Line data={chartData} options={chartOptions} />
              ) : (
                <div className="w-full h-full flex items-center justify-center text-[10px] font-bold text-muted-foreground uppercase tracking-[0.4em] opacity-50">
                  Aggregating Global Stream...
                </div>
              )}
            </div>

            <div className="flex items-center justify-between text-[8px] font-mono text-muted-foreground tracking-[0.3em] uppercase opacity-30 border-t border-border/50 pt-4 shrink-0">
              <span className="flex items-center gap-2"><div className="w-1 h-1 bg-green-400" /> Dynamic Data Analytics / Scale_Linear</span>
              <span className="flex items-center gap-4">
                <span className="w-1.5 h-1.5 rounded-full bg-green-400 animate-pulse shadow-[0_0_8px_rgba(74,222,128,0.8)]" /> Live Feed
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
        {growth && <div className="text-[9px] font-black tracking-widest text-foreground uppercase">{growth}</div>}
      </div>
      <p className="text-[9px] font-black text-muted-foreground uppercase tracking-[0.2em] leading-none mb-3">{title}</p>
      <div className="text-4xl font-black text-foreground tabular-nums tracking-tighter">
        {typeof value === 'number' ? value.toLocaleString() : value}
      </div>
    </div>
  );
}
