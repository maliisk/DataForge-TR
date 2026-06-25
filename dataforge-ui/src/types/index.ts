export interface Transaction {
  id: number;
  amount: number;
  transactionType: string;
  createdAt: string;
}

export interface Account {
  id: number;
  iban: string;
  balance: number;
  currency: string;
  status: string;
  transactions?: Transaction[];
}

export interface CreditCard {
  id: number;
  cardNumber: string;
  cardLimit: number;
  currentDebt: number;
  expiryDate: string;
  cvv: string;
}

export interface Loan {
  id: number;
  totalAmount: number;
  remainingDebt: number;
  interestRate: number;
  installmentCount: number;
  startDate: string;
}

export interface Customer {
  id: number;
  customerType: "INDIVIDUAL" | "CORPORATE";
  tckn?: string;
  firstName?: string;
  lastName?: string;
  birthDate?: string;
  taxNumber?: string;
  companyName?: string;
  sector?: string;
  riskScore: number;
  accounts: Account[];
  creditCards?: CreditCard[];
  loans?: Loan[];
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface GenerateBatchRequest {
  customerCount: number;
  scenario?: "NORMAL_CUSTOMER" | "HIGH_RISK_CUSTOMER" | "AML_SUSPICIOUS";
}

export interface DashboardMetrics {
  totalCustomers: number;
  individualCount: number;
  corporateCount: number;
  totalLiquidity: number;
  totalDebt: number;
  averageRiskScore: number;
  riskSegmentation: Record<string, number>;
}
