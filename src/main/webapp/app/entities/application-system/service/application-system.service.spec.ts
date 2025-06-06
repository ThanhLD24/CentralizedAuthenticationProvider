import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IApplicationSystem } from '../application-system.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../application-system.test-samples';

import { ApplicationSystemService, RestApplicationSystem } from './application-system.service';

const requireRestSample: RestApplicationSystem = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  updatedDate: sampleWithRequiredData.updatedDate?.toJSON(),
};

describe('ApplicationSystem Service', () => {
  let service: ApplicationSystemService;
  let httpMock: HttpTestingController;
  let expectedResult: IApplicationSystem | IApplicationSystem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ApplicationSystemService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ApplicationSystem', () => {
      const applicationSystem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(applicationSystem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ApplicationSystem', () => {
      const applicationSystem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(applicationSystem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ApplicationSystem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ApplicationSystem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ApplicationSystem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addApplicationSystemToCollectionIfMissing', () => {
      it('should add a ApplicationSystem to an empty array', () => {
        const applicationSystem: IApplicationSystem = sampleWithRequiredData;
        expectedResult = service.addApplicationSystemToCollectionIfMissing([], applicationSystem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(applicationSystem);
      });

      it('should not add a ApplicationSystem to an array that contains it', () => {
        const applicationSystem: IApplicationSystem = sampleWithRequiredData;
        const applicationSystemCollection: IApplicationSystem[] = [
          {
            ...applicationSystem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addApplicationSystemToCollectionIfMissing(applicationSystemCollection, applicationSystem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ApplicationSystem to an array that doesn't contain it", () => {
        const applicationSystem: IApplicationSystem = sampleWithRequiredData;
        const applicationSystemCollection: IApplicationSystem[] = [sampleWithPartialData];
        expectedResult = service.addApplicationSystemToCollectionIfMissing(applicationSystemCollection, applicationSystem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(applicationSystem);
      });

      it('should add only unique ApplicationSystem to an array', () => {
        const applicationSystemArray: IApplicationSystem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const applicationSystemCollection: IApplicationSystem[] = [sampleWithRequiredData];
        expectedResult = service.addApplicationSystemToCollectionIfMissing(applicationSystemCollection, ...applicationSystemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const applicationSystem: IApplicationSystem = sampleWithRequiredData;
        const applicationSystem2: IApplicationSystem = sampleWithPartialData;
        expectedResult = service.addApplicationSystemToCollectionIfMissing([], applicationSystem, applicationSystem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(applicationSystem);
        expect(expectedResult).toContain(applicationSystem2);
      });

      it('should accept null and undefined values', () => {
        const applicationSystem: IApplicationSystem = sampleWithRequiredData;
        expectedResult = service.addApplicationSystemToCollectionIfMissing([], null, applicationSystem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(applicationSystem);
      });

      it('should return initial array if no ApplicationSystem is added', () => {
        const applicationSystemCollection: IApplicationSystem[] = [sampleWithRequiredData];
        expectedResult = service.addApplicationSystemToCollectionIfMissing(applicationSystemCollection, undefined, null);
        expect(expectedResult).toEqual(applicationSystemCollection);
      });
    });

    describe('compareApplicationSystem', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareApplicationSystem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 13520 };
        const entity2 = null;

        const compareResult1 = service.compareApplicationSystem(entity1, entity2);
        const compareResult2 = service.compareApplicationSystem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 13520 };
        const entity2 = { id: 7708 };

        const compareResult1 = service.compareApplicationSystem(entity1, entity2);
        const compareResult2 = service.compareApplicationSystem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 13520 };
        const entity2 = { id: 13520 };

        const compareResult1 = service.compareApplicationSystem(entity1, entity2);
        const compareResult2 = service.compareApplicationSystem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
