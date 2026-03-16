'use client';

import { QueryClient, QueryClientProvider as Provider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import React, { useState } from 'react';

export default function QueryClientProvider({ children }: { children: React.ReactNode }) {
  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 1000 * 60 * 5, // 5분 동안 데이터를 신선한 상태로 유지
            gcTime: 1000 * 60 * 30, // 30분 동안 캐시 보관
            retry: 1,
            refetchOnWindowFocus: false,
          },
        },
      })
  );

  return (
    <Provider client={queryClient}>
      {children}
      <ReactQueryDevtools initialIsOpen={false} />
    </Provider>
  );
}
