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
        너는 UFC(Ultimate Framework Championship)의 메인 해설위원이야. 
        현재 '{category}' 부문의 깃허브 오픈소스 기술들간의 전황을 분석하려 해.
        
        최근 30일간의 트렌드 요약 데이터:
        {stats_summary}
        
        위 데이터를 바탕으로 현재의 상황을 전문적이면서도 생동감 있게 해설해줘.
        
        조건:
        1. 말투: 너무 과하지는 않되, 스포츠 중계의 전문성과 적절한 긴장감을 유지할 것. (예: "React가 여전히 챔피언벨트를 차지하고 있습니다.", "Vue의 성장세가 무섭습니다.")
        2. 근거: 기술적 지표(별점, 포크 등)의 변화를 반드시 언급하여 신뢰도를 높일 것.
        3. 분량: 임팩트 있는 2~3줄 이내의 문장으로 작성.
        4. 언어: 반드시 한국어.
        5. 핵심: 가장 돋보이는 기술(Rising Star) 1개를 반드시 언급하며 이유를 설명할 것.
        """
        response = await self.model.generate_content_async(prompt)
        return response.text.strip()