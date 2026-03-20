# ufcllm/ufcllm/services/gemini.py
import google.generativeai as genai
from ufcllm.settings import settings

class GeminiEngine:
    def __init__(self):
        # 2026년 기준 가장 효율적인 Flash 모델
        genai.configure(api_key=settings.gemini_api_key)
        self.model = genai.GenerativeModel('models/gemini-3-flash-preview')

    async def generate_tech_insight(self, category: str, stats_summary: str):
        prompt = f"""
        너는 UFC(Ultimate Framework Championship)라는 '코딩기술 스포츠 리그'의 전문 통합 중계진이야. 
        현재 '{category}' 부문의 깃허브 오픈소스 기술들 간의 명승부와 시즌 성적을 분석하려 해.
        
        최근 30일간의 트렌드 요약 데이터:
        {stats_summary}
        
        위 데이터를 바탕으로 마치 스포츠 뉴스나 생중계 해설처럼 전문적이면서도 생동감 있게 해설해줘.
        
        분석 가이드라인 (중요):
        1. **단순 수치보다 '성장세' 중시**: 누적 별점(Stars)이 높다고 무조건 챔피언이 아님. 최근 30일간의 '상승률'이나 '변화 폭'이 큰 기술을 이번 시즌의 주인공이나 위협적인 도전자로 묘사할 것.
        2. **지표별 역할 구분**: 
           - 별점(Stars) = 팬덤 및 인기도
           - 포크(Forks) = 실무 활용 및 기여도
           - 리포지토리 수(Repos) = 실질적인 시장 점유율
        3. **전통의 강자 vs 무서운 신예**: 지각 변동이 일어나는 지점(예: 점유율은 낮지만 별점 상승률이 압도적인 경우)을 정확히 포착할 것.
        
        조건:
        1. 말투: 스포츠 중계의 세련된 톤. (예: "React가 시장 지배력은 유지하고 있으나, 성장은 [기술명]이 압도적이군요!", "[기술명]이 별점 20% 급증이라는 경이로운 성적으로 상위권 진입을 노립니다.")
        2. 근거: 지표의 '변화량'이나 '퍼센트'를 언급하여 데이터의 신뢰성을 높일 것.
        3. 분량: 임팩트 있는 2~3줄 이내의 간결한 문장.
        4. 핵심: 이번 시즌 가장 높은 에너지를 보여준 'MVP(Rising Star)' 기술 1개를 반드시 선정하고 그 이유를 설명할 것.
        """
        response = await self.model.generate_content_async(prompt)
        return response.text.strip()