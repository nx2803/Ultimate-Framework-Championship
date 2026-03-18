import type { Metadata } from 'next';
import { Inter, Geologica } from 'next/font/google';
import './globals.css';
import { ThemeProvider } from '../components/ThemeProvider';
import QueryClientProvider from '../components/providers/QueryClientProvider';

const inter = Inter({ subsets: ['latin'], variable: '--font-sans' });
const geologica = Geologica({ subsets: ['latin'], variable: '--font-geologica' });

export const metadata: Metadata = {
  title: 'UFC - Ultimate Framework Championship',
  description: 'Pure, Sophisticated Tech Trend Analysis',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={`${inter.variable} ${geologica.variable} font-sans antialiased selection:bg-foreground selection:text-background`}>
        <ThemeProvider
          attribute="data-theme"
          defaultTheme="light"
          enableSystem
          disableTransitionOnChange
        >
          <QueryClientProvider>
            {children}
          </QueryClientProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
