import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITokenHistory, NewTokenHistory } from '../token-history.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITokenHistory for edit and NewTokenHistoryFormGroupInput for create.
 */
type TokenHistoryFormGroupInput = ITokenHistory | PartialWithRequiredKeyOf<NewTokenHistory>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITokenHistory | NewTokenHistory> = Omit<T, 'createdDate' | 'updatedDate'> & {
  createdDate?: string | null;
  updatedDate?: string | null;
};

type TokenHistoryFormRawValue = FormValueOf<ITokenHistory>;

type NewTokenHistoryFormRawValue = FormValueOf<NewTokenHistory>;

type TokenHistoryFormDefaults = Pick<NewTokenHistory, 'id' | 'createdDate' | 'updatedDate'>;

type TokenHistoryFormGroupContent = {
  id: FormControl<TokenHistoryFormRawValue['id'] | NewTokenHistory['id']>;
  hashedToken: FormControl<TokenHistoryFormRawValue['hashedToken']>;
  createdDate: FormControl<TokenHistoryFormRawValue['createdDate']>;
  updatedDate: FormControl<TokenHistoryFormRawValue['updatedDate']>;
  status: FormControl<TokenHistoryFormRawValue['status']>;
};

export type TokenHistoryFormGroup = FormGroup<TokenHistoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TokenHistoryFormService {
  createTokenHistoryFormGroup(tokenHistory: TokenHistoryFormGroupInput = { id: null }): TokenHistoryFormGroup {
    const tokenHistoryRawValue = this.convertTokenHistoryToTokenHistoryRawValue({
      ...this.getFormDefaults(),
      ...tokenHistory,
    });
    return new FormGroup<TokenHistoryFormGroupContent>({
      id: new FormControl(
        { value: tokenHistoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      hashedToken: new FormControl(tokenHistoryRawValue.hashedToken),
      createdDate: new FormControl(tokenHistoryRawValue.createdDate),
      updatedDate: new FormControl(tokenHistoryRawValue.updatedDate),
      status: new FormControl(tokenHistoryRawValue.status),
    });
  }

  getTokenHistory(form: TokenHistoryFormGroup): ITokenHistory | NewTokenHistory {
    return this.convertTokenHistoryRawValueToTokenHistory(form.getRawValue() as TokenHistoryFormRawValue | NewTokenHistoryFormRawValue);
  }

  resetForm(form: TokenHistoryFormGroup, tokenHistory: TokenHistoryFormGroupInput): void {
    const tokenHistoryRawValue = this.convertTokenHistoryToTokenHistoryRawValue({ ...this.getFormDefaults(), ...tokenHistory });
    form.reset(
      {
        ...tokenHistoryRawValue,
        id: { value: tokenHistoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TokenHistoryFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      updatedDate: currentTime,
    };
  }

  private convertTokenHistoryRawValueToTokenHistory(
    rawTokenHistory: TokenHistoryFormRawValue | NewTokenHistoryFormRawValue,
  ): ITokenHistory | NewTokenHistory {
    return {
      ...rawTokenHistory,
      createdDate: dayjs(rawTokenHistory.createdDate, DATE_TIME_FORMAT),
      updatedDate: dayjs(rawTokenHistory.updatedDate, DATE_TIME_FORMAT),
    };
  }

  private convertTokenHistoryToTokenHistoryRawValue(
    tokenHistory: ITokenHistory | (Partial<NewTokenHistory> & TokenHistoryFormDefaults),
  ): TokenHistoryFormRawValue | PartialWithRequiredKeyOf<NewTokenHistoryFormRawValue> {
    return {
      ...tokenHistory,
      createdDate: tokenHistory.createdDate ? tokenHistory.createdDate.format(DATE_TIME_FORMAT) : undefined,
      updatedDate: tokenHistory.updatedDate ? tokenHistory.updatedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
