import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IAccessToken } from '../access-token.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../access-token.test-samples';

import { AccessTokenService, RestAccessToken } from './access-token.service';

const requireRestSample: RestAccessToken = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  updatedDate: sampleWithRequiredData.updatedDate?.toJSON(),
};

describe('AccessToken Service', () => {
  let service: AccessTokenService;
  let httpMock: HttpTestingController;
  let expectedResult: IAccessToken | IAccessToken[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AccessTokenService);
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

    it('should create a AccessToken', () => {
      const accessToken = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(accessToken).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a AccessToken', () => {
      const accessToken = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(accessToken).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a AccessToken', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of AccessToken', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a AccessToken', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAccessTokenToCollectionIfMissing', () => {
      it('should add a AccessToken to an empty array', () => {
        const accessToken: IAccessToken = sampleWithRequiredData;
        expectedResult = service.addAccessTokenToCollectionIfMissing([], accessToken);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(accessToken);
      });

      it('should not add a AccessToken to an array that contains it', () => {
        const accessToken: IAccessToken = sampleWithRequiredData;
        const accessTokenCollection: IAccessToken[] = [
          {
            ...accessToken,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAccessTokenToCollectionIfMissing(accessTokenCollection, accessToken);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a AccessToken to an array that doesn't contain it", () => {
        const accessToken: IAccessToken = sampleWithRequiredData;
        const accessTokenCollection: IAccessToken[] = [sampleWithPartialData];
        expectedResult = service.addAccessTokenToCollectionIfMissing(accessTokenCollection, accessToken);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(accessToken);
      });

      it('should add only unique AccessToken to an array', () => {
        const accessTokenArray: IAccessToken[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const accessTokenCollection: IAccessToken[] = [sampleWithRequiredData];
        expectedResult = service.addAccessTokenToCollectionIfMissing(accessTokenCollection, ...accessTokenArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const accessToken: IAccessToken = sampleWithRequiredData;
        const accessToken2: IAccessToken = sampleWithPartialData;
        expectedResult = service.addAccessTokenToCollectionIfMissing([], accessToken, accessToken2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(accessToken);
        expect(expectedResult).toContain(accessToken2);
      });

      it('should accept null and undefined values', () => {
        const accessToken: IAccessToken = sampleWithRequiredData;
        expectedResult = service.addAccessTokenToCollectionIfMissing([], null, accessToken, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(accessToken);
      });

      it('should return initial array if no AccessToken is added', () => {
        const accessTokenCollection: IAccessToken[] = [sampleWithRequiredData];
        expectedResult = service.addAccessTokenToCollectionIfMissing(accessTokenCollection, undefined, null);
        expect(expectedResult).toEqual(accessTokenCollection);
      });
    });

    describe('compareAccessToken', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAccessToken(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 16439 };
        const entity2 = null;

        const compareResult1 = service.compareAccessToken(entity1, entity2);
        const compareResult2 = service.compareAccessToken(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 16439 };
        const entity2 = { id: 15796 };

        const compareResult1 = service.compareAccessToken(entity1, entity2);
        const compareResult2 = service.compareAccessToken(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 16439 };
        const entity2 = { id: 16439 };

        const compareResult1 = service.compareAccessToken(entity1, entity2);
        const compareResult2 = service.compareAccessToken(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
