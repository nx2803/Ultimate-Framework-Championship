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
 * 테마에 따른 최적화된 차트/UI 색상 반환
 * @param baseColor 기술의 기본 브랜드 색상 (HEX)
 * @param techName 기술 이름 (필요 시 보조 수단으로 사용)
 * @param theme 현재 테마 ('light' | 'dark')
 */
export function getThemeColor(baseColor: string, techName: string, theme: string | undefined): string {
  if (theme === 'dark') {
    const lowerColor = baseColor.toLowerCase();
    
    // 검은색이거나 너무 어두운 회색인 경우에만 흰색으로 반전
    const isBlackish = 
      lowerColor === '#000000' || 
      lowerColor === 'black' || 
      lowerColor === '#1a1a1a' ||
      lowerColor === '#111111';
    
    if (isBlackish) {
      return '#ffffff';
    }
  }
  return baseColor;
}
