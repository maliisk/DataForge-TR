import { useState, useMemo } from "react";
import { Customer } from "../types";
import { ExportService } from "../utils/exportUtils";

interface CustomerTableProps {
  customers: Customer[];
  loading: boolean;
  onRefresh: () => void;
  page: number;
  totalPages: number;
  onPageChange: (newPage: number) => void;
}

export default function CustomerTable({
  customers,
  loading,
  onRefresh,
  page,
  totalPages,
  onPageChange,
}: CustomerTableProps) {
  const [sortConfig, setSortConfig] = useState<{
    key: string;
    direction: "asc" | "desc";
  } | null>(null);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("tr-TR", {
      style: "currency",
      currency: "TRY",
    }).format(amount);
  };

  const sortedCustomers = useMemo(() => {
    let sortableItems = [...customers];
    if (sortConfig !== null) {
      sortableItems.sort((a, b) => {
        let aValue: any;
        let bValue: any;

        if (sortConfig.key === "balance") {
          aValue = a.accounts?.[0]?.balance || 0;
          bValue = b.accounts?.[0]?.balance || 0;
        } else if (sortConfig.key === "name") {
          aValue =
            a.customerType === "CORPORATE"
              ? a.companyName
              : `${a.firstName} ${a.lastName}`;
          bValue =
            b.customerType === "CORPORATE"
              ? b.companyName
              : `${b.firstName} ${b.lastName}`;
        } else {
          aValue = (a as any)[sortConfig.key];
          bValue = (b as any)[sortConfig.key];
        }

        if (aValue < bValue) return sortConfig.direction === "asc" ? -1 : 1;
        if (aValue > bValue) return sortConfig.direction === "asc" ? 1 : -1;
        return 0;
      });
    }
    return sortableItems;
  }, [customers, sortConfig]);

  const requestSort = (key: string) => {
    let direction: "asc" | "desc" = "asc";
    if (
      sortConfig &&
      sortConfig.key === key &&
      sortConfig.direction === "asc"
    ) {
      direction = "desc";
    }
    setSortConfig({ key, direction });
  };

  const getSortIcon = (key: string) => {
    if (!sortConfig || sortConfig.key !== key)
      return (
        <span className="ml-1 text-slate-300 opacity-0 group-hover:opacity-100 transition-opacity">
          ↕
        </span>
      );
    return sortConfig.direction === "asc" ? (
      <span className="ml-1 text-blue-600">↑</span>
    ) : (
      <span className="ml-1 text-blue-600">↓</span>
    );
  };

  return (
    <div className="bg-white shadow-sm ring-1 ring-slate-200 sm:rounded-lg mt-8 overflow-hidden w-full transition-all duration-300 hover:shadow-md">
      <div className="px-4 py-5 border-b border-slate-200 flex flex-col xl:flex-row justify-between items-start xl:items-center bg-slate-50 gap-4">
        <h3 className="text-base font-semibold leading-6 text-slate-900 whitespace-nowrap">
          Son Üretilen Müşteriler (QA Havuzu)
        </h3>

        <div className="flex flex-wrap items-center gap-3 w-full xl:w-auto justify-start xl:justify-end">
          <div className="flex bg-white shadow-sm rounded-md border border-slate-200">
            <button
              onClick={() => ExportService.downloadCSV(sortedCustomers)}
              disabled={sortedCustomers.length === 0}
              className="px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50 border-r border-slate-200 disabled:opacity-50 transition-colors"
            >
              Sayfa CSV
            </button>
            <button
              onClick={() => ExportService.downloadJSON(sortedCustomers)}
              disabled={sortedCustomers.length === 0}
              className="px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50 border-r border-slate-200 disabled:opacity-50 transition-colors"
            >
              JSON
            </button>
            <button
              onClick={() => ExportService.downloadSQL(sortedCustomers)}
              disabled={sortedCustomers.length === 0}
              className="px-3 py-1.5 text-xs font-medium text-blue-700 hover:bg-blue-50 disabled:opacity-50 transition-colors"
            >
              SQL Script
            </button>
          </div>
          <button
            onClick={() =>
              window.open(
                "http://localhost:8080/api/v1/customers/export/csv",
                "_blank",
              )
            }
            className="inline-flex items-center rounded-md bg-emerald-50 px-3 py-1.5 text-sm font-semibold text-emerald-700 shadow-sm ring-1 ring-inset ring-emerald-600/20 hover:bg-emerald-100 transition-colors"
          >
            Tümünü Export Et (CSV)
          </button>
          <button
            onClick={onRefresh}
            disabled={loading}
            className="inline-flex items-center rounded-md bg-white px-3 py-1.5 text-sm font-semibold text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 hover:bg-slate-50 disabled:opacity-50 transition-colors"
          >
            {loading ? "Yenileniyor..." : "🔄 Yenile"}
          </button>
          <button
            onClick={async () => {
              if (
                window.confirm(
                  "DİKKAT! Tüm test veritabanı silinecek. Emin misiniz?",
                )
              ) {
                const { CustomerService } = await import("../services/api");
                await CustomerService.clearDatabase();
                onRefresh();
              }
            }}
            className="inline-flex items-center rounded-md bg-red-50 px-3 py-1.5 text-sm font-semibold text-red-700 shadow-sm ring-1 ring-inset ring-red-600/20 hover:bg-red-100 transition-colors"
          >
            🗑️ Havuzu Sıfırla
          </button>
        </div>
      </div>

      <div className="w-full overflow-x-auto">
        <table className="w-full text-left border-collapse">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              <th
                scope="col"
                className="px-4 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider w-1/6"
              >
                Tip / Kimlik
              </th>

              <th
                scope="col"
                onClick={() => requestSort("name")}
                className="px-4 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider w-1/4 cursor-pointer group hover:bg-slate-100 transition-colors select-none"
              >
                Müşteri Adı / Ünvan {getSortIcon("name")}
              </th>

              <th
                scope="col"
                className="px-4 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider"
              >
                Finansal Ürünler
              </th>

              <th
                scope="col"
                onClick={() => requestSort("riskScore")}
                className="px-4 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider w-24 cursor-pointer group hover:bg-slate-100 transition-colors select-none"
              >
                Risk Skoru {getSortIcon("riskScore")}
              </th>

              <th
                scope="col"
                onClick={() => requestSort("balance")}
                className="px-4 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider text-right w-1/5 cursor-pointer group hover:bg-slate-100 transition-colors select-none"
              >
                Ana Hesap Bakiyesi {getSortIcon("balance")}
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-slate-100">
            {sortedCustomers.length === 0 && !loading && (
              <tr>
                <td
                  colSpan={5}
                  className="px-4 py-12 text-center text-sm text-slate-500"
                >
                  Henüz veri bulunmuyor.
                </td>
              </tr>
            )}
            {sortedCustomers.map((customer) => {
              const isCorp = customer.customerType === "CORPORATE";
              const name = isCorp
                ? customer.companyName
                : `${customer.firstName} ${customer.lastName}`;
              const identity = isCorp ? customer.taxNumber : customer.tckn;
              const subText = isCorp
                ? `Sektör: ${customer.sector}`
                : `Doğum: ${customer.birthDate}`;
              const hasCreditCard =
                customer.creditCards && customer.creditCards.length > 0;
              const hasLoan = customer.loans && customer.loans.length > 0;

              return (
                <tr
                  key={customer.id}
                  className="hover:bg-slate-50 transition-colors"
                >
                  <td className="px-4 py-4 align-top">
                    <div className="flex flex-col gap-1 items-start">
                      <span
                        className={`inline-flex items-center rounded-md px-2 py-0.5 text-xs font-medium ring-1 ring-inset ${isCorp ? "bg-indigo-50 text-indigo-700 ring-indigo-600/20" : "bg-blue-50 text-blue-700 ring-blue-600/20"}`}
                      >
                        {isCorp ? "🏢 Kurumsal" : "👤 Bireysel"}
                      </span>
                      <div className="text-sm font-medium text-slate-900 mt-1">
                        {identity}
                      </div>
                      <div className="text-xs text-slate-500">
                        ID: {customer.id}
                      </div>
                    </div>
                  </td>
                  <td className="px-4 py-4 align-top">
                    <div className="text-sm text-slate-900 font-medium leading-tight">
                      {name}
                    </div>
                    <div className="text-xs text-slate-500 mt-1">{subText}</div>
                  </td>
                  <td className="px-4 py-4 align-top">
                    <div className="flex flex-wrap gap-2">
                      <span className="inline-flex items-center rounded-md bg-slate-100 px-2 py-1 text-xs font-medium text-slate-600 ring-1 ring-inset ring-slate-500/10">
                        🏦 {customer.accounts?.length || 0} Hesap
                      </span>
                      {hasCreditCard && (
                        <span className="inline-flex items-center rounded-md bg-amber-50 px-2 py-1 text-xs font-medium text-amber-700 ring-1 ring-inset ring-amber-600/20">
                          💳 {customer.creditCards?.length} Kart
                        </span>
                      )}
                      {hasLoan && (
                        <span className="inline-flex items-center rounded-md bg-rose-50 px-2 py-1 text-xs font-medium text-rose-700 ring-1 ring-inset ring-rose-600/20">
                          💰 Kredili
                        </span>
                      )}
                    </div>
                  </td>
                  <td className="px-4 py-4 align-top">
                    <span
                      className={`inline-flex items-center rounded-md px-2 py-1 text-xs font-medium ring-1 ring-inset ${customer.riskScore > 84 ? "bg-red-50 text-red-700 ring-red-600/20" : customer.riskScore > 50 ? "bg-yellow-50 text-yellow-800 ring-yellow-600/20" : "bg-emerald-50 text-emerald-700 ring-emerald-600/20"}`}
                    >
                      {customer.riskScore} / 100
                    </span>
                  </td>
                  <td className="px-4 py-4 align-top text-right">
                    <div className="flex flex-col items-end">
                      <span className="text-sm font-semibold text-slate-900">
                        {customer.accounts && customer.accounts.length > 0
                          ? formatCurrency(customer.accounts[0].balance)
                          : "0,00 ₺"}
                      </span>
                      {customer.accounts && customer.accounts.length > 0 && (
                        <span className="text-xs text-slate-500 font-mono font-normal mt-1">
                          {customer.accounts[0].iban}
                        </span>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <div className="px-4 py-3 border-t border-slate-200 flex items-center justify-between bg-slate-50">
        <div className="text-sm text-slate-500">
          Sayfa <span className="font-medium text-slate-900">{page + 1}</span> /{" "}
          <span className="font-medium text-slate-900">
            {totalPages === 0 ? 1 : totalPages}
          </span>
        </div>
        <div className="flex space-x-2">
          <button
            onClick={() => onPageChange(page - 1)}
            disabled={page === 0 || loading}
            className="px-3 py-1 text-sm border border-slate-300 rounded-md bg-white text-slate-700 hover:bg-slate-50 disabled:opacity-50 transition-colors"
          >
            Önceki
          </button>
          <button
            onClick={() => onPageChange(page + 1)}
            disabled={page >= totalPages - 1 || loading}
            className="px-3 py-1 text-sm border border-slate-300 rounded-md bg-white text-slate-700 hover:bg-slate-50 disabled:opacity-50 transition-colors"
          >
            Sonraki
          </button>
        </div>
      </div>
    </div>
  );
}
