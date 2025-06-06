import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IApplicationSystem, NewApplicationSystem } from '../application-system.model';

export type PartialUpdateApplicationSystem = Partial<IApplicationSystem> & Pick<IApplicationSystem, 'id'>;

type RestOf<T extends IApplicationSystem | NewApplicationSystem> = Omit<T, 'createdDate' | 'updatedDate'> & {
  createdDate?: string | null;
  updatedDate?: string | null;
};

export type RestApplicationSystem = RestOf<IApplicationSystem>;

export type NewRestApplicationSystem = RestOf<NewApplicationSystem>;

export type PartialUpdateRestApplicationSystem = RestOf<PartialUpdateApplicationSystem>;

export type EntityResponseType = HttpResponse<IApplicationSystem>;
export type EntityArrayResponseType = HttpResponse<IApplicationSystem[]>;

@Injectable({ providedIn: 'root' })
export class ApplicationSystemService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/application-systems');

  create(applicationSystem: NewApplicationSystem): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(applicationSystem);
    return this.http
      .post<RestApplicationSystem>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(applicationSystem: IApplicationSystem): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(applicationSystem);
    return this.http
      .put<RestApplicationSystem>(`${this.resourceUrl}/${this.getApplicationSystemIdentifier(applicationSystem)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(applicationSystem: PartialUpdateApplicationSystem): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(applicationSystem);
    return this.http
      .patch<RestApplicationSystem>(`${this.resourceUrl}/${this.getApplicationSystemIdentifier(applicationSystem)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestApplicationSystem>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestApplicationSystem[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getApplicationSystemIdentifier(applicationSystem: Pick<IApplicationSystem, 'id'>): number {
    return applicationSystem.id;
  }

  compareApplicationSystem(o1: Pick<IApplicationSystem, 'id'> | null, o2: Pick<IApplicationSystem, 'id'> | null): boolean {
    return o1 && o2 ? this.getApplicationSystemIdentifier(o1) === this.getApplicationSystemIdentifier(o2) : o1 === o2;
  }

  addApplicationSystemToCollectionIfMissing<Type extends Pick<IApplicationSystem, 'id'>>(
    applicationSystemCollection: Type[],
    ...applicationSystemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const applicationSystems: Type[] = applicationSystemsToCheck.filter(isPresent);
    if (applicationSystems.length > 0) {
      const applicationSystemCollectionIdentifiers = applicationSystemCollection.map(applicationSystemItem =>
        this.getApplicationSystemIdentifier(applicationSystemItem),
      );
      const applicationSystemsToAdd = applicationSystems.filter(applicationSystemItem => {
        const applicationSystemIdentifier = this.getApplicationSystemIdentifier(applicationSystemItem);
        if (applicationSystemCollectionIdentifiers.includes(applicationSystemIdentifier)) {
          return false;
        }
        applicationSystemCollectionIdentifiers.push(applicationSystemIdentifier);
        return true;
      });
      return [...applicationSystemsToAdd, ...applicationSystemCollection];
    }
    return applicationSystemCollection;
  }

  protected convertDateFromClient<T extends IApplicationSystem | NewApplicationSystem | PartialUpdateApplicationSystem>(
    applicationSystem: T,
  ): RestOf<T> {
    return {
      ...applicationSystem,
      createdDate: applicationSystem.createdDate?.toJSON() ?? null,
      updatedDate: applicationSystem.updatedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restApplicationSystem: RestApplicationSystem): IApplicationSystem {
    return {
      ...restApplicationSystem,
      createdDate: restApplicationSystem.createdDate ? dayjs(restApplicationSystem.createdDate) : undefined,
      updatedDate: restApplicationSystem.updatedDate ? dayjs(restApplicationSystem.updatedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestApplicationSystem>): HttpResponse<IApplicationSystem> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestApplicationSystem[]>): HttpResponse<IApplicationSystem[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
