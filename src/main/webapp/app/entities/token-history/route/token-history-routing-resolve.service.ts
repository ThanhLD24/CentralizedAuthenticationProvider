import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITokenHistory } from '../token-history.model';
import { TokenHistoryService } from '../service/token-history.service';

const tokenHistoryResolve = (route: ActivatedRouteSnapshot): Observable<null | ITokenHistory> => {
  const id = route.params.id;
  if (id) {
    return inject(TokenHistoryService)
      .find(id)
      .pipe(
        mergeMap((tokenHistory: HttpResponse<ITokenHistory>) => {
          if (tokenHistory.body) {
            return of(tokenHistory.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default tokenHistoryResolve;
