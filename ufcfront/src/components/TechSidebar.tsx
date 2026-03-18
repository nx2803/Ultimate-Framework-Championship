'use client';

import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { TechList } from '../types';
import { Search, Hash, Cpu, Layout, Layers, Package, ChevronRight } from 'lucide-react';
import { cn } from '../lib/utils';
import { ThemeToggle } from './ThemeToggle';
import { useTheme } from 'next-themes';
import { getLogoUrl } from '../lib/logoUtils';
import { TypewriterText } from './TypewriterText';

const CornerMarkers = () => (
  <>
    <div className="corner-top-left" />
    <div className="corner-top-right" />
    <div className="corner-bottom-left" />
    <div className="corner-bottom-right" />
  </>
);

interface TechSidebarProps {
  techs: TechList[];
  selectedCategory: string;
  onSelectCategory: (category: string) => void;
  techRankings?: Record<string, { rank: number; share: number; rankDiff: number }>;
  hoveredTech: string | null;
  onHoverTech: (name: string | null) => void;
  selectedTechNames: string[];
  onToggleTech: (name: string) => void;
  isMobileOpen?: boolean;
  onMobileClose?: () => void;
}

const CategoryIcon = ({ category }: { category: string }) => {
  const iconProps = { className: "w-4 h-4", strokeWidth: 2.5 };
  switch (category.toUpperCase()) {
    case 'LANGUAGE': return <Hash {...iconProps} />;
    case 'FRONTEND': return <Layout {...iconProps} />;
    case 'BACKEND': return <Layers {...iconProps} />;
    case 'MOBILE': return <Cpu {...iconProps} />;
    case 'INFRA': case 'DATABASE': case 'AI_ML': return <Package {...iconProps} />;
    default: return <Package {...iconProps} />;
  }
};

const CATEGORIES = ['LANGUAGE', 'FRONTEND', 'BACKEND', 'MOBILE', 'INFRA', 'DATABASE', 'AI_ML'];

