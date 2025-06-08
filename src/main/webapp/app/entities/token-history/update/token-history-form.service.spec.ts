import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../token-history.test-samples';

import { TokenHistoryFormService } from './token-history-form.service';

describe('TokenHistory Form Service', () => {
  let service: TokenHistoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TokenHistoryFormService);
  });

  describe('Service methods', () => {
    describe('createTokenHistoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTokenHistoryFormGroup();

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

      it('passing ITokenHistory should create a new form with FormGroup', () => {
        const formGroup = service.createTokenHistoryFormGroup(sampleWithRequiredData);

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

    describe('getTokenHistory', () => {
      it('should return NewTokenHistory for default TokenHistory initial value', () => {
        const formGroup = service.createTokenHistoryFormGroup(sampleWithNewData);

        const tokenHistory = service.getTokenHistory(formGroup) as any;

        expect(tokenHistory).toMatchObject(sampleWithNewData);
      });

      it('should return NewTokenHistory for empty TokenHistory initial value', () => {
        const formGroup = service.createTokenHistoryFormGroup();

        const tokenHistory = service.getTokenHistory(formGroup) as any;

        expect(tokenHistory).toMatchObject({});
      });

      it('should return ITokenHistory', () => {
        const formGroup = service.createTokenHistoryFormGroup(sampleWithRequiredData);

        const tokenHistory = service.getTokenHistory(formGroup) as any;

        expect(tokenHistory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITokenHistory should not enable id FormControl', () => {
        const formGroup = service.createTokenHistoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTokenHistory should disable id FormControl', () => {
        const formGroup = service.createTokenHistoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
