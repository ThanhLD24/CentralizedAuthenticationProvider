import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../application-system.test-samples';

import { ApplicationSystemFormService } from './application-system-form.service';

describe('ApplicationSystem Form Service', () => {
  let service: ApplicationSystemFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApplicationSystemFormService);
  });

  describe('Service methods', () => {
    describe('createApplicationSystemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createApplicationSystemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            createdDate: expect.any(Object),
            updatedDate: expect.any(Object),
            active: expect.any(Object),
            users: expect.any(Object),
          }),
        );
      });

      it('passing IApplicationSystem should create a new form with FormGroup', () => {
        const formGroup = service.createApplicationSystemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            createdDate: expect.any(Object),
            updatedDate: expect.any(Object),
            active: expect.any(Object),
            users: expect.any(Object),
          }),
        );
      });
    });

    describe('getApplicationSystem', () => {
      it('should return NewApplicationSystem for default ApplicationSystem initial value', () => {
        const formGroup = service.createApplicationSystemFormGroup(sampleWithNewData);

        const applicationSystem = service.getApplicationSystem(formGroup) as any;

        expect(applicationSystem).toMatchObject(sampleWithNewData);
      });

      it('should return NewApplicationSystem for empty ApplicationSystem initial value', () => {
        const formGroup = service.createApplicationSystemFormGroup();

        const applicationSystem = service.getApplicationSystem(formGroup) as any;

        expect(applicationSystem).toMatchObject({});
      });

      it('should return IApplicationSystem', () => {
        const formGroup = service.createApplicationSystemFormGroup(sampleWithRequiredData);

        const applicationSystem = service.getApplicationSystem(formGroup) as any;

        expect(applicationSystem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IApplicationSystem should not enable id FormControl', () => {
        const formGroup = service.createApplicationSystemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewApplicationSystem should disable id FormControl', () => {
        const formGroup = service.createApplicationSystemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
