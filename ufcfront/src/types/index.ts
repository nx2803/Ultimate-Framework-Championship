export interface TechList {
  id: number;
  name: string;
  category: string;
  topicKeyword: string;
  githubRepoId: number | null;
  color: string;
  logoUrl: string;
  createdAt: string;
}

export interface TechStats {
  techId: number;
  techName: string;
  collectedAt: string;
  starCount: number;
  forkCount: number;
  repoCount: number;
  marketShare: number;
  starGrowth: number | null;
}

export type Period = 7 | 30 | 90;
