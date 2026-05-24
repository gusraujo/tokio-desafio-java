import { Routes } from '@angular/router';
import { TransferSchedulingComponent } from './pages/transfer-scheduling/transfer-scheduling.component';

export const routes: Routes = [
  { path: '', component: TransferSchedulingComponent },
  { path: '**', redirectTo: '' },
];