export default function TechSidebar({
  techs,
  selectedCategory,
  onSelectCategory,
  techRankings,
  hoveredTech,
  onHoverTech,
  selectedTechNames,
  onToggleTech,
  isMobileOpen,
  onMobileClose
}: TechSidebarProps) {
  const { theme } = useTheme();
  const [searchTerm, setSearchTerm] = React.useState('');

  const filteredTechs = techs.filter(tech => {
    const matchesSearch = tech.name.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = tech.category === selectedCategory;
    return matchesSearch && matchesCategory;
  }).sort((a, b) => {
    const rankA = techRankings?.[a.name]?.rank || 999;
    const rankB = techRankings?.[b.name]?.rank || 999;
    return rankA - rankB;
  });

  return (
    <aside className={cn(
      "w-80 h-screen bg-background flex flex-col fixed lg:sticky top-0 z-50 lg:z-0 transition-transform duration-300 font-sans",
      isMobileOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"
    )}>
      <div className="p-8 border-b border-border flex items-center justify-between">
        <div className="flex flex-col">
          <TypewriterText
            text="UFC."
            className="text-3xl font-medium tracking-tighter uppercase leading-none text-foreground"
            speed={150}
          />
          <TypewriterText
            text="Ultimate Framework Championship"
            className="text-[9px] font-medium text-muted-foreground mt-3 uppercase tracking-[0.3em] leading-tight"
            speed={30}
            delay={0.5}
          />
        </div>
        {onMobileClose && (
          <button onClick={onMobileClose} className="lg:hidden p-2 text-muted-foreground hover:text-foreground">
            <ChevronRight className="w-5 h-5 rotate-180" />
          </button>
        )}
      </div>

      <div className="p-6 space-y-6">
        {/* Search */}
        <div className="relative group">
          <Search className="absolute left-0 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground/50 transition-colors group-focus-within:text-foreground" />
          <input
            type="text"
            placeholder="Search Index..."
            className="w-full h-10 bg-transparent border-b border-border rounded-none pl-7 pr-4 text-xs focus:outline-none focus:border-green-500/50 transition-all placeholder:text-muted-foreground/30 font-medium uppercase tracking-wider"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {/* Categories Grid */}
        <div className="grid grid-cols-2 gap-1.5">
          {CATEGORIES.map(cat => (
            <button
              key={cat}
              onClick={() => onSelectCategory(cat)}
              className={cn(
                "px-2 py-2 text-[8px] font-medium tracking-widest transition-all border uppercase",
                selectedCategory === cat
                  ? "bg-foreground text-background border-green-500 shadow-[0_0_20px_rgba(74,222,128,0.1)]"
                  : "bg-transparent text-muted-foreground border-border hover:border-muted-foreground"
              )}
            >
              <TypewriterText key={cat} text={cat} speed={40} delay={0.2} />
            </button>
          ))}
        </div>
      </div>

      <div className="flex-1 overflow-y-auto px-6 py-4 space-y-px scrollbar-hide">
        {filteredTechs.length > 0 ? (
          <div className="flex flex-col">
            <div className="flex items-center gap-2 mb-4 mt-2">
              <div className="w-1 h-3 bg-green-500" />
              <p className="text-[10px] font-bold text-muted-foreground uppercase tracking-[0.2em]">Ranking</p>
            </div>
            <ul className="space-y-1">
              <AnimatePresence mode="popLayout">
                {filteredTechs.map((tech, i) => {
                  const ranking = techRankings?.[tech.name];
                  const isHovered = hoveredTech === tech.name;
                  const isSelected = selectedTechNames.includes(tech.name);

                  return (
                    <motion.li
                      layout
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, scale: 0.95 }}
                      transition={{ duration: 0.4, ease: "easeOut" }}
                      key={tech.id}
                      onMouseEnter={() => onHoverTech(tech.name)}
                      onMouseLeave={() => onHoverTech(null)}
                    >
                      <button
                        onClick={() => onToggleTech(tech.name)}
                        className={cn(
                          "w-full text-left px-4 py-3 transition-all duration-300 flex items-center justify-between group rounded-sm relative",
                          isHovered || isSelected ? "bg-accent/50 text-foreground" : "text-muted-foreground hover:bg-accent/30",
                          "border-b border-border/5"
                        )}
                      >
                        <div className="flex items-center gap-4">
                          {/* Left: Rank & Logo */}
                          <div className="flex items-center gap-2">
                            {ranking && (
                              <span className={cn(
                                "text-lg font-medium tracking-tighter w-6 text-left leading-none transition-colors",
                                isHovered || isSelected ? "text-foreground" : "text-muted-foreground"
                              )}>
                                {ranking.rank}
                              </span>
                            )}
                            <div className="w-8 h-8 flex items-center justify-center overflow-hidden shrink-0">
                              {tech.logoUrl ? (
                                <img
                                  src={getLogoUrl(tech.logoUrl, tech.name, theme)}
                                  alt={tech.name}
                                  className="w-full h-full object-contain transition-all duration-300"
                                />
                              ) : (
                                <div className="w-full h-full bg-foreground/10 rounded-full" />
                              )}
                            </div>
                          </div>

                          {/* Center: Name & Stats */}
                          <div className="flex flex-col">
                            <span className={cn("text-sm transition-all", isHovered || isSelected ? "font-medium tracking-tight" : "font-normal tracking-tight")}>
                              <TypewriterText
                                key={`${tech.name}-${selectedCategory}`}
                                text={tech.name}
                                speed={40}
                                delay={0.1 * (i % 10)}
                              />
                            </span>
                            {ranking && (
                              <div className="flex items-center gap-2 text-[9px] font-mono text-muted-foreground">
                                {ranking.rankDiff !== 0 && (
                                  <span className={cn(
                                    "flex items-center gap-0.5 font-medium",
                                    ranking.rankDiff > 0 ? "text-green-500" : "text-red-500"
                                  )}>
                                    {ranking.rankDiff > 0 ? '▲' : '▼'}{Math.abs(ranking.rankDiff)}
                                  </span>
                                )}
                                <span className="">SCORE</span>
                                <span>{ranking.share.toLocaleString()}</span>
                              </div>
                            )}
                          </div>
                        </div>

                        {/* Right: Selection Checkbox */}
                        <div className={cn(
                          "w-3 h-3 border transition-all duration-300 shrink-0",
                          isSelected
                            ? "bg-green-500 border-green-500 shadow-[0_0_10px_rgba(74,222,128,0.4)]"
                            : "border-border group-hover:border-muted-foreground"
                        )} />
                      </button>
                    </motion.li>
                  )
                })}
              </AnimatePresence>
            </ul>
          </div>
        ) : (
          <div className="text-center py-20">
            <p className="text-[10px] font-bold text-muted-foreground/30 uppercase tracking-[0.2em]">Void System</p>
          </div>
        )}
      </div>

      <div className="p-6 border-t border-border mt-auto bg-background/50 backdrop-blur-sm flex items-center justify-between">
        <div className="flex flex-col">
          <TypewriterText
            text="UFC Collector"
            className="text-[9px] font-bold uppercase tracking-[0.2em] text-foreground text-balance"
            speed={50}
            delay={2}
          />
          <TypewriterText
            text="v1.2.0-STABLE"
            className="text-[8px] font-mono mt-1"
            speed={20}
            delay={2.5}
          />
        </div>
        <ThemeToggle className="rounded-none border border-border" />
      </div>
    </aside>
  );
}
