import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITransaction, NewTransaction } from '../transaction.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITransaction for edit and NewTransactionFormGroupInput for create.
 */
type TransactionFormGroupInput = ITransaction | PartialWithRequiredKeyOf<NewTransaction>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITransaction | NewTransaction> = Omit<T, 'createdDate'> & {
  createdDate?: string | null;
};

type TransactionFormRawValue = FormValueOf<ITransaction>;

type NewTransactionFormRawValue = FormValueOf<NewTransaction>;

type TransactionFormDefaults = Pick<NewTransaction, 'id' | 'createdDate'>;

type TransactionFormGroupContent = {
  id: FormControl<TransactionFormRawValue['id'] | NewTransaction['id']>;
  action: FormControl<TransactionFormRawValue['action']>;
  status: FormControl<TransactionFormRawValue['status']>;
  message: FormControl<TransactionFormRawValue['message']>;
  deviceInfo: FormControl<TransactionFormRawValue['deviceInfo']>;
  createdDate: FormControl<TransactionFormRawValue['createdDate']>;
  clientIp: FormControl<TransactionFormRawValue['clientIp']>;
  requestPath: FormControl<TransactionFormRawValue['requestPath']>;
  requestMethod: FormControl<TransactionFormRawValue['requestMethod']>;
  username: FormControl<TransactionFormRawValue['username']>;
  userId: FormControl<TransactionFormRawValue['userId']>;
  duration: FormControl<TransactionFormRawValue['duration']>;
};

export type TransactionFormGroup = FormGroup<TransactionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TransactionFormService {
  createTransactionFormGroup(transaction: TransactionFormGroupInput = { id: null }): TransactionFormGroup {
    const transactionRawValue = this.convertTransactionToTransactionRawValue({
      ...this.getFormDefaults(),
      ...transaction,
    });
    return new FormGroup<TransactionFormGroupContent>({
      id: new FormControl(
        { value: transactionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      action: new FormControl(transactionRawValue.action),
      status: new FormControl(transactionRawValue.status),
      message: new FormControl(transactionRawValue.message),
      deviceInfo: new FormControl(transactionRawValue.deviceInfo),
      createdDate: new FormControl(transactionRawValue.createdDate),
      clientIp: new FormControl(transactionRawValue.clientIp),
      requestPath: new FormControl(transactionRawValue.requestPath),
      requestMethod: new FormControl(transactionRawValue.requestMethod),
      username: new FormControl(transactionRawValue.username),
      userId: new FormControl(transactionRawValue.userId),
      duration: new FormControl(transactionRawValue.duration),
    });
  }

  getTransaction(form: TransactionFormGroup): ITransaction | NewTransaction {
    return this.convertTransactionRawValueToTransaction(form.getRawValue() as TransactionFormRawValue | NewTransactionFormRawValue);
  }

  resetForm(form: TransactionFormGroup, transaction: TransactionFormGroupInput): void {
    const transactionRawValue = this.convertTransactionToTransactionRawValue({ ...this.getFormDefaults(), ...transaction });
    form.reset(
      {
        ...transactionRawValue,
        id: { value: transactionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TransactionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
    };
  }

  private convertTransactionRawValueToTransaction(
    rawTransaction: TransactionFormRawValue | NewTransactionFormRawValue,
  ): ITransaction | NewTransaction {
    return {
      ...rawTransaction,
      createdDate: dayjs(rawTransaction.createdDate, DATE_TIME_FORMAT),
    };
  }

  private convertTransactionToTransactionRawValue(
    transaction: ITransaction | (Partial<NewTransaction> & TransactionFormDefaults),
  ): TransactionFormRawValue | PartialWithRequiredKeyOf<NewTransactionFormRawValue> {
    return {
      ...transaction,
      createdDate: transaction.createdDate ? transaction.createdDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
