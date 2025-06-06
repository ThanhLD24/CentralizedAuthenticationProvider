import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAccessToken } from '../access-token.model';
import { AccessTokenService } from '../service/access-token.service';

const accessTokenResolve = (route: ActivatedRouteSnapshot): Observable<null | IAccessToken> => {
  const id = route.params.id;
  if (id) {
    return inject(AccessTokenService)
      .find(id)
      .pipe(
        mergeMap((accessToken: HttpResponse<IAccessToken>) => {
          if (accessToken.body) {
            return of(accessToken.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default accessTokenResolve;
