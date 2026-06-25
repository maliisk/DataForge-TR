"use client";

import { useEffect, useState } from "react";
import {
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  Legend,
  BarChart,
  Bar,
  XAxis,
  YAxis,
} from "recharts";
import { DashboardMetrics } from "../types";

interface AnalyticsDashboardProps {
  metrics: DashboardMetrics | null;
}

export default function AnalyticsDashboard({
  metrics,
}: AnalyticsDashboardProps) {
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  if (!metrics || !isMounted) return null;

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat("tr-TR", {
      style: "currency",
      currency: "TRY",
      maximumFractionDigits: 0,
    }).format(value);
  };

  const pieData = Object.entries(metrics.riskSegmentation).map(
    ([key, val]) => ({
      name: key,
      value: val,
    }),
  );

  const COLORS = ["#10b981", "#f59e0b", "#f97316", "#ef4444"];

  const financialData = [
    { name: "Toplam Mevduat (Likidite)", Tutar: metrics.totalLiquidity },
    { name: "Toplam Kredi/Kart Riski", Tutar: metrics.totalDebt },
  ];

  return (
    <div className="space-y-6 mt-6">
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-white p-5 shadow-sm ring-1 ring-slate-200 rounded-lg">
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
            Toplam Aktif Müşteri
          </p>
          <p className="text-2xl font-bold text-slate-900 mt-2">
            {metrics.totalCustomers} Firma / Kişi
          </p>
          <p className="text-xs text-slate-400 mt-1">
            👤 {metrics.individualCount} Bireysel | 🏢 {metrics.corporateCount}{" "}
            Kurumsal
          </p>
        </div>

        <div className="bg-white p-5 shadow-sm ring-1 ring-slate-200 rounded-lg">
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
            Hazine Toplam Likidite
          </p>
          <p className="text-2xl font-bold text-emerald-600 mt-2">
            {formatCurrency(metrics.totalLiquidity)}
          </p>
          <p className="text-xs text-slate-400 mt-1">
            Vadesiz TRY Mevduat Havuzu
          </p>
        </div>

        <div className="bg-white p-5 shadow-sm ring-1 ring-slate-200 rounded-lg">
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
            Toplam Kredi / Kart Riski
          </p>
          <p className="text-2xl font-bold text-rose-600 mt-2">
            {formatCurrency(metrics.totalDebt)}
          </p>
          <p className="text-xs text-slate-400 mt-1">
            Piyasadaki Dağıtılmış Borç Yükü
          </p>
        </div>

        <div className="bg-white p-5 shadow-sm ring-1 ring-slate-200 rounded-lg">
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
            Sistem Ortalama Risk Skoru
          </p>
          <p className="text-2xl font-bold text-slate-900 mt-2">
            {metrics.averageRiskScore} / 100
          </p>
          <div className="w-full bg-slate-100 h-1.5 rounded-full mt-2 overflow-hidden">
            <div
              className={`h-full ${metrics.averageRiskScore > 70 ? "bg-red-500" : metrics.averageRiskScore > 40 ? "bg-yellow-500" : "bg-emerald-500"}`}
              style={{ width: `${metrics.averageRiskScore}%` }}
            />
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white p-6 shadow-sm ring-1 ring-slate-200 rounded-lg flex flex-col">
          <h4 className="text-sm font-semibold text-slate-900 border-b border-slate-100 pb-3 mb-4">
            📊 Veri Madenciliği: Müşteri Risk Segmentasyonu (Clustering)
          </h4>
          <div className="h-64 w-full flex items-center justify-center">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={80}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {pieData.map((entry, index) => (
                    <Cell
                      key={`cell-${index}`}
                      fill={COLORS[index % COLORS.length]}
                    />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => [`${value} Müşteri`, "Hacim"]} />
                <Legend verticalAlign="bottom" height={36} iconType="circle" />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="bg-white p-6 shadow-sm ring-1 ring-slate-200 rounded-lg flex flex-col">
          <h4 className="text-sm font-semibold text-slate-900 border-b border-slate-100 pb-3 mb-4">
            🏛️ Finansal Likidite ve Risk Dengesi (Asset-Liability)
          </h4>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                data={financialData}
                margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
              >
                <XAxis
                  dataKey="name"
                  stroke="#64748b"
                  fontSize={12}
                  tickLine={false}
                />
                <YAxis
                  tickFormatter={(v) => `${v / 1000}k ₺`}
                  stroke="#64748b"
                  fontSize={12}
                  tickLine={false}
                />
                <Tooltip
                  formatter={(value: any) => [formatCurrency(value), "Tutar"]}
                />
                <Bar dataKey="Tutar" radius={[4, 4, 0, 0]}>
                  <Cell fill="#10b981" />
                  <Cell fill="#ef4444" />
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}
