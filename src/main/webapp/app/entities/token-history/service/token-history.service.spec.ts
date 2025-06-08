import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ITokenHistory } from '../token-history.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../token-history.test-samples';

import { RestTokenHistory, TokenHistoryService } from './token-history.service';

const requireRestSample: RestTokenHistory = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  updatedDate: sampleWithRequiredData.updatedDate?.toJSON(),
};

describe('TokenHistory Service', () => {
  let service: TokenHistoryService;
  let httpMock: HttpTestingController;
  let expectedResult: ITokenHistory | ITokenHistory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TokenHistoryService);
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

    it('should create a TokenHistory', () => {
      const tokenHistory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(tokenHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TokenHistory', () => {
      const tokenHistory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(tokenHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TokenHistory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TokenHistory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TokenHistory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTokenHistoryToCollectionIfMissing', () => {
      it('should add a TokenHistory to an empty array', () => {
        const tokenHistory: ITokenHistory = sampleWithRequiredData;
        expectedResult = service.addTokenHistoryToCollectionIfMissing([], tokenHistory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tokenHistory);
      });

      it('should not add a TokenHistory to an array that contains it', () => {
        const tokenHistory: ITokenHistory = sampleWithRequiredData;
        const tokenHistoryCollection: ITokenHistory[] = [
          {
            ...tokenHistory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTokenHistoryToCollectionIfMissing(tokenHistoryCollection, tokenHistory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TokenHistory to an array that doesn't contain it", () => {
        const tokenHistory: ITokenHistory = sampleWithRequiredData;
        const tokenHistoryCollection: ITokenHistory[] = [sampleWithPartialData];
        expectedResult = service.addTokenHistoryToCollectionIfMissing(tokenHistoryCollection, tokenHistory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tokenHistory);
      });

      it('should add only unique TokenHistory to an array', () => {
        const tokenHistoryArray: ITokenHistory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tokenHistoryCollection: ITokenHistory[] = [sampleWithRequiredData];
        expectedResult = service.addTokenHistoryToCollectionIfMissing(tokenHistoryCollection, ...tokenHistoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tokenHistory: ITokenHistory = sampleWithRequiredData;
        const tokenHistory2: ITokenHistory = sampleWithPartialData;
        expectedResult = service.addTokenHistoryToCollectionIfMissing([], tokenHistory, tokenHistory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tokenHistory);
        expect(expectedResult).toContain(tokenHistory2);
      });

      it('should accept null and undefined values', () => {
        const tokenHistory: ITokenHistory = sampleWithRequiredData;
        expectedResult = service.addTokenHistoryToCollectionIfMissing([], null, tokenHistory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tokenHistory);
      });

      it('should return initial array if no TokenHistory is added', () => {
        const tokenHistoryCollection: ITokenHistory[] = [sampleWithRequiredData];
        expectedResult = service.addTokenHistoryToCollectionIfMissing(tokenHistoryCollection, undefined, null);
        expect(expectedResult).toEqual(tokenHistoryCollection);
      });
    });

    describe('compareTokenHistory', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTokenHistory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 6766 };
        const entity2 = null;

        const compareResult1 = service.compareTokenHistory(entity1, entity2);
        const compareResult2 = service.compareTokenHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 6766 };
        const entity2 = { id: 12938 };

        const compareResult1 = service.compareTokenHistory(entity1, entity2);
        const compareResult2 = service.compareTokenHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 6766 };
        const entity2 = { id: 6766 };

        const compareResult1 = service.compareTokenHistory(entity1, entity2);
        const compareResult2 = service.compareTokenHistory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
