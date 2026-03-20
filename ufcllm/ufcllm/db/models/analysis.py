from datetime import datetime
from sqlalchemy import BigInteger, Column, DateTime, String, Text
from ufcllm.db.base import Base

class TechAnalysis(Base):
    """AI generated analysis for a tech category."""

    __tablename__ = "tech_analysis"
    __table_args__ = {"schema": "ufc"}

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    category = Column(String(50), nullable=False, index=True)
    insight = Column(Text, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)

    def __repr__(self) -> str:
        return f"<TechAnalysis(id={self.id}, category={self.category})>"
