export interface ScheduleTransferRequest {
  sourceAccount: string;
  destinationAccount: string;
  amount: number;
  transferDate: string;
}

export interface ScheduledTransferResponse {
  id: number;
  sourceAccount: string;
  destinationAccount: string;
  amount: number;
  fee: number;
  totalAmount: number;
  transferDate: string;
  schedulingDate: string;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: Record<string, string>;
}
