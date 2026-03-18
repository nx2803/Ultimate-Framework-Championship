/**
 * Simple Icons CDN을 통해 로고를 가져올 때 다크모드 대응을 위한 유틸리티
 */

// 검은색이 기본인 기술 로고 리스트 (다크모드에서 /white 추가 필요)
const BLACK_LOGO_TECHS = [
  'Next.js',
  'java',
  'rust',
  'angular',
  'remix',
  'express',
  'expo',
  'prisma'
];

/**
 * 해당 기술이 검은색 로고를 사용하는지 여부 확인
 */
export function isBlackLogoTech(techName: string, originalUrl?: string | null): boolean {
  return BLACK_LOGO_TECHS.some(name =>
    techName.toLowerCase() === name.toLowerCase() ||
    (originalUrl && originalUrl.toLowerCase().endsWith(name.toLowerCase().replace('.', 'dot')))
  );
}

/**
 * 테마에 따른 최적화된 로고 URL 반환
 */
export function getLogoUrl(originalUrl: string | null | undefined, techName: string, theme: string | undefined): string {
  if (!originalUrl) return '';

  if (originalUrl.includes('cdn.simpleicons.org')) {
    if (theme === 'dark' && isBlackLogoTech(techName, originalUrl)) {
      if (!originalUrl.split('/').pop()?.includes('?')) {
        return `${originalUrl}/white`;
      }
    }
  }

  return originalUrl;
}

/**
 * HEX 색상의 상대 밝기(relative luminance, 0~1) 계산
 * 0에 가까울수록 어둡고, 1에 가까울수록 밝음
 */
function getLuminance(hex: string): number {
  const clean = hex.replace('#', '');
  if (clean.length !== 6) return 0.5; // 파싱 불가 시 중간값

  const r = parseInt(clean.slice(0, 2), 16) / 255;
  const g = parseInt(clean.slice(2, 4), 16) / 255;
  const b = parseInt(clean.slice(4, 6), 16) / 255;

  // sRGB → linear 변환 후 luminance 계산 (WCAG 2.x standard)
  const toLinear = (c: number) => c <= 0.04045 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
  return 0.2126 * toLinear(r) + 0.7152 * toLinear(g) + 0.0722 * toLinear(b);
}

/**
 * 어두운 HEX 색상을 다크모드용으로 밝게 보정
 * - 거의 검정(luminance < 0.01): 흰색으로 반환
 * - 어두운 컬러(luminance < 0.08): HSL 밝기를 올려서 채도는 유지
 */
function lightenForDark(hex: string): string {
  const clean = hex.replace('#', '');
  const r = parseInt(clean.slice(0, 2), 16);
  const g = parseInt(clean.slice(2, 4), 16);
  const b = parseInt(clean.slice(4, 6), 16);

  // RGB → HSL
  const rn = r / 255, gn = g / 255, bn = b / 255;
  const max = Math.max(rn, gn, bn), min = Math.min(rn, gn, bn);
  const l = (max + min) / 2;
  const d = max - min;
  const s = d === 0 ? 0 : d / (1 - Math.abs(2 * l - 1));
  let h = 0;
  if (d !== 0) {
    switch (max) {
      case rn: h = ((gn - bn) / d) % 6; break;
      case gn: h = (bn - rn) / d + 2; break;
      case bn: h = (rn - gn) / d + 4; break;
    }
    h = Math.round(h * 60);
    if (h < 0) h += 360;
  }

  // 밝기를 다크모드용으로 올림 (최소 60% 밝기 보장)
  const newL = Math.max(l, 0.60);

  // HSL → RGB
  const c2 = (1 - Math.abs(2 * newL - 1)) * s;
  const x = c2 * (1 - Math.abs((h / 60) % 2 - 1));
  const m = newL - c2 / 2;
  let r2 = 0, g2 = 0, b2 = 0;
  if      (h < 60)  { r2 = c2; g2 = x;  b2 = 0;  }
  else if (h < 120) { r2 = x;  g2 = c2; b2 = 0;  }
  else if (h < 180) { r2 = 0;  g2 = c2; b2 = x;  }
  else if (h < 240) { r2 = 0;  g2 = x;  b2 = c2; }
  else if (h < 300) { r2 = x;  g2 = 0;  b2 = c2; }
  else              { r2 = c2; g2 = 0;  b2 = x;  }

  const toHex = (v: number) => Math.round((v + m) * 255).toString(16).padStart(2, '0');
  return `#${toHex(r2)}${toHex(g2)}${toHex(b2)}`;
}

/**
 * 밝은 HEX 색상을 라이트모드용으로 어둡게 보정
 * - 밝은 컬러(luminance > 0.70): HSL 밝기를 낮춰서 채도는 유지
 */
function darkenForLight(hex: string): string {
  const clean = hex.replace('#', '');
  const r = parseInt(clean.slice(0, 2), 16);
  const g = parseInt(clean.slice(2, 4), 16);
  const b = parseInt(clean.slice(4, 6), 16);

  // RGB → HSL
  const rn = r / 255, gn = g / 255, bn = b / 255;
  const max = Math.max(rn, gn, bn), min = Math.min(rn, gn, bn);
  const l = (max + min) / 2;
  const d = max - min;
  const s = d === 0 ? 0 : d / (1 - Math.abs(2 * l - 1));
  let h = 0;
  if (d !== 0) {
    switch (max) {
      case rn: h = ((gn - bn) / d) % 6; break;
      case gn: h = (bn - rn) / d + 2; break;
      case bn: h = (rn - gn) / d + 4; break;
    }
    h = Math.round(h * 60);
    if (h < 0) h += 360;
  }

  // 밝기를 화이트모드용으로 낮춤 (최대 45% 밝기로 한정)
  const newL = Math.min(l, 0.45);

  // HSL → RGB
  const c2 = (1 - Math.abs(2 * newL - 1)) * s;
  const x = c2 * (1 - Math.abs((h / 60) % 2 - 1));
  const m = newL - c2 / 2;
  let r2 = 0, g2 = 0, b2 = 0;
  if      (h < 60)  { r2 = c2; g2 = x;  b2 = 0;  }
  else if (h < 120) { r2 = x;  g2 = c2; b2 = 0;  }
  else if (h < 180) { r2 = 0;  g2 = c2; b2 = x;  }
  else if (h < 240) { r2 = 0;  g2 = x;  b2 = c2; }
  else if (h < 300) { r2 = x;  g2 = 0;  b2 = c2; }
  else              { r2 = c2; g2 = 0;  b2 = x;  }

  const toHex = (v: number) => Math.round((v + m) * 255).toString(16).padStart(2, '0');
  return `#${toHex(r2)}${toHex(g2)}${toHex(b2)}`;
}

/**
 * 테마에 따른 최적화된 차트/UI 색상 반환
 * - 다크모드: 어두운 색상(luminance < 0.08)을 자동으로 밝게 보정
 * - 라이트모드: 밝은 색상(luminance > 0.70)을 자동으로 어둡게 보정
 */
export function getThemeColor(baseColor: string, techName: string, theme: string | undefined): string {
  const luminance = getLuminance(baseColor);

  if (theme === 'dark') {
    if (luminance < 0.008) return '#ffffff'; // 거의 완전한 검정 → 흰색으로
    if (luminance < 0.08) return lightenForDark(baseColor); // 어두운 컬러 → 밝게
  } else {
    if (luminance > 0.95) return '#000000'; // 거의 완전한 흰색 → 검정으로
    if (luminance > 0.70) return darkenForLight(baseColor); // 밝은 컬러(노란색 등) → 어둡게
  }

  return baseColor;
}
