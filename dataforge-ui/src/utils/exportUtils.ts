import { Customer } from "../types";

const triggerDownload = (blob: Blob, filename: string) => {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
};

export const ExportService = {
  downloadJSON: (data: Customer[]) => {
    const blob = new Blob([JSON.stringify(data, null, 2)], {
      type: "application/json",
    });
    triggerDownload(blob, "dataforge_mock_data.json");
  },

  downloadCSV: (data: Customer[]) => {
    const headers = [
      "Musteri_ID",
      "TCKN",
      "Ad",
      "Soyad",
      "Dogum_Tarihi",
      "Risk_Skoru",
      "IBAN",
      "Bakiye",
    ];
    const rows = data.map((c) => {
      const iban =
        c.accounts && c.accounts.length > 0 ? c.accounts[0].iban : "YOK";
      const balance =
        c.accounts && c.accounts.length > 0 ? c.accounts[0].balance : 0;
      return [
        c.id,
        c.tckn,
        c.firstName,
        c.lastName,
        c.birthDate,
        c.riskScore,
        iban,
        balance,
      ].join(";");
    });

    const csvContent = "\uFEFF" + [headers.join(";"), ...rows].join("\n");
    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    triggerDownload(blob, "dataforge_mock_data.csv");
  },

  downloadSQL: (data: Customer[]) => {
    const statements = data.map((c) => {
      return `INSERT INTO customers (id, tckn, first_name, last_name, birth_date, risk_score) VALUES (${c.id}, '${c.tckn}', '${c.firstName}', '${c.lastName}', '${c.birthDate}', ${c.riskScore});`;
    });
    const blob = new Blob([statements.join("\n")], { type: "text/plain" });
    triggerDownload(blob, "dataforge_insert_scripts.sql");
  },
};
