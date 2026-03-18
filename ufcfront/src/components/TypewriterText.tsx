'use client';

import React from 'react';
import { motion } from 'framer-motion';
import { cn } from '../lib/utils';

interface TypewriterTextProps {
  text: string;
  className?: string;
  delay?: number;
  speed?: number;
  onComplete?: () => void;
}

export const TypewriterText = ({ text, className, delay = 0, speed = 40, onComplete }: TypewriterTextProps) => {
  const [displayedText, setDisplayedText] = React.useState('');
  const [showCursor, setShowCursor] = React.useState(true);
  const [isCompleted, setIsCompleted] = React.useState(false);

  React.useEffect(() => {
    let timeout: NodeJS.Timeout;
    let currentIndex = 0;

    setDisplayedText('');
    setIsCompleted(false);

    const type = () => {
      if (currentIndex < text.length) {
        setDisplayedText(text.substring(0, currentIndex + 1));
        currentIndex++;
        timeout = setTimeout(type, speed);
      } else {
        setIsCompleted(true);
        if (onComplete) onComplete();
      }
    };

    const startTimeout = setTimeout(type, delay * 1000);

    return () => {
      clearTimeout(startTimeout);
      clearTimeout(timeout);
    };
  }, [text, delay, speed, onComplete]);

  React.useEffect(() => {
    if (isCompleted) return;
    const interval = setInterval(() => {
      setShowCursor(prev => !prev);
    }, 500);
    return () => clearInterval(interval);
  }, [isCompleted]);

  return (
    <div className={cn("block leading-tight", className)}>
      <span className="whitespace-pre-wrap relative">
        {text.split('').map((char, i) => (
          <span
            key={i}
            className={cn(
              "relative",
              i < displayedText.length ? "text-current" : "text-transparent"
            )}
            aria-hidden={i >= displayedText.length}
          >
            {char}
            {/* 현재 마지막으로 써진 글자 바로 뒤에 커서 표시 */}
            {!isCompleted && i === displayedText.length - 1 && (
              <motion.span
                animate={{ opacity: showCursor ? 1 : 0 }}
                className="absolute left-[calc(100%+2px)] bottom-[0.1em] inline-block w-0.5 h-[0.9em] bg-green-500 shrink-0"
              />
            )}
          </span>
        ))}

        {/* 첫 글자 시작 전의 커서 위치 처리 */}
        {!isCompleted && displayedText.length === 0 && (
          <motion.span
            animate={{ opacity: showCursor ? 1 : 0 }}
            className="absolute left-0 bottom-[0.1em] inline-block w-0.5 h-[0.9em] bg-green-500"
          />
        )}
      </span>
    </div>
  );
};
