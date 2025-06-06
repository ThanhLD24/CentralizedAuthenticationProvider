import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAccessToken, NewAccessToken } from '../access-token.model';

export type PartialUpdateAccessToken = Partial<IAccessToken> & Pick<IAccessToken, 'id'>;

type RestOf<T extends IAccessToken | NewAccessToken> = Omit<T, 'createdDate' | 'updatedDate'> & {
  createdDate?: string | null;
  updatedDate?: string | null;
};

export type RestAccessToken = RestOf<IAccessToken>;

export type NewRestAccessToken = RestOf<NewAccessToken>;

export type PartialUpdateRestAccessToken = RestOf<PartialUpdateAccessToken>;

export type EntityResponseType = HttpResponse<IAccessToken>;
export type EntityArrayResponseType = HttpResponse<IAccessToken[]>;

@Injectable({ providedIn: 'root' })
export class AccessTokenService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/access-tokens');

  create(accessToken: NewAccessToken): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(accessToken);
    return this.http
      .post<RestAccessToken>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(accessToken: IAccessToken): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(accessToken);
    return this.http
      .put<RestAccessToken>(`${this.resourceUrl}/${this.getAccessTokenIdentifier(accessToken)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(accessToken: PartialUpdateAccessToken): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(accessToken);
    return this.http
      .patch<RestAccessToken>(`${this.resourceUrl}/${this.getAccessTokenIdentifier(accessToken)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestAccessToken>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAccessToken[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAccessTokenIdentifier(accessToken: Pick<IAccessToken, 'id'>): number {
    return accessToken.id;
  }

  compareAccessToken(o1: Pick<IAccessToken, 'id'> | null, o2: Pick<IAccessToken, 'id'> | null): boolean {
    return o1 && o2 ? this.getAccessTokenIdentifier(o1) === this.getAccessTokenIdentifier(o2) : o1 === o2;
  }

  addAccessTokenToCollectionIfMissing<Type extends Pick<IAccessToken, 'id'>>(
    accessTokenCollection: Type[],
    ...accessTokensToCheck: (Type | null | undefined)[]
  ): Type[] {
    const accessTokens: Type[] = accessTokensToCheck.filter(isPresent);
    if (accessTokens.length > 0) {
      const accessTokenCollectionIdentifiers = accessTokenCollection.map(accessTokenItem => this.getAccessTokenIdentifier(accessTokenItem));
      const accessTokensToAdd = accessTokens.filter(accessTokenItem => {
        const accessTokenIdentifier = this.getAccessTokenIdentifier(accessTokenItem);
        if (accessTokenCollectionIdentifiers.includes(accessTokenIdentifier)) {
          return false;
        }
        accessTokenCollectionIdentifiers.push(accessTokenIdentifier);
        return true;
      });
      return [...accessTokensToAdd, ...accessTokenCollection];
    }
    return accessTokenCollection;
  }

  protected convertDateFromClient<T extends IAccessToken | NewAccessToken | PartialUpdateAccessToken>(accessToken: T): RestOf<T> {
    return {
      ...accessToken,
      createdDate: accessToken.createdDate?.toJSON() ?? null,
      updatedDate: accessToken.updatedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restAccessToken: RestAccessToken): IAccessToken {
    return {
      ...restAccessToken,
      createdDate: restAccessToken.createdDate ? dayjs(restAccessToken.createdDate) : undefined,
      updatedDate: restAccessToken.updatedDate ? dayjs(restAccessToken.updatedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestAccessToken>): HttpResponse<IAccessToken> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestAccessToken[]>): HttpResponse<IAccessToken[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
