"use client";

import { useState, useEffect } from "react";
import {
  GeneratorService,
  CustomerService,
  AnalyticsService,
} from "../services/api";
import { ApiResponse, Customer, DashboardMetrics } from "../types";
import CustomerTable from "../components/CustomerTable";
import AnalyticsDashboard from "../components/AnalyticsDashboard";

export default function Home() {
  const [customerCount, setCustomerCount] = useState<number>(50);
  const [scenario, setScenario] = useState<string>("NORMAL_CUSTOMER");
  const [generating, setGenerating] = useState<boolean>(false);
  const [response, setResponse] = useState<ApiResponse<void> | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [customers, setCustomers] = useState<Customer[]>([]);
  const [tableLoading, setTableLoading] = useState<boolean>(true);
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);

  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);

  const loadData = async (currentPage: number) => {
    setTableLoading(true);
    try {
      const resTable = await CustomerService.getCustomers(currentPage, 10);
      setCustomers(resTable.data.content);
      setTotalPages(resTable.data.totalPages);

      const resMetrics = await AnalyticsService.getDashboardMetrics();
      setMetrics(resMetrics.data);
    } catch (err) {
      console.error("Veriler yüklenirken hata oluştu:", err);
    } finally {
      setTableLoading(false);
    }
  };

  useEffect(() => {
    loadData(page);
  }, [page]);

  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault();
    setGenerating(true);
    setError(null);
    setResponse(null);

    try {
      const res = await GeneratorService.generateBatch({
        customerCount,
        scenario: scenario as any,
      });
      setResponse(res);

      setTimeout(() => {
        loadData(0);
        setPage(0);
      }, 2000);
    } catch (err: any) {
      setError(
        err.response?.data?.message || "Sunucuya bağlanırken bir hata oluştu.",
      );
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto mt-6 px-4">
      <div className="bg-white shadow-sm ring-1 ring-slate-200 sm:rounded-lg p-8">
        <div className="border-b border-slate-200 pb-5 mb-6">
          <h2 className="text-xl font-semibold leading-6 text-slate-900">
            Toplu Sentetik Veri Üretimi
          </h2>
        </div>

        <form onSubmit={handleGenerate} className="space-y-6">
          <div className="flex flex-col sm:flex-row items-end gap-4">
            <div className="flex-1">
              <label
                htmlFor="customerCount"
                className="block text-sm font-medium leading-6 text-slate-900"
              >
                Üretilecek Müşteri Sayısı
              </label>
              <div className="mt-2">
                <input
                  type="number"
                  id="customerCount"
                  min="1"
                  max="10000"
                  value={customerCount}
                  onChange={(e) => setCustomerCount(Number(e.target.value))}
                  className="block w-full rounded-md border-0 py-2 px-3 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-blue-600 sm:text-sm sm:leading-6"
                  required
                />
              </div>
            </div>

            <div className="flex-1">
              <label
                htmlFor="scenario"
                className="block text-sm font-medium leading-6 text-slate-900"
              >
                Üretim Senaryosu (Fraud Zekası)
              </label>
              <div className="mt-2">
                <select
                  id="scenario"
                  value={scenario}
                  onChange={(e) => setScenario(e.target.value)}
                  className="block w-full rounded-md border-0 py-2 px-3 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 focus:ring-2 focus:ring-blue-600 sm:text-sm sm:leading-6"
                >
                  <option value="NORMAL_CUSTOMER">
                    Normal Profil (Rastgele Harcamalar)
                  </option>
                  <option value="HIGH_RISK_CUSTOMER">
                    Yüksek Riskli Müşteri
                  </option>
                  <option value="AML_SUSPICIOUS">
                    Kara Para Aklama (Smurfing Simülasyonu)
                  </option>
                </select>
              </div>
            </div>

            <button
              type="submit"
              disabled={generating}
              className="rounded-md bg-slate-800 px-8 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {generating ? "İletiliyor..." : "Üretimi Başlat"}
            </button>
          </div>
        </form>

        {error && (
          <div className="mt-4 p-3 rounded-md bg-red-50 text-sm text-red-600">
            {error}
          </div>
        )}
        {response && (
          <div className="mt-4 p-3 rounded-md bg-emerald-50 text-sm text-emerald-700 font-medium">
            {response.message}
          </div>
        )}
      </div>

      <AnalyticsDashboard metrics={metrics} />

      <CustomerTable
        customers={customers}
        loading={tableLoading}
        onRefresh={() => loadData(page)}
        page={page}
        totalPages={totalPages}
        onPageChange={(newPage) => setPage(newPage)}
      />
    </div>
  );
}
