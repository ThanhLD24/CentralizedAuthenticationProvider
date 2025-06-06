import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IApplicationSystem } from '../application-system.model';
import { ApplicationSystemService } from '../service/application-system.service';

const applicationSystemResolve = (route: ActivatedRouteSnapshot): Observable<null | IApplicationSystem> => {
  const id = route.params.id;
  if (id) {
    return inject(ApplicationSystemService)
      .find(id)
      .pipe(
        mergeMap((applicationSystem: HttpResponse<IApplicationSystem>) => {
          if (applicationSystem.body) {
            return of(applicationSystem.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default applicationSystemResolve;
