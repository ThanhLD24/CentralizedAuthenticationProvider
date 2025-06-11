import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IApplicationSystem, NewApplicationSystem } from '../application-system.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IApplicationSystem for edit and NewApplicationSystemFormGroupInput for create.
 */
type ApplicationSystemFormGroupInput = IApplicationSystem | PartialWithRequiredKeyOf<NewApplicationSystem>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IApplicationSystem | NewApplicationSystem> = Omit<T, 'createdDate' | 'updatedDate'> & {
  createdDate?: string | null;
  updatedDate?: string | null;
};

type ApplicationSystemFormRawValue = FormValueOf<IApplicationSystem>;

type NewApplicationSystemFormRawValue = FormValueOf<NewApplicationSystem>;

type ApplicationSystemFormDefaults = Pick<NewApplicationSystem, 'id' | 'createdDate' | 'updatedDate' | 'active' | 'users'>;

type ApplicationSystemFormGroupContent = {
  id: FormControl<ApplicationSystemFormRawValue['id'] | NewApplicationSystem['id']>;
  name: FormControl<ApplicationSystemFormRawValue['name']>;
  description: FormControl<ApplicationSystemFormRawValue['description']>;
  createdDate: FormControl<ApplicationSystemFormRawValue['createdDate']>;
  updatedDate: FormControl<ApplicationSystemFormRawValue['updatedDate']>;
  active: FormControl<ApplicationSystemFormRawValue['active']>;
  hashedSecretKey: FormControl<ApplicationSystemFormRawValue['hashedSecretKey']>;
  users: FormControl<ApplicationSystemFormRawValue['users']>;
};

export type ApplicationSystemFormGroup = FormGroup<ApplicationSystemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ApplicationSystemFormService {
  createApplicationSystemFormGroup(applicationSystem: ApplicationSystemFormGroupInput = { id: null }): ApplicationSystemFormGroup {
    const applicationSystemRawValue = this.convertApplicationSystemToApplicationSystemRawValue({
      ...this.getFormDefaults(),
      ...applicationSystem,
    });
    return new FormGroup<ApplicationSystemFormGroupContent>({
      id: new FormControl(
        { value: applicationSystemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(applicationSystemRawValue.name),
      description: new FormControl(applicationSystemRawValue.description),
      createdDate: new FormControl(applicationSystemRawValue.createdDate),
      updatedDate: new FormControl(applicationSystemRawValue.updatedDate),
      active: new FormControl(applicationSystemRawValue.active),
      hashedSecretKey: new FormControl(applicationSystemRawValue.hashedSecretKey),
      users: new FormControl(applicationSystemRawValue.users ?? []),
    });
  }

  getApplicationSystem(form: ApplicationSystemFormGroup): IApplicationSystem | NewApplicationSystem {
    return this.convertApplicationSystemRawValueToApplicationSystem(
      form.getRawValue() as ApplicationSystemFormRawValue | NewApplicationSystemFormRawValue,
    );
  }

  resetForm(form: ApplicationSystemFormGroup, applicationSystem: ApplicationSystemFormGroupInput): void {
    const applicationSystemRawValue = this.convertApplicationSystemToApplicationSystemRawValue({
      ...this.getFormDefaults(),
      ...applicationSystem,
    });
    form.reset(
      {
        ...applicationSystemRawValue,
        id: { value: applicationSystemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ApplicationSystemFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      updatedDate: currentTime,
      active: false,
      users: [],
    };
  }

  private convertApplicationSystemRawValueToApplicationSystem(
    rawApplicationSystem: ApplicationSystemFormRawValue | NewApplicationSystemFormRawValue,
  ): IApplicationSystem | NewApplicationSystem {
    return {
      ...rawApplicationSystem,
      createdDate: dayjs(rawApplicationSystem.createdDate, DATE_TIME_FORMAT),
      updatedDate: dayjs(rawApplicationSystem.updatedDate, DATE_TIME_FORMAT),
    };
  }

  private convertApplicationSystemToApplicationSystemRawValue(
    applicationSystem: IApplicationSystem | (Partial<NewApplicationSystem> & ApplicationSystemFormDefaults),
  ): ApplicationSystemFormRawValue | PartialWithRequiredKeyOf<NewApplicationSystemFormRawValue> {
    return {
      ...applicationSystem,
      createdDate: applicationSystem.createdDate ? applicationSystem.createdDate.format(DATE_TIME_FORMAT) : undefined,
      updatedDate: applicationSystem.updatedDate ? applicationSystem.updatedDate.format(DATE_TIME_FORMAT) : undefined,
      users: applicationSystem.users ?? [],
    };
  }
}
