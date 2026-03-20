"use client";

import { useEffect, useState, useRef } from "react";
import { techApi } from "@/lib/api";
import { Sparkles } from "lucide-react";

interface AICommentaryProps {
  category: string;
}

const CornerMarkers = () => (
  <>
    <div className="corner-top-left" />
    <div className="corner-top-right" />
    <div className="corner-bottom-left" />
    <div className="corner-bottom-right" />
  </>
);

export const AICommentary = ({ category }: AICommentaryProps) => {
  const [insight, setInsight] = useState<string | null>(null);
  const [displayedText, setDisplayedText] = useState("");
  const [isTyping, setIsTyping] = useState(false);
  const [timestamp, setTimestamp] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const prevInsightRef = useRef<string | null>(null);

  useEffect(() => {
    const fetchAnalysis = async () => {
      try {
        setLoading(true);
        const data = await techApi.getLatestAnalysis(category);
        if (data) {
          setInsight(data.insight);
          setTimestamp(formatRelativeTime(data.createdAt));
        } else {
          setInsight(null);
        }
      } catch (error) {
        console.error("Failed to fetch AI analysis:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchAnalysis();
  }, [category]);

  // 타이핑 애니메이션 효과
  useEffect(() => {
    if (!insight) {
      setDisplayedText("");
      setIsTyping(false);
      prevInsightRef.current = null;
      return;
    }

    // 이미 같은 내용이 출력 중이거나 완료되었다면 재시작하지 않음 (깜빡임 방지)
    if (insight === prevInsightRef.current) return;
    prevInsightRef.current = insight;

    let currentIndex = 0;
    setDisplayedText("");
    setIsTyping(true);

    const typingInterval = setInterval(() => {
      setDisplayedText(insight.substring(0, currentIndex + 1));
      currentIndex++;

      if (currentIndex >= insight.length) {
        clearInterval(typingInterval);
        setIsTyping(false);
      }
    }, 20);

    return () => clearInterval(typingInterval);
  }, [insight]);

  const formatRelativeTime = (dateStr: string) => {
    const date = new Date(dateStr);
    const now = new Date();
    const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / 60000);

    if (diffInMinutes < 1) return "방금 전";
    if (diffInMinutes < 60) return `${diffInMinutes}분 전`;
    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) return `${diffInHours}시간 전`;
    return date.toLocaleDateString('ko-KR');
  };

  if (loading) {
    return (
      <div className="bg-background/20 p-6 rounded-sm h-full flex flex-col gap-4 relative corner-frame overflow-hidden min-h-45">
        <CornerMarkers />
        <div className="h-4 w-32 bg-foreground/5 animate-pulse rounded" />
        <div className="flex-1 space-y-2">
          <div className="h-3 w-full bg-foreground/5 animate-pulse rounded" />
          <div className="h-3 w-full bg-foreground/5 animate-pulse rounded" />
          <div className="h-3 w-2/3 bg-foreground/5 animate-pulse rounded" />
        </div>
      </div>
    );
  }

  return (
    <div className="bg-background p-6 rounded-sm h-full group/commentbox transition-all duration-300 relative corner-frame flex flex-col min-h-45">
      <CornerMarkers />

      <div className="flex justify-between items-center mb-4 relative z-10">
        <h4 className="text-[10px] font-bold uppercase tracking-[0.2em] text-muted-foreground flex items-center gap-2">
          <Sparkles className="w-3 h-3 text-[#22c55e] animate-pulse" /> Analyzed Insight
        </h4>
      </div>

      <div className="flex-1 flex gap-3 relative z-10">

        {insight ? (
          <div className="relative min-h-12 w-full">
            <p className="text-[12px] md:text-[13px] font-medium text-foreground/80 leading-relaxed whitespace-pre-wrap">
              {displayedText}
              {isTyping && (
                <span className="inline-block w-0.5 h-[1.1em] ml-1 bg-green-500 animate-pulse align-middle" />
              )}
            </p>
          </div>
        ) : (
          <p className="text-[11px] text-muted-foreground/50 italic flex items-center h-full">
            분석 데이터를 기다리는 중입니다...
          </p>
        )}
      </div>
    </div>
  );
};
