import axios from 'axios';
import { TechList, TechStats, Period } from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const techApi = {
  // 기술 목록 조회
  getAllTechs: async (): Promise<TechList[]> => {
    const { data } = await api.get<TechList[]>('/techs');
    return data;
  },
  
  // 차트 데이터 조회 (단일)
  getTechStats: async (techId: number, days: Period = 30): Promise<TechStats[]> => {
    const { data } = await api.get<TechStats[]>(`/charts/${techId}`, {
      params: { days },
    });
    return data;
  },

  // 차트 데이터 조회 (카테고리별 다중 비교)
  getCategoryStats: async (category: string, days: Period = 30): Promise<TechStats[]> => {
    const { data } = await api.get<TechStats[]>(`/charts/category/${category}`, {
      params: { days },
    });
    return data;
  },
};

export default api;
