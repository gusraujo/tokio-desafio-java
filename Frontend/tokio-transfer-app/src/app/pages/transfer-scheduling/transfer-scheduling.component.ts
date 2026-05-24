import { Component, OnInit, inject, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { ScheduledTransferService } from '../../services/scheduled-transfer.service';
import { ScheduledTransferResponse, ApiErrorResponse } from '../../models/transfer.models';

function differentAccountsValidator(group: AbstractControl): ValidationErrors | null {
  const source = group.get('sourceAccount')?.value;
  const dest = group.get('destinationAccount')?.value;
  if (source && dest && source === dest) {
    return { sameAccount: true };
  }
  return null;
}

// Erros da API que devem ser associados ao campo transferDate
const DATE_API_ERRORS = new Set([
  'no applicable fee for this transfer date',
]);

@Component({
  selector: 'app-transfer-scheduling',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transfer-scheduling.component.html',
  styleUrl: './transfer-scheduling.component.css',
})
export class TransferSchedulingComponent implements OnInit {
  private fb = inject(FormBuilder);
  private service = inject(ScheduledTransferService);
  private cdr = inject(ChangeDetectorRef);

  form!: FormGroup;
  transfers: ScheduledTransferResponse[] = [];
  lastTransfer: ScheduledTransferResponse | null = null;

  submitting = false;
  loadingList = false;
  listLoadError = false;
  successMessage = '';
  errorMessage = '';
  fieldErrors: Record<string, string> = {};

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        sourceAccount: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
        destinationAccount: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
        amount: [null, [Validators.required, Validators.min(0.01)]],
        transferDate: ['', Validators.required],
      },
      { validators: differentAccountsValidator }
    );

    this.loadTransfers();
  }

  loadTransfers(): void {
    this.loadingList = true;
    this.listLoadError = false;
    this.service.getAll().subscribe({
      next: (data) => {
        this.transfers = data;
        this.loadingList = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingList = false;
        this.listLoadError = true;
        this.cdr.markForCheck();
      },
    });
  }

  get sourceAccount() { return this.form.get('sourceAccount')!; }
  get destinationAccount() { return this.form.get('destinationAccount')!; }
  get amount() { return this.form.get('amount')!; }
  get transferDate() { return this.form.get('transferDate')!; }

  fieldError(field: string): string | null {
    const ctrl = this.form.get(field);
    if (!ctrl?.touched) return null;
    if (ctrl.errors?.['required']) return 'Campo obrigatório.';
    if (ctrl.errors?.['pattern']) return 'Deve conter exatamente 10 dígitos numéricos.';
    if (ctrl.errors?.['min']) return 'O valor deve ser maior que zero.';
    return this.fieldErrors[field] ?? null;
  }

  get sameAccountError(): boolean {
    return (
      !!this.form.errors?.['sameAccount'] &&
      this.destinationAccount.touched &&
      this.sourceAccount.touched
    );
  }

  onSubmit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.submitting = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.fieldErrors = {};

    const { sourceAccount, destinationAccount, amount, transferDate } = this.form.value;

    this.service.create({ sourceAccount, destinationAccount, amount, transferDate }).subscribe({
      next: (response) => {
        this.lastTransfer = response;
        this.successMessage = 'Transferência agendada com sucesso!';
        const { sourceAccount, destinationAccount } = this.form.value;
        this.form.reset();
        this.form.patchValue({ sourceAccount, destinationAccount });
        this.submitting = false;
        this.cdr.markForCheck();
        this.loadTransfers();
      },
      error: (err) => {
        this.submitting = false;
        this.handleApiError(err);
        this.cdr.markForCheck();
      },
    });
  }

  private handleApiError(err: any): void {
    if (!err.error || err.status === 0) {
      this.errorMessage = 'Serviço indisponível. Verifique se a API está em execução.';
      return;
    }

    const apiError: ApiErrorResponse = err.error;

    if (apiError.fieldErrors && Object.keys(apiError.fieldErrors).length > 0) {
      this.fieldErrors = { ...apiError.fieldErrors };
    }

    if (apiError.message) {
      this.errorMessage = this.translateError(apiError.message);

      // Erros de negócio relacionados à data são associados ao campo para feedback visual
      if (DATE_API_ERRORS.has(apiError.message)) {
        this.fieldErrors = { ...this.fieldErrors, transferDate: this.errorMessage };
      }
    } else if (!apiError.fieldErrors) {
      this.errorMessage = 'Ocorreu um erro inesperado. Tente novamente.';
    }
  }

  private translateError(msg: string): string {
    const map: Record<string, string> = {
      'no applicable fee for this transfer date':
        'Não há taxa aplicável para a data de transferência informada.',
    };
    return map[msg] ?? msg;
  }

  formatId(id: number): string {
    return `TR-${id}`;
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '-';
    const [year, month, day] = dateStr.split('-');
    return `${day}/${month}/${year}`;
  }
}
