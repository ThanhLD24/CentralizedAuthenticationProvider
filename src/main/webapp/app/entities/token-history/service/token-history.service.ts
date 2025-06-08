import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITokenHistory, NewTokenHistory } from '../token-history.model';

export type PartialUpdateTokenHistory = Partial<ITokenHistory> & Pick<ITokenHistory, 'id'>;

type RestOf<T extends ITokenHistory | NewTokenHistory> = Omit<T, 'createdDate' | 'updatedDate'> & {
  createdDate?: string | null;
  updatedDate?: string | null;
};

export type RestTokenHistory = RestOf<ITokenHistory>;

export type NewRestTokenHistory = RestOf<NewTokenHistory>;

export type PartialUpdateRestTokenHistory = RestOf<PartialUpdateTokenHistory>;

export type EntityResponseType = HttpResponse<ITokenHistory>;
export type EntityArrayResponseType = HttpResponse<ITokenHistory[]>;

@Injectable({ providedIn: 'root' })
export class TokenHistoryService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/token-histories');

  create(tokenHistory: NewTokenHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tokenHistory);
    return this.http
      .post<RestTokenHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(tokenHistory: ITokenHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tokenHistory);
    return this.http
      .put<RestTokenHistory>(`${this.resourceUrl}/${this.getTokenHistoryIdentifier(tokenHistory)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(tokenHistory: PartialUpdateTokenHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tokenHistory);
    return this.http
      .patch<RestTokenHistory>(`${this.resourceUrl}/${this.getTokenHistoryIdentifier(tokenHistory)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTokenHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTokenHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTokenHistoryIdentifier(tokenHistory: Pick<ITokenHistory, 'id'>): number {
    return tokenHistory.id;
  }

  compareTokenHistory(o1: Pick<ITokenHistory, 'id'> | null, o2: Pick<ITokenHistory, 'id'> | null): boolean {
    return o1 && o2 ? this.getTokenHistoryIdentifier(o1) === this.getTokenHistoryIdentifier(o2) : o1 === o2;
  }

  addTokenHistoryToCollectionIfMissing<Type extends Pick<ITokenHistory, 'id'>>(
    tokenHistoryCollection: Type[],
    ...tokenHistoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tokenHistories: Type[] = tokenHistoriesToCheck.filter(isPresent);
    if (tokenHistories.length > 0) {
      const tokenHistoryCollectionIdentifiers = tokenHistoryCollection.map(tokenHistoryItem =>
        this.getTokenHistoryIdentifier(tokenHistoryItem),
      );
      const tokenHistoriesToAdd = tokenHistories.filter(tokenHistoryItem => {
        const tokenHistoryIdentifier = this.getTokenHistoryIdentifier(tokenHistoryItem);
        if (tokenHistoryCollectionIdentifiers.includes(tokenHistoryIdentifier)) {
          return false;
        }
        tokenHistoryCollectionIdentifiers.push(tokenHistoryIdentifier);
        return true;
      });
      return [...tokenHistoriesToAdd, ...tokenHistoryCollection];
    }
    return tokenHistoryCollection;
  }

  protected convertDateFromClient<T extends ITokenHistory | NewTokenHistory | PartialUpdateTokenHistory>(tokenHistory: T): RestOf<T> {
    return {
      ...tokenHistory,
      createdDate: tokenHistory.createdDate?.toJSON() ?? null,
      updatedDate: tokenHistory.updatedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTokenHistory: RestTokenHistory): ITokenHistory {
    return {
      ...restTokenHistory,
      createdDate: restTokenHistory.createdDate ? dayjs(restTokenHistory.createdDate) : undefined,
      updatedDate: restTokenHistory.updatedDate ? dayjs(restTokenHistory.updatedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTokenHistory>): HttpResponse<ITokenHistory> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTokenHistory[]>): HttpResponse<ITokenHistory[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
