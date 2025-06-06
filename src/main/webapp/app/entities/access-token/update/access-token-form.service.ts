import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAccessToken, NewAccessToken } from '../access-token.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAccessToken for edit and NewAccessTokenFormGroupInput for create.
 */
type AccessTokenFormGroupInput = IAccessToken | PartialWithRequiredKeyOf<NewAccessToken>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAccessToken | NewAccessToken> = Omit<T, 'createdDate' | 'updatedDate'> & {
  createdDate?: string | null;
  updatedDate?: string | null;
};

type AccessTokenFormRawValue = FormValueOf<IAccessToken>;

type NewAccessTokenFormRawValue = FormValueOf<NewAccessToken>;

type AccessTokenFormDefaults = Pick<NewAccessToken, 'id' | 'createdDate' | 'updatedDate'>;

type AccessTokenFormGroupContent = {
  id: FormControl<AccessTokenFormRawValue['id'] | NewAccessToken['id']>;
  hashedToken: FormControl<AccessTokenFormRawValue['hashedToken']>;
  createdDate: FormControl<AccessTokenFormRawValue['createdDate']>;
  updatedDate: FormControl<AccessTokenFormRawValue['updatedDate']>;
  status: FormControl<AccessTokenFormRawValue['status']>;
};

export type AccessTokenFormGroup = FormGroup<AccessTokenFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AccessTokenFormService {
  createAccessTokenFormGroup(accessToken: AccessTokenFormGroupInput = { id: null }): AccessTokenFormGroup {
    const accessTokenRawValue = this.convertAccessTokenToAccessTokenRawValue({
      ...this.getFormDefaults(),
      ...accessToken,
    });
    return new FormGroup<AccessTokenFormGroupContent>({
      id: new FormControl(
        { value: accessTokenRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      hashedToken: new FormControl(accessTokenRawValue.hashedToken),
      createdDate: new FormControl(accessTokenRawValue.createdDate),
      updatedDate: new FormControl(accessTokenRawValue.updatedDate),
      status: new FormControl(accessTokenRawValue.status),
    });
  }

  getAccessToken(form: AccessTokenFormGroup): IAccessToken | NewAccessToken {
    return this.convertAccessTokenRawValueToAccessToken(form.getRawValue() as AccessTokenFormRawValue | NewAccessTokenFormRawValue);
  }

  resetForm(form: AccessTokenFormGroup, accessToken: AccessTokenFormGroupInput): void {
    const accessTokenRawValue = this.convertAccessTokenToAccessTokenRawValue({ ...this.getFormDefaults(), ...accessToken });
    form.reset(
      {
        ...accessTokenRawValue,
        id: { value: accessTokenRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AccessTokenFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      updatedDate: currentTime,
    };
  }

  private convertAccessTokenRawValueToAccessToken(
    rawAccessToken: AccessTokenFormRawValue | NewAccessTokenFormRawValue,
  ): IAccessToken | NewAccessToken {
    return {
      ...rawAccessToken,
      createdDate: dayjs(rawAccessToken.createdDate, DATE_TIME_FORMAT),
      updatedDate: dayjs(rawAccessToken.updatedDate, DATE_TIME_FORMAT),
    };
  }

  private convertAccessTokenToAccessTokenRawValue(
    accessToken: IAccessToken | (Partial<NewAccessToken> & AccessTokenFormDefaults),
  ): AccessTokenFormRawValue | PartialWithRequiredKeyOf<NewAccessTokenFormRawValue> {
    return {
      ...accessToken,
      createdDate: accessToken.createdDate ? accessToken.createdDate.format(DATE_TIME_FORMAT) : undefined,
      updatedDate: accessToken.updatedDate ? accessToken.updatedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
