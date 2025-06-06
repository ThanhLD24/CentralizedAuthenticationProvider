import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../access-token.test-samples';

import { AccessTokenFormService } from './access-token-form.service';

describe('AccessToken Form Service', () => {
  let service: AccessTokenFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccessTokenFormService);
  });

  describe('Service methods', () => {
    describe('createAccessTokenFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAccessTokenFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            hashedToken: expect.any(Object),
            createdDate: expect.any(Object),
            updatedDate: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });

      it('passing IAccessToken should create a new form with FormGroup', () => {
        const formGroup = service.createAccessTokenFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            hashedToken: expect.any(Object),
            createdDate: expect.any(Object),
            updatedDate: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });
    });

    describe('getAccessToken', () => {
      it('should return NewAccessToken for default AccessToken initial value', () => {
        const formGroup = service.createAccessTokenFormGroup(sampleWithNewData);

        const accessToken = service.getAccessToken(formGroup) as any;

        expect(accessToken).toMatchObject(sampleWithNewData);
      });

      it('should return NewAccessToken for empty AccessToken initial value', () => {
        const formGroup = service.createAccessTokenFormGroup();

        const accessToken = service.getAccessToken(formGroup) as any;

        expect(accessToken).toMatchObject({});
      });

      it('should return IAccessToken', () => {
        const formGroup = service.createAccessTokenFormGroup(sampleWithRequiredData);

        const accessToken = service.getAccessToken(formGroup) as any;

        expect(accessToken).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAccessToken should not enable id FormControl', () => {
        const formGroup = service.createAccessTokenFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAccessToken should disable id FormControl', () => {
        const formGroup = service.createAccessTokenFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
