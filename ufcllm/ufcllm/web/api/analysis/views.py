from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import select, desc, text
from sqlalchemy.ext.asyncio import AsyncSession
from ufcllm.db.dependencies import get_db_session
from ufcllm.db.models.analysis import TechAnalysis
from ufcllm.services.gemini import GeminiEngine
from typing import List, Any
import json
from datetime import datetime, timedelta

# Note: We need to define the TechStats table mapping here or in models
# Since TechStats was created by Spring, we'll use a reflected/mapped table or a basic model
from sqlalchemy import Table, MetaData
from ufcllm.db.meta import meta

router = APIRouter()

@router.post("/analyze")
async def analyze_category(
    category: str, 
    engine: GeminiEngine = Depends(),
    db: AsyncSession = Depends(get_db_session)
):
    """
    Analyzes the trend for a given category and stores the insight.
    """
    try:
        # 1. Fetch tech_stats for the category (Last 30 days)
        # Using raw SQL or a dynamic table for 'ufc.tech_stats' since it's shared
        query = text("""
            SELECT tl.name, ts.collected_at, ts.star_count, ts.fork_count, ts.repo_count
            FROM ufc.tech_stats ts
            JOIN ufc.tech_list tl ON ts.tech_id = tl.id
            WHERE tl.category = :category
            AND ts.collected_at > NOW() - INTERVAL '30 days'
            ORDER BY ts.collected_at DESC
        """)
        result = await db.execute(query, {"category": category})
        rows = result.fetchall()

        if not rows:
            return {"status": "skipped", "message": "No data found for this category."}

        # 2. Format summary for Gemini
        summary_data = []
        for r in rows[:20]: # Latest 20 records for context
            summary_data.append({
                "tech": r[0],
                "date": str(r[1]),
                "stars": r[2],
                "forks": r[3]
            })
        
        stats_summary = json.dumps(summary_data, ensure_ascii=False)

        # 3. Generate Insight
        insight = await engine.generate_tech_insight(category, stats_summary)

        # 4. Save to TechAnalysis
        new_analysis = TechAnalysis(
            category=category,
            insight=insight
        )
        db.add(new_analysis)
        await db.commit()

        return {"status": "success", "insight": insight}
    except Exception as e:
        await db.rollback()
        raise HTTPException(status_code=500, detail=str(e))