import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ScheduleTransferRequest, ScheduledTransferResponse } from '../models/transfer.models';

@Injectable({ providedIn: 'root' })
export class ScheduledTransferService {
  private http = inject(HttpClient);
  private readonly baseUrl = '/api/scheduled-transfers';

  getAll(): Observable<ScheduledTransferResponse[]> {
    return this.http.get<ScheduledTransferResponse[]>(this.baseUrl);
  }

  create(request: ScheduleTransferRequest): Observable<ScheduledTransferResponse> {
    return this.http.post<ScheduledTransferResponse>(this.baseUrl, request);
  }
}
