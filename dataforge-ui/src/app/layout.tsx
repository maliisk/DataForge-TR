import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "DataForge TR ",
  description: "BDDK Uyumlu Sentetik Veri Üretim Motoru",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="tr">
      <body
        className={`${inter.className} bg-slate-50 text-slate-900 antialiased min-h-screen flex flex-col`}
        suppressHydrationWarning
      >
        <header className="bg-white border-b border-slate-200 px-8 py-6 flex flex-col items-center justify-center shadow-sm">
          <div className="flex items-center gap-3">
            <div className="bg-slate-800 text-white font-extrabold text-xl rounded-md px-2.5 py-1 tracking-wider shadow-inner">
              DF
            </div>
            <h1 className="text-3xl font-bold text-slate-800 tracking-tight">
              DataForge<span className="text-blue-600">TR</span>
            </h1>
          </div>
          <p className="text-xs font-semibold text-slate-500 mt-2 uppercase tracking-widest">
            Enterprise QA Mock Data Engine
          </p>
        </header>

        <main className="flex-1 max-w-7xl w-full mx-auto p-8">{children}</main>
      </body>
    </html>
  );
}
