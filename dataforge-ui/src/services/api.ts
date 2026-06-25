import axios from "axios";
import {
  ApiResponse,
  Customer,
  PageResponse,
  GenerateBatchRequest,
  DashboardMetrics,
} from "../types";

const apiClient = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

export const GeneratorService = {
  generateBatch: async (
    data: GenerateBatchRequest,
  ): Promise<ApiResponse<void>> => {
    const response = await apiClient.post<ApiResponse<void>>(
      "/generator/batch",
      data,
    );
    return response.data;
  },
};

export const CustomerService = {
  getCustomers: async (
    page: number,
    size: number,
  ): Promise<ApiResponse<PageResponse<Customer>>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<Customer>>>(
      `/customers?page=${page}&size=${size}`,
    );
    return response.data;
  },
  clearDatabase: async (): Promise<ApiResponse<void>> => {
    const response =
      await apiClient.delete<ApiResponse<void>>("/customers/clear");
    return response.data;
  },
};

export const AnalyticsService = {
  getDashboardMetrics: async (): Promise<ApiResponse<DashboardMetrics>> => {
    const response = await apiClient.get<ApiResponse<DashboardMetrics>>(
      "/analytics/dashboard",
    );
    return response.data;
  },
};
