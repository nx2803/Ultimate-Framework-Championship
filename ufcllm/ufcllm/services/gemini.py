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
        
        조건:
        1. 말투: 격투기 중심이 아닌, 축구, 농구, 야구 등 '종합 스포츠 중계'의 세련된 톤을 유지할 것. 
           (예: "React가 압도적인 점유율로 리그 단독 선두를 질주하고 있습니다.", "Vue가 별점 추격전에서 놀라운 뒷심을 발휘하며 역전의 발판을 마련 중이군요.")
        2. 근거: 별점(Stars), 포크(Forks) 등의 구체적인 '시즌 지표'를 언급하여 분석의 전문성을 높일 것.
        3. 분량: 임팩트 있는 2~3줄 이내의 간결한 문장.
        4. 언어: 반드시 한국어.
        5. 핵심: 가장 돋보이는 'MVP(Rising Star)' 기술 1개를 반드시 선정하고 그 이유를 설명할 것.
        """
        response = await self.model.generate_content_async(prompt)
        return response.text.strip()